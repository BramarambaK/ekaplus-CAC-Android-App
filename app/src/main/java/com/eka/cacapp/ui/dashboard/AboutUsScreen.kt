package com.eka.cacapp.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import com.eka.cacapp.R
import com.eka.cacapp.utils.AppUtil


class AboutUsScreen (mContext: Context, themeId: Int): Dialog(mContext,themeId) {

    init {
        setCancelable(false)
    }

    private lateinit var closeImg : ImageView
    private lateinit var aboutUsTxt : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.about_us_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)
        aboutUsTxt =  findViewById<TextView>(R.id.about_us_txt_vw)

        closeImg.setOnClickListener {
                this.dismiss()
        }

        val version = AppUtil.getVersionNameTxt()
        val text = "<font color=#002D49><big><b>Eka</b></big></font> "+version+"" +
                "<br><br>"+context.getString(R.string.about_us_r1) +
                "<br><br>"+context.getString(R.string.about_usr2) +
                "<br><br>"+context.getString(R.string.about_usr3)
        aboutUsTxt.setText(Html.fromHtml(text))
    }
}