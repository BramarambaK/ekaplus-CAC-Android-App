package com.eka.cacapp.data.qtcLayout

data class ListItem (
        var itemWeight: Float = 0f,
        var itemKey: String? = null,
        var itemLabel: String? = null,
        var itemValue: String? = null,
        var itemPlacement: String? = null,
        val placementIndex : Int = 0,
        val itemType : String,
        val hasSort : Boolean,
        val hasFilter :Boolean

)