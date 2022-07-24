package com.eka.cacapp.data.search

data class SearchData(
    val _id: Int,
    val actions: Any,
    val apptype: Any,
    val category: Any,
    val chartType: String,
    val description: String,
    val entityType: String,
    val isFavourite: Any,
    val isSlicerPresent: Boolean,
    val name: String,
    val selectedDataviewIds: List<String>,
    val selectedInsightIds: Any,
    val slicerPresent: Boolean
)