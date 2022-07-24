package com.eka.cacapp.ui.farmerConnect

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.FarmerConnectSortAdptr
import com.eka.cacapp.adapter.FcBidListOfOffersAdptr
import com.eka.cacapp.adapter.FcBidsWithStatusAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.farmconnect.FCSortData
import com.eka.cacapp.data.farmconnect.ListOfOfferData
import com.eka.cacapp.data.insight.AdvanceFltrData

import com.eka.cacapp.databinding.BidderHomeFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.FarmerConnectRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.FarmerConnectUtils.*

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject

class BidderHomeFrag : Fragment() , FilterSelectListener {

    private lateinit var mBinding : BidderHomeFragBinding
    private lateinit var mViewModel: FarmerConnectViewModel
    private lateinit var bidListAdapter :FcBidListOfOffersAdptr
    private lateinit var bidListWithStatusAdapter :FcBidsWithStatusAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var fcSortAdapter:  FarmerConnectSortAdptr
    private  var listSorData : ArrayList<FCSortData> = ArrayList()
    private var sortSelectedPos = -1
    private var sortSelectedType = ""



    private var selectedTab = PUBLISED_PRICE_TAB
    var userName = ""
    private lateinit var lastSentReqParams : JsonObject

    //for filter
    private  var hashMapIsRowChecked = HashMap<String,Boolean>()
    private  var hashMapAdvanceFltrData = HashMap<String,AdvanceFltrData>()
    private  var hashMapIsRowValuesChecked = HashMap<String,ArrayList<String>>()
    private lateinit var advaceFltData : AdvanceFltrData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.bidder_home_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()

     //   (activity as DashboardActivity).setTitle("Farmer Connect (BID)")

        mBinding.serviceTtl.setText(getString(R.string.fc_ttl_bid))


        userName = AppPreferences.getKeyValue(Constants.PrefCode.USER_NAME,"").toString()

        clearSort()

        handleBack()

