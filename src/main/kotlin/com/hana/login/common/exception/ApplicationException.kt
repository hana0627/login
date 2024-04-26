package com.hana.login.common.exception

import com.hana.login.common.exception.en.ErrorCode
import java.lang.RuntimeException

data class ApplicationException(
    val errorCode: ErrorCode,
    override val message: String? = null
) : RuntimeException() {

    val getMessage: String
        get() =
        // message가 null이면 errorCode의 메세지를 사용
            // throw EntityNotFoundException()
            if (message == null) {
                errorCode.message
            }
            // message가 null이 아니라면 errorCode.message에 message를 대입
            // throw EntityNotFoundException("예외발생!")
            else {
                message
            }
}
