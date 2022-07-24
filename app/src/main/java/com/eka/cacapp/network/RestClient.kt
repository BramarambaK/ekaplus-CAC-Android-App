package com.eka.cacapp.network

import android.content.Context
import android.util.Log
import com.eka.cacapp.BuildConfig
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyManagementException
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit


class RestClient private constructor(url: String,postLogin : Boolean) {

    companion object {

        private lateinit var mApiServices: ApiServices
        private var mInstance: RestClient? = null
        private lateinit var ctx : Context
        private  var postLoginCall = false

        fun getInstance(context: Context,url: String): RestClient {
            ctx = context
            if (mInstance == null) {
                synchronized(this) {

                    mInstance = RestClient(url,false)
                }

            }
            return mInstance!!
        }

        fun updateBaseUrl(url: String) {
            mInstance = RestClient(url,false)
        }
        fun addRefreshToken(url: String) {
            postLoginCall = true
            mInstance = RestClient(url,true)
        }

    }

    init {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val authenticator = TokenAuthenticator( ctx,buildTokenApi(url))

        var okHttpClient : OkHttpClient? = null
        val httpLoggingInterceptor = HttpLoggingInterceptor(PrettyApiLogger())
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        try {
            val tlsSocketFactory = TLSSocketFactory()
            if (tlsSocketFactory.trustManager != null) {
             okHttpClient = OkHttpClient().newBuilder().connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
             .sslSocketFactory(tlsSocketFactory, tlsSocketFactory.trustManager!!)
            .addInterceptor(logging)

           // .addInterceptor(httpLoggingInterceptor)

            .build()
            }
        } catch (e: KeyManagementException) {

        } catch (e: NoSuchAlgorithmException) {

        } catch (e: KeyStoreException) {

        }

        mApiServices = if(postLogin){
            val retrofit = Retrofit.Builder().
            baseUrl(url)
                    .client(getRetrofitClient(authenticator))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            retrofit.create(ApiServices::class.java)
        }else{
            val retrofit = Retrofit.Builder().
            baseUrl(url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            retrofit.create(ApiServices::class.java)
        }

    }

    fun getApiService() = mApiServices

    private fun buildTokenApi(baseUrl : String): ApiServices {
        return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getRetrofitClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiServices::class.java)
    }

    private fun getRetrofitClient(authenticator: Authenticator? = null): OkHttpClient {
        return OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        it.addHeader("Accept", "application/json")
                    }.build())
                }.also { client ->
                    authenticator?.let { client.authenticator(it) }
                    if (BuildConfig.DEBUG) {
                        val logging = HttpLoggingInterceptor()
                        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logging)
                    }
                }.build()
    }

}

class PrettyApiLogger : HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        val logName = "EKA"
        if (message.startsWith("{") || message.startsWith("[")) {
            try {
                val prettyPrintJson = GsonBuilder().setPrettyPrinting()
                        .create().toJson(JsonParser().parse(message))
                Log.d(logName, prettyPrintJson)
            } catch (m: JsonSyntaxException) {
                Log.d(logName, message)
            }
        } else {
            Log.d(logName, message)
            return
        }
    }
}