package com.eka.cacapp.data.qtcLayout

import com.google.gson.JsonObject

data class QtcFormValidation (
        val hasValidationErro : Boolean,
        val fieldsHashMap : HashMap<String,String>,
        val dataObj : JsonObject
)
