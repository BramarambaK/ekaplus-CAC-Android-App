package com.eka.cacapp.ui.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.SettingsRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class SettingsViewModel (private val repository: SettingsRepository,val context : Context) :
    BaseViewModel(repository,context) {

    private val _logoutRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val logoutResponse: LiveData<Resource<ResponseBody>>
        get() = _logoutRes

    private val _validatePassRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val validatePassResponse: LiveData<Resource<ResponseBody>>
        get() = _validatePassRes

    private val _validateNewPassRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val validateNewPassResponse: LiveData<Resource<ResponseBody>>
        get() = _validateNewPassRes

    private val _getPassPolicyRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val getPassPolicyResponse: LiveData<Resource<ResponseBody>>
        get() = _getPassPolicyRes

    private val _validatePassPolicyRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val validatePasswordPolicy: LiveData<Resource<ResponseBody>>
        get() = _validatePassPolicyRes

    private val _changePassRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val changePassResponse: LiveData<Resource<ResponseBody>>
        get() = _changePassRes

    private val _removeFcmMappingRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val removeFcmMappingResponse: LiveData<Resource<ResponseBody>>
        get() = _removeFcmMappingRes


    fun logout() = viewModelScope.launch {
        _logoutRes.value = Resource.Loading
        _logoutRes.value = repository.logoutCall(context)
    }

    fun removeFcmMapping(bodyParams : JsonObject) = viewModelScope.launch {
        _removeFcmMappingRes.value = Resource.Loading
        _removeFcmMappingRes.value = repository.removeFcmMapping(context,bodyParams)
    }

    fun validatePassword(bodyParams : JsonObject) = viewModelScope.launch {
        _validatePassRes.value = Resource.Loading
        _validatePassRes.value = repository.validatePassword(context,bodyParams)
    }

    fun validateNewPassword(bodyParams : JsonObject) = viewModelScope.launch {
        _validateNewPassRes.value = Resource.Loading
        _validateNewPassRes.value = repository.validateNewPassword(context,bodyParams)
    }

    fun changePassword(bodyParams : JsonObject) = viewModelScope.launch {
        _changePassRes.value = Resource.Loading
        _changePassRes.value = repository.changePassword(context,bodyParams)
    }


    fun getPasswordPolicy() = viewModelScope.launch {
        _getPassPolicyRes.value = Resource.Loading
        _getPassPolicyRes.value = repository.getPasswordPolicy(context)
    }

    fun validatePasswordPolicy(bodyParams : JsonObject) = viewModelScope.launch {
        _validatePassPolicyRes.value = Resource.Loading
        _validatePassPolicyRes.value = repository.validatePasswordPolicy(context,bodyParams)
    }


}