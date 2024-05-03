package com.hana.login.common.utils.impl

import com.hana.login.common.TokenRepository
import com.hana.login.common.domain.Token
import com.hana.login.common.utils.JwtUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
@RequiredArgsConstructor
class JwtUtilsImpl(
    private val tokenRepository: TokenRepository
) : JwtUtils {
    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null

    @Value("\${jwt.token.expired-time-ms}")
    private val expiredMs: Long? = null

    @Value("\${jwt.refresh.expired-time-ms}")
    private val refreshMs: Long? = null

    /**
     * 토큰생성
     */
    override fun generateToken(
        response: HttpServletResponse,
        memberId: String,
        memberName: String,
    ): String {
        if (secretKey == null || expiredMs == null) {
            throw NullPointerException("SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }

        // refreshToken 쿠키에 저장 - start
        val refreshCookie: ResponseCookie = getRefreshTokenCookie(getRefreshToken(memberId))
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        // refreshToken 쿠키에 저장 - end


        val claims: Claims = Jwts.claims()
        claims.put("memberName", memberName)
        claims.put("memberId", memberId)

        return "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs * 1000))
            .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
            .compact()
    }


    /**
     * 토큰 만료여부 확인
     * 만료된 토큰 : return ture
     * 만료되지 않은 토큰 : return false
     */
    override fun isExpired(token: String): Boolean {
        val expiredDate: Date = extreactClaims(token).expiration
        return expiredDate.before(Date())
    }

    /**
     * token -> 회원 아이디 추출
     */
    override fun getMemberId(token: String): String {
        val claims: Claims = extreactClaims(token)
        return claims["memberId"].toString()
    }

    /**
     *  유효하지 않은 토큰인지 검증
     *  유요하지 않은 토큰 : return ture
     *  유효한 토큰 : return false
     */
    override fun isInValidated(token: String): Boolean {
        // 토큰을 (Header, Payload, Signature)으로 분할
        val chunks: List<String> = token.split(".")

        // 시그니처 추출
        val signature = chunks[2]

        // 헤더+시그니처를 암호화
        val headerAndClaims = "${chunks[0]}.${chunks[1]}"
        val expectedSignature = generateSignature(headerAndClaims)

        // 헤더+시그너처를 암호화한 값과 토큰의 Signature가 일치하는지 여부 확인
        return signature != expectedSignature
    }


    private fun generateSignature(headerAndClaims: String): String {
        if (secretKey == null || expiredMs == null) {
            throw NullPointerException("key 혹은 expiredMs가 존재하지 않습니다.")
        }

        // 시크릿 키를 바이트 배열로 변환
        val keyBytes = secretKey.toByteArray(Charsets.UTF_8)

        // HMAC-SHA256 알고리즘을 사용하여 Mac 객체 초기화
        val hmacSha256 = Mac.getInstance("HmacSHA256")
        val secretKeySpec = SecretKeySpec(keyBytes, "HmacSHA256")
        hmacSha256.init(secretKeySpec)

        // 입력 값을 사용하여 서명 생성
        val signatureBytes = hmacSha256.doFinal(headerAndClaims.toByteArray(Charsets.UTF_8))

        // Base64 인코딩하여 문자열로 반환 (패딩 없는 Base64 인코딩 사용)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes)
    }


    // 토큰 claims 정보 추출
    private fun extreactClaims(token: String): Claims {
        if (secretKey == null || expiredMs == null) {
            throw NullPointerException("key 혹은 expiredMs가 존재하지 않습니다.")
        }

        return Jwts.parserBuilder().setSigningKey(getKey(secretKey))
            .build().parseClaimsJws(token).body
    }

    // 서명키 생성
    private fun getKey(key: String): Key {
        val keyByte: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
        return Keys.hmacShaKeyFor(keyByte)
    }

    private fun getRefreshToken(memberId: String): String {
        if (refreshMs == null || secretKey == null) {
            throw NullPointerException("secretKey 혹은 refreshMs가 존재하지 않습니다.")
        }

        val expired: Date = Date(Date().time + (refreshMs * 1000))


        val claims: Claims = Jwts.claims()
        claims.put("memberId", memberId)


        val result: String = Jwts.builder()
            .setClaims(claims)
            .setExpiration(expired)
            .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
            .compact()

        // 토큰정보 저장
        tokenRepository.save(Token(
            memberId = memberId,
            expiredAt = expired,
            refreshToken = result,
            id = null))

        return result
    }

    private fun getRefreshTokenCookie(refreshToken: String): ResponseCookie {
        return ResponseCookie.from("refresh", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite(Cookie.SameSite.NONE.attributeValue())
            .build()
    }

}
