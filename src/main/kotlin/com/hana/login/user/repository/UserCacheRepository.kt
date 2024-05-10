package com.hana.login.user.repository

import com.hana.login.user.domain.UserEntity
import com.hana.login.common.config.USER_CACHE_TTL
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class UserCacheRepository(
    private val redisTemplate: RedisTemplate<String, UserEntity>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun setUser(user: UserEntity): Unit {
        val key = "User:${user.userId}"
        log.info("Set User to Redis {}, {}", key, user);
        redisTemplate.opsForValue().set(key, user, USER_CACHE_TTL)
    }

    fun getUser(userId: String): UserEntity? {
        val key = "User:$userId"
        val user = redisTemplate.opsForValue().get(key)
        log.info("Get User from Redis {} , {}", key, user);
        return user
    }

    fun flushAll() {
        redisTemplate.execute { connection ->
            connection.flushAll()
            null // Kotlin은 마지막 표현식을 반환하므로 명시적으로 null을 반환합니다.
        }
    }
}

