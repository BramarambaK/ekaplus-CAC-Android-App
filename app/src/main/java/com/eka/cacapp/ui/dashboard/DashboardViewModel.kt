package com.eka.cacapp.ui.dashboard

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class DashboardViewModel (private val repository: DashboardRepository,
        private val context: Context) :
        BaseViewModel(repository,context) {

    private val _appMetaRes: MutableLiveData<Resource<JsonObject>> =
            MutableLiveData()
    val appMetaResponse: LiveData<Resource<JsonObject>>
        get() = _appMetaRes


    private val _appMetaMenuRes: MutableLiveData<Resource<JsonObject>> =
            MutableLiveData()
    val appMetaMenuResponse: LiveData<Resource<JsonObject>>
        get() = _appMetaMenuRes


    private val _connectAppDataRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectAppDataResponse: LiveData<Resource<ResponseBody>>
        get() = _connectAppDataRes


    private val _connectColumnDataRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectColumnDataResponse: LiveData<Resource<ResponseBody>>
        get() = _connectColumnDataRes



    private val _connectAppLayoutRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectAppLayoutResponse: LiveData<Resource<ResponseBody>>
        get() = _connectAppLayoutRes


    private val _connectAppDecisionRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectAppDecisionResponse: LiveData<Resource<ResponseBody>>
        get() = _connectAppDecisionRes


    private val _connectAppMdmRes: MutableLiveData<Resource<ResponseBody>> =
        MutableLiveData()
    val connectAppMdmResponse: LiveData<Resource<ResponseBody>>
        get() = _connectAppMdmRes

    private val _connectAppRecommendationRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectAppRecommendationResponse: LiveData<Resource<ResponseBody>>
        get() = _connectAppRecommendationRes


    private val _connectSentenceProcessRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val connectNlpSentenceResponse: LiveData<Resource<ResponseBody>>
        get() = _connectSentenceProcessRes



    fun connectAppDecision(workFlowName: String,workFlowTask: String,
                       outputObj: JsonObject) = viewModelScope.launch {
        _connectAppDecisionRes.value = Resource.Loading
        _connectAppDecisionRes.value = repository.connectAppDecision(context,workFlowName,workFlowTask,outputObj)
    }


    fun getAppMeta(platformAppId : String) = viewModelScope.launch {
        _appMetaRes.value = Resource.Loading
        _appMetaRes.value = repository.appMetaInfo(context,platformAppId)
    }

    fun getAppMenuMeta(connectAppId : String) = viewModelScope.launch {
        _appMetaMenuRes.value = Resource.Loading
        _appMetaMenuRes.value = repository.appMetaMenuInfo(context,connectAppId)
    }


    fun connectAppData(workFlowTask: String,
                       queryParamObject: JsonObject, paginationArray: JsonArray,
                       payLoadObj: JsonObject) = viewModelScope.launch {
        _connectAppDataRes.value = Resource.Loading
        _connectAppDataRes.value = repository.connectAppData(context,workFlowTask,queryParamObject,paginationArray,payLoadObj)
    }


    fun connectSearchAppData(workFlowTask: String,queryParamObject: JsonObject,
         operationArray: JsonArray) = viewModelScope.launch {
        _connectAppDataRes.value = Resource.Loading
        _connectAppDataRes.value = repository.connectAppDataSearch(context,workFlowTask,queryParamObject,operationArray)
    }


    fun connectAppLayout(workFlowTask: String) = viewModelScope.launch {
        _connectAppLayoutRes.value = Resource.Loading
        _connectAppLayoutRes.value = repository.connectAppLayout(context,workFlowTask)
    }


    fun connectDistinctColumnData(workFlowTask: String,distinctColumns : JsonArray) = viewModelScope.launch {
        _connectColumnDataRes.value = Resource.Loading
        _connectColumnDataRes.value = repository.connectColumnData(context,workFlowTask,distinctColumns)
    }


    fun connectFilterAppData(workFlowTask: String,queryParamObject: JsonObject,
                             operationArray: JsonArray
            ) = viewModelScope.launch {
        _connectAppDataRes.value = Resource.Loading
        _connectAppDataRes.value = repository.connectFilterAppData(context,workFlowTask,queryParamObject,operationArray)
    }

    fun connectSortAppData(workFlowTask: String,queryParamObject: JsonObject,operationArray: JsonArray
            ) = viewModelScope.launch {
        _connectAppDataRes.value = Resource.Loading
        _connectAppDataRes.value = repository.connectSortAppData(context,workFlowTask,queryParamObject,operationArray)
    }

    //connectMdmData

    fun connectMdmDataCall(workFlowTask: String,
                        dataArray: JsonArray
                       ) = viewModelScope.launch {
        _connectAppMdmRes.value = Resource.Loading
        _connectAppMdmRes.value = repository.connectMdmData(context,workFlowTask,dataArray)
    }


    fun connectRecommendation(workFlowTask: String,recommendedField : String,
                              xObjStr : String
    ) = viewModelScope.launch {
        _connectAppRecommendationRes.value = Resource.Loading
        _connectAppRecommendationRes.value = repository.connectRecommendation(context,workFlowTask,recommendedField,
        xObjStr)
    }


    fun connectNlpSentenceProcess(workFlowTask: String,sentenceTxt : String,
                              xObjStr : String
    ) = viewModelScope.launch {
        _connectSentenceProcessRes.value = Resource.Loading
        _connectSentenceProcessRes.value = repository.connectSentenceProcess(context,workFlowTask,sentenceTxt,
                xObjStr)
    }






}