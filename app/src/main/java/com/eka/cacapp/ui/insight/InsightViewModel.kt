package com.eka.cacapp.ui.insight

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.network.Resource

import com.eka.cacapp.repositories.InsightRepository
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class InsightViewModel  (private val repository: InsightRepository,private val context: Context) :
        BaseViewModel(repository,context) {

    private val _appInsightsRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appInsightResponse: LiveData<Resource<ResponseBody>>
        get() = _appInsightsRes

    private val _appInsightsDataViewRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appInsightDataViewResponse: LiveData<Resource<ResponseBody>>
        get() = _appInsightsDataViewRes

    private val _slicerInsightsDataViewRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val slicerInsightDataViewResponse: LiveData<Resource<ResponseBody>>
        get() = _slicerInsightsDataViewRes

    private val _appInsightsDataVisulizeRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appInsightDataVisulizeResponse: LiveData<Resource<ResponseBody>>
        get() = _appInsightsDataVisulizeRes


    private val _appSLicerInsightsDataVisulizeRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appSLicerInsightDataVisulizeResponse: LiveData<Resource<ResponseBody>>
        get() = _appSLicerInsightsDataVisulizeRes


    private val _appCollectionColumnMapping: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appCollectionColumMapingResponse: LiveData<Resource<ResponseBody>>
        get() = _appCollectionColumnMapping


    private val _appInsightEditInfo: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appInsightEditInfoResponse: LiveData<Resource<ResponseBody>>
        get() = _appInsightEditInfo

    private val _appDateSlicerInfo: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appDateSlicerInfoResponse: LiveData<Resource<ResponseBody>>
        get() = _appDateSlicerInfo

    private val _slicerDataViewMap: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appSlicerDataMapResponse: LiveData<Resource<ResponseBody>>
        get() = _slicerDataViewMap


    private val _appInsightsDtlFltrColumsRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appInsightDtlFltrCoumResponse: LiveData<Resource<ResponseBody>>
        get() = _appInsightsDtlFltrColumsRes

    private val _appPermCodeRes: MutableLiveData<Resource<ResponseBody>> =
            MutableLiveData()
    val appPermCodeResponse: LiveData<Resource<ResponseBody>>
        get() = _appPermCodeRes


    fun getAppInsights(appId: String
    ) = viewModelScope.launch {
        _appInsightsRes.value = Resource.Loading
        _appInsightsRes.value = repository.getAppInsights(context,appId)
    }

    fun getInsightDataView(dataViewId: String
    ) = viewModelScope.launch {
        _appInsightsDataViewRes.value = Resource.Loading
        _appInsightsDataViewRes.value = repository.getInsightsDataView(context,dataViewId)
    }

    fun getSlicerInsightDataView(dataViewId: String
    ) = viewModelScope.launch {
        _slicerInsightsDataViewRes.value = Resource.Loading
        _slicerInsightsDataViewRes.value = repository.getInsightsDataView(context,dataViewId)
    }

    fun getInsightDataVisulize(data: JsonObject
    ) = viewModelScope.launch {
        _appInsightsDataVisulizeRes.value = Resource.Loading
        _appInsightsDataVisulizeRes.value = repository.getInsightsDataVisulize(context,data)
    }

    fun getSlicerInsightDataVisulize(data: JsonObject
    ) = viewModelScope.launch {
        _appSLicerInsightsDataVisulizeRes.value = Resource.Loading
        _appSLicerInsightsDataVisulizeRes.value = repository.getInsightsDataVisulize(context,data)
    }



    fun getInsightCollectionMapping(id: String
    ) = viewModelScope.launch {
        _appCollectionColumnMapping.value = Resource.Loading
        _appCollectionColumnMapping.value = repository.getCollectionColumnMaping(context,id)
    }


    fun getInsightQuickEditInfo(id: String
    ) = viewModelScope.launch {
        _appInsightEditInfo.value = Resource.Loading
        _appInsightEditInfo.value = repository.getInsightQuickEditInfo(context,id)
    }

    fun getDateSlicerInfo() = viewModelScope.launch {
        _appDateSlicerInfo.value = Resource.Loading
        _appDateSlicerInfo.value = repository.getDateSlicerOptions(context)
    }

    fun getSlicerDataViewMap(id: String
    ) = viewModelScope.launch {
        _slicerDataViewMap.value = Resource.Loading
        _slicerDataViewMap.value = repository.getSlicerDataViewMap(context,id)
    }


    fun getInsightDtlFltrCoums(data: JsonObject
    ) = viewModelScope.launch {
        _appInsightsDtlFltrColumsRes.value = Resource.Loading
        _appInsightsDtlFltrColumsRes.value = repository.getInsightsDtlFltCoums(context,data)
    }


    fun getAppPermCodes(appId: String
    ) = viewModelScope.launch {
        _appPermCodeRes.value = Resource.Loading
        _appPermCodeRes.value = repository.getAppPermCodes(context,appId)
    }

}