package com.eka.cacapp.ui.loginWithEka

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.login.LoginWithEkaResModel
import com.eka.cacapp.databinding.MfaOtpVerFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.LoginRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.lang.Exception


class MfaOtpVeriFrag : Fragment() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var mBinding: MfaOtpVerFragBinding
    private var userName = ""
    private var userPass = ""
    private var uniqueToken = ""
    private lateinit var handler: Handler
    private var userOtp = ""

    override fun onCreateView(inflater:
                              LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onActivityCreated(savedInstanceState)

        try {


            mBinding = DataBindingUtil.inflate(inflater, R.layout.mfa_otp_ver_frag, container, false)
            val factory = ViewModelFactory(LoginRepository(), requireContext())

            mViewModel = ViewModelProvider(this, factory).get(LoginViewModel::class.java)

            userName = arguments?.getString("userName", "") ?: ""
            userPass = arguments?.getString("userPass", "") ?: ""
            uniqueToken = arguments?.getString("uniqueToken", "") ?: ""


            mBinding.resendOtp.setOnClickListener {
                val bodyParams = JsonObject()
                bodyParams.addProperty("userName", userName)
                bodyParams.addProperty("uniqueToken", uniqueToken)
                mViewModel.mfaResendOtp(bodyParams)
            }

            mBinding.verifyBtn.setOnClickListener {

                userOtp = mBinding.otpEdttxt.text.toString().trim()

                if (checkInternet()) {
                    if (userOtp.isNotEmpty()) {
                        it.hideKeyboard()
                        if (userOtp.length < 6) {
                            DialogUtil.showErrorDialog(requireActivity(), getString(R.string.pls_entr_six_dgt_otp))
                        } else {
                            mViewModel.mfaOtpVerification(userName, userOtp, uniqueToken)
                        }

                    } else {
                        DialogUtil.showErrorDialog(requireActivity(), getString(R.string.pls_enter_the_otp))
                    }
                }

            }

            mBinding.closeIcon.setOnClickListener {
                findNavController().navigate(R.id.action_mfaOtpVeriFrag_to_loginWithEkaFrag)
            }

            delayResendOtp()

            resendOtpObserver()
            otpVerifyObserver()
        } catch (e: Exception) {

        }

        return mBinding.root
    }

    private fun delayResendOtp() {
        var delayInSec = AppPreferences.getKeyValue(Constants.PrefCode.DISABLE_RESENT_OTP_TIME, "0")
        val delayInMillis = delayInSec!!.toLong() * 1000
        mBinding.resendOtp.setEnabled(false)
        mBinding.resendOtp.setTextColor(ContextCompat.getColor(requireContext(),
                R.color.resned_otp_dsbl_txt_clr))
        handler = Handler()
        handler.postDelayed({
            mBinding.resendOtp.setEnabled(true)
            mBinding.resendOtp.setTextColor(ContextCompat.getColor(requireContext(),
                    R.color.resned_otp_enbl_txt_clr))
        }, delayInMillis)
    }

    private fun otpVerifyObserver() {

        mViewModel.mfaOtpVeriResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {


                    val loginResponse: LoginWithEkaResModel = Gson().fromJson(
                            it.value.toString(),
                            LoginWithEkaResModel::class.java
                    )


                    AppPreferences.saveValue(Constants.PrefCode.USER_ID,
                            loginResponse.userInfo.userId.toString())

                    AppPreferences.saveValue(Constants.PrefCode.FIRST_NAME,
                            loginResponse.userInfo.firstName)

                    AppPreferences.saveValue(Constants.PrefCode.USER_NAME,
                            loginResponse.userInfo.userName)

                    AppPreferences.saveValue(Constants.PrefCode.USER_TOKEN,
                            loginResponse.tokenResponse.auth2AccessToken.access_token)

                    AppPreferences.saveValue(Constants.PrefCode.REFRESH_TOKEN,
                            loginResponse.tokenResponse.auth2AccessToken.refresh_token)

                    getToken()
                    }catch (e : Exception){

                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
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


    private fun resendOtpObserver() {

        mViewModel.mfaResendOtpResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "OTP has been sent successfully.", Toast.LENGTH_SHORT).show()
                    delayResendOtp()
                    ProgressDialogUtil.hideProgressDialog()
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })

    }

    override fun onPause() {
        super.onPause()
        if (::handler.isInitialized) {
            handler.removeCallbacksAndMessages(null);
        }
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
                        // mViewModel.getFavCategory()
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


}