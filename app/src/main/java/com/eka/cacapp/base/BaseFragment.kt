package com.eka.cacapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

/**
 * BaseFragment
 * */
abstract class BaseFragment<VM : BaseViewModel, R : BaseRepository> : Fragment() {
    protected lateinit var viewModel: VM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanseState: Bundle?
    ): View? {
        val factory = ViewModelFactory(getFragmentRepository(),requireContext())
        viewModel = ViewModelProvider(this,factory).get(getViewModel())
        return onCreateFragView(inflater, container, savedInstanseState)
    }

    abstract fun onCreateFragView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View

    abstract fun getViewModel() : Class<VM>

    abstract fun getFragmentRepository(): R


}