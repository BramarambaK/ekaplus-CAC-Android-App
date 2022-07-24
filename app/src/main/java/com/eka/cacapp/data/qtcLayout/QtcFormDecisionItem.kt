package com.eka.cacapp.data.qtcLayout

import org.json.JSONArray

data class QtcFormDecisionItem(
    val label: String,
    val labelkey: String,
    val outcomes: JSONArray,
    val position: String,
    val task: String,
    val type: String
)
