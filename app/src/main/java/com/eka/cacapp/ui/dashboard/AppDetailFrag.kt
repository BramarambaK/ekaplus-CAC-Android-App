package com.eka.cacapp.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.eka.cacapp.R
import com.eka.cacapp.databinding.AppDtlFragBinding
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.allowAllOrientation


class AppDetailFrag : Fragment() {

    private lateinit var mBinding : AppDtlFragBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allowAllOrientation()
        mBinding = DataBindingUtil.inflate(inflater, R.layout.app_dtl_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()

        return mBinding.root

    }
}