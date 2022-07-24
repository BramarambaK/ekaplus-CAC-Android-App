package com.eka.cacapp.ui.settings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.databinding.NewPassFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.SettingsRepository
import com.eka.cacapp.ui.MainActivity
import com.eka.cacapp.utils.*
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject

class UpdateNewPassword : Fragment() {

    private lateinit var mBinding: NewPassFragBinding
    private lateinit var mViewModel: SettingsViewModel
    private var poilcyValidatorMap : HashMap<String, TextView> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.new_pass_frag, container, false)

        val currentPasword : String = arguments?.getString("currPass") ?: ""

        val userName = AppPreferences.getKeyValue(Constants.PrefCode.USER_NAME,"").toString()
        mBinding.newPasswordEdttxt.setOnFocusChangeListener { v, hasFocus ->

            val currentPass = (v as EditText).text.toString().trim()

            if (currentPass.isNotEmpty() && !hasFocus) {
                val jsonObject = JsonObject()
                jsonObject.addProperty("userName",userName)
                jsonObject.addProperty("password",currentPass)
                mViewModel.validatePasswordPolicy(jsonObject)
            }
        }

        mBinding.nextBtn.setOnClickListener {

            val newPass = mBinding.newPasswordEdttxt.text.toString().trim()
            val confNewPass = mBinding.confrmPasswordEdttxt.text.toString().trim()

            if ( newPass.isEmpty() || confNewPass.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.pls_entr_all_flds),
                    Toast.LENGTH_SHORT).show()
            } else {

                if (newPass.equals(confNewPass)) {
                    it.hideKeyboard()
                    val jsonObj = JsonObject()
                    jsonObj.addProperty("oldPassword", currentPasword)
                    jsonObj.addProperty("newPassword", newPass)
                    jsonObj.addProperty("confirmNewPassword", confNewPass)
                    mViewModel.changePassword(jsonObj)
                } else {
                    Toast.makeText(requireContext(), getString(R.string.new_pass_and_conf_same_msg),
                        Toast.LENGTH_SHORT).show()
                }
            }


        }


        val factory = ViewModelFactory(SettingsRepository(), requireContext())

        mViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)

        mViewModel.getPasswordPolicy()


        mBinding.closeIcon.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding.cancelTv.setOnClickListener {
            activity?.onBackPressed()
        }

        mViewModel.logoutResponse.observe(viewLifecycleOwner, Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    //  ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
//                    ProgressDialogUtil.hideProgressDialog()
                    AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN, "N")
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.putExtra("logout", true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })


        validateNewPassObserver()
        changePassObserver()


        mViewModel.getPassPolicyResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonArray = JSONArray(result)

                        if(jsonArray.length()>0){
                            mBinding.passPolLay.visibility= View.VISIBLE
                            updatePolicyValues(jsonArray)
                        }

                    } catch (e: Exception) {

                    }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })


        mViewModel.validatePasswordPolicy.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {
                           ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonObject = JSONObject(result)
                        val passwordValidatorResultList = jsonObject.optJSONArray("passwordValidatorResultList")

                        val isPass = jsonObject.optBoolean("pass")

                        for(i in 0 until passwordValidatorResultList.length()){

                            val jsonObj = passwordValidatorResultList.optJSONObject(i)

                            val msg = jsonObj.optString("message")
                            val mandatory = jsonObj.optBoolean("mandatory")
                            val pass = jsonObj.optBoolean("pass")
                            if(poilcyValidatorMap.containsKey(msg)){

                                if(mandatory && pass){
                                    poilcyValidatorMap.get(msg)!!.leftDrawable(
                                        R.drawable.ic_psw_check_pass,
                                        R.dimen.psw_chck_icon_sz)
                                }else {
                                    poilcyValidatorMap.get(msg)!!.leftDrawable(
                                        R.drawable.ic_psw_error,
                                        R.dimen.psw_chck_icon_sz)
                                }

                            }

                            if(msg.startsWith("Password Strength")){


                                updateSrengthMsg(msg)

                            }




                        }

                        if(isPass){
                            val currentPass = mBinding.currPassEdttxt.text.toString().trim()
                            val jsonOb = JsonObject()
                            jsonOb.addProperty("pwd", currentPass)
                            mViewModel.validateNewPassword(jsonOb)
                        }

                    } catch (e: Exception) {

                    }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }

        })



        return mBinding.root
    }

    private fun updateSrengthMsg(msg : String) {
        mBinding.pswStrengthCheck.setText("Password Strength :")
        val list = msg.split(":")
        var strength = ""
        if(list.size>1){
            strength = list[1]
        }
        val wordTwo: Spannable = SpannableString(strength)
        if(strength.contains("Strong",true)
            ||strength.contains("Good",true)){
            wordTwo.setSpan(
                ForegroundColorSpan(Color.parseColor("#4CAF50")),
                0,
                wordTwo.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )


        }else if(strength.contains("Weak")
        ){
            wordTwo.setSpan(
                ForegroundColorSpan(Color.parseColor("#D0021B")),
                0,
                wordTwo.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        else if(strength.contains("Fair")
        ){
            wordTwo.setSpan(
                ForegroundColorSpan(Color.parseColor("#FF861F")),
                0,
                wordTwo.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }else {
            wordTwo.setSpan(
                ForegroundColorSpan(Color.parseColor("#000000")),
                0,
                wordTwo.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        mBinding.pswStrengthCheck.append(wordTwo)

        mBinding.pswStrengthCheck.visibility = View.VISIBLE
    }

    private fun updatePolicyValues(jsonArray : JSONArray) {
        for(i in 0 until jsonArray.length()){
            when (i) {
                0 -> {
                    mBinding.pswTitleTxt.setText(jsonArray[i].toString())
                }
                1 -> {
                    mBinding.pswAllowedChars.setText(jsonArray[i].toString())
                }
                2 -> {
                    mBinding.pswCheckOne.visibility = View.VISIBLE
                    mBinding.pswCheckOne.setText(jsonArray[i].toString())
                    poilcyValidatorMap.put(jsonArray[i].toString(),mBinding.pswCheckOne)
                }
                3 -> {
                    mBinding.pswCheckTwo.visibility = View.VISIBLE
                    mBinding.pswCheckTwo.setText(jsonArray[i].toString())
                    poilcyValidatorMap.put(jsonArray[i].toString(),mBinding.pswCheckTwo)
                }
                4 -> {
                    mBinding.pswCheckThree.visibility = View.VISIBLE
                    mBinding.pswCheckThree.setText(jsonArray[i].toString())
                    poilcyValidatorMap.put(jsonArray[i].toString(),mBinding.pswCheckThree)
                }
                5 -> {
                    mBinding.pswCheckFour.visibility = View.VISIBLE
                    mBinding.pswCheckFour.setText(jsonArray[i].toString())
                    poilcyValidatorMap.put(jsonArray[i].toString(),mBinding.pswCheckFour)
                }
            }
        }
    }


    private fun validateNewPassObserver() {
        mViewModel.validateNewPassResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        val isSuccess = jsonObj.optBoolean("success")
                        if (!isSuccess) {
                            mBinding.newPasswordEdttxt.setText("")
                            Toast.makeText(requireContext(), jsonObj.optString("seccessMessage"),
                                Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {

                    }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                }
            }

        })
    }

    private fun changePassObserver() {
        mViewModel.changePassResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            }
            when (it) {
                is Resource.Success -> {

                    try {

                        ProgressDialogUtil.hideProgressDialog()
                        val tokn = AppPreferences.getKeyValue(Constants.PrefCode.FCM_TOKEN, "")
                        val bodyParams = JsonObject()
                        bodyParams.addProperty("deviceType", "android")
                        bodyParams.addProperty("deviceToken", tokn)
                        mViewModel.removeFcmMapping(bodyParams)

                        Toast.makeText(requireContext(),getString(R.string.succ_change_pass_msg),
                            Toast.LENGTH_SHORT).show()

                        mViewModel.logout()

                        AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN, "N")
                        val intent = Intent(requireActivity(), MainActivity::class.java)
                        intent.putExtra("logout", true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        requireActivity().startActivity(intent)
                        requireActivity().finish()

                    } catch (e: Exception) {

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



}