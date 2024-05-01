//package com.hana.login.common.controller
//
//import com.hana.login.common.exception.ApplicationException
//import com.hana.login.common.exception.en.ErrorCode
//import org.slf4j.LoggerFactory
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.ExceptionHandler
//import org.springframework.web.bind.annotation.RestControllerAdvice
//
//@RestControllerAdvice
//class ControllerAdvice {
//
//    private val log = LoggerFactory.getLogger(javaClass)
//
//    @ExceptionHandler(ApplicationException::class)
//    fun applicationHandler(error: ApplicationException) : ResponseEntity<Any> {
//            log.debug("Error occurred $error")
//        return ResponseEntity.status(error.errorCode.status)
//            .body(error)
//    }
//
//    @ExceptionHandler(Exception::class)
//    fun applicationHandler(error: RuntimeException) : ResponseEntity<Any> {
//        log.error("!!Error occurred!! $error")
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//            .body(ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR))
//    }
//}
