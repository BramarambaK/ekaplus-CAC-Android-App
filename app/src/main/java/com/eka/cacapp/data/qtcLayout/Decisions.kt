package com.eka.cacapp.data.qtcLayout

data class Decisions(
    val displayed: String,
    val label: String,
    val labelkey: String,
    val selection: String,
    val position: String,
    val outcomes: List<Outcome>

)