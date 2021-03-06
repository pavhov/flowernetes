package ru.flowernetes.script.api.domain.usecase

import ru.flowernetes.entity.script.SourceScript

interface GetSourceScriptByIdUseCase {
    fun exec(id: String): SourceScript
}