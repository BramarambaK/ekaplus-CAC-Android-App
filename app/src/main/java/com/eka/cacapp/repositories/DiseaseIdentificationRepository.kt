package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.utils.AppUtil
import com.eka.cacapp.utils.Constants
import com.google.gson.JsonObject

class DiseaseIdentificationRepository () : BaseRepository() {

    suspend fun validateImageName(context : Context, imageName: String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().validateImgNameDiseaseIden(
                        headers,imageName)
    }


    suspend fun diseaseIdenCount(context : Context)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getDiseaseIdenCount(
                        headers)
    }

    suspend fun diseaseIdenPredList(context : Context)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getDiseaseIdenPredList(
                        headers)
    }


    suspend fun diseaseIdenDetails(context : Context, selId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
            .getApiService().getDiseaseIdenDetails(
                headers,selId)
    }


    suspend fun deleteDiseaseIdenRecord(context : Context, reqId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().deleteDisIdenReq(
                        headers,reqId)
    }

    suspend fun diseaseIdenFeedback(context : Context, reqId : String,bodyParams : JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
            .getApiService().disIdentFeedback(
                headers,reqId,bodyParams)
    }

}