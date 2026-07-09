package com.joaovictorcostadev.pequi_short.dto.response

data class ResponseDto<V>(
    val code:Int,
    val data: V,
    val message:String
)
