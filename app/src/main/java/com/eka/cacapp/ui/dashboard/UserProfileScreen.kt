package com.eka.cacapp.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.eka.cacapp.R
import org.json.JSONObject

class UserProfileScreen (mContext: Context, themeId: Int, profileData : JSONObject): Dialog(mContext,themeId) {

    init {
        setCancelable(false)
    }

    private lateinit var closeImg : ImageView

    private var userProfileData : JSONObject = profileData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.user_prfl_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)


        closeImg.setOnClickListener {
            this.dismiss()
        }

        try {
            findViewById<TextView>(R.id.name_value).setText(userProfileData.optString("firstName")?: "")
            findViewById<TextView>(R.id.email_value).setText(userProfileData.optString("email")?: "")
            findViewById<TextView>(R.id.mob_value).setText(userProfileData.optString("mobile")?: "")
            findViewById<TextView>(R.id.phone_value).setText(userProfileData.optString("phone")?: "")
            findViewById<TextView>(R.id.fax_value).setText(userProfileData.optString("fax")?: "")
            findViewById<TextView>(R.id.website_value).setText(userProfileData.optString("website")?: "")

            findViewById<TextView>(R.id.postl_addrs_value).setText(userProfileData.optString("postalAddress")?: "")

            findViewById<TextView>(R.id.acc_hldr_name_value).setText(userProfileData.optString("accountHolderName")?: "")
            findViewById<TextView>(R.id.iban_value).setText(userProfileData.optString("iban")?: "")
            findViewById<TextView>(R.id.currency_value).setText(userProfileData.optString("currencyName")?: "")
            findViewById<TextView>(R.id.bnk_addrs_value).setText(userProfileData.optString("bankAddress")?: "")






        }catch (e : Exception){

        }


    }
}