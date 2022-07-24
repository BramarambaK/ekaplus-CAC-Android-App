package com.eka.cacapp.network

import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants
import java.util.*
import kotlin.collections.HashMap
/**
* for default request Parameters
* */
object RequestParams {

    fun getDefaultHeaderParams() : HashMap<String,String>{
        val header = java.util.HashMap<String, String>()
        header[Constants.RequestParamCode.REQUEST_ID] = UUID.randomUUID().toString().replace("-", "")
        header[Constants.RequestParamCode.CONTENT_TYPE] = Constants.RequestParamCode.APPLICATION_JSON
        header[Constants.RequestParamCode.CHARSET] = Constants.RequestParamCode.UTF_8
        header[Constants.RequestParamCode.SOURCE_DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        return header
    }

    fun getPostLoginHeaderParams() : HashMap<String,String>{
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();
        val headers = getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers[Constants.RequestParamCode.TENANT_DOMAIN] = baseUrl
        headers[Constants.RequestParamCode.AUTHORIZATION] = AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
        headers[Constants.RequestParamCode.USER_ID] = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"").toString()
        return headers
    }

    fun getRefreshTokenHeaderParams() : HashMap<String,String>{
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();
        val headers = getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers[Constants.RequestParamCode.TENANT_DOMAIN] = baseUrl
        headers[Constants.RequestParamCode.AUTHORIZATION] = AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
        headers[Constants.RequestParamCode.USER_ID] = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"").toString()
        headers["refresh_token"] = AppPreferences.getKeyValue(Constants.PrefCode.REFRESH_TOKEN,"").toString()
        headers["access_token"] = AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
        headers["grant_type"] = "cloud_credentials"
        headers["client_id"] = "2"

        return headers
    }


}