package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.AppUtil
import com.eka.cacapp.utils.Constants
import com.google.gson.JsonArray
import com.google.gson.JsonObject

class DashboardRepository : BaseRepository() {

    suspend fun appMetaInfo(context: Context,platformAppId: String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient(context)
                .getApiService().getAppMeta(
                        headers, platformAppId)
    }


    suspend fun appMetaMenuInfo(context: Context,connectAppId: String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        AppPreferences.saveValue(Constants.PrefCode.APP_ID, connectAppId)
        getRestClient(context)
                .getApiService().getAppMetaMenu(
                        headers, connectAppId)
    }


    suspend fun connectAppData(context: Context,workFlowTask: String, queryParamObject: JsonObject,
                               operationArray: JsonArray,payLoadObject: JsonObject
    ) = safeApiCall {


        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        val bodyParams = JsonObject()


        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("refresh", true)
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("operation", operationArray)
        bodyParams.add("qP", queryParamObject)
        bodyParams.add("payLoadData", payLoadObject)


        getRestClient(context)
                .getApiService().connectDataApiCall(
                        headers, bodyParams)
    }

    suspend fun connectAppDecision(context: Context,workFlowName : String ,workFlowTask: String, outPutObject: JsonObject
    ) = safeApiCall {


        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        headers["X-ObjectAction"] = "CREATE"

        val bodyParams = JsonObject()


        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workflowTaskName", workFlowName)
        bodyParams.addProperty("task", workFlowTask)
/*        bodyParams.add("output", operationArray)*/
        bodyParams.add("output", outPutObject)
    //    bodyParams.add("payLoadData", payLoadObject)


        getRestClient(context)
                .getApiService().connectWorkflowDecision(
                        headers, bodyParams)
    }





    suspend fun connectAppDataSearch(context: Context,workFlowTask: String, qpObject: JsonObject, operationArray: JsonArray) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        val bodyParams = JsonObject()


        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("operation", operationArray)
        bodyParams.add("qP", qpObject)


        getRestClient(context)
                .getApiService().connectDataApiCall(
                        headers, bodyParams)
    }


    suspend fun connectFilterAppData(context: Context,workFlowTask: String,
                                     queryParamObject: JsonObject, operationArray: JsonArray) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        val bodyParams = JsonObject()

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("operation", operationArray)
        bodyParams.add("qP", queryParamObject)


        getRestClient(context)
                .getApiService().connectDataApiCall(
                        headers, bodyParams)
    }

    suspend fun connectSortAppData(context: Context,workFlowTask: String,
                                   queryParamObject: JsonObject,
                                   operationArray: JsonArray) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        val bodyParams = JsonObject()

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("operation", operationArray)
        bodyParams.add("qP", queryParamObject)


        getRestClient(context)
                .getApiService().connectDataApiCall(
                        headers, bodyParams)
    }


    suspend fun connectAppLayout(context: Context,workFlowTask: String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        val bodyParams = HashMap<String, String>()
        bodyParams.put("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.put("deviceType", "mobile")
        bodyParams.put("workFlowTask", workFlowTask)

        getRestClient(context)
                .getApiService().connectLayoutApiCall(
                        headers, bodyParams)
    }


    suspend fun connectColumnData(context: Context,workFlowTask: String, distinctColumns: JsonArray) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        val bodyParams = JsonObject()

        val opertationArr = JsonArray()
        opertationArr.add("distinctColumns")

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("totalCount", 10000)
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("operation", opertationArr)
        bodyParams.add("distinctColumns", distinctColumns)


        getRestClient(context)
                .getApiService().connectDataApiCall(
                        headers, bodyParams)
    }


    suspend fun connectMdmData(context: Context,workFlowTask: String,

                                   dataArray: JsonArray) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        headers["X-ObjectAction"] = "CREATE"


        val bodyParams = JsonObject()

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.add("data", dataArray)


        getRestClient(context)
            .getApiService().connectMdmCall(
                headers, bodyParams)
    }


    suspend fun connectRecommendation(context: Context,workFlowTask: String,recommendedField : String,
             xObjectStr : String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        headers["X-Object"] = xObjectStr


        val bodyParams = JsonObject()

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        if(recommendedField.trim().isNotEmpty()){
            bodyParams.addProperty("recommendation_field",recommendedField)
        }

        getRestClient(context)
                .getApiService().connectRecommendation(
                        headers, bodyParams)
    }



    suspend fun connectSentenceProcess(context: Context,workFlowTask: String,sentence : String,
                                      xObjectStr : String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();

        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        headers["X-Object"] = xObjectStr


        val bodyParams = JsonObject()

        bodyParams.addProperty("appId", AppPreferences.getKeyValue(Constants.PrefCode.APP_ID, "").toString())
        bodyParams.addProperty("deviceType", "mobile")
        bodyParams.addProperty("workFlowTask", workFlowTask)
        bodyParams.addProperty("sentence",sentence)


        getRestClient(context)
                .getApiService().connectNlpProcessSentence(
                        headers, bodyParams)
    }




}