package com.hana.login.common.utils.impl

import com.hana.login.common.domain.Token
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.TokenRepository
import com.hana.login.common.utils.JwtUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@Component
@RequiredArgsConstructor
class JwtUtilsImpl(
    private val tokenRepository: TokenRepository,
) : JwtUtils {
    @Value("\${jwt.secret-key}")
    private val secretKey: String? = null

    @Value("\${jwt.token.expired-time-ms}")
    private val expiredMs: Long? = null

    @Value("\${jwt.refresh.expired-time-ms}")
    private val refreshMs: Long? = null

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * 토큰생성
     */
    override fun generateToken(
        response: HttpServletResponse,
        memberId: String,
        memberName: String,
    ): String {
        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }
        // refreshToken 쿠키에 저장 - start
        val refreshCookie: ResponseCookie = createRefreshToken(memberId)
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        // refreshToken 쿠키에 저장 - end

        return createToken(secretKey, memberId, memberName, expiredMs)
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
     * token -> 회원 이름 추출
     */
    private fun getMemberName(token: String): String {
        val claims: Claims = extreactClaims(token)
        return claims["memberName"].toString()
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

    override fun reGenerateToken(response: HttpServletResponse, accessToken: String, refreshToken: String?): String {
        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }

        if (refreshToken == null) {
            throw ApplicationException(ErrorCode.TOKEN_NOT_FOUND, "refreshToken이 존재하지 않습니다.")
        }

        // token 검증 - start
        tokenValidate(refreshToken, accessToken)
        // token 검증 - end

        // 신규토큰 생성
        val newToken = createToken(secretKey, getMemberId(accessToken), getMemberName(accessToken), expiredMs)

        val refreshTokenExpired: Date = extreactClaims(refreshToken).expiration
        val newTokenExpired: Date = Date(System.currentTimeMillis() + expiredMs * 1000)

        // newAccessToken의 만료시간이 refreshToken의 만료시간보다 길면 refreshToken 갱신
        if (newTokenExpired > refreshTokenExpired) {
            val refreshCookie: ResponseCookie = createRefreshToken(getMemberId(accessToken))
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        }

        return newToken
    }

    private fun tokenValidate(
        refreshToken: String,
        accessToken: String
    ) {
        // refreshToken 검증 - start
        if (isInValidated(refreshToken)) {
            log.error("inValidated refreshToken")
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "refreshToken이 유효하지 않습니다.")
        }
        if (isExpired(refreshToken)) {
            log.error("refreshToken is expired")
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "refreshToken이 만료되었습니다.")
        }
        // refreshToken 검증 - end

        // accessToken의 id와 refreshToken id가 같은지 확인
        if(getMemberId(accessToken) != getMemberId(refreshToken)) {
            log.error("inValidated is token")
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다.")
        }

        // refreshToken이 DB에 저장되어 있는지 확인
        if(tokenRepository.findById(getMemberId(refreshToken)).isEmpty) {
            log.error("inValidated is refreshToken")
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다")
        }
    }


    private fun generateSignature(headerAndClaims: String): String {
        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"key 혹은 expiredMs가 존재하지 않습니다.")
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
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "key 혹은 expiredMs가 존재하지 않습니다.")
        }

        return Jwts.parserBuilder().setSigningKey(getKey(secretKey))
            .build().parseClaimsJws(token).body
    }

    // 서명키 생성
    private fun getKey(key: String): Key {
        val keyByte: ByteArray = key.toByteArray(StandardCharsets.UTF_8)
        return Keys.hmacShaKeyFor(keyByte)
    }
    private fun createRefreshToken(memberId: String): ResponseCookie {
        if (refreshMs == null || secretKey == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "secretKey 혹은 refreshMs가 존재하지 않습니다.")
        }

        val claims: Claims = Jwts.claims()
        claims.put("memberId", memberId)
        val expiredDate: Date = Date(Date().time + (refreshMs * 1000))
        
        // refreshToken 생성
        val refreshToken: String =  Jwts.builder()
            .setClaims(claims)
            .setExpiration(expiredDate)
            .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
            .compact()
        // 토큰 저장
        //TODO QueryDsl
        tokenRepository.save(Token(memberId = memberId,
            expiredAt = expiredDate,
            refreshToken = refreshToken))

        // ResponseCookie 객체 생성
        return ResponseCookie.from("refresh", refreshToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .sameSite(Cookie.SameSite.NONE.attributeValue())
            .build()
    }

    private fun createToken(
        secretKey: String,
        memberId: String,
        memberName: String,
        expiredMs: Long
    ): String {

        val claims: Claims = Jwts.claims()
        claims.put("memberId", memberId)
        claims.put("memberName", memberName)

        val token: String = "Bearer " + Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiredMs * 1000))
            .signWith(getKey(secretKey), SignatureAlgorithm.HS256)
            .compact()
        return token
    }

}
