package com.eka.cacapp.ui.settings


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.databinding.SettingsHomeFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.SettingsRepository
import com.eka.cacapp.ui.MainActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.Constants.PrefCode.FCM_TOKEN
import com.google.gson.JsonObject
import io.intercom.android.sdk.Intercom


class SettingsHomeFrag : Fragment() {

    private lateinit var mBinding: SettingsHomeFragBinding
    private lateinit var mViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.settings_home_frag, container, false)


        if(AppPreferences.getKeyValue(Constants.PrefCode.SHOW_EKA_LOGIN,"N").toString().equals("Y",true)){
            mBinding.changePassTv.visibility = View.VISIBLE
            mBinding.changPassFootr.visibility = View.VISIBLE
        }else{
            mBinding.changePassTv.visibility = View.GONE
            mBinding.changPassFootr.visibility = View.GONE
        }


        val factory  = ViewModelFactory(SettingsRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(SettingsViewModel::class.java)

        mBinding.logoutTv.setOnClickListener {
            logoutConfirmation()
        }

        mBinding.closeIcon.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding.changePassTv.setOnClickListener {
            findNavController().navigate(R.id.action_settingsHomeFrag_to_changePasswordFrag)
        }

        mViewModel.logoutResponse.observe(viewLifecycleOwner, Observer {

            if (it is Resource.Loading) {
                activity?.let { it1 ->
              //      ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
               //     ProgressDialogUtil.hideProgressDialog()
                    AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"N")
                    val intent = Intent(requireActivity(),MainActivity::class.java)
                    intent.putExtra("logout",true)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    requireActivity().startActivity(intent)
                    requireActivity().finish()
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })

        return mBinding.root
    }



    private fun logoutConfirmation(){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder1.setTitle("Confirmation")
        builder1.setMessage(getString(R.string.logout_confirmation))
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { dialog, _ -> dialog.cancel()

            AppUtil.sendGoogleEvent(requireContext(),
                    "Logout","Logout","General")

            val tokn = AppPreferences.getKeyValue(FCM_TOKEN,"")
            val bodyParams = JsonObject()
            bodyParams.addProperty("deviceType","android")
            bodyParams.addProperty("deviceToken",tokn)
            mViewModel.removeFcmMapping(bodyParams)
            Intercom.client().logout()
            mViewModel.logout()

            AppPreferences.saveValue(Constants.PrefCode.IS_LOGGED_IN,"N")
            val intent = Intent(requireActivity(),MainActivity::class.java)
            intent.putExtra("logout",true)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            requireActivity().startActivity(intent)
            requireActivity().finish()
        }

        builder1.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }


}