package com.eka.cacapp.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.AppSubListAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMeta.AppMetaResponse
import com.eka.cacapp.data.appSubList.AppSubList
import com.eka.cacapp.data.appSubList.AppSubListItem
import com.eka.cacapp.databinding.AppSubListFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject

class AppSubListFrag : Fragment() {

    private lateinit var mBinding : AppSubListFragBinding
    private lateinit var mViewModel: DashboardViewModel
    private var isListClicked = false
    private lateinit var myRecyclerViewAdapter :AppSubListAdapter
    private lateinit var appSubListRes : AppSubList
    private var favClickedPos = 0
    private var updatedStatus = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allowAllOrientation()
        mBinding = DataBindingUtil.inflate(inflater, R.layout.app_sub_list_frag, container, false)
        AppPreferences.saveValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                "")




        mBinding.root.rootView.isFocusableInTouchMode = true
        mBinding.root.rootView.requestFocus()
        mBinding.root.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        findNavController().navigate(R.id.action_appSubListFrag_to_dashboardFrag)
                        return true
                    }
                }
                return false
            }
        })


        addCategoriesToAdapter()

        (activity as DashboardActivity).clearSelectedViews()

        (activity as DashboardActivity).setTitle(R.string.apps)

        (activity as DashboardActivity).showHamburgerMenu()

        val factory  = ViewModelFactory(DashboardRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DashboardViewModel::class.java)


        mViewModel.appMetaResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
            when (it) {
                is Resource.Success -> {

                    AppPreferences.saveValue(Constants.PrefCode.APP_META_RES,it.value.toString())

                    val appMetaRes: AppMetaResponse = Gson().fromJson(
                            it.value.toString(),
                            AppMetaResponse::class.java
                    )




                        mViewModel.getAppMenuMeta(appMetaRes.sys__UUID)


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }
        }

        })


        mViewModel.appMetaMenuResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> //ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    AppPreferences.saveValue(Constants.PrefCode.NAV_MENU_LIST,it.value.toString())

                    if(isListClicked){
                        isListClicked = false
                        AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                        findNavController().navigate(R.id.action_appSubListFrag_to_appDetailFrag)
                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    // handleApiError(it){null}
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP, "N")
                    findNavController().navigate(R.id.action_appSubListFrag_to_appDetailFrag)
                }
            }
        }

        })



        mViewModel.toggleFavApiResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()
                    appSubListRes.get(favClickedPos).apply {
                        this.isFavourite = updatedStatus.toString()
                    }
                    myRecyclerViewAdapter.notifyDataSetChanged()

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

    /**
     * Function to add categories to recycler view adapter
     * */
    private fun addCategoriesToAdapter(){

        try {

            val catList= AppPreferences.getKeyValue(Constants.PrefCode.SUB_CAT_LIST,"")
            appSubListRes = Gson().fromJson(
                    catList,
                    AppSubList::class.java
            )



            myRecyclerViewAdapter = AppSubListAdapter(requireContext(),appSubListRes)
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.catRecyclerView.layoutManager = llm
            mBinding.catRecyclerView.adapter = myRecyclerViewAdapter

            myRecyclerViewAdapter.onItemClick = { selectedItem ->
                AppUtil.sendGoogleEvent(requireContext(),"Home","App Category",selectedItem.name)
                isListClicked = true
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_ID,selectedItem._id.toString())

                AppUtil.saveAppSelectedFavDtl(selectedItem._id.toString(),selectedItem.apptype,selectedItem.isFavourite,"")
                if(selectedItem.isWorkFlowApp
                        || selectedItem._id==39){
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"Y")
                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                    mViewModel.getAppMeta(selectedItem._id.toString())
                }else if (selectedItem._id == 20){
                    //Disease Identification
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                    findNavController().navigate(R.id.action_appSubListFrag_to_diseaseIdenHome)

                }
                else {
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                    AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                    findNavController().navigate(R.id.action_appSubListFrag_to_appDetailFrag)
                }
            }

            myRecyclerViewAdapter.onFavImgClick = { appSubListItem: AppSubListItem, i: Int ->

                favClickedPos = i
                var currentFavStatus = false
                if(appSubListItem.isFavourite.equals("true",true)){
                    currentFavStatus = true
                }
                 updatedStatus = !currentFavStatus



                var appType = appSubListItem.apptype
                if(appType.equals("Standard Apps")){
                    appType = "Std_App"
                }else {
                    appType = "Custom_Apps"
                }
                var appId = appSubListItem._id
                val json = JsonObject()
                json.addProperty("isFavourite",updatedStatus)
                mViewModel.toggleFav(json,appId.toString(),appType)


            }



        }catch (e : Exception){

        }

    }





}