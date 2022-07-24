package com.eka.cacapp.repositories

import android.content.Context
import android.util.Base64
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants
import com.eka.cacapp.utils.JWTUtils
import com.google.gson.JsonObject
import org.json.JSONObject

import kotlin.collections.HashMap

class LoginRepository () : BaseRepository() {

    suspend fun loginWithEka(context: Context,userName: String,userPass : String,isMfa :Boolean) = safeApiCall {

        val token = userName+":"+userPass

        val basicToken = Base64.encodeToString(token.toByteArray(), Base64.NO_WRAP)
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers["Tenant-Domain"] = baseUrl
        headers["Authorization"] = "Basic "+basicToken

        if(isMfa){
            headers["verifymfa"] = "true"
        }


        val queryMap = HashMap<String,String>()
        queryMap["grant_type"] = "cloud_credentials"
        queryMap["client_id"] = "2"

        if(isMfa){
            RestClient.getInstance(
                    context,baseUrl)
                    .getApiService().getMfaLoginApi(
                            headers,queryMap)
        }else{
            RestClient.getInstance(
                    context,baseUrl)
                    .getApiService().getLoginApi(
                            headers,queryMap)
        }

    }

    suspend fun mfaOtpVerify(context: Context,userName: String,userPass : String,
               otpToken : String) = safeApiCall {

        val token = userName+":"+userPass

        val basicToken = Base64.encodeToString(token.toByteArray(), Base64.NO_WRAP)
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers["Tenant-Domain"] = baseUrl
        headers["Authorization"] = "OTP Basic "+basicToken
        headers["otp-token"] = otpToken
        headers["verifymfa"] = "true"

        val queryMap = HashMap<String,String>()
        queryMap["grant_type"] = "cloud_credentials"
        queryMap["client_id"] = "2"

        RestClient.getInstance(
                    context,baseUrl)
                    .getApiService().mfaOtpVerificationApi(
                            headers,queryMap)


    }


    suspend fun regenerateOtp(context: Context,
                           bodyParam:JsonObject ) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
        headers["Tenant-Domain"] = baseUrl

        val queryMap = HashMap<String,String>()
        queryMap["grant_type"] = "cloud_credentials"
        queryMap["client_id"] = "2"

        RestClient.getInstance(
                context,baseUrl)
                .getApiService().resendMfaLoginOtp(
                        headers,queryMap,bodyParam)


    }

    suspend fun azureLoginWithToken(context: Context,azureToken: String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()

        val queryMap = HashMap<String,String>()
        queryMap["grant_type"] = "cloud_credentials"
        queryMap["client_id"] = "2"
        queryMap["id_token"] = azureToken

        RestClient.getInstance(
            context,baseUrl)
            .getApiService().azureLogin(
                headers,queryMap)


    }

    suspend fun oktaLoginWithToken(context: Context,oktaToken: String) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val jwt = JWTUtils()
        val jwtBody = JSONObject(jwt.getBody(oktaToken))
        val nonce = jwtBody.optString("nonce")

        val headers = RequestParams.getDefaultHeaderParams()
        headers[Constants.RequestParamCode.DEVICE_ID] = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()

        val queryMap = HashMap<String,String>()
        queryMap["grant_type"] = "cloud_credentials"
        queryMap["client_id"] = "2"
        queryMap["id_token"] = oktaToken
        queryMap["nonce"] = nonce

        RestClient.getInstance(
            context,baseUrl)
            .getApiService().oktaLogin(
                headers,queryMap)


    }


    suspend fun getUserInfo(context: Context) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getPostLoginHeaderParams()

        val queryMap = HashMap<String,String>()
        queryMap["filter"] = "all"

        RestClient.getInstance(
            context,baseUrl)
            .getApiService().getUserInfo(
                headers,queryMap)


    }

    suspend fun getPolicyDetails(context: Context) = safeApiCall {

        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();

        val headers = RequestParams.getDefaultHeaderParams()

        RestClient.getInstance(
            context,baseUrl)
            .getApiService().policyDetails(
                headers)


    }

}