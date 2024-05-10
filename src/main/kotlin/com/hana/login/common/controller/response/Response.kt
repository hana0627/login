package com.hana.login.common.controller.response

data class Response<Any>(
    val resultCode: String,
    val result: Any
) {

    companion object {
        fun error(errorCode: String): Response<Nothing?> {
            return Response(errorCode, null)
        }
        fun <Any> success(result: Any): Response<Any> {
            return Response("SUCCESS", result)
        }

    }

    fun toStream(): String {
        val resultValue = result ?: "null"

        return """{
            "resultCode": "$resultCode",
            "result": $resultValue
            }"""
    }
}