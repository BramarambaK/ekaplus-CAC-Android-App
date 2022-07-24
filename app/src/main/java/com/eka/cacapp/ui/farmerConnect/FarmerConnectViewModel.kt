package com.eka.cacapp.ui.farmerConnect

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.FarmerConnectRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class FarmerConnectViewModel(private val repository: FarmerConnectRepository, private val context: Context) :
        BaseViewModel(repository,context) {


    private val _farConnGenSettingRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val farmerConnectGenSettingsResponse : LiveData<Resource<ResponseBody>>
        get() = _farConnGenSettingRes

    private val _listOfOffersRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val listOfOffersResponse : LiveData<Resource<ResponseBody>>
        get() = _listOfOffersRes

    private val _listOfBidsRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val listOfBidsResponse : LiveData<Resource<ResponseBody>>
        get() = _listOfBidsRes

    private val _listOfBidsOfferorRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val listOfBidsOfferorResponse : LiveData<Resource<ResponseBody>>
        get() = _listOfBidsOfferorRes


    private val _pubBidsColumnRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val pubBidsColumnResponse : LiveData<Resource<ResponseBody>>
        get() = _pubBidsColumnRes

    private val _fcNewOfferDropDownRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcOfferDropdownValues : LiveData<Resource<ResponseBody>>
        get() = _fcNewOfferDropDownRes


    private val _fcPubishNewOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcPubishNewOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _fcPubishNewOfferRes

    private val _fcPubishPutOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcPubishPutOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _fcPubishPutOfferRes

    private val _fcRejectOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcRejectOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _fcRejectOfferRes

    private val _fcDeleteOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcDeleteOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _fcDeleteOfferRes


    private val _fcCancelOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcCancelOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _fcCancelOfferRes


    private val _acceptOfferRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val acceptOfferResponse : LiveData<Resource<ResponseBody>>
        get() = _acceptOfferRes


    private val _fcAcceptCounterByOfferorRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcAcceptCountrByOfferorResponse : LiveData<Resource<ResponseBody>>
        get() = _fcAcceptCounterByOfferorRes

    private val _fcOfferRatingRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val fcOfferRatingResponse : LiveData<Resource<ResponseBody>>
        get() = _fcOfferRatingRes


    private val _bidLogsRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val bidLogsResponse : LiveData<Resource<ResponseBody>>
        get() = _bidLogsRes


    fun getFarmConnecGenSettings() = viewModelScope.launch {
        _farConnGenSettingRes.value = Resource.Loading
        _farConnGenSettingRes.value = repository.getFarmerConnectGenSettings(context)
    }


    fun getListOfOffers(reqParams : JsonObject) = viewModelScope.launch {
        _listOfOffersRes.value = Resource.Loading
        _listOfOffersRes.value = repository.getListOfOffers(context,reqParams)
    }

    fun getListOfBids(reqParams : JsonObject) = viewModelScope.launch {
        _listOfBidsRes.value = Resource.Loading
        _listOfBidsRes.value = repository.getListOfBids(context,reqParams)
    }

    fun getListOfBidsOfferor(reqParams : JsonObject) = viewModelScope.launch {
        _listOfBidsOfferorRes.value = Resource.Loading
        _listOfBidsOfferorRes.value = repository.getListOfBidsOfferor(context,reqParams)
    }


    fun getPublishedBidColumns(columnId : String) = viewModelScope.launch {
        _pubBidsColumnRes.value = Resource.Loading
        _pubBidsColumnRes.value = repository.getPublishedBidColumns(context,columnId)
    }

    fun getFcOfferDropDownFields(fields : String) = viewModelScope.launch {
        _fcNewOfferDropDownRes.value = Resource.Loading
        _fcNewOfferDropDownRes.value = repository.getFcOfferDropDownFields(context,fields)
    }


    fun sendFcPublishNewOfferReq(reqParams: JsonObject) = viewModelScope.launch {
        _fcPubishNewOfferRes.value = Resource.Loading
        _fcPubishNewOfferRes.value = repository.sendPublishOfferReq(context,reqParams)
    }


    fun sendFcPublishPutOfferReq(reqParams: JsonObject,offerId : String) = viewModelScope.launch {
        _fcPubishPutOfferRes.value = Resource.Loading
        _fcPubishPutOfferRes.value = repository.sendPublishOfferPutReq(context,reqParams,offerId)
    }

    fun sendDeleteOfferReq(offerId : String) = viewModelScope.launch {
        _fcDeleteOfferRes.value = Resource.Loading
        _fcDeleteOfferRes.value = repository.sendDeleteOfferReq(context,offerId)
    }

    fun sendCancelOfferReq(reqParams: JsonObject,offerId : String,isOfferor : Boolean) = viewModelScope.launch {
        _fcCancelOfferRes.value = Resource.Loading
        _fcCancelOfferRes.value = repository.sendCancelOfferReq(context,reqParams,offerId,isOfferor)
    }


    fun sendRejectOfferReq(reqParams: JsonObject,offerId : String,isOfferor : Boolean) = viewModelScope.launch {
        _fcRejectOfferRes.value = Resource.Loading
        _fcRejectOfferRes.value = repository.sendRejectOfferReq(context,reqParams,offerId,isOfferor)
    }


    fun acceptOfferReq(reqParams: JsonObject) = viewModelScope.launch {
        _acceptOfferRes.value = Resource.Loading
        _acceptOfferRes.value = repository.acceptOfferReq(context,reqParams)
    }


    fun sendAcceptCounterByOfferor(reqParams: JsonObject,offerId : String,isOfferor : Boolean) = viewModelScope.launch {
        _fcAcceptCounterByOfferorRes.value = Resource.Loading
        _fcAcceptCounterByOfferorRes.value = repository.acceptCounterByOfferor(context,reqParams,offerId,isOfferor)
    }


    fun getBidLogs(bidId : String) = viewModelScope.launch {
        _bidLogsRes.value = Resource.Loading
        _bidLogsRes.value = repository.getBidLogs(context,bidId)
    }

    fun sendOfferRating(reqParams: JsonObject,bidId : String,ratings : String) = viewModelScope.launch {
        _fcOfferRatingRes.value = Resource.Loading
        _fcOfferRatingRes.value = repository.sendOfferRatings(context,reqParams,bidId,ratings)
    }

}