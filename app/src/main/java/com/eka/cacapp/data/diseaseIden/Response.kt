package com.eka.cacapp.data.diseaseIden

data class Response(
    val createdDate: Long,
    val imageName: String,
    val keyName: String,
    val processTypes: List<String>,
    val requestId: String,
    val responseInfo: List<ResponseInfo>,
    val status: String,
    val tn_image_url: String,
    val updatedDate: Long
)