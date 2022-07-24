package com.eka.cacapp.network

import android.content.Context
import android.content.Intent
import com.eka.cacapp.base.BaseRepository
import com.eka.cacapp.network.RequestParams.getRefreshTokenHeaderParams
import com.eka.cacapp.ui.MainActivity
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants
import com.eka.cacapp.utils.ProgressDialogUtil
import kotlinx.coroutines.*
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import org.json.JSONObject

class TokenAuthenticator  constructor(
         val context: Context,
         val tokenApi: ApiServices

) : Authenticator , BaseRepository() {


    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            when (val tokenResponse = getUpdatedToken()) {
                is Resource.Success -> {

                    val result = tokenResponse.value.string()

                    val jsonObject = JSONObject(result)
                    AppPreferences.saveValue(Constants.PrefCode.USER_TOKEN,
                            jsonObject.optString("access_token"))
                    AppPreferences.saveValue(Constants.PrefCode.REFRESH_TOKEN,
                            jsonObject.optString("refresh_token"))
                    response.request.newBuilder()
                            .header("Authorization", jsonObject.optString("access_token"))
                            .build()
                }
                else -> {
                    GlobalScope.launch(Dispatchers.Main) {
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"N")
                        val intent = Intent(context.applicationContext,MainActivity::class.java)
                        intent.putExtra("logout",true)
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.applicationContext?.startActivity(intent)
                    }
                    null
                }
            }
        }
    }

     suspend fun getUpdatedToken(): Resource<okhttp3.ResponseBody> {
        val refreshToken =   getRefreshTokenHeaderParams()
        return safeApiCall { tokenApi.refreshAccessToken(refreshToken) }
    }

}