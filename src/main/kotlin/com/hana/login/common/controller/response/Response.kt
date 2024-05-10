package com.hana.login.common.controller.response

data class Response<Any>(
    val resultCode: String,
    val result: Any,
) {

    companion object {
//        fun error(errorCode: String, errorMessage: String): Response<Any> {
//            return Response(errorCode, errorMessage)
//        }
        fun <Any> success(result: Any): Response<Any> {
            return Response("SUCCESS", result)
        }

    }
}