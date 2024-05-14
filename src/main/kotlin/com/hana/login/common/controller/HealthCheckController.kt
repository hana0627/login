package com.hana.login.common.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController {


    @GetMapping("/")
    fun hello(): String {
        return "helloWorld"
    }


    @GetMapping("/api/v1/hello")
    fun hello2(): String {
        return "healthCheck"
    }
}
