package com.eka.cacapp.data.qtcLayout

data class ListRow (
    var name: String = "",
    var weightSum: Float = 0f,
    var listItems: List<ListItem>? = null
)