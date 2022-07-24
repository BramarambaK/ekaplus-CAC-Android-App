package com.eka.cacapp.data.appMeta

data class ApiMenuData(
        val iconCls: String,
        val itemId: String,
        val label: String,
        val menuItems: List<MenuItem>,
        val reference: String
)