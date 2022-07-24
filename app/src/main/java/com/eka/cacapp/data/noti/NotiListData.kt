package com.eka.cacapp.data.noti

import com.google.gson.annotations.SerializedName

data class NotiListData(
    val Actuals: String,
    @SerializedName("Breach Limit")
    val Breach_Limit: String,
    val Dimensions: String,
    @SerializedName("Group Name")
    val Group_Name: String,
    @SerializedName("Limit Type")
    val Limit_Type: String,
    @SerializedName("Measure Name")
    val Measure_Name: String,
    val Name: String,
    @SerializedName("Run Date")
    val Run_Date: String,
    val Status: String,
    val TIMESTAMP: Long,
    @SerializedName("Threshold Limit")
    val Threshold_Limit: String,
    @SerializedName("Value Type")
    val Value_Type: String
)