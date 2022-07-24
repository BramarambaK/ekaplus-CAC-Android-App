package com.eka.cacapp.data.qtcLayout

data class ListLayoutRow (
    val key : String,
    val label : String,
    val displayName : String,
    val type : String,
    val placementName : String,
    val hasSort : Boolean ,
    val hasFilter : Boolean,
    val dropDownValue : String,
    val valueExpression : String = ""


)