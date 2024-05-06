package com.hana.login.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000", "https://accounts.google.com", "http://localhost:8080", "https://mydomain.com", "http://localhost:8080/login/oauth2/code/google")
            .allowedMethods("GET","POST","PATCH","DELETE")
            .allowCredentials(true)
    }
}
