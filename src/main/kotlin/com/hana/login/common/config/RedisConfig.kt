package com.hana.login.common.config

import com.hana.login.common.domain.RefreshToken
import com.hana.login.user.domain.UserEntity
import io.lettuce.core.RedisURI
import lombok.RequiredArgsConstructor
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories // redisRepository 쓸거야!
@RequiredArgsConstructor
class RedisConfig (
    private val redisProperties: RedisProperties,
){


    // 커넥션 정보 설정
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        // 기본포트 사용
        val redisURI = RedisURI.Builder.redis(redisProperties.host, redisProperties.port).build()
        val configuration = LettuceConnectionFactory.createRedisConfiguration(redisURI)
        val factory = LettuceConnectionFactory(configuration)
        factory.afterPropertiesSet()
        return factory
    }

    // RedisTemplate 설정
    @Bean
    fun userRedisTemplate(): RedisTemplate<String, UserEntity> {
        val redisTemplate: RedisTemplate<String, UserEntity> = RedisTemplate()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(UserEntity::class.java)
        return redisTemplate
    }

    // RedisTemplate 설정
    @Bean
    fun tokenRedisTemplate(): RedisTemplate<String, RefreshToken> {
        val redisTemplate: RedisTemplate<String, RefreshToken> = RedisTemplate()
        redisTemplate.connectionFactory = redisConnectionFactory()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = Jackson2JsonRedisSerializer(RefreshToken::class.java)
        return redisTemplate
    }
}
