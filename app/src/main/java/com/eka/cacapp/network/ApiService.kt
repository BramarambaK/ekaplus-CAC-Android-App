package com.eka.cacapp.network

import com.eka.cacapp.data.domainverify.DomainVerifyResModel
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiServices {

    @GET()
    suspend fun getDomainVerifyCall(
        @Url url: String,
        @HeaderMap headers: Map<String, String>,
        @Query("type") type: String
    ): DomainVerifyResModel

    @POST( "cac-mobile-app/login")
    suspend  fun getLoginApi(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>
    ): JsonObject

    @POST( "cac-mobile-app/unique-token")
    suspend  fun getMfaLoginApi(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>
    ): JsonObject

    @POST( "cac-mobile-app/userinfo")
    suspend  fun mfaOtpVerificationApi(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>
    ): JsonObject


    @POST( "cac-mobile-app/regenerate-otp")
    suspend  fun resendMfaLoginOtp(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>,
            @Body  bodyParams: JsonObject
    ): JsonObject

    @GET("cac-mobile-app/apps/categoryinfo")
    suspend fun getCategoryApiCall(
            @HeaderMap headers: Map<String, String>
    ): JsonArray

    @GET("cac-mobile-app/apps/favourite")
    suspend fun getFavoriteApiCall(
            @HeaderMap headers: Map<String, String>
    ): JsonArray

    @GET("cac-mobile-app/apps/{appId}")
    suspend fun getAppSubCat(
            @HeaderMap headers: Map<String, String>,
            @Path("appId") appId: String
    ): JsonArray

    @POST( "connect/api/meta/app/{plat_app_id}")
    suspend  fun getAppMeta(
            @HeaderMap headers: Map<String, String>,
            @Path("plat_app_id") appId: String
    ): JsonObject

    @GET( "connect/api/meta/menuObject/{connect_app_id}?deviceType=mobile")
    suspend  fun getAppMetaMenu(
            @HeaderMap headers: Map<String, String>,
            @Path("connect_app_id") appId: String
    ): JsonObject

    @POST( "cac-mobile-app/logout")
    suspend  fun logoutApiCall(
        @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody


    @POST( "connect/api/workflow/data")
    suspend  fun connectDataApiCall(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @POST( "connect/api/workflow/layout")
    suspend  fun connectLayoutApiCall(
            @HeaderMap headers: Map<String, String>,
            @Body bodyParams: Map<String, String>
    ): okhttp3.ResponseBody

    @POST( "connect/api/workflow")
    suspend  fun connectWorkflowDecision(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @POST( "connect/api/workflow/mdm")
    suspend  fun connectMdmCall(
        @HeaderMap headers: Map<String, String>,
        @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @POST( "connect/api/workflow/recommendation")
    suspend  fun connectRecommendation(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @POST( "connect/api/workflow/aws-api/process-sentence-v2")
    suspend  fun connectNlpProcessSentence(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/insights/")
    suspend  fun getAppInsights(
            @HeaderMap headers: Map<String, String>,
            @Query("linkedAppIds") appId: String,
            @Query("isMobileClient") isMobile: Boolean
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/dataviews/{dataViewId}")
    suspend  fun getInsightsDataView(
            @HeaderMap headers: Map<String, String>,
            @Path("dataViewId") appId: String
    ): okhttp3.ResponseBody

    @POST( "/cac-mobile-app/dataviews/visualize")
    suspend  fun insightDataVisualize(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @GET("cac-mobile-app/dataviews/dateSlicerOptions")
    suspend  fun dateSlicerOptions(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/collections/{collection_id}/column-map")
    suspend  fun insightCollectionsMap(
            @HeaderMap headers: Map<String, String>,
            @Path("collection_id") appId: String
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/collections/{collection_id}/quick-edit-info")
    suspend  fun insightQuickEditInfo(
            @HeaderMap headers: Map<String, String>,
            @Path("collection_id") appId: String
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/insights/{id}/dataview-name-map")
    suspend  fun slicerDataViewMap(
            @HeaderMap headers: Map<String, String>,
            @Path("id") appId: String
    ): okhttp3.ResponseBody

    @POST("/cac-security/api/oauth/refreshToken")
    suspend fun refreshAccessToken(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody

    @POST( "/cac-mobile-app/dataviews/column-values")
    suspend  fun insightDataFltrColums(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @POST( "/cac-mobile-app/entities/{appType}/{app_id}/toggle-favourite")
    suspend  fun toggleAppFav(
        @HeaderMap headers: Map<String, String>,
        @Body  bodyParams: JsonObject,
        @Path("app_id") appId: String,
        @Path("appType") appType: String
    ): Response<Unit>

    @GET( "/cac-mobile-app/analytics/validate")
    suspend  fun validateImgNameDiseaseIden(
            @HeaderMap headers: Map<String, String>,
            @Query("imageName") imageName :String
    ): okhttp3.ResponseBody

    @GET( "/cac-mobile-app/analytics/count")
    suspend  fun getDiseaseIdenCount(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody


    @GET( "/cac-mobile-app/analytics?count=20&orderby=desc&format=Summary")
    suspend  fun getDiseaseIdenPredList(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody


    @GET( "/cac-mobile-app/analytics/{itemId}?format=details")
    suspend  fun getDiseaseIdenDetails(
        @HeaderMap headers: Map<String, String>,
        @Path("itemId") itemId: String
    ): okhttp3.ResponseBody



    @GET( "cac-mobile-app/permcodes/{appId}")
    suspend  fun getAppPermCodes(
            @HeaderMap headers: Map<String, String>,
            @Path("appId") appId: String
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/farmerconnect/general-settings")
    suspend  fun getFarConnectGenSettings(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/published-bids")
    suspend  fun getListOfOffers(
            @HeaderMap headers: Map<String, String>,
            @Query("requestParams") reqParams :JsonObject
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/bids/farmer")
    suspend  fun getListOfBids(
            @HeaderMap headers: Map<String, String>,
            @Query("requestParams") reqParams :JsonObject
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/bids/offeror")
    suspend  fun getListOfBidsOfferor(
            @HeaderMap headers: Map<String, String>,
            @Query("requestParams") reqParams :JsonObject
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/published-bids/values/{columnId}")
    suspend  fun getPublishedBidColumns(
        @HeaderMap headers: Map<String, String>,
        @Path("columnId") columnId :String
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/published-bids/values/{columnId}")
      fun getPublishedBidColumnsNormal(
        @HeaderMap headers: Map<String, String>,
        @Path("columnId") columnId :String
    ): Call<ResponseBody>

    @GET( "cac-mobile-app/bids/values/{columnId}")
    fun getFcBidsColumnsNormal(
            @HeaderMap headers: Map<String, String>,
            @Path("columnId") columnId :String
    ): Call<ResponseBody>

    @GET( "cac-mobile-app/offers/fields/{fields}/values")
    suspend  fun getFcOfferDropDownFields(
            @HeaderMap headers: Map<String, String>,
            @Path("fields") columnId :String
    ): okhttp3.ResponseBody


    @POST( "cac-mobile-app/offers")
    suspend  fun sendPubishOfferReq(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @PUT( "cac-mobile-app/offers/{offerId}")
    suspend  fun sendPubishOfferPutReq(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject,
            @Path("offerId") offerId :String
    ): okhttp3.ResponseBody


    @DELETE( "cac-mobile-app/offers/{offerId}")
    suspend  fun sendDeleteOfferReq(
            @HeaderMap headers: Map<String, String>,
            @Path("offerId") offerId :String
    ): okhttp3.ResponseBody

    @POST( "cac-mobile-app/bids/cancel/{refIds}")
    suspend  fun cancelOffer(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject,
            @Path("refIds") refId :String,
            @Query("byOfferor") byOfferor :Boolean
    ): okhttp3.ResponseBody


    @PUT( "cac-mobile-app/bids/{offerId}")
    suspend  fun rejectOfferReq(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject,
            @Path("offerId") offerId :String,
            @Query("byOfferor") byOfferor :Boolean
    ): okhttp3.ResponseBody

    @POST( "cac-mobile-app/bids")
    suspend  fun acceptOfferReq(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @PUT( "cac-mobile-app/bids/{offerId}")
    suspend  fun rejectOfferByBidder(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject,
            @Path("offerId") offerId :String
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/bids/logs/{bidId}")
    suspend  fun getBidLogs(
            @HeaderMap headers: Map<String, String>,
            @Path("bidId") bidId :String
    ): okhttp3.ResponseBody

    @POST( "cac-mobile-app/bids/ratings/{bidId}/{ratings}")
    suspend  fun offerRating(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject,
            @Path("bidId") bidId :String,
            @Path("ratings") ratings :String
    ): okhttp3.ResponseBody


    @PUT( "cac-mobile-app/device-mappings")
    suspend  fun fcmDeviceMapping(
        @HeaderMap headers: Map<String, String>,
        @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @HTTP(method = "DELETE", path = "cac-mobile-app/device-mappings", hasBody = true)
    suspend  fun removeFcmDeviceMapping(
        @HeaderMap headers: Map<String, String>,
        @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @POST( "cac-security/callback/api/oauth/token/azure")
    suspend fun azureLogin(
        @HeaderMap headers: Map<String, String>,
        @QueryMap query: Map<String,String>
    ): JsonObject

    @POST( "cac-security/callback/api/oauth/token/okta")
    suspend fun oktaLogin(
        @HeaderMap headers: Map<String, String>,
        @QueryMap query: Map<String,String>
    ): JsonObject

    @GET( "cac-security/api/userinfo")
    suspend fun getUserInfo(
        @HeaderMap headers: Map<String, String>,
        @QueryMap query: Map<String,String>
    ): JsonObject

    @POST( "cac-mobile-app/validatePassword")
    suspend  fun validatePassword(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @POST( "cac-mobile-app/validateNewPassword")
    suspend  fun validateNewPassword(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @POST( "cac-mobile-app/change-password")
    suspend  fun changePassword(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/getPasswordPolicy")
    suspend  fun getPasswordPolicy(
        @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody


    @POST( "cac-mobile-app/passwordValidator")
    suspend  fun validatePasswordPolicy(
        @HeaderMap headers: Map<String, String>,
        @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/notifications/business-alerts")
    suspend  fun getNotificationsList(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody

    @GET( "cac-mobile-app/search")
    suspend  fun globalSearch(
            @HeaderMap headers: Map<String, String>,
            @Query("searchBy") searchBy :String
    ): okhttp3.ResponseBody


    @GET( "mdmapi/user/userLoginDetails")
    suspend  fun getCurrentCorporate(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>
    ): okhttp3.ResponseBody



    @GET( "mdmapi/taglist")
    suspend  fun getListOfCorporate(
            @HeaderMap headers: Map<String, String>,
            @QueryMap query: Map<String,String>
    ): okhttp3.ResponseBody


    @POST( "mdmapi/masterdatas/switchCorporate")
    suspend  fun switchCorporate(
            @HeaderMap headers: Map<String, String>,
            @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody


    @GET( "cac-mobile-app/customers/my-profile")
    suspend  fun userProfile(
            @HeaderMap headers: Map<String, String>
    ): okhttp3.ResponseBody

    @GET( "spring/policies/detail")
    suspend  fun policyDetails(
        @HeaderMap headers: Map<String, String>
    ): JsonObject


    @DELETE( "cac-mobile-app/analytics/{reqId}")
    suspend  fun deleteDisIdenReq(
            @HeaderMap headers: Map<String, String>,
            @Path("reqId") reqId :String
    ): okhttp3.ResponseBody

    @PUT( "cac-mobile-app/analytics/{reqId}")
    suspend  fun disIdentFeedback(
        @HeaderMap headers: Map<String, String>,
        @Path("reqId") reqId :String,
        @Body  bodyParams: JsonObject
    ): okhttp3.ResponseBody
}