package com.eka.cacapp.data.appMeta

data class MenuItem(
        val cls: String,
        val disabled: String,
        val entityVersionMap: EntityVersionMap,
        val gridID: String,
        val handler: String,
        val iconCls: String,
        val itemId: String,
        val items: List<Item>,
        val levelNo: Int,
        val tabRequestId: String,
        val text: String
)