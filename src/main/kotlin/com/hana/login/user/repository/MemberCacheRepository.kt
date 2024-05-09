package com.hana.login.user.repository

import com.hana.login.user.domain.MemberEntity
import com.hana.sns.common.config.MEMBER_CACHE_TTL
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisCallback
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
@RequiredArgsConstructor
class MemberCacheRepository(
    private val redisTemplate: RedisTemplate<String, MemberEntity>
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun setMember(member: MemberEntity): Unit {
        val key = "Member:${member.memberId}"
        log.info("Set Member to Redis {}, {}", key, member);
        redisTemplate.opsForValue().set(key, member, MEMBER_CACHE_TTL)
    }

    fun getMember(memberName: String): MemberEntity? {
        val key = "Member:$memberName"
        val member = redisTemplate.opsForValue().get(key)
        log.info("Get Member from Redis {} , {}", key, member);
        return member
    }

    fun flushAll() {
        redisTemplate.execute { connection ->
            connection.flushAll()
            null // Kotlin은 마지막 표현식을 반환하므로 명시적으로 null을 반환합니다.
        }
    }
}

