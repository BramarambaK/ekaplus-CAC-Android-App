package com.eka.cacapp.data.qtcLayout

data class SortData(
       val sortKeys : ArrayList<String>,
       val sortDisplayName : ArrayList<String>,
       val selItemPos : Int,
       val selItemType : String,
       val isSelected : Boolean
)