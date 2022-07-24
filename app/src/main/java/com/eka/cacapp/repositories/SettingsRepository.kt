package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.google.gson.JsonObject

class SettingsRepository () : BaseRepository() {

    suspend fun logoutCall(context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
            .getApiService().logoutApiCall(
                headers)
    }

    suspend fun removeFcmMapping(context: Context, bodyParams : JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
            .getApiService().removeFcmDeviceMapping(
                headers,bodyParams)
    }

    suspend fun validatePassword(context: Context, bodyParams : JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
                .getApiService().validatePassword(
                        headers,bodyParams)
    }

    suspend fun validateNewPassword(context: Context, bodyParams : JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
                .getApiService().validateNewPassword(
                        headers,bodyParams)
    }

    suspend fun changePassword(context: Context, bodyParams : JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
                .getApiService().changePassword(
                        headers,bodyParams)
    }

    suspend fun getPasswordPolicy(context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
            .getApiService().getPasswordPolicy(
                headers)
    }

    suspend fun validatePasswordPolicy(context: Context, bodyParams : JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
            .getApiService().validatePasswordPolicy(
                headers,bodyParams)
    }
}