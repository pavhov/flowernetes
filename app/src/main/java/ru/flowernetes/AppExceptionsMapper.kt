package ru.flowernetes

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import ru.flowernetes.util.extensions.toResponseEntityStatus
import java.lang.UnsupportedOperationException

@ControllerAdvice
class AppExceptionsMapper {

    @ExceptionHandler
    fun illegalArgument(e: IllegalArgumentException) = e.toResponseEntityStatus(HttpStatus.BAD_REQUEST)

    @ExceptionHandler
    fun noSuchElement(e: NoSuchElementException) = e.toResponseEntityStatus(HttpStatus.NOT_FOUND)

    @ExceptionHandler
    fun unsupportedOperationException(e: UnsupportedOperationException) = e.toResponseEntityStatus(HttpStatus.BAD_REQUEST)
}