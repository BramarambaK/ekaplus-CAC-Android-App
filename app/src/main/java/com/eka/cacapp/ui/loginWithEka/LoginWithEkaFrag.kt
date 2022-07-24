package com.eka.cacapp.ui.loginWithEka

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.eka.cacapp.data.appMainList.CategoryInfoModel
import com.eka.cacapp.data.login.LoginWithEkaResModel
import com.eka.cacapp.databinding.SignInEkaFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.repositories.LoginRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.DialogUtil.showErrorDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.intercom.android.sdk.Intercom
import io.intercom.android.sdk.IntercomError
import io.intercom.android.sdk.IntercomStatusCallback
import io.intercom.android.sdk.identity.Registration
import org.json.JSONObject
import java.lang.Exception

/**
 * Login with Eka Fragment
 * */
class LoginWithEkaFrag : Fragment() {

    private lateinit var mViewModel: LoginViewModel
    private lateinit var mBinding: SignInEkaFragBinding
    private  var isMFAEnabled = false
    private  var userName = ""
    private var userPass = ""

    override fun onCreateView(inflater:
                              LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
          super.onActivityCreated(savedInstanceState)

            mBinding = DataBindingUtil.inflate(inflater, R.layout.sign_in_eka_frag, container, false)
            val factory  = ViewModelFactory(LoginRepository(),requireContext())

           mViewModel = ViewModelProvider(this,factory).get(LoginViewModel::class.java)



        isMFAEnabled = AppPreferences.getKeyValue(
                Constants.PrefCode.IS_MFA_ENABLED,"").equals("true",true)


        mBinding.signInBtn.setOnClickListener {

             userName = mBinding.userNameEdttxt.text.toString().trim()
             userPass = mBinding.passwordEdttxt.text.toString().trim()

                if(checkInternet()){
                    if(userName.isNotEmpty() && userPass.isNotEmpty()){
                        it.hideKeyboard()
                        AppUtil.sendGoogleEvent(requireContext(),
                                "Login","Login","General")
                        mViewModel.login(userName, userPass,isMFAEnabled)
                    }else{
                        showErrorDialog(requireActivity(),getString(R.string.pls_enter_the_cred))
                    }
                }

        }


        mBinding.closeIcon.setOnClickListener {
            findNavController().navigate(R.id.action_loginWithEkaFrag_to_singInOptionsFrag)
        }


        mViewModel.loginWithEkaResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {

                        if (isMFAEnabled) {
                            ProgressDialogUtil.hideProgressDialog()
                            val res = it.value.toString()
                            val jsonObj = JSONObject(res)
                            val isMfaEnabled = jsonObj.optBoolean("isMFAEnabled")
                            val uniqueToken = jsonObj.optString("uniqueToken")
                            val mfaAuthMethod = jsonObj.optString("mfaAuthenticationMethod")

                            val bundle = bundleOf("uniqueToken" to uniqueToken,
                                    "userName" to userName,
                                    "userPass" to userPass
                            )

                            findNavController().navigate(R.id.action_loginWithEkaFrag_to_mfaOtpVeriFrag, bundle)

                        } else {
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

                            AppPreferences.saveValue(Constants.PrefCode.USER_TYPE,
                                    loginResponse.userInfo.userType.toString())


                            intercomLogin(userName)

                            getToken()
                        }
                    } catch (e: Exception) {

                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                     // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })

        mViewModel.getFavCategoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> //ProgressDialogUtil.showProgressDialog(it1)
                     }
            }
            when (it) {
                is Resource.Success -> {


                    val favCategoryResponse: CategoryInfoModel = Gson().fromJson(
                            it.value.toString(),
                            CategoryInfoModel::class.java
                    )

                    if(favCategoryResponse.isEmpty()){
                        mViewModel.getCategory()
                    }else{
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.FAV_LIST,it.value.toString())
                        AppPreferences.saveValue(Constants.PrefCode.NAV_TO_FAV,"Y")

                        activity?.let{

                            AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"Y")
                            val intent = Intent (it, DashboardActivity::class.java)
                            it.startActivity(intent)
                            it.finish()

                        }
                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })



        mViewModel.getCategoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> //ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()
                    AppPreferences.saveValue(Constants.PrefCode.CAT_LIST,it.value.toString())
                    AppPreferences.saveValue(Constants.PrefCode.NAV_TO_FAV,"N")

                    activity?.let{
                        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();
                        RestClient.addRefreshToken(baseUrl)
                        AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"Y")
                        val intent = Intent (it, DashboardActivity::class.java)
                        it.startActivity(intent)
                        it.finish()
                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })

        AppPreferences.saveValue(Constants.PrefCode.RECENT_SEARCH_DATA, "")

         return mBinding.root
    }

    private fun intercomLogin(userEmail : String) {
        val registration = Registration.create().withEmail(userEmail)
        Intercom.client().loginIdentifiedUser(
            userRegistration = registration,
            intercomStatusCallback = object : IntercomStatusCallback {
                override fun onSuccess() {
                }

                override fun onFailure(intercomError: IntercomError) {

                }

            }
        )
    }


    /**
     * for creating client image path
     * */
    private fun getClientImagePath (): String{
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"")
        val clientId = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"")
        val imageUrl = baseUrl+"/apps/platform/classic/resources/images/clientLogo/"+clientId+".png"
        return imageUrl
    }

    private fun getToken(){
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

    private fun fcmTokenMapping(token : String){
        AppPreferences.saveValue(Constants.PrefCode.FCM_TOKEN,token)
        val bodyParams = JsonObject()
        bodyParams.addProperty("deviceType","android")
        bodyParams.addProperty("deviceToken",token)
        mViewModel.fcmDeviceMapping(bodyParams)
    }


    private fun checkIfClientImageExists(imageURL: String){

        Glide.with(this)
                .asBitmap()
                .load(imageURL)
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        AppPreferences.saveValue(Constants.PrefCode.BANNER_LOGO,AppUtil.bitmapToString(resource).toString())
                        showBannerToast(resource)
                        //TODO
                      //  mViewModel.getFavCategory()
                        AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"Y")
                        ProgressDialogUtil.hideProgressDialog()

                        val intent = Intent (requireActivity(), DashboardActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finish()

                    }
                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        AppPreferences.saveValue(Constants.PrefCode.BANNER_LOGO,"")
                       // mViewModel.getFavCategory()
                        AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"Y")

                        ProgressDialogUtil.hideProgressDialog()
                        val intent = Intent (requireActivity(), DashboardActivity::class.java)
                        requireActivity().startActivity(intent)
                        requireActivity().finish()
                    }

                })
    }



    fun showBannerToast(imageBitmap : Bitmap){
        ClientBannerToast.infoToast(requireActivity(),"",imageBitmap,Gravity.TOP)
    }





}