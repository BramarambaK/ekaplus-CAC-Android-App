package com.eka.cacapp.ui


import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import com.eka.cacapp.utils.AppPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import io.intercom.android.sdk.Intercom
import java.util.*


class App : Application() {


    var sDefSystemLanguage: String? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate() {
        super.onCreate()
        AppPreferences.init(this)

        sDefSystemLanguage = Locale.getDefault().language
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Intercom.initialize(this, "android_sdk-d8adc8ce44a0ab2990f280e0b821abe029e142f5", "nz59z1ge")
    }

    fun sendEvent(itemId : String,itemName : String,contentType :String){
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, contentType)
        mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
    }

    fun updateLocale(context: Context, langCode : String) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        context.getApplicationContext().getResources().updateConfiguration(config, null)
    }



}