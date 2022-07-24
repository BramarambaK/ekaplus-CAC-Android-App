package com.eka.cacapp.ui.diseaseIdent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DiseaseIdentificationRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class DiseaseIdenViewModel(private val repository: DiseaseIdentificationRepository, private val context: Context) :
        BaseViewModel(repository,context) {

    private val _validateImageRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val validateImageResponse : LiveData<Resource<ResponseBody>>
        get() = _validateImageRes


    private val _diseaseIdenCountRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val diseaseIdenCountResponse : LiveData<Resource<ResponseBody>>
        get() = _diseaseIdenCountRes


    private val _diseaseIdenPreListRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val diseaseIdenPreListResponse : LiveData<Resource<ResponseBody>>
        get() = _diseaseIdenPreListRes

    private val _diseaseIdenDetailsRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val diseaseIdenDetailsResponse : LiveData<Resource<ResponseBody>>
        get() = _diseaseIdenDetailsRes

    private val _diseaseIdenDeleteRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val diseaseIdenDeleteResponse : LiveData<Resource<ResponseBody>>
        get() = _diseaseIdenDeleteRes

    private val _diseaseIdenFeedbackRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val diseaseIdenFeedbackResponse : LiveData<Resource<ResponseBody>>
        get() = _diseaseIdenFeedbackRes

    fun validateImage(imageName: String) = viewModelScope.launch {
        _validateImageRes.value = Resource.Loading
        _validateImageRes.value = repository.validateImageName(context,imageName)
    }

    fun getdiseaseIdenCount() = viewModelScope.launch {
        _diseaseIdenCountRes.value = Resource.Loading
        _diseaseIdenCountRes.value = repository.diseaseIdenCount(context)
    }

    fun getdiseaseIdenPredList() = viewModelScope.launch {
        _diseaseIdenPreListRes.value = Resource.Loading
        _diseaseIdenPreListRes.value = repository.diseaseIdenPredList(context)
    }


    fun getdiseaseIdenDetails(selId :String) = viewModelScope.launch {
        _diseaseIdenDetailsRes.value = Resource.Loading
        _diseaseIdenDetailsRes.value = repository.diseaseIdenDetails(context,selId)
    }

    fun deleteDisIdenRecord(reqId :String) = viewModelScope.launch {
        _diseaseIdenDeleteRes.value = Resource.Loading
        _diseaseIdenDeleteRes.value = repository.deleteDiseaseIdenRecord(context,reqId)
    }

    fun disIdenFeedback(reqId :String,bodyParams :JsonObject) = viewModelScope.launch {
        _diseaseIdenFeedbackRes.value = Resource.Loading
        _diseaseIdenFeedbackRes.value = repository.diseaseIdenFeedback(context,reqId,bodyParams)
    }

}