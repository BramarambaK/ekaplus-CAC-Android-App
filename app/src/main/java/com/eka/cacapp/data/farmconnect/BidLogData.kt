package com.eka.cacapp.data.farmconnect

data class BidLogData (
    val by: String,
    val date: Long,
    val logType: Int,
    val name: String,
    val remarks: String,
    val userId: String,
    val price : Int
)