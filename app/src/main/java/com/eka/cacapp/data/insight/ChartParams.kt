package com.eka.cacapp.data.insight

import org.json.JSONObject

data class ChartParams (
        val chartType : String,
        val chartTitle : String,
        val chartSubTitle : String,
        val enableExport : Boolean,
        val xAxisTitle : String,
        val yAxisTitle : String,
        val xAxisId : String,
        val valueIdList : ArrayList<String>,
        var dataJson : JSONObject,
        var columnMapDataJson : JSONObject,
        var dataViewJson : JSONObject,
        var dataJsonStr : String = "",
        var columnMapDataJsonStr : String = "",
        var dataViewJsonStr : String = "",
        val dataViewId : String = ""
)