package com.eka.cacapp.ui.dashboard

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.InsightMainSearchFltrAdapter
import com.eka.cacapp.adapter.InsightsFragAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMeta.AppMetaResponse
import com.eka.cacapp.data.insight.InsightChartSelFltr
import com.eka.cacapp.data.metaMenu.AppMetaMenuData
import com.eka.cacapp.databinding.WorkflowAppDtlFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.InsightRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.insight.InsightChartFragment
import com.eka.cacapp.ui.insight.InsightViewModel
import com.eka.cacapp.ui.insight.LearnMoreScreen
import com.eka.cacapp.ui.insight.WhatIamSeeingScrn
import com.eka.cacapp.utils.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject


class WorkFlowAppDetailFrag : Fragment() {

    private lateinit var mBinding : WorkflowAppDtlFragBinding
    private lateinit var mViewModel: InsightViewModel
    private var insightTitleList : ArrayList<String> = ArrayList()
    private var insightSelFltrList : ArrayList<InsightChartSelFltr> = ArrayList()
    private var slicerIdHashMap : HashMap<Int,String> = HashMap()
    private var hasSlicerHashMap : HashMap<Int,Boolean> = HashMap()
    private lateinit var  adapter : InsightsFragAdapter

    private var fc_userAccess = ""
    private  val BIDER = "bider"
    private  val OFFERER = "offrer"
    private  val BOTH = "both"
    private var appTitle = ""
    private var navigatedViaSearch = true
    private lateinit var favMenuItem : MenuItem

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var updatedFavStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        appTitle = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_NAME, "").toString()


        if(showOverflowMenu(appTitle)){
            inflater.inflate(R.menu.app_dtl_menu, menu);
            favMenuItem= menu.findItem(R.id.menu_item_fav)
            if(appTitle.equals("Options Valuation")
                    ||appTitle.equals("Disease Identification")
            ){
                menu.findItem(R.id.what_i_see).setVisible(false);
            }

        }else {

            inflater.inflate(R.menu.app_dtl_fav_mnu,menu)
            favMenuItem= menu.findItem(R.id.menu_item_fav)
        }


        val entityType = AppPreferences.getKeyValue(Constants.PrefCode.ENTITY_TYPE,"").toString()
        if(entityType.equals("insight",true)
                ||AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_ID,"").toString().equals("Error")){
            favMenuItem.setVisible(false)
        }else {
            val favValue = AppPreferences.getKeyValue(Constants.PrefCode.IS_FAV_APP,"").toString()
            updateFavIcon(favValue)
        }

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allowAllOrientation()
        mBinding = DataBindingUtil.inflate(inflater, R.layout.workflow_app_dtl_frag, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as DashboardActivity).clearSelectedViews()

        val isWorkFlowApp = AppPreferences.getKeyValue(Constants.PrefCode.IS_WF_APP,"N").toString()

        val title = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_NAME, "").toString()

        (activity as DashboardActivity).setTitle(title)


        navigatedViaSearch= AppPreferences.getKeyValue(Constants.PrefCode.NAV_VIA_SEARCH,"N").equals("Y")

        if(isWorkFlowApp.equals("Y",true)){
            (activity as DashboardActivity).showHamburgerMenu()

            val catList= AppPreferences.getKeyValue(Constants.PrefCode.NAV_MENU_LIST,"")

            val navMenuListRes: AppMetaMenuData = Gson().fromJson(
                    catList,
                    AppMetaMenuData::class.java
            )

            val list: ArrayList<String> = ArrayList()
            for (cat in navMenuListRes.navbar[0].apiMenuData[0].menuItems[0].items){
                list.add(cat.text)
            }

            val handlerlist: ArrayList<String> = ArrayList()
            for (cat in navMenuListRes.navbar[0].apiMenuData[0].menuItems[0].items){
                handlerlist.add(cat.handler)
            }



            (activity as DashboardActivity).addItemsToConnectList(handlerlist,list, navMenuListRes.navbar[0].apiMenuData[0].label)

            val res = AppPreferences.getKeyValue(Constants.PrefCode.APP_META_RES,"")

            val appMetaRes: AppMetaResponse = Gson().fromJson(
                    res,
                    AppMetaResponse::class.java
            )


        }else {

            (activity as DashboardActivity).showBackButton()

            (activity as DashboardActivity).handleBackButtonAction()
            (activity as DashboardActivity).clearListView()



        }


            val appId = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_ID,"").toString()

            val factory  = ViewModelFactory(InsightRepository(),requireContext())

            mViewModel = ViewModelProvider(this,factory).get(InsightViewModel::class.java)

            if(appId.equals("22")){
              mViewModel.getAppPermCodes(appId)
            }
            else if (appId.equals("Error")){

            }
            else {
                if(navigatedViaSearch){

                    val json: String = AppPreferences.getKeyValue(Constants.PrefCode.SEARCH_SEL_ITEM, "").toString()
                    val jsonObject = JSONObject(json)
                    (activity as DashboardActivity).setTitle(jsonObject.optString("name"))
                    adapter = InsightsFragAdapter(childFragmentManager)
                    InsightChartFragment().newInstance(jsonObject.toString(),true)?.let { adapter.addFragment(it) }
                    mBinding.viewPager.adapter = adapter
                    mBinding.footerView.visibility = View.GONE

                }else{
                    mViewModel.getAppInsights(appId)
                }

            }


        mViewModel.appPermCodeResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    val result = it.value.string()
                    val resultJson = JSONObject(result)
                    AppPreferences.saveValue(Constants.PrefCode.FC_PERM_CODES,result.toString())
                    val perCodesArr = resultJson.optJSONArray("permCodes")
                    var count = 0
                    for (i in 0 until perCodesArr.length()) {

                        val permCode = perCodesArr[i]
                        if (permCode.equals("STD_APP_BIDS_OFFEROR")) {
                            count++
                            fc_userAccess = OFFERER
                        }
                        if (permCode.equals("STD_APP_BIDDER_BID")) {
                            count++
                            fc_userAccess = BIDER
                        }

                    }
                    if (count >= 2) {
                        fc_userAccess = BOTH
                    }

                    if (fc_userAccess.equals(BIDER)) {
                        mBinding.fcMenuBtn.visibility = View.VISIBLE
                        mBinding.fcMenuBtn.setTextBitmap("Bid", 50f, Color.WHITE)
                        mBinding.fcMenuBtn.scaleType = ImageView.ScaleType.CENTER
                        mBinding.fcMenuBtn.adjustViewBounds = false

                    } else if (fc_userAccess.equals(OFFERER)) {
                        mBinding.fcMenuBtn.visibility = View.VISIBLE
                        mBinding.fcMenuBtn.setTextBitmap("Offer", 40f, Color.WHITE)
                        mBinding.fcMenuBtn.scaleType = ImageView.ScaleType.CENTER
                        mBinding.fcMenuBtn.adjustViewBounds = false
                    } else if (fc_userAccess.equals(BOTH)) {
                        mBinding.fcMenuBtn.visibility = View.VISIBLE
                        mBinding.fcMenuBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(),
                                R.drawable.ic_menu_white));
                    } else {
                        mBinding.fcMenuBtn.visibility = View.GONE
                    }

                    mViewModel.getAppInsights(appId)

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    mViewModel.getAppInsights(appId)
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }
        }

        })





        mViewModel.appInsightResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

            when (it) {
                is Resource.Success -> {

                    ProgressDialogUtil.hideProgressDialog()

                    val result = it.value.string()
                    val jsonArr = JSONArray(result)
                    adapter = InsightsFragAdapter(childFragmentManager)


                    if (jsonArr.length() > 0) {
                        mBinding.viewLine.visibility = View.VISIBLE
                        mBinding.selectInsightImg.visibility = View.VISIBLE
                        mBinding.noInsightTv.visibility = View.GONE

                    }else {
                        mBinding.noInsightTv.visibility = View.VISIBLE
                    }
                    insightSelFltrList.clear()
                    for (i in 0 until jsonArr.length()) {
                        val jsonObject = jsonArr.optJSONObject(i) ?: JSONObject()
                        insightTitleList.add(i, jsonObject.optString("name"))

                        insightSelFltrList.add(InsightChartSelFltr(jsonObject.optString("name"),
                                jsonObject.optString("chartType")))

                        if (i == 0) {
                            mBinding.insightTitle.setText(jsonObject.optString("name"))
                        }
                        val contentObj = jsonObject.optJSONObject("contents") ?: JSONObject()
                        val dataViewArr = contentObj.optJSONArray("dataviews") ?: JSONArray()
                        var hasSlicer = false
                        for (indx in 0 until dataViewArr.length()) {
                            val dvObj = dataViewArr.optJSONObject(indx)
                            val chartType = dvObj.optString("chartType")
                            if (chartType.equals("RadioSlicer", true)
                                    || chartType.equals("ComboSlicer", true)
                                    || chartType.equals("CheckSlicer", true)
                                    || chartType.equals("DateRangeSlicer", true)
                                    || chartType.equals("TagSlicer", true)) {
                                hasSlicer = true
                            }
                        }


                        hasSlicerHashMap.put(i, hasSlicer)
                        slicerIdHashMap.put(i, jsonObject.optInt("_id").toString())

                        if (i == 0 && hasSlicer) {
                            mBinding.slicerImg.visibility = View.VISIBLE
                        }


                        InsightChartFragment().newInstance(jsonObject.toString(),false)?.let { adapter.addFragment(it) }
                    }
                    mBinding.viewPager.adapter = adapter
                    mBinding.dots.attachViewPager(mBinding.viewPager)
                    mBinding.dots.setDotDrawable(R.drawable.onboarding_selected_circle, R.drawable.onboarding_circle)
                    viewPagerChangeListener()


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it) { null }
                }
            }
        }

            })


        mBinding.selectInsightImg.setOnClickListener {
            showInsightSelectFilter()
        }

        mBinding.slicerImg.setOnClickListener {
            AppUtil.sendGoogleEvent(requireContext(),
                    "Insight","Slicer",mBinding.insightTitle.text.toString())
           val pos = mBinding.viewPager.currentItem
            val id = slicerIdHashMap[pos]

            val frag = adapter.getItem(pos) as InsightChartFragment
            frag.onSlicerClicked()

        }

        mBinding.fcMenuBtn.setOnClickListener {
            if(fc_userAccess.equals(BIDER)){
                AppUtil.sendGoogleEvent(requireContext(),"Apps",
                        "View Bids List","FarmerConnect")
                findNavController().navigate(R.id.action_appDetailFrag_to_bidderHomeFrag)
            }else if(fc_userAccess.equals(OFFERER)){
                findNavController().navigate(R.id.action_appDetailFrag_to_offrerHomeFrag)
            }else if(fc_userAccess.equals(BOTH)){
                showFarmerConnSelectMenu()
            }
        }



        favResObserver()
        return mBinding.root


    }


    private fun viewPagerChangeListener() {

        try {

            mBinding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {

                    mBinding.insightTitle.setText(insightTitleList[position])

                    if(hasSlicerHashMap[position]!!){
                        mBinding.slicerImg.visibility = View.VISIBLE
                    }else{
                        mBinding.slicerImg.visibility = View.INVISIBLE
                    }

                }

                override fun onPageScrollStateChanged(state: Int) {}
            })




        } catch (e: Exception) {

        }

    }

    private fun showInsightSelectFilter() {
        lateinit var insightRecyclerView:  RecyclerView

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.select_insight_filter_lay)

        dialog.findViewById<ImageView>(R.id.close_icon).setOnClickListener {
            dialog.dismiss()
        }

        dialog.findViewById<ImageView>(R.id.search_mag_icon).setOnClickListener {
            dialog.findViewById<SearchView>(R.id.insight_search_view).isIconified = false
            dialog.findViewById<SearchView>(R.id.insight_search_view).visibility=
                    View.VISIBLE
        }

        insightRecyclerView = dialog.findViewById(R.id.insight_rv)
        insightRecyclerView.layoutManager = LinearLayoutManager(insightRecyclerView.context)
        insightRecyclerView.setHasFixedSize(true)


        var adapter: InsightMainSearchFltrAdapter = InsightMainSearchFltrAdapter(requireContext(),
                insightSelFltrList)
        insightRecyclerView.adapter = adapter

        dialog.findViewById<SearchView>(R.id.insight_search_view).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }

        })

        adapter.onItemClick = { favItem ->

            for(item in insightTitleList.indices){
                if(insightTitleList[item].equals(favItem.insightName)){
                    mBinding.viewPager.setCurrentItem(item)
                    dialog.dismiss()
                    break
                }
            }


        }


        dialog.show()
    }


    fun ImageView.setTextBitmap(text: String, textSize: Float, textColor: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = textSize
        paint.color = textColor
        paint.textAlign = Paint.Align.LEFT
        val lines = text.split("\n")
        var maxWidth = 0
        for (line in lines) {
            val width = paint.measureText(line).toInt()
            if (width > maxWidth) {
                maxWidth = width
            }
        }
        val height = paint.descent() - paint.ascent()
        val bitmap = Bitmap.createBitmap(maxWidth, height.toInt() * lines.size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        var y = - paint.ascent()
        for (line in lines) {
            canvas.drawText(line, 0f, y, paint)
            y += height
        }
        setImageBitmap(bitmap)
    }

    private fun showFarmerConnSelectMenu() {
        val view: View = layoutInflater.inflate(R.layout.fc_menu_sel_lay, null)
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val bid = view.findViewById<TextView>(R.id.bid)
        val offer = view.findViewById<TextView>(R.id.offer)
        val cancel = view.findViewById<TextView>(R.id.cancel)
        bottomSheetDialog.setContentView(view)

        bid.setOnClickListener {
            bottomSheetDialog.dismiss()
            findNavController().navigate(R.id.action_appDetailFrag_to_bidderHomeFrag)
        }

        offer.setOnClickListener {
            bottomSheetDialog.dismiss()
            findNavController().navigate(R.id.action_appDetailFrag_to_offrerHomeFrag)
        }

        cancel.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()



    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.learn_more){
            AppUtil.sendGoogleEvent(requireContext(),
                    "Apps","Learn More",appTitle)
            val learnMoreScreen = LearnMoreScreen(requireContext(),
                    android.R.style.Theme_Light_NoTitleBar_Fullscreen,
                    appTitle)
            learnMoreScreen.show()
        }else if (item.itemId == R.id.what_i_see){
            AppUtil.sendGoogleEvent(requireContext(),
                    "Apps","What Am I Seeing",appTitle)
            val whatIamSeeingScrn = WhatIamSeeingScrn(requireContext(),
                    android.R.style.Theme_Light_NoTitleBar_Fullscreen,
                    appTitle)
            whatIamSeeingScrn.show()
        }else if (item.itemId == R.id.menu_item_fav){
            onFavClickEvent()
        }
        else if (item.itemId == R.id.dwnld_insight){
            val pos = mBinding.viewPager.currentItem
            val frag = adapter.getItem(pos) as InsightChartFragment
            frag.downloadInsight()
        }
        return false
    }

    private fun showOverflowMenu(ttl : String):Boolean{
        var result : Boolean
        when(ttl){
        "Regulatory and Compliance",
        "Position and Mark to Market",
        "P&L Explained", "Trade Finance", "VaR",
        "Risk and Monitoring", "Purchase Analysis",
        "Procurement Analysis","Inventory Analytics",
        "Plan Performance","Pre-Trade Analysis",
        "Farmer Connect","Disease Prediction","Cash Flow",
        "Yield Forecast","Crop Intelligence",
        "Disease Risk Assessment","Basis Analysis",
        "Freight Exposure","Logistics Operations Analysis",
        "Reconciliation","Credit Risk","Thomson Reuters App",
        "Hyper Local Weather","Power Spread Analysis",
        "Quality Arbitrage Analysis","Vessel Management",
        "Invoice Aging","Plant Outage","Emissions Hedging",
        "Customer Connect","Supply Demand","Price Trend Analysis",
        "Options Valuation","Disease Identification" -> result =true
            else -> result =false
        }
        return result

    }

    private fun updateFavIcon(isFavourite : String){
        favMenuItem.setVisible(true)
        if(isFavourite.equals("true",true)){
            favMenuItem.setIcon(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_star_filled))
        }else{
            favMenuItem.setIcon(ContextCompat.getDrawable(requireContext(),
                R.drawable.ic_star_border))
        }

    }

    private fun favResObserver(){
        mViewModel.toggleFavApiResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()

                    AppPreferences.saveValue(Constants.PrefCode.IS_FAV_APP,updatedFavStatus.toString())
                    updateFavIcon(updatedFavStatus.toString())
                    AppPreferences.saveValue(Constants.PrefCode.FAV_LIST,"")

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })
    }


    private fun onFavClickEvent(){
        var currentFavStatus = false
        val favValue = AppPreferences.getKeyValue(Constants.PrefCode.IS_FAV_APP,"").toString()
        if(favValue.equals("true",true)){
            currentFavStatus = true
        }

        updatedFavStatus = !currentFavStatus

        var appType = AppPreferences.getKeyValue(Constants.PrefCode.FAV_APP_TYPE,"").toString()
        val appId = AppPreferences.getKeyValue(Constants.PrefCode.FAV_APP_ID,"").toString()

        if(appType.equals("Standard Apps")){
            appType = "Std_App"
        }else {
            appType = "Custom_Apps"
        }

        val json = JsonObject()
        json.addProperty("isFavourite",updatedFavStatus)
        mViewModel.toggleFav(json,appId.toString(),appType)
    }


}



