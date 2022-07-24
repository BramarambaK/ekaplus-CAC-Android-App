package com.eka.cacapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object AppPreferences {
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
       // prefs =
       //         context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

        val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

        prefs = EncryptedSharedPreferences.create(
                context,
                "Eka_pref",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)

    }

    /**
     * function to save value in pref
     * @param key
     * @param value
     */
    fun saveValue(key: String, value: String) {
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * function to get value from pref
     * @param key
     * @param defaultValue
     * @return {value} if stored in pref or retrun the default param value.
     */
    fun getKeyValue(key: String, defaultValue: String): String? {
        return prefs.getString(key, defaultValue)
    }
}