package com.eka.cacapp.base

import android.content.Context
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.network.Resource
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.AppUtil
import com.eka.cacapp.utils.Constants
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

import retrofit2.HttpException
import javax.net.ssl.SSLHandshakeException

/**
 * Base Repository class
 * */
abstract class BaseRepository {

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                Resource.Success(apiCall.invoke())
            } catch (throwable: Throwable) {
                when (throwable) {

                    is HttpException -> {

                        Resource.Failure(
                            false,
                            throwable.code(),
                            throwable.response()?.errorBody()
                        )
                    }
                    is SSLHandshakeException -> {
                        if(throwable.cause?.message.equals("Pin verification failed")){
                            Resource.Failure(
                                false,
                                5001,
                                null

                            )
                        }else {
                            Resource.Failure(
                                true,
                                null,
                                null
                            )
                        }

                    }
                    else -> {

                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }


    fun getRestClient( context : Context) : RestClient{
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        return RestClient.getInstance(
                context, baseUrl)
    }

    suspend fun getCategoryInfo(context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().getCategoryApiCall(
                        headers)
    }


    suspend fun getFavCategoryInfo(context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().getFavoriteApiCall(
                        headers)
    }


    suspend fun getSubCategories( context: Context,appId: String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().getAppSubCat(
                        headers,appId)
    }

    suspend fun fcmDeviceMapping( context: Context,bodyParam: JsonObject) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
            .getApiService().fcmDeviceMapping(
                headers,bodyParam)
    }

    suspend fun getNotificationList( context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().getNotificationsList(
                        headers)
    }

    suspend fun getGlobalSearch( context: Context,searchParam :String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().globalSearch(
                        headers,searchParam)
    }

    suspend fun getToggleFavReq(context : Context,data: JsonObject,
                                appId: String, appType :String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
            .getApiService().toggleAppFav(
                headers,data,appId,appType)
    }


    suspend fun getListOfCorporates( context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        val queryMap = HashMap<String,String>()
        queryMap["serviceKey"] = "corporateListForCurrentUser"
        queryMap["isStatic"] = "No"
        queryMap["attributeOne"] = "Y"
        queryMap["page"] = "1"
        queryMap["start"] = "0"
        queryMap["limit"] = "25"
        getRestClient( context)
                .getApiService().getListOfCorporate(
                        headers,queryMap)
    }


    suspend fun switchCorporate( context: Context, corporateId : String) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        val queryMap = HashMap<String,String>()
        queryMap["method"] = "switchCorporate"
        queryMap["corporateId"] = corporateId//"CH03"

        val jsonObject = JsonObject()
        jsonObject.addProperty("corporateId",corporateId)

        getRestClient( context)
                .getApiService().switchCorporate(
                        headers,jsonObject)
    }

    suspend fun getCurrentCorporate( context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        val queryMap = HashMap<String,String>()
        queryMap[Constants.RequestParamCode.CONTENT_TYPE] = Constants.RequestParamCode.APPLICATION_JSON

        getRestClient( context)
                .getApiService().getCurrentCorporate(
                        headers,queryMap)
    }


    suspend fun getUserProfileDetails( context: Context) = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        getRestClient( context)
                .getApiService().userProfile(
                        headers)
    }

}