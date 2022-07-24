package com.eka.cacapp.data.diseaseIden

data class ResponseInfo(
    val category: String,
    val message: String,
    val probability: Double,
    val processName: String,
    val status: String
)