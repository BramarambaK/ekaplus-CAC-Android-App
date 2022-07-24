package com.eka.cacapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.DashCategoryAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMainList.CategoryInfoModel
import com.eka.cacapp.data.appMainList.CategoryInfoModelItem
import com.eka.cacapp.databinding.DasboardFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.gson.Gson


class DashboardFrag : Fragment() {

    private lateinit var mBinding : DasboardFragBinding
    private lateinit var mViewModel: DashboardViewModel
    private var currentAppName = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allowAllOrientation()
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dasboard_frag, container, false)

        addCategoriesToAdapter()
        (activity as DashboardActivity).setTitle(R.string.apps)

        (activity as DashboardActivity).clearListView()

        (activity as DashboardActivity).makeAppTabSelected()

        (activity as DashboardActivity).showHamburgerMenu()

        val factory  = ViewModelFactory(DashboardRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DashboardViewModel::class.java)

        mViewModel.getSubCategoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()
                    AppPreferences.saveValue(Constants.PrefCode.SUB_CAT_LIST,it.value.toString())

                    findNavController().navigate(R.id.action_dashboardFrag_to_appSubListFrag)

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                    AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_ID,"Error")
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,currentAppName)
                  //  AppPreferences.saveValue(Constants.PrefCode.IS_FAV_APP,"")
                 //   AppPreferences.saveValue(Constants.PrefCode.ENTITY_TYPE,"insight")
                    findNavController().navigate(R.id.action_dashboardFrag_to_appDetailFrag)

                    // handleError(it) {null}
                  //  handleApiError(it){null}
                }
            }

        })




        return mBinding.root

    }

    /**
     * Function to add categories to recycler view adapter
     * */
    private fun addCategoriesToAdapter(){

        try {

        val catList= AppPreferences.getKeyValue(Constants.PrefCode.CAT_LIST,"")
            if(catList.toString().trim().isNotEmpty()){



        val categoryResponse: CategoryInfoModel = Gson().fromJson(
                catList,
                CategoryInfoModel::class.java
        )

        val list: ArrayList<CategoryInfoModelItem> = ArrayList()

            //Add only those items which has app counts more than zero
        for (cat in categoryResponse){
            if(cat.appsCount>0){
                list.add(cat)
            }
        }



        val myRecyclerViewAdapter = DashCategoryAdapter(list)
        val llm = LinearLayoutManager(requireActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        mBinding.catRecyclerView.layoutManager = llm
        mBinding.catRecyclerView.adapter = myRecyclerViewAdapter

            myRecyclerViewAdapter.onItemClick = { selectedItem ->
                mViewModel.getSubCategories(selectedItem._id.toString())

                currentAppName = selectedItem.name

            }
            }

        }catch (e : Exception){

        }

    }

}