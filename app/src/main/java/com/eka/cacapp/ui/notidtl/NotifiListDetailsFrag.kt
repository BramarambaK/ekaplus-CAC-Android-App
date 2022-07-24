package com.eka.cacapp.ui.notidtl

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.eka.cacapp.R
import com.eka.cacapp.data.noti.NotiListData
import com.eka.cacapp.databinding.NotiLstDtlScrnFragBinding
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants
import com.google.gson.Gson


class NotifiListDetailsFrag : Fragment()  {

    private lateinit var mBinding : NotiLstDtlScrnFragBinding
    private lateinit var dataItem : NotiListData



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.noti_lst_dtl_scrn_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()


        mBinding.serviceTtl.setText(getString(R.string.noti_details))

        try {

            val gson = Gson()
            val json: String = AppPreferences.getKeyValue(Constants.PrefCode.NOTI_SEL_ITEM, "").toString()
            dataItem = gson.fromJson(json, NotiListData::class.java)

            mBinding.notiTvR1.setLabelAndValue(getString(R.string.policy_name),dataItem.Name?:"")
            mBinding.notiTvR2.setLabelAndValue(getString(R.string.status),dataItem.Status?:"")

            mBinding.notiTvR2.changeValueColor(R.color.red_color)

            mBinding.notiTvR3.setLabelAndValue(getString(R.string.run_date),dataItem.Run_Date?:"")
            mBinding.notiTvR4.setLabelAndValue(getString(R.string.grp_name),dataItem.Group_Name?:"")
            mBinding.notiTvR5.setLabelAndValue(getString(R.string.lmt_type),dataItem.Limit_Type?:"")
            mBinding.notiTvR6.setLabelAndValue(getString(R.string.value_type),dataItem.Value_Type?:"")
            mBinding.notiTvR7.setLabelAndValue(getString(R.string.mea_name),dataItem.Measure_Name?:"")
            mBinding.notiTvR8.setLabelAndValue(getString(R.string.brch_limit),dataItem.Breach_Limit?:"")
            mBinding.notiTvR9.setLabelAndValue(getString(R.string.thrsld_lmt),dataItem.Threshold_Limit?:"")
            mBinding.notiTvR10.setLabelAndValue(getString(R.string.actuals),dataItem.Actuals?:"")

            mBinding.dimensHdrView.setLabelAndValue("Dimensions","Value")


            val dimenValue =  dataItem.Dimensions.substringAfter("(").substringBefore(")")
            mBinding.dimenValView.setLabelAndValue("Name",dimenValue)

            mBinding.dimensHdrView.makeValueBold()
            mBinding.dimensHdrView.makeLabelBold()


        }catch (e : Exception){

        }

        handleBack()

        return mBinding.root

    }

    private fun handleBack() {
        mBinding.backImg.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }






    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }



}