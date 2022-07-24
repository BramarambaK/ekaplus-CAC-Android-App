package com.eka.cacapp.data.insight

class AdvanceFltrData(
        val firstOperator : Int=0,
        val secondOperator : Int=0,
        val mainOperator : String="and",
        val firstInput : String,
        val SecondInput : String,
        val isDataSet : Boolean = false,
        val columId : String
)