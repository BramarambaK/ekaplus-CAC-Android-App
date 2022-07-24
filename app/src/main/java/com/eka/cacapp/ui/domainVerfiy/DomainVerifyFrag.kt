package com.eka.cacapp.ui.domainVerfiy


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.BuildConfig
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.databinding.DomainVerifyFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.repositories.DomainVerifyRepository
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.DialogUtil.infoPopUp
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.safetynet.SafetyNet
import com.google.android.gms.safetynet.SafetyNetApi
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.security.SecureRandom
import java.util.*

/**
 * Domain verification fragment used for verifying the domain name entered by the user
 *
 **/

class DomainVerifyFrag : Fragment() {

    private lateinit var mBinding: DomainVerifyFragBinding
    private lateinit var mViewModel: DomainVerViewModel
    private lateinit var deviceId : String
    private var urlValue =""

    private val TAG = "EKA"

    private val BUNDLE_RESULT = "result"

    private val mRandom: Random = SecureRandom()

    private var mResult: String? = null

    private val mPendingResult: String? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.domain_verify_frag, container, false)

        setUpDomainEditWatcher()

        mBinding.nextButton.isEnabled = false

        val factory  = ViewModelFactory(DomainVerifyRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DomainVerViewModel::class.java)


         deviceId = AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()

        if(deviceId==""){
            deviceId = UUID.randomUUID().toString()
            AppPreferences.saveValue(Constants.PrefCode.DEVICE_ID,deviceId)
        }



        mBinding.nextButton.setOnClickListener {
            urlValue = mBinding.domainEdtTxt.text.toString().trim().removeSuffix("/")

            if(checkInternet()){
                it.hideKeyboard()
                mViewModel.domainVerification(urlValue)
            }

        }

        mViewModel.domainVerifyResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if(it is Resource.Loading){
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it){
                is  Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()
                    val showEkaLogin = it.value.identifyProviderSetting.show_eka_login


                    AppPreferences.saveValue(Constants.PrefCode.MOBILE_CLIENT_ID,
                    it.value.identifyProviderSetting.mobile_client_id?:"")
                    AppPreferences.saveValue(Constants.PrefCode.IDENTITY_PROVIDER_TYPE,
                        it.value.identifyProviderSetting.identity_provider_type?:"")
                    AppPreferences.saveValue(Constants.PrefCode.ENABLED_SSO_MOBILE,
                        it.value.identifyProviderSetting.enabled_sso_mobile.toString()?:"")
                    AppPreferences.saveValue(Constants.PrefCode.MOBILE_REDIRECT_URI,
                        it.value.identifyProviderSetting.mobile_redirect_uri?:"")
                    AppPreferences.saveValue(Constants.PrefCode.ISSUER,
                        it.value.identifyProviderSetting.issuer?:"")
                    AppPreferences.saveValue(Constants.PrefCode.MOBILE_LOGOUT_REDIRECT_URI,
                        it.value.identifyProviderSetting.mobile_logout_redirect_uri?:"")
                    AppPreferences.saveValue(Constants.PrefCode.TENANT_ID,
                        it.value.identifyProviderSetting.tenant_id?:"")
                    AppPreferences.saveValue(Constants.PrefCode.TENANT_NAME,
                        it.value.identifyProviderSetting.tenant_name?:"")


                    AppPreferences.saveValue(Constants.PrefCode.DISABLE_RESENT_OTP_TIME,
                            it.value.disbale_resend_otp_link_in_seconds.toString())
                    AppPreferences.saveValue(Constants.PrefCode.IS_MFA_ENABLED,
                            it.value.is_mfa_enabled.toString())

                   if(showEkaLogin){
                        AppPreferences.saveValue(Constants.PrefCode.SHOW_EKA_LOGIN,"Y")
                    }else {
                       AppPreferences.saveValue(Constants.PrefCode.SHOW_EKA_LOGIN,"N")
                   }
                    RestClient.updateBaseUrl(urlValue)
                    AppPreferences.saveValue(Constants.PrefCode.BASE_URL,urlValue)
                    findNavController().navigate(R.id.action_domainVerifyFrag_to_singInOptionsFrag)

                }
                is Resource.Failure ->{
                    ProgressDialogUtil.hideProgressDialog()
                    handleApiError(it) {null}
                }
            }

        })


        //show back button only if user clicked on change tenant
        val showBackButton : Boolean = arguments?.getBoolean("showBackButton") ?: false

         if(showBackButton) {
                mBinding.backTv.visibility = View.VISIBLE
                mBinding.domainEdtTxt.setText(AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,""))
         }




        mBinding.backTv.setOnClickListener {
            findNavController().navigate(R.id.action_domainVerifyFrag_to_singInOptionsFrag)
        }


     //   sendSafetyNetRequest()

        return mBinding.root
    }






    /**
     * Edit text watcher for enable/disable next button based on user input
     * */
    private fun setUpDomainEditWatcher() {
        mBinding.domainEdtTxt.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(
                    s: CharSequence,
                    start: Int,
                    before: Int,
                    count: Int
            ) {
            }

            override fun beforeTextChanged(
                    s: CharSequence, start: Int, count: Int,
                    after: Int
            ) {

            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().trim().isNotEmpty() && s.toString().trim().startsWith("http")) {
                    enableButton(mBinding.nextButton)
                } else {
                    disableButton(mBinding.nextButton)
                }

            }
        })
    }

    /**
     * function to make button clickable and set alpha to one
     * */
    private fun enableButton(button: Button) {
        button.isEnabled = true
        button.alpha = 1.0f
    }

    /**
     * function to make button disable and set alpha to 0.3f
     * */
    private fun disableButton(button: Button) {
        button.isEnabled = false
        button.alpha = 0.3f
    }

    private fun sendSafetyNetRequest(){
        val nonceData = "EKA App: " + System.currentTimeMillis()
        val nonce: ByteArray = getRequestNonce(nonceData)!!


        val client = SafetyNet.getClient(requireActivity())
        val task =
            client.attest(nonce, BuildConfig.SFNT_KEY)

        task.addOnSuccessListener(requireActivity(), mSuccessListener)
            .addOnFailureListener(requireActivity(), mFailureListener)
    }


    private fun getRequestNonce(data: String): ByteArray? {
        val byteStream = ByteArrayOutputStream()
        val bytes = ByteArray(24)
        mRandom.nextBytes(bytes)
        try {
            byteStream.write(bytes)
            byteStream.write(data.toByteArray())
        } catch (e: IOException) {
            return null
        }
        return byteStream.toByteArray()
    }

    /**
     * Called after successfully communicating with the SafetyNet API.
     * The #onSuccess callback receives an
     * [com.google.android.gms.safetynet.SafetyNetApi.AttestationResponse] that contains a
     * JwsResult with the attestation result.
     */
    private val mSuccessListener =
        OnSuccessListener<SafetyNetApi.AttestationResponse> { attestationResponse ->
            mResult = attestationResponse.jwsResult

            val offlineVerify = OfflineVerify.process(mResult)
            if(!offlineVerify.hasBasicIntegrity() ||
                    !offlineVerify.isCtsProfileMatch() ||
                    !offlineVerify.hasBasicEvaluationType() ||
                    !offlineVerify.hasHardwareBackedEvaluationType()
                    ){
                infoPopUp(requireContext(),"","This device is not supported by the app.",
                        {closeApp()})
            }else{

            }

        }


    private fun closeApp(){
        requireActivity().finishAffinity()
        requireActivity().finish()
    }

    /**
     * Called when an error occurred when communicating with the SafetyNet API.
     */
    private val mFailureListener =
        OnFailureListener { e ->
            // An error occurred while communicating with the service.
            mResult = null

            if (e is ApiException) {
                // An error with the Google Play Services API contains some additional details.
                val apiException = e


            } else {
                // A different, unknown type of error occurred.

            }
        }


}