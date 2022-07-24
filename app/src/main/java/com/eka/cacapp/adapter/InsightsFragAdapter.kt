package com.eka.cacapp.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*


class InsightsFragAdapter (supportFragmentManager: FragmentManager?) :
FragmentStatePagerAdapter(
supportFragmentManager!!,
BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    private val mList: MutableList<Fragment> =
            ArrayList()

    override fun getItem(i: Int): Fragment {
        return mList[i]
    }

    override fun getCount(): Int {
        return mList.size
    }

    fun addFragment(fragment: Fragment) {
        mList.add(fragment)
    }




}