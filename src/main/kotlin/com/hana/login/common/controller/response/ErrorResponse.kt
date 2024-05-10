package com.hana.login.common.controller.response

import com.hana.login.common.exception.ApplicationException

data class ErrorResponse<ApplicationException> (
    val error: ApplicationException
){
}