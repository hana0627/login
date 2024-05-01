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
import kotlin.collections.List

@Component
class JwtUtils(
) {

    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null
    @Value("\${jwt.token.expired-time-ms}")
    private val expiredMs: Long? = null



    // 토큰생성
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

    // 토큰 만료여부 확인
    fun isExpired(token: String): Boolean {
        val expiredDate: Date = extreactClaims(token).expiration
        return expiredDate.before(Date())
    }

    fun decodeToken(token: String): Boolean {
        // 토큰을 각 섹션(Header, Payload, Signature)으로 분할
        println("token = $token")
        val chunks: List<String> = token.split(".")
        val decoder = Base64.getUrlDecoder()
        println("여기한번확인")
        println("chunck.size() = ${chunks.size}")
        chunks.forEach{it -> println(it) }

        val header = String(decoder.decode(chunks[0]))
        val payload = String(decoder.decode(chunks[1]))
        return true
    }


    // 토큰 claims 정보 추출
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
