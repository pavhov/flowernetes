package ru.flowernetes.task.api.domain.usecase

import ru.flowernetes.entity.task.Task

interface UserKillTaskUseCase {
    fun exec(task: Task)
}