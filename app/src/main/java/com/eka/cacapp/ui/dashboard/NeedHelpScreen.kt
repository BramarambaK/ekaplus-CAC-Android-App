package com.eka.cacapp.ui.dashboard

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.eka.cacapp.R
import com.eka.cacapp.utils.ProgressDialogUtil


class NeedHelpScreen (mContext: Context, themeId: Int): Dialog(mContext,themeId) {

    init {
        setCancelable(false)
    }

    private lateinit var closeImg : ImageView
    private lateinit var webView: WebView
    private var cntx = mContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.need_help_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)
        webView = findViewById(R.id.web_view)


        closeImg.setOnClickListener {
            ProgressDialogUtil.hideProgressDialog()
            this.dismiss()
        }

        loadUrl("https://eka1.com/service-request/",webView)

    }
    private fun loadUrl(url: String,webView : WebView) {
        val settings: WebSettings = webView.getSettings()
        settings.javaScriptEnabled = true
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY)
        webView.getSettings().setBuiltInZoomControls(true)
        webView.getSettings().setUseWideViewPort(true)
        webView.getSettings().setLoadWithOverviewMode(true)

        ProgressDialogUtil.showProgressDialog(cntx)
        webView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                ProgressDialogUtil.hideProgressDialog()
            }


        })
        webView.loadUrl(url)
    }
}