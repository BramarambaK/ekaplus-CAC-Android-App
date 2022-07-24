package com.eka.cacapp.repositories

import android.content.Context
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.utils.Constants

class DomainVerifyRepository () : BaseRepository() {

   suspend fun verifyDomain(context : Context,url: String) = safeApiCall {
       val headers = RequestParams.getDefaultHeaderParams()
       headers[Constants.RequestParamCode.TENANT_DOMAIN] = url
       RestClient.getInstance(context,url)
               .getApiService().getDomainVerifyCall(
               url + "/cac-mobile-app/settings",
               headers
               , "mobile_identity_provider_settings")
   }

}