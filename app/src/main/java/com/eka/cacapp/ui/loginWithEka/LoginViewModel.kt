package com.eka.cacapp.ui.loginWithEka

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.LoginRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class LoginViewModel (private val repository: LoginRepository,private val context: Context) :
        BaseViewModel(repository,context) {

    private val _loginWithEkaRes: MutableLiveData<Resource<JsonObject>> =
            MutableLiveData()
    val loginWithEkaResponse: LiveData<Resource<JsonObject>>
        get() = _loginWithEkaRes

    private val _mfaOtpVerifRes: MutableLiveData<Resource<JsonObject>> =
            MutableLiveData()
    val mfaOtpVeriResponse: LiveData<Resource<JsonObject>>
        get() = _mfaOtpVerifRes


    private val _mfaResendOtpRes: MutableLiveData<Resource<JsonObject>> =
            MutableLiveData()
    val mfaResendOtpResponse: LiveData<Resource<JsonObject>>
        get() = _mfaResendOtpRes

    private val _azureLoginRes: MutableLiveData<Resource<JsonObject>> =
        MutableLiveData()
    val azureLoginResponse: LiveData<Resource<JsonObject>>
        get() = _azureLoginRes

    private val _oktaLoginRes: MutableLiveData<Resource<JsonObject>> =
        MutableLiveData()
    val oktaLoginResponse: LiveData<Resource<JsonObject>>
        get() = _oktaLoginRes

    private val _userInfoRes: MutableLiveData<Resource<JsonObject>> =
        MutableLiveData()
    val userInfoResponse: LiveData<Resource<JsonObject>>
        get() = _userInfoRes

    private val _policyDetailsRes: MutableLiveData<Resource<JsonObject>> =
        MutableLiveData()
    val policyDetailsResponse: LiveData<Resource<JsonObject>>
        get() = _policyDetailsRes


    fun login(userName: String,userPass: String,isMfaEnabled : Boolean) = viewModelScope.launch {
        _loginWithEkaRes.value = Resource.Loading
        _loginWithEkaRes.value = repository.loginWithEka(context,userName,userPass,isMfaEnabled)
    }


    fun mfaOtpVerification(userName: String,userPass: String,otpToken : String) = viewModelScope.launch {
        _mfaOtpVerifRes.value = Resource.Loading
        _mfaOtpVerifRes.value = repository.mfaOtpVerify(context,userName,userPass,otpToken)
    }


    fun mfaResendOtp(bodyParams:JsonObject) = viewModelScope.launch {
        _mfaResendOtpRes.value = Resource.Loading
        _mfaResendOtpRes.value = repository.regenerateOtp(context,bodyParams)
    }

    fun azureLoginWithToken(azureToken: String) = viewModelScope.launch {
        _azureLoginRes.value = Resource.Loading
        _azureLoginRes.value = repository.azureLoginWithToken(context,azureToken)
    }

    fun getUserInfo() = viewModelScope.launch {
        _userInfoRes.value = Resource.Loading
        _userInfoRes.value = repository.getUserInfo(context)
    }

    fun oktaLoginWithToken(azureToken: String) = viewModelScope.launch {
        _oktaLoginRes.value = Resource.Loading
        _oktaLoginRes.value = repository.oktaLoginWithToken(context,azureToken)
    }


    fun getPolicyDetails() = viewModelScope.launch {
        _policyDetailsRes.value = Resource.Loading
        _policyDetailsRes.value = repository.getPolicyDetails(context)
    }


}