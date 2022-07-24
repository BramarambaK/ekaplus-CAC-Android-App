package com.eka.cacapp.data.appMeta

data class AppMetaResponse(
        val ObjectMetaIds: List<String>,
        val _id: String,
        val appType: String,
        val config: Config,
        val createdBy: String,
        val createdOn: String,
        val lastModifiedBy: String,
        val lastModifiedOn: String,
        val name: String,
        val platform_id: String,
        val sys__UUID: String,
        val tenantID: String,
        val title: String,
        val type: String,
        val version: String
)