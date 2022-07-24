package com.eka.cacapp.data.appSubList

data class AppSubListItem(
    val _id: Int,
    val apptype: String,
    val category: String,
    val description: String,
    var isFavourite: String,
    val isWorkFlowApp: Boolean,
    val name: String,
    val selectedInsightIds: Any,
    val workFlowApp: Boolean
)