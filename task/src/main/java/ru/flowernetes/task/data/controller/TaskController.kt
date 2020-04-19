package ru.flowernetes.task.data.controller

import org.springframework.web.bind.annotation.*
import ru.flowernetes.entity.task.Task
import ru.flowernetes.task.api.domain.dto.TaskDto
import ru.flowernetes.task.api.domain.usecase.AddTaskUseCase
import ru.flowernetes.task.api.domain.usecase.DeleteTaskByIdUseCase
import ru.flowernetes.task.api.domain.usecase.GetTaskByIdUseCase

@RestController
@RequestMapping("/tasks")
class TaskController(
  private val addTaskUseCase: AddTaskUseCase,
  private val getTaskByIdUseCase: GetTaskByIdUseCase,
  private val deleteTaskByIdUseCase: DeleteTaskByIdUseCase
) {

    @PutMapping
    fun addTask(@RequestBody taskDto: TaskDto): Task {
        return addTaskUseCase.exec(taskDto)
    }

    @GetMapping("/{id}")
    fun getTask(@PathVariable id: Long): Task {
        return getTaskByIdUseCase.exec(id)
    }

    @DeleteMapping("/{id}")
    fun deleteTask(@PathVariable id: Long) {
        deleteTaskByIdUseCase.exec(id)
    }
}