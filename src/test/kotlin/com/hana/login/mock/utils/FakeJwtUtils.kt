/**
 * ~~
 * 20240705_HANA
 * 처음에는 토큰값이 수시로 변경되어 FakeJwtUtil 클래스를 만들어서 값을 제어했는데
 * 기존 통합테스트 SpringBootTest 에서 단위테스트인 WebMvcTest으로 변경하면서
 * 의존성 주입을 위해 불필요한 @Bean이 생성되고, 테스트코드의 작성에 어려움을 겪었다.
 * 본 클래스를 대신하여
 * @MockBean 사용을 통해 값을 제어할 예정
 * ~~
 * 20240705_HANA
 * 캐시생성과 같은 내부 로직 구현은 단순히 willReturn() 구문에서 작성하기 어려웠다.
 * api 호출시 캐시가 제대로 생성되었는지 확인하기 위해서
 * FakeJwtUtils를 사용하는것으로 결정.
 * 단순히 mocking으로 해결하기 힘든 코드인 것 같다.
 * 좀 더 나은 방법이 있을것으로 예상됨
 *
 * 20240709_HANA
 * 메서드에 호출에 대한 응답만 적절히 작성했어도 되었을 것 같은데
 * FakeClass를 너무 상세하게 작성했다는 생각이 든다.
 */

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
import org.springframework.boot.web.server.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import java.nio.charset.StandardCharsets
import java.security.Key
import java.time.LocalDateTime
import java.util.*

class FakeJwtUtils (
    private val tokenCacheRepository: TokenCacheRepository,
    private val userCacheRepository: UserCacheRepository,
    private val loginLogRepository: LoginLogRepository,
) : JwtUtils{


    private val secretKey: String = "testSecret.01234567890.testSecrettest0987654321"

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

//        val refreshTokenExpired: Date = extreactClaims(refreshToken).expiration
//        val newTokenExpired: Date = Date(System.currentTimeMillis() + expiredMs * 1000)

        // newAccessToken의 만료시간이 refreshToken의 만료시간보다 길면 refreshToken 갱신
//        if (newTokenExpired > refreshTokenExpired) {
//            val refreshCookie: ResponseCookie = createRefreshToken(getUserId(accessToken))
//            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString())
//        }
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