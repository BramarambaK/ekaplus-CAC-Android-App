package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.AppUtil
import com.eka.cacapp.utils.Constants
import com.google.gson.JsonObject

class InsightRepository : BaseRepository() {


    suspend fun getAppInsights(context : Context,appId: String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        getRestClient(context)
                .getApiService().getAppInsights(
                        headers,appId,true)
    }


    suspend fun getInsightsDataView(context : Context,dataViewId: String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        getRestClient(context)
                .getApiService().getInsightsDataView(
                        headers,dataViewId)
    }


    suspend fun getInsightsDataVisulize(context : Context,data: JsonObject) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().insightDataVisualize(
                        headers,data)
    }






    suspend fun getCollectionColumnMaping(context : Context,collectionId : String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().insightCollectionsMap(
                        headers,collectionId)
    }

    suspend fun getDateSlicerOptions(context : Context) = safeApiCall {
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().dateSlicerOptions(
                        headers)

    }


    suspend fun getInsightQuickEditInfo(context : Context,collectionId : String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().insightQuickEditInfo(
                        headers,collectionId)
    }


    suspend fun getSlicerDataViewMap(context : Context,id : String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().slicerDataViewMap(
                        headers,id)
    }

    suspend fun getInsightsDtlFltCoums(context : Context,data: JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().insightDataFltrColums(
                        headers,data)
    }



    suspend fun getAppPermCodes(context : Context,appId: String) = safeApiCall {

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        getRestClient(context)
                .getApiService().getAppPermCodes(
                        headers,appId)
    }
}