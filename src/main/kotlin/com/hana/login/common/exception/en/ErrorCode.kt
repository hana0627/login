package com.hana.login.common.exception.en

import org.springframework.http.HttpStatus

enum class ErrorCode(
    val status: HttpStatus,
    val message: String
) {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "예상하지 못한 에러가 발생했습니다<br>다시 시도해주세요."),
    // 중복된 유저이름
    DUPLICATED_MEMBER_ID(HttpStatus.CONFLICT,"중복된 아이디 입니다."),
    // 존재하지 않는 회원
    MEMBER_NOT_FOUNT(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    // 권한 없음
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "접근 권한이 없습니다."),
    // 권한 없음
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 로그인 타입입니다."),
    // jwt토큰 없음
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "jwt토큰을 찾을 수 없습니다."),
}
