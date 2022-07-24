package com.eka.cacapp.ui.fav

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.FavCategoryAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMainList.CategoryInfoModel
import com.eka.cacapp.data.appMeta.AppMetaResponse
import com.eka.cacapp.data.appSubList.AppSubList
import com.eka.cacapp.databinding.FavCatFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.*

import com.google.gson.Gson


class FavoriteCatFrag : Fragment() {

    private lateinit var mBinding : FavCatFragBinding
    private lateinit var mViewModel: DashboardViewModel
    private var isListClicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allowAllOrientation()

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fav_cat_frag, container, false)
        AppPreferences.saveValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                "")

        (activity as DashboardActivity).setTitle(R.string.fav_title)

        (activity as DashboardActivity).showHamburgerMenu()

        (activity as DashboardActivity).clearListView()

        (activity as DashboardActivity).makeFavTabSelected()

        val factory  = ViewModelFactory(DashboardRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DashboardViewModel::class.java)

        val catList= AppPreferences.getKeyValue(Constants.PrefCode.FAV_LIST,"").toString()
        if(catList.trim().isNotEmpty()){
            addCategoriesToAdapter(catList)
        }else{
            mViewModel.getFavCategory()
        }

        mViewModel.appMetaResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
            when (it) {
                is Resource.Success -> {

                    //   ProgressDialogUtil.hideProgressDialog()


                    val appMetaRes: AppMetaResponse = Gson().fromJson(
                            it.value.toString(),
                            AppMetaResponse::class.java
                    )

                    AppPreferences.saveValue(Constants.PrefCode.APP_META_RES, it.value.toString())

                    mViewModel.getAppMenuMeta(appMetaRes.sys__UUID)

/*
                    val appSubListRes: AppSubList = Gson().fromJson(
                            it.value.toString(),
                            AppSubList::class.java
                    )
*/


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
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

                    AppUtil.printLongLog("App Menu Res",it.value.toString())


/*                    val appMenuMetaRes: AppMetaMenuData = Gson().fromJson(
                            it.value.toString(),
                            AppMetaMenuData::class.java
                    )*/

                    AppPreferences.saveValue(Constants.PrefCode.NAV_MENU_LIST,it.value.toString())

                    if(isListClicked){
                        isListClicked = false
                        AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                        findNavController().navigate(R.id.action_favoriteCatFrag_to_appDetailFrag)
                    }


/*
                    val appSubListRes: AppSubList = Gson().fromJson(
                            it.value.toString(),
                            AppSubList::class.java
                    )
*/


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    //handleApiError(it){null}
                    AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP, "N")
                    findNavController().navigate(R.id.action_favoriteCatFrag_to_appDetailFrag)
                }
            }
        }
        })


        favResObserver()


        return mBinding.root


    }

    /**
     * Function to add categories to recycler view adapter
     * */
    private fun addCategoriesToAdapter(catList :String){

        try {


            if(catList.toString().trim().isNotEmpty()) {

                val categoryResponse: AppSubList = Gson().fromJson(
                        catList,
                        AppSubList::class.java
                )


                val myRecyclerViewAdapter = FavCategoryAdapter(categoryResponse)
                val llm = LinearLayoutManager(requireActivity())
                llm.orientation = LinearLayoutManager.VERTICAL
                mBinding.catRecyclerView.layoutManager = llm
                mBinding.catRecyclerView.adapter = myRecyclerViewAdapter


                myRecyclerViewAdapter.onItemClick = { favItem ->
                    isListClicked = true
                    AppUtil.sendGoogleEvent(requireContext(),"Home","Favourites",favItem.name)

                    AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_ID,favItem._id.toString())
                    AppUtil.saveAppSelectedFavDtl(favItem._id.toString(),favItem.apptype,"true","")
                    if (favItem._id == 39 || favItem.isWorkFlowApp) {
                        AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"Y")
                        AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,favItem.name)
                        mViewModel.getAppMeta(favItem._id.toString())
                    }else if (favItem._id == 20){
                        //Disease Identification
                        AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                        AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,favItem.name)
                        findNavController().navigate(R.id.action_favoriteCatFrag_to_diseaseIdenHome)

                    }
                    else {
                        AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                        AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,favItem.name)
                        AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                        findNavController().navigate(R.id.action_favoriteCatFrag_to_appDetailFrag)
                    }


                }
            }

        }catch (e : Exception){

        }

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    private fun favResObserver(){
        mViewModel.getFavCategoryResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
            if (it is Resource.Loading) {
                this.let { it1 ->
                    activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }}
            when (it) {
                is Resource.Success -> {

                    val favCategoryResponse: CategoryInfoModel = Gson().fromJson(
                        it.value.toString(),
                        CategoryInfoModel::class.java
                    )

                    if (favCategoryResponse.isEmpty()) {
                            ProgressDialogUtil.hideProgressDialog()


                    } else {
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.FAV_LIST, it.value.toString())
                        addCategoriesToAdapter(it.value.toString())

                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }
        }})
            }
    }



