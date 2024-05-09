package com.hana.login.mock.utils

import com.hana.login.common.domain.Token
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.TokenRepository
import com.hana.login.common.repositroy.impl.TokenQueryRepository
import com.hana.login.common.utils.JwtUtils
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*

@SpringBootTest
class FakeJwtUtils @Autowired constructor(
    private val tokenRepository: TokenRepository,
    private val tokenQueryRepository: TokenQueryRepository,
) : JwtUtils{


    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"

    private val expiredMs: Long = 1800

    private val refreshMs: Long = 5000


    override fun generateToken(response: HttpServletResponse, memberId: String, memberName: String): String {

        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }
        // refreshToken 쿠키에 저장 - start
        val refreshCookie: ResponseCookie = createRefreshToken(memberId)
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        // refreshToken 쿠키에 저장 - end

        return createToken(secretKey, memberId, memberName, expiredMs)

//        return "Bearer tokenHeader.tokenPayload.tokenSignature"
    }

    override fun isExpired(token: String): Boolean {
        return false;
    }

    override fun getMemberId(token: String): String {
        return "hanana0627"
    }

    private fun getMemberName(token: String): String {
        return "박하나"
    }

    override fun isInValidated(token: String): Boolean {
        return false
    }

    override fun reGenerateToken(response: HttpServletResponse, accessToken: String, refreshToken: String?): String {
        if (secretKey == null || expiredMs == null) {
            throw throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR, "SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }

        if (refreshToken == null) {
            throw throw ApplicationException(ErrorCode.TOKEN_NOT_FOUND,"accessToken 혹은 refreshToken이 존재하지 않습니다.")
        }

        // token 검증 - start
        tokenValidate(refreshToken, accessToken)
        // token 검증 - end

        // 신규토큰 생성
        val newToken = createToken(secretKey, getMemberId(accessToken), getMemberName(accessToken), expiredMs).replace("Bearer ","Bearer new")

        return newToken
    }


    private fun tokenValidate(
        refreshToken: String,
        accessToken: String
    ) {
        // refreshToken 검증 - start
        if (isInValidated(refreshToken)) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "refreshToken이 유효하지 않습니다.")
        }
        if (isExpired(refreshToken)) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "refreshToken이 만료되었습니다.")
        }
        // refreshToken 검증 - end

        // accessToken의 id와 refreshToken id가 같은지 확인
        if(getMemberId(accessToken) != getMemberId(refreshToken)) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다.")
        }

        // refreshToken이 DB에 저장되어 있는지 확인
        if(tokenRepository.findById(getMemberId(refreshToken)).isEmpty) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "유효하지 않은 토큰입니다")
        }
    }
    private fun generateSignature(headerAndClaims: String): String {
        return "NOT USED"
    }


    // 토큰 claims 정보 추출
    private fun extreactClaims(token: String): Claims {
        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"key 혹은 expiredMs가 존재하지 않습니다.")
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
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"secretKey 혹은 refreshMs가 존재하지 않습니다.")
        }

        val claims: Claims = Jwts.claims()
        claims.put("memberId", memberId)
        val expiredDate: Date = Date(Date().time + (refreshMs * 1000))

        // refreshToken 생성
        val refreshToken: String =   "Refresh" + secretKey + memberId  + refreshMs
        // 토큰 저장
        val token = Token(
            memberId = memberId,
            expiredAt = expiredDate,
            refreshToken = refreshToken)

        val exist: Boolean = tokenRepository.existsById(memberId)
        if (exist) {
            //@Transactional : private Method라서 repository 레이어에 적용했음
            tokenQueryRepository.update(token)
        } else {
            tokenRepository.save(token)
        }

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

        val token: String = "Bearer " + secretKey + memberId + memberName + expiredMs
        return token
    }

}