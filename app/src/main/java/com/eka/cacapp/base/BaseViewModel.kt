package com.eka.cacapp.base

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.network.Resource
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response

/**
 * Base View Model
 * */

abstract class BaseViewModel (private val repository: BaseRepository,private val context: Context) : ViewModel() {

    private val _getCategoryRes: MutableLiveData<Resource<JsonArray>> =
            MutableLiveData()
    val getCategoryResponse: LiveData<Resource<JsonArray>>
        get() = _getCategoryRes


    private val _getFavCategoryRes: MutableLiveData<Resource<JsonArray>> =
            MutableLiveData()
    val getFavCategoryResponse: LiveData<Resource<JsonArray>>
        get() = _getFavCategoryRes

    private val _getSubCategoryRes: MutableLiveData<Resource<JsonArray>> =
            MutableLiveData()
    val getSubCategoryResponse: LiveData<Resource<JsonArray>>
        get() = _getSubCategoryRes

    private val _fcmDeviceMappingRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val fcmDeviceMappingResponse: LiveData<Resource<ResponseBody>>
        get() = _fcmDeviceMappingRes

    private val _getNotiListRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val notificationListResponse: LiveData<Resource<ResponseBody>>
        get() = _getNotiListRes

    private val _getListofCorporates: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val listOfCorporateResponse: LiveData<Resource<ResponseBody>>
        get() = _getListofCorporates

    private val _getCurrentCorporates: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val currentCorporateResponse: LiveData<Resource<ResponseBody>>
        get() = _getCurrentCorporates

    private val _switchCorporates: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val switchCorporateResponse: LiveData<Resource<ResponseBody>>
        get() = _switchCorporates

    private val _globalSearchRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val globalSearchResponse: LiveData<Resource<ResponseBody>>
        get() = _globalSearchRes

    private val _toggleFavApiRes: MutableLiveData<Resource<Response<Unit>>> =
        MutableLiveData()
    val toggleFavApiResponse: LiveData<Resource<Response<Unit>>>
        get() = _toggleFavApiRes


    private val _getUserProfile: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val userProfileResponse: LiveData<Resource<ResponseBody>>
        get() = _getUserProfile


    fun getCategory() = viewModelScope.launch {
        _getCategoryRes.value = Resource.Loading
        _getCategoryRes.value = repository.getCategoryInfo(context)
    }

    fun getFavCategory() = viewModelScope.launch {
        _getFavCategoryRes.value = Resource.Loading
        _getFavCategoryRes.value = repository.getFavCategoryInfo(context)
    }

    fun getSubCategories(appId:String ) = viewModelScope.launch {
        _getSubCategoryRes.value = Resource.Loading
        _getSubCategoryRes.value = repository.getSubCategories(context,appId)
    }

    fun fcmDeviceMapping(bodyParams :JsonObject ) = viewModelScope.launch {
        _fcmDeviceMappingRes.value = Resource.Loading
        _fcmDeviceMappingRes.value = repository.fcmDeviceMapping(context,bodyParams)
    }

    fun getNotificationList( ) = viewModelScope.launch {
        _getNotiListRes.value = Resource.Loading
        _getNotiListRes.value = repository.getNotificationList(context)
    }

    fun getGlobalSearch(searchParam: String ) = viewModelScope.launch {
        _globalSearchRes.value = Resource.Loading
        _globalSearchRes.value = repository.getGlobalSearch(context,searchParam)
    }

    fun toggleFav(bodyJson: JsonObject,appId: String, appType :String
    ) = viewModelScope.launch {
        _toggleFavApiRes.value = Resource.Loading
        _toggleFavApiRes.value = repository.getToggleFavReq(context,bodyJson,appId,
            appType)

    }

    fun getListOfCorporate( ) = viewModelScope.launch {
        _getListofCorporates.value = Resource.Loading
        _getListofCorporates.value = repository.getListOfCorporates(context)
    }

    fun getCurrentCorporate( ) = viewModelScope.launch {
        _getCurrentCorporates.value = Resource.Loading
        _getCurrentCorporates.value = repository.getCurrentCorporate(context)
    }

    fun switchCorporate(corpId : String ) = viewModelScope.launch {
        _switchCorporates.value = Resource.Loading
        _switchCorporates.value = repository.switchCorporate(context,corpId)
    }

    fun getUserProfile( ) = viewModelScope.launch {
        _getUserProfile.value = Resource.Loading
        _getUserProfile.value = repository.getUserProfileDetails(context)
    }
}