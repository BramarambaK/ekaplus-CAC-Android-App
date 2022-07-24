package com.eka.cacapp.ui.signInOptions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.databinding.SignInOptFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.LoginRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.loginWithEka.LoginViewModel
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.ProgressDialogUtil.hideProgressDialog
import com.eka.cacapp.utils.ProgressDialogUtil.showProgressDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.IPublicClientApplication.ApplicationCreatedListener
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.okta.oidc.AuthorizationStatus
import com.okta.oidc.OIDCConfig
import com.okta.oidc.Okta.WebAuthBuilder
import com.okta.oidc.ResultCallback
import com.okta.oidc.storage.SharedPreferenceStorage
import com.okta.oidc.util.AuthorizationException
import org.json.JSONObject
import java.util.concurrent.Executors


class SingInOptionsFrag : Fragment()  {

    private lateinit var mBinding: SignInOptFragBinding
    private lateinit var mViewModel: LoginViewModel

    //for azure
    private val SCOPES: Array<String> = arrayOf<String>("Files.Read")

    /* Azure AD v2 Configs */
    private var mSingleAccountApp: IPublicClientApplication? = null

    private var termsCondContentType = ""
    private var termsCondContent = ""

    private var privacyPolContentType = ""
    private var privacyPolContent = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.sign_in_opt_frag, container, false)

        setTermsCondTextWithSpan()

        val factory  = ViewModelFactory(LoginRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(LoginViewModel::class.java)

        getPolicyDetails()

        mBinding.signInEkaBtn.setOnClickListener {
            findNavController().navigate(R.id.action_singInOptionsFrag_to_loginWithEkaFrag)
        }

        mBinding.changeTenantTv.setOnClickListener {
            val bundle = bundleOf("showBackButton" to true)
            findNavController().navigate(R.id.action_singInOptionsFrag_to_domainVerifyFrag,bundle)
        }

        mBinding.ekaFooterTv.text = AppUtil.getAppFooterTxt()
        mBinding.versionTv.text = AppUtil.getVersionNameTxt()


        mBinding.signInOkta.setOnClickListener {
            onOktaSignIn()
        }
        mBinding.signInAzure.setOnClickListener {
            onAzureLogin()
        }
        mBinding.signInAzureLay.setOnClickListener {
            onAzureLogin()
        }

        checkSignInOptionVisiblity()

        signInWithOktaObserver()
        getUserInfoObserver()
        signInWithAzureObserver()
        policyDetailsObserver()

        termsAndCondUiSpan()



        return mBinding.root
    }

    private fun termsAndCondUiSpan() {
        val termContTxt ="Terms & Conditions"
        val prvPolTxt ="Privacy & Policy"
        val cookPlcyTxt ="Cookie Policy"
        val spanText = SpannableStringBuilder()
        spanText.append("By using this application, you're accepting all the ")
        spanText.append(termContTxt)

        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val termsCondAndPolicyViewer = TermsAndPolicyViewer(requireContext(),
                        android.R.style.Theme_Light_NoTitleBar_Fullscreen,termsCondContent,termContTxt,termsCondContentType)
                termsCondAndPolicyViewer.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.parseColor("#3E81CF"))
                ds.setUnderlineText(false)
            }
        },spanText.length - termContTxt.length,spanText.length,0)



        spanText.append(" , ")
        spanText.append(prvPolTxt)

        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val termsCondAndPolicyViewer = TermsAndPolicyViewer(requireContext(),
                        android.R.style.Theme_Light_NoTitleBar_Fullscreen,privacyPolContent,prvPolTxt,privacyPolContentType)
                termsCondAndPolicyViewer.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.parseColor("#3E81CF"))
                ds.setUnderlineText(false)
            }
        },spanText.length - prvPolTxt.length,spanText.length,0)


        spanText.append(" and ")
        spanText.append(cookPlcyTxt)

        spanText.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val termsCondAndPolicyViewer = TermsAndPolicyViewer(requireContext(),
                        android.R.style.Theme_Light_NoTitleBar_Fullscreen,"https://eka1.com/cookie-policy/",cookPlcyTxt,"enterUrl")
                termsCondAndPolicyViewer.show()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.setColor(Color.parseColor("#3E81CF"))
                ds.setUnderlineText(false)
            }
        },spanText.length - cookPlcyTxt.length,spanText.length,0)


        mBinding.termAndCondTv.setMovementMethod(LinkMovementMethod.getInstance());
        mBinding.termAndCondTv.setText(spanText, TextView.BufferType.SPANNABLE);
    }

    private fun getPolicyDetails(){
        mViewModel.getPolicyDetails()
    }

    private fun policyDetailsObserver(){
        mViewModel.policyDetailsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let {
                        it1 ->
                }
            }
            when (it) {
                is Resource.Success -> {

                    try {


                    val result = it.value.toString()
                    val jsonObj = JSONObject(result)
                    val termCondObj = jsonObj.optJSONObject("termsCondition")
                    termsCondContentType = termCondObj.optString("type")
                    termsCondContent = termCondObj.optString("content")

                    val privPolObj = jsonObj.optJSONObject("privacyPolicy")
                    privacyPolContentType = privPolObj.optString("type")
                    privacyPolContent = privPolObj.optString("content")
                    }catch (e :Exception){

                    }



                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    handleApiError(it) { null }
                }
            }

        })
    }

    private fun checkSignInOptionVisiblity(){
        if(AppPreferences.getKeyValue(Constants.PrefCode.SHOW_EKA_LOGIN,"Y").
            toString().equals("Y",true)){
            mBinding.signInEkaBtn.visibility = View.VISIBLE
        }else{
            mBinding.signInEkaBtn.visibility = View.GONE
        }

        if(AppPreferences.getKeyValue(Constants.PrefCode.IDENTITY_PROVIDER_TYPE,"").
            toString().equals("okta",true)){
            mBinding.signInOkta.visibility = View.VISIBLE
        }

        if(AppPreferences.getKeyValue(Constants.PrefCode.IDENTITY_PROVIDER_TYPE,"").
        toString().equals("azure",true)) {
            mBinding.signInAzureLay.visibility = View.VISIBLE
        }


    }

    private fun getUserInfoObserver(){
        mViewModel.userInfoResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let {
                        it1 ->
                }
            }
            when (it) {
                is Resource.Success -> {

                    val result = it.value.toString()
                    val jsonObj = JSONObject(result)


                    AppPreferences.saveValue(Constants.PrefCode.USER_ID,
                        jsonObj.optString("id"))

                    AppPreferences.saveValue(Constants.PrefCode.FIRST_NAME,
                        jsonObj.optString("firstName"))

                    AppPreferences.saveValue(Constants.PrefCode.USER_NAME,
                        jsonObj.optString("userName"))

                    getToken()

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    handleApiError(it) { null }
                }
            }

        })
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                val imageUrl = getClientImagePath()
                checkIfClientImageExists(imageUrl)
                return@OnCompleteListener
            }
            val token = task.result

            fcmTokenMapping(token.toString())

            val imageUrl = getClientImagePath()
            checkIfClientImageExists(imageUrl)

        })
    }

    private fun fcmTokenMapping(token: String) {
        AppPreferences.saveValue(Constants.PrefCode.FCM_TOKEN, token)
        val bodyParams = JsonObject()
        bodyParams.addProperty("deviceType", "android")
        bodyParams.addProperty("deviceToken", token)
        mViewModel.fcmDeviceMapping(bodyParams)
    }

    private fun getClientImagePath(): String {
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "")
        val clientId = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID, "")
        val imageUrl = baseUrl + "/apps/platform/classic/resources/images/clientLogo/" + clientId + ".png"
        return imageUrl
    }


    private fun signInWithOktaObserver(){
        mViewModel.oktaLoginResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    val result = it.value.toString()
                    val jsonObj = JSONObject(result)
                    val auth2AccessToken = jsonObj.optJSONObject("auth2AccessToken")

                    AppPreferences.saveValue(Constants.PrefCode.USER_TOKEN,
                        auth2AccessToken.optString("access_token"))

                    AppPreferences.saveValue(Constants.PrefCode.REFRESH_TOKEN,
                        auth2AccessToken.optString("refresh_token"))


                    mViewModel.getUserInfo()

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })
    }

    private fun signInWithAzureObserver(){
        mViewModel.azureLoginResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    val result = it.value.toString()
                    val jsonObj = JSONObject(result)
                    val auth2AccessToken = jsonObj.optJSONObject("auth2AccessToken")

                    AppPreferences.saveValue(Constants.PrefCode.USER_TOKEN,
                        auth2AccessToken.optString("access_token"))

                    AppPreferences.saveValue(Constants.PrefCode.REFRESH_TOKEN,
                        auth2AccessToken.optString("refresh_token"))


                    mViewModel.getUserInfo()
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })
    }

    private fun checkIfClientImageExists(imageURL: String) {

        Glide.with(this)
            .asBitmap()
            .load(imageURL)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    AppPreferences.saveValue(Constants.PrefCode.BANNER_LOGO, AppUtil.bitmapToString(resource).toString())
                    showBannerToast(resource)
                    AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN, "Y")
                    ProgressDialogUtil.hideProgressDialog()

                    val intent = Intent(requireActivity(), DashboardActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()

                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    AppPreferences.saveValue(Constants.PrefCode.BANNER_LOGO, "")
                    AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN, "Y")

                    ProgressDialogUtil.hideProgressDialog()
                    val intent = Intent(requireActivity(), DashboardActivity::class.java)
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }

            })
    }


    fun showBannerToast(imageBitmap: Bitmap) {
        ClientBannerToast.infoToast(requireActivity(), "", imageBitmap, Gravity.TOP)
    }



    /**
     * function to set terms and condition text view with different colors
     * **/
    private fun setTermsCondTextWithSpan() {
        val wordOne: Spannable =
                SpannableString(getString(R.string.by_using_terms_cond_prefix))

        val wordTwo: Spannable = SpannableString(getString(R.string.terms_cond_part2))
        val wordThree: Spannable = SpannableString(getString(R.string.terms_cond_part3))
        val wordFour: Spannable = SpannableString(getString(R.string.term_cond_part4))

        setWordSpan(wordOne, R.color.terms_cond_clr_1)
        setWordSpan(wordTwo, R.color.terms_cond_clr_1)
        setWordSpan(wordThree, R.color.terms_cond_clr_1)
        setWordSpan(wordFour, R.color.terms_cond_clr_1)

        mBinding.termAndCondTv.text = wordOne
        mBinding.termAndCondTv.append(wordTwo)
        mBinding.termAndCondTv.append(wordThree)
        mBinding.termAndCondTv.append(wordFour)

    }


    private fun setWordSpan(word: Spannable, color: Int) {
        word.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireActivity(), color)),
            0,
            word.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

    }



    private fun onAzureLogin(){

        val clientId = AppPreferences.getKeyValue(Constants.PrefCode.MOBILE_CLIENT_ID,"").toString()
        val tenantId = AppPreferences.getKeyValue(Constants.PrefCode.TENANT_ID,"").toString()

/*        PublicClientApplication.create(requireContext(),
            clientId,
            "https://login.microsoftonline.com/"+tenantId,
            "msauth://com.eka.cac/1yTYoRKhX9UOOpDjyHHfhnddqhU%3D"
            , object : ApplicationCreatedListener {

                override fun onCreated(application: IPublicClientApplication) {
                    mSingleAccountApp = application
                }

                override fun onError(exception: MsalException) {
                }
            })*/

        //Note Signature hash is of jks key so it will only work on release mode
        PublicClientApplication.create(requireContext(),
            clientId,
            "https://login.microsoftonline.com/"+tenantId,
            "msauth://com.eka.CACApp/QZLcTJjkENile5Jc1v6d2dWqOmw%3D"
            , object : ApplicationCreatedListener {

                override fun onCreated(application: IPublicClientApplication) {
                    mSingleAccountApp = application
                }

                override fun onError(exception: MsalException) {
                }
            })


        loadLoginScreen()
    }


    private fun onOktaSignIn(){

        val clientId = AppPreferences.getKeyValue(Constants.PrefCode.MOBILE_CLIENT_ID,"").toString()
        val redirectUri = AppPreferences.getKeyValue(Constants.PrefCode.MOBILE_REDIRECT_URI,"").toString()
        val logoutUri = AppPreferences.getKeyValue(Constants.PrefCode.MOBILE_LOGOUT_REDIRECT_URI,"").toString()
        val issuer = AppPreferences.getKeyValue(Constants.PrefCode.ISSUER,"").toString()


        val config = OIDCConfig.Builder()
            .clientId(clientId)
            .redirectUri(redirectUri)
            .endSessionRedirectUri(logoutUri)
            .scopes("openid", "profile", "offline_access")
            .discoveryUri(issuer)
            .create()


        val client = WebAuthBuilder()
                .withConfig(config)
                .withContext(requireContext())
                .withStorage(SharedPreferenceStorage(requireContext()))
                .withCallbackExecutor(Executors.newSingleThreadExecutor())
                .withTabColor(Color.BLUE)
                .supportedBrowsers("com.android.chrome", "org.mozilla.firefox")
                .create()

        val sessionClient = client.sessionClient

        client.registerCallback(object : ResultCallback<AuthorizationStatus, AuthorizationException?> {
            override fun onSuccess(status: AuthorizationStatus) {
                if (status == AuthorizationStatus.AUTHORIZED) {
                    //client is authorized.
                    try {
                        val tokens = sessionClient.tokens
                        mViewModel.oktaLoginWithToken(tokens.idToken.toString())

                    } catch (e: AuthorizationException) {

                    }
                } else if (status == AuthorizationStatus.SIGNED_OUT) {

                }
            }

            override fun onCancel() {
                //authorization canceled
            }

            override fun onError(msg: String?, exception: AuthorizationException?) {


            }
        }, requireActivity())

        if (sessionClient.isAuthenticated) {
            //user already logged in. Skip login screen.
            try {
                mViewModel.oktaLoginWithToken(sessionClient.tokens.idToken.toString())

            } catch (e: AuthorizationException) {
                //handle error
            }
        } else {

            client.signIn(requireActivity(), null)
        }

    }


    private fun getAuthInteractiveCallback(): AuthenticationCallback? {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {

              mViewModel.azureLoginWithToken(authenticationResult.account.idToken.toString())
            }

            override fun onError(exception: MsalException) {
                /* Failed to acquireToken */


            }

            override fun onCancel() {
                /* User canceled the authentication */

            }
        }
    }

    private fun loadLoginScreen() {
        showProgressDialog(requireContext())
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            hideProgressDialog()
           if (mSingleAccountApp == null) {
            return@Runnable
        }
            mSingleAccountApp!!.acquireToken(
                requireActivity(),
                SCOPES,
                getAuthInteractiveCallback()!!
            )
        }, 2000)
    }



}