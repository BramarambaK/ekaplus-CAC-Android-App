package com.eka.cacapp.ui.onBoarding

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.eka.cacapp.R
import com.eka.cacapp.databinding.OnboardFourFragBinding
import com.eka.cacapp.ui.MainActivity
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants

/**
 *Fragment used for onboarding screen last page
 */

class OnBoardingFourFrag : Fragment() {
    private lateinit var mBinding: OnboardFourFragBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.onboard_four_frag, container, false)

        mBinding.signInBtn.setOnClickListener {
            // setting on boarding flag to false, to disable showing it from next time
            AppPreferences.saveValue(Constants.PrefCode.SHOW_ON_BOARDING,"N")
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return mBinding.root
    }
}