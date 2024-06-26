package com.hana.login.mock.utils

import com.hana.login.common.domain.LoginLog
import com.hana.login.common.domain.RefreshToken
import com.hana.login.common.exception.ApplicationException
import com.hana.login.common.exception.en.ErrorCode
import com.hana.login.common.repositroy.LoginLogRepository
import com.hana.login.common.repositroy.TokenCacheRepository
import com.hana.login.common.utils.JwtUtils
import com.hana.login.user.domain.UserEntity
import com.hana.login.user.repository.UserCacheRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.LocalDateTime
import java.util.*

@SpringBootTest
class FakeJwtUtils @Autowired constructor(
    private val tokenCacheRepository: TokenCacheRepository,
    private val userCacheRepository: UserCacheRepository,
    private val loginLogRepository: LoginLogRepository,
) : JwtUtils{


    private val secretKey: String = "testSecret01234567890testSecrettest0987654321"

    private val expiredMs: Long = 1800

    private val refreshMs: Long = 5000


    override fun generateToken(request: HttpServletRequest,
                               response: HttpServletResponse,
                               userId: String,
                               userName: String,
                               phoneNumber: String,
                               password: String
    ): String {

        if (secretKey == null || expiredMs == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"SecretKey 혹은 expiredMs가 존재하지 않습니다.")
        }
        // refreshToken 쿠키에 저장 - start
        val refreshCookie: ResponseCookie = createRefreshToken(userId)
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        // refreshToken 쿠키에 저장 - end

        userCacheRepository.setUser(
            UserEntity.fixture(userId= userId, userName = userName, phoneNumber= phoneNumber, password= password)
        )

        loginLogRepository.save(
            LoginLog.fixture(
                userId = userId,
                timeStamp = LocalDateTime.now(),
                loginType = "LOGIN",
                userIp = request.remoteAddr,
                id = null,
            )
        )



        return createToken(secretKey, userId, userName, expiredMs)
    }

    override fun isExpired(token: String): Boolean {
        return false;
    }

    override fun getUserId(token: String): String {
        return "hanana0627"
    }

    private fun getUserName(token: String): String {
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
        val newToken = createToken(secretKey, getUserId(accessToken), getUserName(accessToken),  expiredMs)

        val refreshTokenExpired: Date = extreactClaims(refreshToken).expiration
        val newTokenExpired: Date = Date(System.currentTimeMillis() + expiredMs * 1000)

        // newAccessToken의 만료시간이 refreshToken의 만료시간보다 길면 refreshToken 갱신
        if (newTokenExpired > refreshTokenExpired) {
            val refreshCookie: ResponseCookie = createRefreshToken(getUserId(accessToken))
            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
        }
        return newToken
    }

    override fun logout(request: HttpServletRequest, userId: String): Boolean {
        tokenCacheRepository.deleteToken(userId)

        loginLogRepository.save(
            LoginLog.fixture(
                userId = userId,
                timeStamp = LocalDateTime.now(),
                loginType = "LOGOUT",
                userIp = request.remoteAddr,
                id = null,
            )
        )
        return true
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
        if(getUserId(accessToken) != getUserId(refreshToken)) {
            throw ApplicationException(ErrorCode.UNAUTHORIZED, "토큰 정보가 일치하지 않습니다.")
        }

        // refreshToken이 DB에 저장되어 있는지 확인
//        if(tokenRepository.findById(getUserId(refreshToken)).isEmpty) {
        if(tokenCacheRepository.getToken(getUserId(refreshToken)) == null) {
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

    private fun createRefreshToken(userId: String): ResponseCookie {
        if (refreshMs == null || secretKey == null) {
            throw ApplicationException(ErrorCode.INTERNAL_SERVER_ERROR,"secretKey 혹은 refreshMs가 존재하지 않습니다.")
        }

        val claims: Claims = Jwts.claims()
        claims.put("userId", userId)
        val expiredDate: Date = Date(Date().time + (refreshMs * 1000))

        // refreshToken 생성
        val refreshToken: String =   "Refresh" + secretKey + userId  + refreshMs
        // 토큰 저장
        val token = RefreshToken(
            userId = userId,
            expiredAt = expiredDate,
            refreshToken = refreshToken)

        tokenCacheRepository.setToken(token)

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
        userId: String,
        userName: String,
        expiredMs: Long
    ): String {

        val claims: Claims = Jwts.claims()
        claims.put("userId", userId)
        claims.put("userName", userName)

        val token: String = "Bearer " + secretKey + userId + userName + expiredMs

        return token
    }

}