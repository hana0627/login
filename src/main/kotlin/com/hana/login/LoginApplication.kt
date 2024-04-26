package com.hana.login

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

/**
 *
 * api/v1 => no authentication
 * api/v2 => authentication
 */
@EnableJpaAuditing
@SpringBootApplication
class LoginApplication
fun main(args: Array<String>) {
    runApplication<LoginApplication>(*args)
}
