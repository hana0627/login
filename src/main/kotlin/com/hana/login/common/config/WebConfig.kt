package com.hana.login.common.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {

    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            // .allowedOrigins("*") // 모든 origins에 대해 오픈하는것은 보안취약점이 될 수 있으므로 피하자!
                                    // 또한 저렇게 하면 ssl 연동시에 뭔가.. 문제가 발생했었던.. 것.. 같다...
            .allowedOrigins("http://localhost:3003", "https://mydamin.com")
    }
}