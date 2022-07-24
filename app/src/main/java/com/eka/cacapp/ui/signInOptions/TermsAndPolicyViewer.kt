package com.eka.cacapp.ui.signInOptions

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Html
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.eka.cacapp.R
import com.eka.cacapp.utils.ProgressDialogUtil

class TermsAndPolicyViewer (mContext: Context, themeId: Int,urlValue : String,title : String,contentType : String): Dialog(mContext,themeId) {

    init {
        setCancelable(false)
    }

    private lateinit var closeImg : ImageView
    private lateinit var webView: WebView
    private lateinit var title : TextView
    private var cntx = mContext
    private var urlToLoad : String = urlValue
    private var titleValue : String =title
    private var contentTypeValue : String =contentType
    private lateinit var contetnTv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.terms_policy_vw_scrn)
        closeImg =  findViewById<ImageView>(R.id.close_icon)
        webView = findViewById(R.id.web_view)
        title = findViewById(R.id.ttl)
        contetnTv = findViewById(R.id.content_tv)

        title.setText(titleValue)

        closeImg.setOnClickListener {
            ProgressDialogUtil.hideProgressDialog()
            this.dismiss()
        }

        if(contentTypeValue.equals("htmlEditor")||contentTypeValue.equals("")
                ||urlToLoad.equals("")){
            webView.visibility = View.GONE
            contetnTv.visibility = View.VISIBLE
            if(contentTypeValue.equals("") || urlToLoad.equals("")){
                contetnTv.setText("Data not available")
            }else{
                contetnTv.setText(Html.fromHtml(urlToLoad))
            }

        }else{
            webView.visibility = View.VISIBLE
            contetnTv.visibility = View.GONE
            loadUrl(urlToLoad,webView)
        }



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