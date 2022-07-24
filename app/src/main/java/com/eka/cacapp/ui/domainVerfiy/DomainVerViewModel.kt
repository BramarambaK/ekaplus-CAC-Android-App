package com.eka.cacapp.ui.domainVerfiy

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.eka.cacapp.base.BaseViewModel
import com.eka.cacapp.data.domainverify.DomainVerifyResModel
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DomainVerifyRepository
import kotlinx.coroutines.launch

/**
 * View model for domain verification
 * */
class DomainVerViewModel(private val repository: DomainVerifyRepository,private val context: Context) :
    BaseViewModel(repository,context) {

    private val _domainVerifyRes: MutableLiveData<Resource<DomainVerifyResModel>> =
        MutableLiveData()
    val domainVerifyResponse: LiveData<Resource<DomainVerifyResModel>>
        get() = _domainVerifyRes

    fun domainVerification(url: String) = viewModelScope.launch {
        _domainVerifyRes.value = Resource.Loading
        _domainVerifyRes.value = repository.verifyDomain(context,url)

    }
}