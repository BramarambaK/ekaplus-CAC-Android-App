package com.eka.cacapp.ui.dashboard

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.DashSearchAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMeta.AppMetaResponse
import com.eka.cacapp.data.search.SearchData
import com.eka.cacapp.databinding.SearchLstScrnFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.gson.Gson
import org.json.JSONArray

class SearchFragment : Fragment()  {

    private lateinit var mBinding : SearchLstScrnFragBinding
    private lateinit var mViewModel: DashboardViewModel
    private lateinit var searchListAdapter : DashSearchAdapter
    private var recentVisitedArray = ArrayList<SearchData>()
    private var isListClicked = false

    private lateinit var recentlyVisitedAdapter : DashSearchAdapter
    private var resArray = ArrayList<SearchData>()


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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.search_lst_scrn_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()

        mBinding.searchRecyclerView.isNestedScrollingEnabled = false
        mBinding.recVisRecyclerView.isNestedScrollingEnabled = false

        handleBack()

        val factory  = ViewModelFactory(DashboardRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DashboardViewModel::class.java)

        mBinding.searchView.isIconified = false

        getRecentlyVisited()

        mViewModel.globalSearchResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val dataArr = JSONArray(result)
                        resArray = ArrayList<SearchData>()
                        for(i in 0 until dataArr.length()){
                            val jobj = dataArr[i]
                            val notiListResponse: SearchData = Gson().fromJson(
                                    jobj.toString(),
                                    SearchData::class.java
                            )
                            resArray.add(notiListResponse)
                        }
                        if(resArray.size>0){
                            mBinding.searchRsltTv.visibility = View.VISIBLE
                        }
                        addDataToAdapter()


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }
            }
        })


        mBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String): Boolean {
                if(p0.trim().isNotEmpty()){
                    clearData()
                    AppUtil.sendGoogleEvent(requireContext(),"Home","Search",p0)
                    mViewModel.getGlobalSearch(p0)
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {

                return false
            }
        })



        mViewModel.appMetaResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                if (it is Resource.Loading) {
                    activity?.let { it1 ->
                        ProgressDialogUtil.showProgressDialog(it1)
                    }
                }
                when (it) {
                    is Resource.Success -> {

                        AppPreferences.saveValue(Constants.PrefCode.APP_META_RES, it.value.toString())

                        val appMetaRes: AppMetaResponse = Gson().fromJson(
                                it.value.toString(),
                                AppMetaResponse::class.java
                        )


                        mViewModel.getAppMenuMeta(appMetaRes.sys__UUID)


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
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                if (it is Resource.Loading) {
                    activity?.let { it1 -> //ProgressDialogUtil.showProgressDialog(it1)
                    }
                }
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        AppPreferences.saveValue(Constants.PrefCode.NAV_MENU_LIST, it.value.toString())

                        if (isListClicked) {
                            isListClicked = false
                            AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH, "N")
                            findNavController().navigate(R.id.action_searchFragment_to_appDetailFrag)
                        }

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }
        })



        return mBinding.root

    }

    private fun handleBack() {
        mBinding.close.setOnClickListener {

            it.hideKeyboard()
            requireActivity().onBackPressed()
        }
    }





    private fun addDataToAdapter(){

        try {

            searchListAdapter = DashSearchAdapter(resArray,requireContext())
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.searchRecyclerView.layoutManager = llm
            mBinding.searchRecyclerView.adapter = searchListAdapter

            searchListAdapter.onItemClick = { selectedItem ->

                view?.hideKeyboard()

                storeRecentlyVisited(selectedItem)

                listClickEvent(selectedItem)


            }


        }catch (e : Exception){

        }

    }


    private fun listClickEvent(selectedItem :SearchData){



        if(selectedItem.entityType.equals("insight",true)){
            val gson = Gson()
            val json = gson.toJson(selectedItem)
            AppPreferences.saveValue(Constants.PrefCode.SEARCH_SEL_ITEM,json)
            AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
            AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"Y")
            AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
            AppUtil.saveAppSelectedFavDtl(selectedItem._id?.toString(),selectedItem.apptype?.toString(),"false","insight")
            findNavController().navigate(R.id.action_searchFragment_to_appDetailFrag)
        }else{
            isListClicked = true
            AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_ID,selectedItem._id.toString())
            AppUtil.saveAppSelectedFavDtl(selectedItem._id?.toString(),selectedItem.apptype?.toString(),selectedItem.isFavourite?.toString(),"")
            if( selectedItem._id==39){
                AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"Y")
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                mViewModel.getAppMeta(selectedItem._id.toString())
            }else if (selectedItem._id == 20){
                //Disease Identification
                AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                findNavController().navigate(R.id.action_searchFragment_to_diseaseIdenHome)

            }
            else {
                AppPreferences.saveValue(Constants.PrefCode.IS_WF_APP,"N")
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_NAME,selectedItem.name)
                AppPreferences.saveValue(Constants.PrefCode.NAV_VIA_SEARCH,"N")
                findNavController().navigate(R.id.action_searchFragment_to_appDetailFrag)
            }
        }


    }


    private fun addDataToRecentlyVisited(){

        try {

            recentlyVisitedAdapter = DashSearchAdapter(recentVisitedArray,requireContext())
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.recVisRecyclerView.layoutManager = llm
            mBinding.recVisRecyclerView.adapter = recentlyVisitedAdapter

            recentlyVisitedAdapter.onItemClick = { selectedItem ->

                view?.hideKeyboard()

                listClickEvent(selectedItem)

            }


        }catch (e : Exception){

        }

    }



    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        mBinding.searchView.isIconified = false
    }

    fun clearData() {
        mBinding.searchRsltTv.visibility = View.GONE
        resArray.clear()
        searchListAdapter = DashSearchAdapter(resArray,requireContext())
        val llm = LinearLayoutManager(requireActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        mBinding.searchRecyclerView.layoutManager = llm
        mBinding.searchRecyclerView.adapter = searchListAdapter
        searchListAdapter.notifyDataSetChanged()
    }


    private fun storeRecentlyVisited(searchData: SearchData){
        val gson = Gson()
        val json = gson.toJson(searchData)

        val existingData: String = AppPreferences.getKeyValue(Constants.PrefCode.RECENT_SEARCH_DATA, "").toString()
        if(existingData.trim().isNotEmpty()) {
            val existingArr = JSONArray(existingData)
            if (existingArr.length() < 5) {
                existingArr.put(json)
            }else{
                existingArr.remove(0)
                existingArr.put(json)
            }
            AppPreferences.saveValue(Constants.PrefCode.RECENT_SEARCH_DATA, existingArr.toString())
        }else{
            val jsonArray = JSONArray()
            jsonArray.put(json)
            AppPreferences.saveValue(Constants.PrefCode.RECENT_SEARCH_DATA, jsonArray.toString())
        }


    }

    private fun getRecentlyVisited(){
        val existingData: String = AppPreferences.getKeyValue(Constants.PrefCode.RECENT_SEARCH_DATA, "").toString()

        if(existingData.trim().isNotEmpty()){
            val existingArr = JSONArray(existingData)
            recentVisitedArray = ArrayList<SearchData>()
            for(i in existingArr.length()-1 downTo  0){
                val jobj = existingArr[i]
                val notiListResponse: SearchData = Gson().fromJson(
                        jobj.toString(),
                        SearchData::class.java
                )
                recentVisitedArray.add(notiListResponse)
            }

            if(recentVisitedArray.size>0){
                mBinding.recVstdTv.visibility = View.VISIBLE
            }

            addDataToRecentlyVisited()
        }


    }



}