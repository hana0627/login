package com.hana.login.common.repositroy

import com.hana.login.common.config.TOKEN_CACHE_TTL
import com.hana.login.common.domain.RefreshToken
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class TokenCacheRepository (
    private val redisTemplate: RedisTemplate<String, RefreshToken>
){

    private val log = LoggerFactory.getLogger(javaClass)


    fun setToken(refreshToken: RefreshToken): Unit {
        val key = "RefreshToken:${refreshToken.userId}"
        log.info("Set RefreshToken to Redis {}, {}", key, refreshToken)
        redisTemplate.opsForValue().set(key, refreshToken, TOKEN_CACHE_TTL)
    }
    fun getToken(userId: String): RefreshToken? {
        val key = "RefreshToken:$userId"
        val refreshToken:RefreshToken? = redisTemplate.opsForValue().get(key)
        log.info("Get RefreshToken from Redis {} , {}", key, refreshToken)
        return refreshToken
    }

    fun deleteToken(userId: String): Unit {
        val key = "RefreshToken:$userId"
        val result = redisTemplate.delete(key)
        log.info("delete RefreshToken from Redis {} , {}", key, result)
    }

}