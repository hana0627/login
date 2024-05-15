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

    @GetMapping("/api/v1/hello2")
    fun hello3(): String {
        return "한번에 되면 얼마나 좋을까2트..."
    }
}
