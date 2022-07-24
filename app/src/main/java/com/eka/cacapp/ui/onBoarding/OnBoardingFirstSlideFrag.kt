package com.eka.cacapp.ui.onBoarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.eka.cacapp.R

/**
 *  Fragment for first on boarding screen
 * */

class OnBoardingFirstSlideFrag : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.onboard_first_frag, container, false)
    }
}