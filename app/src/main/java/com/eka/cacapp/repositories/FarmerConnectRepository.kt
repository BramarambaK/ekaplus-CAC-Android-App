package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.utils.AppUtil
import com.eka.cacapp.utils.Constants
import com.google.gson.JsonObject

class FarmerConnectRepository  () : BaseRepository() {

    suspend fun getFarmerConnectGenSettings(context : Context)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getFarConnectGenSettings(
                        headers)
    }

    suspend fun getListOfOffers(context : Context,reqParams : JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getListOfOffers(
                        headers,reqParams)
    }


    suspend fun getListOfBids(context : Context,reqParams : JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getListOfBids(
                        headers,reqParams)
    }

    suspend fun getListOfBidsOfferor(context : Context,reqParams : JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getListOfBidsOfferor(
                        headers,reqParams)
    }

    suspend fun getPublishedBidColumns(context : Context,columnId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
            .getApiService().getPublishedBidColumns(
                headers,columnId)
    }

    suspend fun getFcOfferDropDownFields(context : Context,fields : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getFcOfferDropDownFields(
                        headers,fields)
    }


    suspend fun sendPublishOfferReq(context : Context,reqParams: JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().sendPubishOfferReq(
                        headers,reqParams)
    }


    suspend fun sendPublishOfferPutReq(context : Context,reqParams: JsonObject,offerId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().sendPubishOfferPutReq(
                        headers,reqParams,offerId)
    }


    suspend fun sendDeleteOfferReq(context : Context,offerId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().sendDeleteOfferReq(
                        headers,offerId)
    }



    suspend fun sendCancelOfferReq(context : Context,reqParams: JsonObject,offerId : String,isOfferror : Boolean)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().cancelOffer(
                        headers,reqParams,offerId,isOfferror)
    }


    suspend fun sendRejectOfferReq(context : Context,reqParams: JsonObject,offerId : String,isOfferror : Boolean)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        if(isOfferror){
            getRestClient(context)
                    .getApiService().rejectOfferReq(
                            headers,reqParams,offerId,isOfferror)
        }else{
            getRestClient(context)
                    .getApiService().rejectOfferByBidder(
                            headers,reqParams,offerId)
        }

    }

    suspend fun acceptOfferReq(context : Context,reqParams: JsonObject)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().acceptOfferReq(
                        headers,reqParams)
    }

    suspend fun acceptCounterByOfferor(context : Context,reqParams: JsonObject,offerId : String,isOfferror : Boolean)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().rejectOfferReq(
                        headers,reqParams,offerId,isOfferror)
    }

    suspend fun getBidLogs(context : Context,bidId : String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().getBidLogs(
                        headers,bidId)
    }

    suspend fun sendOfferRatings(context : Context,reqParams: JsonObject,
                    bidId:String,rating :String)
            = safeApiCall {
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()
        getRestClient(context)
                .getApiService().offerRating(
                        headers,reqParams,bidId,rating)
    }

}