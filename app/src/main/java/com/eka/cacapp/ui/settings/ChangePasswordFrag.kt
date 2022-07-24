package com.eka.cacapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.R
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.databinding.ChangePassFragBinding

import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.SettingsRepository
import com.eka.cacapp.utils.*
import com.google.gson.JsonObject
import org.json.JSONObject
import java.lang.Exception


class ChangePasswordFrag : Fragment() {

    private lateinit var mBinding: ChangePassFragBinding
    private lateinit var mViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.change_pass_frag, container, false)


        mBinding.nextBtn.setOnClickListener {
            val currPass = mBinding.currPassEdttxt.text.toString().trim()
            if ( currPass.isEmpty() ) {
                Toast.makeText(requireContext(), getString(R.string.pls_entr_curr_pass),
                    Toast.LENGTH_SHORT).show()
            }else{
                it.hideKeyboard()
                val currentPass = mBinding.currPassEdttxt.text.toString().trim()
                val jsonObj = JsonObject()
                jsonObj.addProperty("pwd", currentPass)
                if (currentPass.isNotEmpty()) {
                    mViewModel.validatePassword(jsonObj)
                }
            }



        }




        val factory = ViewModelFactory(SettingsRepository(), requireContext())

        mViewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)


        mBinding.closeIcon.setOnClickListener {
            activity?.onBackPressed()
        }

        mBinding.cancelTv.setOnClickListener {
            activity?.onBackPressed()
        }


        validatePassObserver()

        return mBinding.root
    }



    private fun validatePassObserver() {
        mViewModel.validatePassResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

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
                            mBinding.currPassEdttxt.setText("")
                            Toast.makeText(requireContext(), jsonObj.optString("seccessMessage"),
                                    Toast.LENGTH_SHORT).show()
                        }else {
                            val currPass = mBinding.currPassEdttxt.text.toString().trim()
                            val bundle = bundleOf("currPass" to currPass)
                            findNavController().navigate(R.id.action_changePasswordFrag_to_updateNewPassword,bundle)
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
    }


}