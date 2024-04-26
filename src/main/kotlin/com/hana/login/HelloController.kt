package com.hana.login

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/hello")
    fun hello() : String {
        println("안녕!!!")
        return "안녕!"
    }
}
