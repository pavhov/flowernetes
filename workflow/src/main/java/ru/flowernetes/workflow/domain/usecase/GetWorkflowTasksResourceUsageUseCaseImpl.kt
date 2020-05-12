package ru.flowernetes.workflow.domain.usecase

import org.springframework.stereotype.Component
import ru.flowernetes.entity.workflow.Workflow
import ru.flowernetes.entity.workload.TaskResourceUsage
import ru.flowernetes.entity.workload.TasksResourceUsage
import ru.flowernetes.entity.workload.Workload
import ru.flowernetes.workflow.api.domain.usecase.GetWorkflowTasksResourceUsageUseCase
import ru.flowernetes.workflow.api.domain.usecase.GetWorkflowTasksTimeIntervalsToWorkloadsUseCase
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class GetWorkflowTasksResourceUsageUseCaseImpl(
  private val getWorkflowTasksTimeIntervalsToWorkloadsUseCase: GetWorkflowTasksTimeIntervalsToWorkloadsUseCase
) : GetWorkflowTasksResourceUsageUseCase {

    override fun <T, R> exec(
      workflow: Workflow,
      from: LocalDate,
      to: LocalDate,
      selectionRequest: (Workload) -> T,
      selectionLimit: (Workload) -> T,
      plus: (R, T) -> R,
      empty: R
    ): List<TasksResourceUsage<T, R>> {
        fun emptyResourceUsage(time: LocalDateTime): TasksResourceUsage<T, R> = TasksResourceUsage(
          time = time,
          usages = emptyList(),
          totalRequest = empty,
          totalLimit = empty
        )

        val timeIntervalsToWorkloads = getWorkflowTasksTimeIntervalsToWorkloadsUseCase.exec(
          workflow = workflow,
          from = from,
          to = to
        ).toList().sortedBy { (interval, _) -> interval.from }

        val maxTime = timeIntervalsToWorkloads.lastOrNull()?.first?.to
        val tasksRamUsages = timeIntervalsToWorkloads.map { (interval, workloads) ->
            TasksResourceUsage(
              interval.from,
              usages = workloads.map { TaskResourceUsage(it.task, selectionRequest(it), selectionLimit(it)) },
              totalRequest = workloads.fold(empty) { acc, w -> plus(acc, selectionRequest(w)) },
              totalLimit = workloads.fold(empty) { acc, w -> plus(acc, selectionLimit(w)) }
            )
        }
        val minTime = tasksRamUsages.firstOrNull()?.time
        val startDate = from.atStartOfDay()

        val start = minTime?.let {
            if (it == startDate) mutableListOf<TasksResourceUsage<T, R>>() else null
        } ?: mutableListOf(emptyResourceUsage(startDate))

        val endDate = to.plusDays(1).atStartOfDay()
        val end = maxTime?.let {
            if (it < endDate) {
                listOf(emptyResourceUsage(it), emptyResourceUsage(endDate))
            } else {
                listOf(emptyResourceUsage(it))
            }
        } ?: listOf(emptyResourceUsage(endDate))

        return start + tasksRamUsages + end
    }
}