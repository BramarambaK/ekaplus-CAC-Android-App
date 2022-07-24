package com.eka.cacapp.data.qtcLayout

data class ListingResponse (
    var listRows: List<ListRow>? = null,
    var payLoadData : String,
    var decisionName : String
)