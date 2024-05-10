package com.hana.login.common.controller

import com.hana.login.common.controller.response.Response
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ApplicationException::class)
    fun applicationHandler(error: ApplicationException, response: HttpServletResponse) : Response<Any> {
        log.info("!!Error occurred!! $error")
        response.status = error.errorCode.status.value()
        return Response.error(error.errorCode.name, error.errorCode.message)
//        return ResponseEntity.status(error.errorCode.status)
//            .body(error)
    }

    @ExceptionHandler(Exception::class)
    fun applicationHandler(error: RuntimeException) : ResponseEntity<Any> {
        log.error("!!Error occurred!! $error")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
    }
}
