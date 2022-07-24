package com.eka.cacapp.data.appMeta

data class Item(
        val cls: String,
        val disabled: String,
        val entityVersionMap: EntityVersionMapX,
        val gridID: String,
        val handler: String,
        val iconCls: String,
        val itemId: String,
        val levelNo: Int,
        val tabRequestId: String,
        val text: String
)