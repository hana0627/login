package com.hana.login.common.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@Component
class JwtUtils(
) {

    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null
    @Value("\${jwt.token.expired-time-ms}")
    private val expiredMs: Long? = null

    fun generateToken(
        memberId: String,
        memberName: String,
    ): String {
        if (secretKey == null || expiredMs == null) {
            throw NullPointerException("SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }

        val claims: Claims = Jwts.claims()
        claims.put("memberName", memberName)
        claims.put("memberId", memberId)

        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs))
            .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
            .compact()
    }


    fun isExpired(token: String): Boolean {
        val expiredDate: Date = extreactClaims(token).expiration
        return expiredDate.before(Date())
    }

    private fun extreactClaims(token: String): Claims {
        if (secretKey == null || expiredMs == null) {
            throw NullPointerException("key 혹은 expiredMs가 존재하지 않습니다.")
        }

        return Jwts.parserBuilder().setSigningKey(getKey(secretKey))
            .build().parseClaimsJws(token).body
    }

    private fun getKey(key: String): Key {
        val keyByte: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
        return Keys.hmacShaKeyFor(keyByte)
    }

}