        val factory  = ViewModelFactory(FarmerConnectRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(FarmerConnectViewModel::class.java)

        mViewModel.getFarmConnecGenSettings()


        setTabSelectListener()

        mViewModel.listOfOffersResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    val result = it.value.string()
                    val offerListResponse: ListOfOfferData = Gson().fromJson(
                            result,
                            ListOfOfferData::class.java
                    )
                    addOffersToAdapter(offerListResponse)


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }
            }
        })

        mViewModel.listOfBidsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED) {
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        val result = it.value.string()
                        val offerListResponse: ListOfOfferData = Gson().fromJson(
                                result,
                                ListOfOfferData::class.java
                        )
                        addBidsToAdapter(offerListResponse)


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            }

        })




        mViewModel.farmerConnectGenSettingsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->



            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {


                    ProgressDialogUtil.hideProgressDialog()

                    val result = it.value.string()

                    val resultJson  = JSONObject(result)
                    AppPreferences.saveValue(Constants.PrefCode.FC_BID_QUANTITY_LOCKED,
                        resultJson.optBoolean("bidQuantityLocked").toString())

                    val reqParams = getReqParamJson()

                    mViewModel.getListOfOffers(reqParams)

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })



        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        mBinding.inProgressImg.setOnClickListener {
            highlightSelectedIcon(true,false,false)
            sendListOfBidsReqWithStatus("In-Progress")
        }
        mBinding.inActiveImg.setOnClickListener {
            highlightSelectedIcon(false,true,false)
            sendListOfBidsReqWithStatus("Accepted\\\",\\\"Cancelled")
        }
        mBinding.inCancelImg.setOnClickListener {
            highlightSelectedIcon(false,false,true)
            sendListOfBidsReqWithStatus("Rejected")
        }

        return mBinding.root

    }

    private fun handleBack() {
        mBinding.backImg.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }

    private fun highlightSelectedIcon(
            inProgressIcon: Boolean,inActiveIcon :Boolean,isCancelIcon:Boolean) {
        clearFilter()
        val inProgessImg = if(inProgressIcon) R.drawable.ic_ongoing_bids_active else R.drawable.ic_ongoing_bids_inactive

        val inActiveImg = if(inActiveIcon) R.drawable.ic_selected_bids_active else R.drawable.ic_selected_bids_inactive

        val inCancelImg = if(isCancelIcon) R.drawable.ic_rejected_bids_active else R.drawable.ic_rejected_bids_inactive

        mBinding.inProgressImg.setImageDrawable(ContextCompat.getDrawable(
                requireContext(),inProgessImg
        ))
        mBinding.inActiveImg.setImageDrawable(ContextCompat.getDrawable(
                requireContext(),inActiveImg
        ))
        mBinding.inCancelImg.setImageDrawable(ContextCompat.getDrawable(
                requireContext(),inCancelImg
        ))
    }

    private fun sendListOfBidsReqWithStatus(statusValue:String) {
        mBinding.bidsRecyclerView.adapter = null
        val userName = AppPreferences.getKeyValue(Constants.PrefCode.USER_NAME,"")
        val reqParams= FarmerConnectUtils.getFcReqParamsWithStatus(statusValue,
                userName, FarmerConnectUtils.NIN, 8, 0);

        lastSentReqParams = reqParams

        mViewModel.getListOfBids(reqParams)
    }


    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        item.setCheckable(true)
        when (item.itemId) {


            R.id.navigation_sort -> {
                showSortBottomSheet()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {

                if(!(::advaceFltData.isInitialized)){
                    advaceFltData = AdvanceFltrData(
                            0, 0, "and",
                            "", "", false, ""
                    )
                }



                val filterView = FarmerConnectFilter(requireContext(),
                    android.R.style.Theme_Light_NoTitleBar_Fullscreen,
                this,
                hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData,selectedTab,hashMapAdvanceFltrData)
                filterView.show()

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showSortBottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.qtc_filter_lay, null)
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val listView = view.findViewById<ListView>(R.id.lst_view)
        val radioButton = view.findViewById<RadioButton>(R.id.radio_button)

        radioButton.setOnClickListener {
            if(sortSelectedType.equals("")){
                clearSort()
                bottomSheetDialog.dismiss()
            }else {
                clearSort()
                bottomSheetDialog.dismiss()
                mViewModel.getListOfOffers(getReqParamJson())


            }

        }


        if (sortSelectedPos != -1) {
            radioButton.isChecked = false
        }


        initSortData()

        fcSortAdapter = FarmerConnectSortAdptr(requireActivity(), listSorData,
            bottomSheetDialog,sortSelectedPos,sortSelectedType)
        listView?.adapter = fcSortAdapter
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        fcSortAdapter.onItemClick = {
                selectedItem ->
            radioButton.isChecked = false

            sortSelectedPos = selectedItem.pos
            sortSelectedType = selectedItem.orderType

            val sortOption = JsonObject()
            sortOption.addProperty(selectedItem.item,selectedItem.orderType)
            var mainObj : JsonObject
            if(selectedTab.equals(PUBLISED_PRICE_TAB)){
                mainObj = getReqParamJson()
                mainObj.add("sortBy",sortOption)

                mViewModel.getListOfOffers(mainObj)

            }else {
                mainObj = lastSentReqParams.deepCopy()
                mainObj.add("sortBy",sortOption)

                mViewModel.getListOfBids(mainObj)
            }

        }

    }


    private fun getReqParamJson() : JsonObject{
        val reqParams= getFarmerConnectGenReqParamObj(userName,NIN,8,0)
        return reqParams
    }


    private fun addOffersToAdapter(listOfData : ListOfOfferData){

        try {

            bidListAdapter = FcBidListOfOffersAdptr(listOfData,requireContext())
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.bidsRecyclerView.layoutManager = llm
            mBinding.bidsRecyclerView.adapter = bidListAdapter

            bidListAdapter.onItemClick = { selectedItem ->
                val gson = Gson()
                val json = gson.toJson(selectedItem)
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_OFFER,json)
                val bundle = bundleOf("bdr_pub_prc" to true)
                findNavController().navigate(R.id.action_bidderHomeFrag_to_bidDetailsFrag,bundle)

            }


        }catch (e : Exception){

        }

    }

    private fun addBidsToAdapter(listOfData : ListOfOfferData){

        try {

            bidListWithStatusAdapter = FcBidsWithStatusAdapter(listOfData,requireContext(), FC_MODE_BID)
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.bidsRecyclerView.layoutManager = llm
            mBinding.bidsRecyclerView.adapter = bidListWithStatusAdapter

            bidListWithStatusAdapter.onItemClick = { selectedItem ->

                val gson = Gson()
                val json = gson.toJson(selectedItem)
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_OFFER,json);
                findNavController().navigate(R.id.action_bidderHomeFrag_to_bidDetailsFrag)

            }


        }catch (e : Exception){

        }

    }


    private fun initSortData(){
        listSorData.clear()
        if(selectedTab.equals(PUBLISED_PRICE_TAB)){
            listSorData = getFcSortFields()
        }else {
            listSorData = getFcSortFields()
            listSorData.add( 7,FCSortData("quantity","Quantity",2))
        }
    }



    fun setTabSelectListener(){
        mBinding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if (tab.position == 0) {
                    AppUtil.sendGoogleEvent(requireContext(),
                            "Apps","View Published Price","FarmerConnect")
                    mBinding.bidsRecyclerView.adapter = null
                    selectedTab = PUBLISED_PRICE_TAB
                    mBinding.subHdr.visibility = View.GONE
                    clearSort()
                    clearFilter()
                    val reqParams = getReqParamJson()

                    mViewModel.getListOfOffers(reqParams)
                } else if (tab.position == 1) {
                    AppUtil.sendGoogleEvent(requireContext(),
                            "Apps","Open Bid","FarmerConnect")
                    clearSort()
                    clearFilter()
                    selectedTab = BIDS_TAB
                    mBinding.subHdr.visibility = View.VISIBLE
                    highlightSelectedIcon(true,false,false)
                    sendListOfBidsReqWithStatus("In-Progress")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })


    }

    fun clearSort(){
        sortSelectedPos = -1
        sortSelectedType = ""
        mBinding.bottomNavigationView.menu.getItem(0).isCheckable = false;
        mBinding.bottomNavigationView.menu.getItem(1).isCheckable = false;
    }

    override fun onBasicFilterSelected(value: JsonArray, hashMapIsRowCheck : HashMap<String,Boolean>,
                                       hashMapIsRowValuesCheck : HashMap<String,ArrayList<String>>,
                                               advncFltrData : AdvanceFltrData,
                                       hMapAdvanceFltrData : HashMap<String,AdvanceFltrData>

    ) {


        hashMapIsRowChecked = hashMapIsRowCheck
        hashMapIsRowValuesChecked = hashMapIsRowValuesCheck
        advaceFltData = advncFltrData
        hashMapAdvanceFltrData = hMapAdvanceFltrData

        val filterArr = value
        var mainObj : JsonObject
        if(selectedTab.equals(PUBLISED_PRICE_TAB)){
            mainObj = getReqParamJson()
            for(item in filterArr){
                mainObj.getAsJsonArray("filters").add(item)
            }

            mViewModel.getListOfOffers(mainObj)

        }else {
            mainObj = lastSentReqParams.deepCopy()
            for(item in filterArr){
                mainObj.getAsJsonArray("filters").add(item)
            }

            mViewModel.getListOfBids(mainObj)
        }
    }


    fun clearFilter(){
        mBinding.bottomNavigationView.menu.getItem(1).isCheckable = false;
        advaceFltData = AdvanceFltrData(
                0, 0, "and",
                "", "", false, ""
        )
        hashMapIsRowChecked.clear()
        hashMapIsRowValuesChecked.clear()
        hashMapAdvanceFltrData.clear()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }



}