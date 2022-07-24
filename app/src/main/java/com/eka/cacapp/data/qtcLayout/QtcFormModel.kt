package com.eka.cacapp.data.qtcLayout

import org.json.JSONArray

data class QtcFormModel(
    val key : String,
    val dependsOn: JSONArray,
    var children: JSONArray,
    val dropdownValue: String,
    val isRequired: Boolean,
    val labelKey: String,
    val originId: String,
    var parent: JSONArray,
    var parentKeys: JSONArray,
    val serviceKey: String,
    val type: String,
    val displayName : String,
    val pageNo : Int,
    val properyKeyArray : JSONArray,
    val itemType : String,
    val itemVisibilityExpr : String,
    val uiVisibilityKey : String,
    val defaultValue : String,
    val event : String,
    var recommendedDefaultValue : String,
    var nlpRecommendValue : String,
    var dateFormat : String = "yyyy-MM-dd",
    var comparisonArr : JSONArray = JSONArray()

)