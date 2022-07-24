package com.eka.cacapp.data.insight

import org.json.JSONObject

data class SlicerData (
        val visualizeData : JSONObject,
        val dataViewData : JSONObject,
        val chartId : String
)