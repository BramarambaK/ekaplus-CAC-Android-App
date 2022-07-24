package com.eka.cacapp.ui.insight

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.adapter.ColorPickerAdapter
import com.eka.cacapp.adapter.InsightBasicFltrAdapter
import com.eka.cacapp.adapter.InsightDtlSortAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.insight.*
import com.eka.cacapp.databinding.InsightDtlFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.InsightRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.highsoft.highcharts.common.hichartsclasses.HILegend
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.common.hichartsclasses.HITitle
import com.highsoft.highcharts.core.HIChartView
import ja.burhanrashid52.photoeditor.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class InsightDetailFrag : Fragment()  {
    private lateinit var mBinding: InsightDtlFragBinding
    private var legendStatus = true
    private var overFlowPopup : PopupMenu? = null
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var insightDtlSortAdapter:  InsightDtlSortAdapter
    private var sortFilterList : ArrayList<InsightSortFilterData>  = ArrayList()
    private var sortList : ArrayList<InsightSortFilterData>  = ArrayList()
    private var drillDownList : ArrayList<InsightSortFilterData>  = ArrayList()
    private var sortSelectedPos = -1
    private var sortSelectedType = ""
    private lateinit var chartParams: ChartParams
    private lateinit var actualChartParams: ChartParams
    private lateinit var mViewModel: InsightViewModel

    private var currentDrillDownIndex = 0


    //for filter
    private  var hashMapIsRowChecked = HashMap<String,Boolean>()
    private  var hashMapAdvanceFltrData = HashMap<String,AdvanceFltrData>()
    private  var hashMapIsRowValuesChecked = HashMap<String,ArrayList<String>>()
    private  var selectedBasicFlterValues = ArrayList<String>()
    private var dataViewJson = JSONObject()
    private lateinit var advaceFltData : AdvanceFltrData
    private  var isBasicFltrAvialble = true

    lateinit var filterListView: ListView
    var currentView = "Basic"
    var mainOperVal ="and"
    lateinit var selectedInshtFltrData : InsightSortFilterData
    var seletedPos = -1
    private lateinit var tabLayout : TabLayout
    private lateinit var basicLay : LinearLayout
    private lateinit var basicFltView :ScrollView
    private lateinit var advanceView :LinearLayout

    private lateinit var andOperView :TextView
    private lateinit var OrOperView :TextView

    private lateinit var valueOneEditText :EditText
    private lateinit var valueTwoEditText :EditText
    private lateinit var spinnerOperOne :Spinner
    private lateinit var spinnerOperTwo :Spinner

    private lateinit var title : TextView
    private lateinit var noBasicFltrTv : TextView
    private lateinit var backImg : ImageView
    private lateinit var subTtile : TextView
    private lateinit var spinnerOneLock : ImageView
    private lateinit var spinnerTwoLock : ImageView
    private lateinit var filterTwoView : LinearLayout
    private lateinit var filterOperView : LinearLayout


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        allowAllOrientation()
        mBinding = DataBindingUtil.inflate(
                inflater, R.layout.insight_dtl_frag, container, false)


        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()


        mBinding.root.rootView.isFocusableInTouchMode = true
        mBinding.root.rootView.requestFocus()

        mBinding.root.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action === KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        mBinding.header.visibility = View.GONE
                        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
                        handleBackPressed()
                        return true
                    }
                }
                return false
            }
        })




        val factory  = ViewModelFactory(InsightRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(InsightViewModel::class.java)


        addOverFlowMenu()
        val chartView = mBinding.hightChartV
        val gson = Gson()
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_DTL_DATA,"").toString()
        chartParams = gson.fromJson(json, ChartParams::class.java)

        chartParams.apply {
            this?.dataJson = JSONObject(this?.dataJsonStr)
            this?.columnMapDataJson = JSONObject(this?.columnMapDataJsonStr)
            this?.dataViewJson = JSONObject(this?.dataViewJsonStr)
        }

        val dataViewJs = chartParams.dataViewJson
        dataViewJson = dataViewJs


        actualChartParams = chartParams


        chartParams?.let { addValuesToFilterData(it) }
        AppUtil.printLongLog("CharData",chartParams.toString())
        val currentChartType = chartParams?.chartType

        if (currentChartType.equals("Pie",true)
            || currentChartType.equals("Pie3D",true)
            ||currentChartType.equals("Donut",true)
            ||currentChartType.equals("Donut3D",true)

        ){
            chartParams.valueIdList.clear()
            val columnsArr = chartParams.dataViewJson.optJSONObject("visualizations")
                .optJSONArray("filters")
            for(i in 0 until columnsArr.length()){
                val fltrObj = columnsArr.optJSONObject(i)
                if(fltrObj.optString("configZone").equals("values")){
                    chartParams.valueIdList.add(fltrObj.optString("columnId"))
                }
            }
        }


        try {

            mBinding.backImgWv.setOnClickListener {

                mBinding.header.visibility = View.GONE
                (activity as AppCompatActivity?)!!.supportActionBar!!.show()

                handleBackPressed()
            }

            mBinding.sharImg.setOnClickListener {
                val bitmap=   screenShot(mBinding.hightChartV)
                bitmap?.let { showShareView(it) }
            }

        if(currentChartType.equals("Card",true)){

            val manager = InsightsViewsManager()
            manager.setCardTypeView(requireContext(),chartView, chartParams,true,false)
            disableBottomNavItem(intArrayOf(0,1),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE

        }else if (currentChartType.equals("Table",true)){
            val manager = InsightsViewsManager()
            manager.setTableTypeView(requireContext(),chartView, chartParams,false)
            disableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE
        }
        else if (currentChartType.equals("Pivot",true)
            ||currentChartType.equals("PointTime",true)
            ||currentChartType.equals("SplineTime",true)
            ||currentChartType.equals("AreaTime",true)
            ||currentChartType.equals("AreaSplineTime",true)
            ||currentChartType.equals("LineTime",true)
            ||currentChartType.equals("ColumnTime",true)
            ||currentChartType.equals("DotMap",true)){
            val manager = InsightsViewsManager()
            val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "")
            val deciveId= AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
            val token= AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
            val appliedFltr= AppPreferences.getKeyValue(Constants.PrefCode.WBVIEW_APPLIED_FLTR,"").toString()
            manager.setChartWebView(requireContext(),chartView,
                chartParams,baseUrl,token,currentChartType,
                deciveId, appliedFltr,false)

            disableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE

            mBinding.wbViewHdr.visibility = View.VISIBLE
            mBinding.header.visibility = View.GONE
            mBinding.bottomNavigationView.visibility = View.GONE
            mBinding.serviceTtlWv.setText(chartParams?.chartTitle)
        }

        else {
            enableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            val hiOptions = OptionsProvider.provideOptionsForChartType(chartParams)
            val hiTitle = HITitle();
            hiTitle.text =""
            hiOptions.title = hiTitle

            chartView.options = hiOptions
            chartView.invalidate()

            val legend = HILegend()
            legend.enabled = legendStatus
            mBinding.hightChartV.options.legend = legend


        }
        }catch (e : Exception){

        }


        mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        mBinding.backImg.setOnClickListener {

            mBinding.header.visibility = View.GONE
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()

            handleBackPressed()
        }


        mBinding.serviceTtl.setText(chartParams?.chartTitle)
        AppUtil.sendGoogleEvent(requireContext(),
                "Insight","View",chartParams?.chartTitle)

        mViewModel.appInsightDataVisulizeResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    try {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonObj = JSONObject(result)

                        AppUtil.printLongLog("BDat",chartParams.dataJson.toString())
                        chartParams.apply {
                            this.dataJson = jsonObj
                        }

                        AppUtil.printLongLog("ADat",chartParams.dataJson.toString())

                        try {

                            if(mBinding.chartErroV.visibility==View.VISIBLE){
                                mBinding.chartErroV.visibility = View.GONE
                            }
                            if(mBinding.hightChartV.visibility==View.GONE){
                                mBinding.hightChartV.visibility = View.VISIBLE
                            }

                            if(currentChartType.equals("Card",true)){
                                chartView.removeAllViews()
                                val manager = InsightsViewsManager()
                                manager.setCardTypeView(requireContext(),chartView, chartParams,true,false)
                            }else if (currentChartType.equals("Table",true)){
                                chartView.removeAllViews()
                                val manager = InsightsViewsManager()
                                manager.setTableTypeView(requireContext(),chartView, chartParams,false)
                            }
                            else {
                                updateChart(chartView)
                            }
                        }catch (e : Exception){

                        }




                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {

                    mBinding.chartErroV.visibility = View.VISIBLE
                    mBinding.hightChartV.visibility = View.GONE


                        ProgressDialogUtil.hideProgressDialog()


                }
            }

        })

        setupDrilldown()

        return mBinding.root
    }

    private fun setupDrilldown() {

        for(i in sortFilterList.indices){
            if(sortFilterList[i].configZone.equals("axis")){
                drillDownList.add(sortFilterList[i])
            }
        }


        currentDrillDownIndex =0
        if(drillDownList.size>1){
            mBinding.drillDown.visibility = View.VISIBLE
        }else{
            mBinding.drillDown.visibility = View.INVISIBLE
        }

        if(drillDownList.size>0){
            mBinding.drillHeaderTtl.setText(drillDownList[0].columnName)
            checkDrillDownUpVisiblity(currentDrillDownIndex,drillDownList.size)
        }else{
            mBinding.drillHeader.visibility = View.GONE
            mBinding.moreImg.visibility = View.GONE
        }

        mBinding.drillDown.setOnClickListener {
            if(currentDrillDownIndex+1<drillDownList.size){
                currentDrillDownIndex++;
                mBinding.drillHeaderTtl.setText(drillDownList[currentDrillDownIndex].columnName)
                checkDrillDownUpVisiblity(currentDrillDownIndex,drillDownList.size)
                drillDownRequest(drillDownList[currentDrillDownIndex].columnId)
            }


        }

        mBinding.drillUp.setOnClickListener {
            if(currentDrillDownIndex-1>=0){
                currentDrillDownIndex--;
                mBinding.drillHeaderTtl.setText(drillDownList[currentDrillDownIndex].columnName)
                checkDrillDownUpVisiblity(currentDrillDownIndex,drillDownList.size)
                drillDownRequest(drillDownList[currentDrillDownIndex].columnId)
            }
        }

    }

    private fun checkDrillDownUpVisiblity(currentDrillDownIndex: Int, size: Int) {
          if(size<=1){
              mBinding.drillUp.visibility = View.INVISIBLE
              mBinding.drillDown.visibility = View.INVISIBLE
          }
          else if(currentDrillDownIndex==0){
               mBinding.drillUp.visibility = View.INVISIBLE
               mBinding.drillDown.visibility = View.VISIBLE
          }else if(currentDrillDownIndex+1<size){
              mBinding.drillUp.visibility = View.VISIBLE
              mBinding.drillDown.visibility = View.VISIBLE
          }else if (currentDrillDownIndex+1==size){
              mBinding.drillUp.visibility = View.VISIBLE
              mBinding.drillDown.visibility = View.INVISIBLE
          }
    }

    fun drillDownRequest( columnId :String){
        val drillDownObj = JSONObject();
        val filtersArr = JSONArray()
        drillDownObj.put("filters",filtersArr)
        drillDownObj.put("level",columnId)
        drillDownObj.put("configZone","axis")
        drillDownObj.put("drillDownAll",true)


        val jsonObj =chartParams.dataViewJson

        val dataViewJson  = JSONObject()


        jsonObj.optJSONObject("visualizations").optJSONObject("configuration").
        put("drillDownInfo",drillDownObj)

        dataViewJson.put("dataViewJson",jsonObj)
        if(jsonObj.has("calculatedMeasures")){
            dataViewJson.put("calculatedMeasures",jsonObj.optJSONArray("calculatedMeasures"))
        }

        if(chartParams.chartType.equals("Bubble",true)||
                chartParams.chartType.equals("Heatmap",true)){
            dataViewJson.put("parseData",true)
        }

        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)

        mViewModel.getInsightDataVisulize(payLoadObj)
    }

    fun updateChart(chartView : HIChartView){
        try {
            val hiTitle = HITitle();
            hiTitle.text =""

            val hiOptions= OptionsProvider.provideOptionsForChartType(chartParams)
            hiOptions.title = hiTitle
            chartView.options = hiOptions
            chartView.reload()

            val legend = HILegend()
            legend.enabled = legendStatus
            mBinding.hightChartV.options.legend = legend
        }catch (e:Exception){

        }

    }

    fun addValuesToFilterData(chartParams:ChartParams){
    try {
        val dataViewJson = chartParams.dataViewJson
        val columnMapDataJson = chartParams.columnMapDataJson
        val filterArr = dataViewJson.optJSONObject("visualizations").optJSONArray("filters")
        for(i in 0 until filterArr.length()){
            val filterObj = filterArr.optJSONObject(i)
            val columnId = filterObj.optString("columnId")
            val configZone = filterObj.optString("configZone")
            var columnType = filterObj.optInt("columnType")

            if(configZone.equals("values",true)){
                columnType = 2
            }

            var columnName = columnMapDataJson.optString(columnId)
            sortFilterList.add(
                InsightSortFilterData(
                    columnType,
                    columnId,
                    configZone,
                    columnName
                )
            )

            if (configZone.equals("axis",true)) {
                sortList.add(
                    InsightSortFilterData(
                        columnType,
                        columnId,
                        configZone,
                        columnName
                    )
                )
            }
        }

    }catch (e : Exception){

    }

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {

            R.id.navigation_legend -> {
                AppUtil.sendGoogleEvent(requireContext(),"DataView","Legend",chartParams?.chartTitle)
                legendStatus = !legendStatus
                val legend = HILegend()
                legend.enabled = legendStatus
                mBinding.hightChartV.options.legend = legend

                return@OnNavigationItemSelectedListener true
            }

            R.id.navigation_share -> {
                AppUtil.sendGoogleEvent(requireContext(),"DataView","Share",chartParams?.chartTitle)
                val bitmap=   screenShot(mBinding.hightChartV)
                bitmap?.let { showShareView(it) }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sort -> {
                AppUtil.sendGoogleEvent(requireContext(),"DataView","Sort",chartParams?.chartTitle)
                showSortBottomSheet()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
                AppUtil.sendGoogleEvent(requireContext(),"DataView","Filter",chartParams?.chartTitle)
                showInsightSelectFilter()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    private fun handleBackPressed(){

        findNavController().navigate(R.id.action_insightDetailFrag_to_appDetailFrag)

    }


    fun addOverFlowMenu(){
                    mBinding.moreImg.visibility = View.VISIBLE
                    mBinding.moreImg.setOnClickListener {
                        overFlowPopup?.show()
                    }

                    if(overFlowPopup==null){
                        overFlowPopup = createPopUpMenu()
                    }
        overFlowPopup?.menu?.add(0, 0, 0, "Disable DrillDown")

            }

    fun createPopUpMenu(): PopupMenu {
        val popup = PopupMenu(requireContext(), mBinding.moreImg)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.dynam_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        return popup

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId==0){
            if(mBinding.drillHeader.visibility == View.VISIBLE){
                mBinding.drillHeader.visibility = View.GONE
                overFlowPopup?.menu?.getItem(0)?.setTitle("Enable DrillDown")
            }else{
                mBinding.drillHeader.visibility = View.VISIBLE
                overFlowPopup?.menu?.getItem(0)?.setTitle("Disable DrillDown")
            }

        }

        return false
    }


    private fun showSortBottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.qtc_filter_lay, null)
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val listView = view.findViewById<ListView>(R.id.lst_view)
        val radioButton = view.findViewById<RadioButton>(R.id.radio_button)

        radioButton.setOnClickListener {
            if(sortSelectedType.equals("")){
                sortSelectedPos = -1
                sortSelectedType = ""
                bottomSheetDialog.dismiss()
            }else {
                sortSelectedPos = -1
                sortSelectedType = ""
                bottomSheetDialog.dismiss()

                val jsonObj =chartParams.dataViewJson

                val dataViewJson  = JSONObject()


                jsonObj.optJSONObject("visualizations").optJSONObject("configuration").
                put("sortBy",JSONArray())

                dataViewJson.put("dataViewJson",jsonObj)
                if(jsonObj.has("calculatedMeasures")){
                    dataViewJson.put("calculatedMeasures",jsonObj.optJSONArray("calculatedMeasures"))
                }

                if(chartParams.chartType.equals("Bubble",true)||
                        chartParams.chartType.equals("Heatmap",true)){
                    dataViewJson.put("parseData",true)
                }

                val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)

                mViewModel.getInsightDataVisulize(payLoadObj)


            }

        }


        if (sortSelectedPos != -1) {
            radioButton.isChecked = false
        }

        insightDtlSortAdapter = InsightDtlSortAdapter(requireActivity(), sortList,
                bottomSheetDialog,sortSelectedPos,sortSelectedType)
        listView?.adapter = insightDtlSortAdapter
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        insightDtlSortAdapter.onItemClick = {
            selectedItem ->
            radioButton.isChecked = false

            sortSelectedPos = selectedItem.pos
            sortSelectedType = selectedItem.orderType
            val item = sortList[sortSelectedPos]
            val sortOption = JSONObject()

                sortOption.put("columnType",item.columnType)
                sortOption.put("columnId",item.columnId)
                sortOption.put("configZone",item.configZone)
                sortOption.put("columnName",item.columnName)
                sortOption.put("orderBy",sortSelectedType)

            val jsonObj =chartParams.dataViewJson

            val dataViewJson  = JSONObject()


            jsonObj.optJSONObject("visualizations").optJSONObject("configuration").
            optJSONArray("sortBy").put(sortOption)

            dataViewJson.put("dataViewJson",jsonObj)
            if(jsonObj.has("calculatedMeasures")){
                dataViewJson.put("calculatedMeasures",jsonObj.optJSONArray("calculatedMeasures"))
            }

            if(chartParams.chartType.equals("Bubble",true)||
                    chartParams.chartType.equals("Heatmap",true)){
                dataViewJson.put("parseData",true)
            }

            val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)

            mViewModel.getInsightDataVisulize(payLoadObj)


        }

    }

    fun disableBottomNavItem(arr : IntArray, bottomNavigationView : BottomNavigationView){

        for(i in 0 until arr.size){
            bottomNavigationView.menu.getItem(i).isEnabled = false;
            bottomNavigationView.menu[i].isChecked = false
            bottomNavigationView.menu[i].isCheckable = false
        }
    }

    fun enableBottomNavItem(arr : IntArray, bottomNavigationView : BottomNavigationView){

        for(i in 0 until arr.size){
            bottomNavigationView.menu.getItem(i).isEnabled = true;
        }
    }

    private fun showInsightSelectFilter() {

        mViewModel.appInsightDtlFltrCoumResponse.removeObservers(viewLifecycleOwner);

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.insight_dtl_fltr_lay)

         noBasicFltrTv = dialog.findViewById<TextView>(R.id.no_basic_ftlr_data_tv)
         title = dialog.findViewById<TextView>(R.id.ttl)
         backImg = dialog.findViewById<ImageView>(R.id.close_icon)
         subTtile = dialog.findViewById<TextView>(R.id.actions_txt)

         tabLayout = dialog.findViewById<TabLayout>(R.id.tabLayout)
         basicLay = dialog.findViewById<LinearLayout>(R.id.basic_dyn_ll)
         basicFltView = dialog.findViewById<ScrollView>(R.id.basic_view)
         advanceView = dialog.findViewById<LinearLayout>(R.id.advance_view)

         andOperView = dialog.findViewById<TextView>(R.id.and_op_txt);
         OrOperView = dialog.findViewById<TextView>(R.id.or_op_txt);

         valueOneEditText = dialog.findViewById<EditText>(R.id.value_one)
         valueTwoEditText = dialog.findViewById<EditText>(R.id.value_two)

        spinnerOneLock = dialog.findViewById(R.id.spiner_one_lock)
        spinnerTwoLock = dialog.findViewById(R.id.spiner_two_lock)
        filterOperView = dialog.findViewById(R.id.filter_opertor_view)
        filterTwoView = dialog.findViewById(R.id.filer_two_view)


        spinnerOneLock.setOnClickListener {
            spinnerOneLock.visibility = View.INVISIBLE
            spinnerOperOne.isEnabled = true
        }
        spinnerTwoLock.setOnClickListener {
            spinnerTwoLock.visibility = View.INVISIBLE
            spinnerOperTwo.isEnabled = true
        }

        andOperView.setOnClickListener {
            filterAndOperClicked()
        }
        OrOperView.setOnClickListener {
             filterOrOperClicked()
        }

        val insightFilterHelper = InsightFilterHelper()


         spinnerOperOne = dialog.findViewById(R.id.oper_one)

         spinnerOperTwo = dialog.findViewById<Spinner>(R.id.oper_two)
        filterListView = dialog.findViewById(R.id.slicer_rv)

        backImg.tag=1
        backImg.setOnClickListener {
            if(backImg.tag==1){
                dialog.dismiss()
            }else {
                showFilterListView()
            }

        }




        var listData  = ArrayList<BasicFltrData>();
        for(i in 0 until sortFilterList.size){

            var isChecked = false
            if(hashMapIsRowChecked.containsKey(sortFilterList[i].columnId)){
                isChecked = true
            }
            listData.add(BasicFltrData(sortFilterList[i].columnName,isChecked))
        }




        val listApapter = InsightBasicFltrAdapter(requireContext(), listData)

        filterListView.adapter = listApapter


        filterListView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->

            selectedInshtFltrData = sortFilterList[position]

             isBasicFltrAvialble = isBasicFilterAvaliable(selectedInshtFltrData.columnType.toString(),
                selectedInshtFltrData.configZone.toString())

            if(selectedInshtFltrData.columnId.equals("_3")){
                isBasicFltrAvialble = false
            }

            if(!isBasicFltrAvialble){
                filterListView.visibility = View.GONE
                noBasicFltrTv.visibility= View.VISIBLE
            }else {
                filterListView.visibility = View.VISIBLE
                noBasicFltrTv.visibility= View.GONE
            }

            title.setText(sortFilterList[position].columnName.toString())
            backImg.tag=2
            seletedPos = position
            currentView = "Basic"

            if(hashMapIsRowValuesChecked.containsKey(selectedInshtFltrData.columnId)){

            }
            basicLay.removeAllViews()
            selectedBasicFlterValues.clear()

            tabLayout.getTabAt(0)!!.select()


            val advanceFltrValues = insightFilterHelper.getOperatorDisplayValues(selectedInshtFltrData.columnType)


            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(),
                    android.R.layout.simple_spinner_item, advanceFltrValues)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view

            spinnerOperOne.adapter = spinnerArrayAdapter
            spinnerOperTwo.adapter = spinnerArrayAdapter

            spinnerClickListener()

            val coluId = sortFilterList[position].columnId

            val json: String = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_DTL_DATA,"").toString()
            val gson = Gson()
            val chartP = gson.fromJson(json, ChartParams::class.java)

            valueOneEditText.resetFltrFieldToDefault()
            valueTwoEditText.resetFltrFieldToDefault()

                val columType = selectedInshtFltrData.columnType
                if (columType == -1 || columType == 2 || columType == 5) {
                    valueOneEditText.setInputType(InputType.TYPE_CLASS_NUMBER)
                    valueTwoEditText.setInputType(InputType.TYPE_CLASS_NUMBER)
                } else if (columType == 1) {
                    valueOneEditText.setInputType(InputType.TYPE_CLASS_TEXT)
                    valueTwoEditText.setInputType(InputType.TYPE_CLASS_TEXT)
                } else if (columType == 3 ) {
                     valueOneEditText.setFocusable(false)
                    valueTwoEditText.setFocusable(false)

                    valueOneEditText.setTag(3)
                    valueTwoEditText.setTag(3)

                    valueOneEditText.setOnTouchListener(object : View.OnTouchListener {
                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                            when (event?.action) {
                                MotionEvent.ACTION_UP ->
                                    openDatePickerDialog(v!!)
                            }

                            return v?.onTouchEvent(event) ?: true
                        }
                    })

                    valueTwoEditText.setOnTouchListener(object : View.OnTouchListener {
                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                            when (event?.action) {
                                MotionEvent.ACTION_UP ->
                                    openDatePickerDialog(v!!)
                            }

                            return v?.onTouchEvent(event) ?: true
                        }
                    })

                }

            val jsonObj = JSONObject(chartP.dataViewJsonStr)
            val dataViewJson  = JSONObject()

            jsonObj.optJSONObject("visualizations").
            optJSONObject("configuration").
            put("basicFilterColumn",coluId)

            dataViewJson.put("dataViewJson",jsonObj)
            if(jsonObj.has("calculatedMeasures")){
                dataViewJson.put("calculatedMeasures",jsonObj.optJSONArray("calculatedMeasures"))
            }

            if(chartParams.chartType.equals("Bubble",true)||
                    chartParams.chartType.equals("Heatmap",true)){
                dataViewJson.put("parseData",true)
            }

            val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)


            if(isBasicFltrAvialble){
                mViewModel.getInsightDtlFltrCoums(payLoadObj)
            }


            subTtile.visibility = View.GONE
            tabLayout.visibility = View.VISIBLE
            filterListView.visibility = View.GONE
            backImg.setImageDrawable(requireContext().getDrawable(R.drawable.ic_arrow_back_gray))
            basicFltView.visibility = View.VISIBLE

        }


        val applyTv = dialog.findViewById<TextView>(R.id.apply_tv)
        val resetTv = dialog.findViewById<TextView>(R.id.reset_tv)

        resetTv.setOnClickListener {

            hashMapIsRowValuesChecked.clear()
            hashMapIsRowChecked.clear()
            hashMapAdvanceFltrData.clear()

            advaceFltData = AdvanceFltrData(0,0,"and",
                    "","",false,"")

            val payLoadObj = getWithoutFilterPayLoadObj()

            dialog.dismiss()
            mViewModel.getInsightDataVisulize(payLoadObj)


        }

        applyTv.setOnClickListener {


            if(backImg.tag==1){

                if (currentView.equals("Basic", true)) {

                   val payLoadObj = getBasicPayLoadObj()

                    dialog.dismiss()
                    mViewModel.getInsightDataVisulize(payLoadObj)

                }else {

                    val payLoadObj = getAdvancePayLoadObj(insightFilterHelper)
                    dialog.dismiss()
                    mViewModel.getInsightDataVisulize(payLoadObj)

                }


            }else {

                if (currentView.equals("Basic", true)) {


                    if (selectedBasicFlterValues.isNotEmpty()) {


                        var arrList = ArrayList<String>()
                        arrList.addAll(selectedBasicFlterValues)
                        hashMapIsRowValuesChecked[selectedInshtFltrData.columnId] = arrList

                        hashMapIsRowChecked[selectedInshtFltrData.columnId] = true
                        listData[seletedPos].apply {
                            this.isChecked = true
                        }

                    } else {
                        if (hashMapIsRowValuesChecked.containsKey(selectedInshtFltrData.columnId)) {
                            hashMapIsRowValuesChecked.remove(selectedInshtFltrData.columnId)
                            hashMapIsRowChecked.remove(selectedInshtFltrData.columnId)
                        }

                        listData[seletedPos].apply {
                            this.isChecked = false
                        }
                    }

                    val listApapter = InsightBasicFltrAdapter(requireContext(), listData)

                    filterListView.adapter = listApapter

                       showFilterListView()
                }else {



                    val fistOperVal = spinnerOperOne.selectedItemPosition
                    val secondOperVal = spinnerOperTwo.selectedItemPosition
                    val mainOper  = mainOperVal
                    val valueOne = valueOneEditText.text.toString().trim()
                    val valueTwo = valueTwoEditText.text.toString().trim()

                    if(fistOperVal !=0 || secondOperVal !=0 ||valueOne.isNotEmpty() || valueTwo.isNotEmpty()){
                        hashMapIsRowChecked[selectedInshtFltrData.columnId] = true
                        listData[seletedPos].apply {
                            this.isChecked = true
                        }
                        advaceFltData = AdvanceFltrData(fistOperVal,secondOperVal,mainOper,
                                valueOne,valueTwo,true,selectedInshtFltrData.columnId)
                        hashMapAdvanceFltrData[selectedInshtFltrData.columnId] = advaceFltData
                    }else {
                        if(hashMapIsRowChecked.containsKey(selectedInshtFltrData.columnId)){
                            hashMapIsRowChecked.remove(selectedInshtFltrData.columnId)
                            listData[seletedPos].apply {
                                this.isChecked = false
                            }
                            advaceFltData = AdvanceFltrData(fistOperVal,secondOperVal,mainOper,
                                    valueOne,valueTwo,false,selectedInshtFltrData.columnId)
                            hashMapAdvanceFltrData[selectedInshtFltrData.columnId] = advaceFltData
                        }
                    }



                    val listApapter = InsightBasicFltrAdapter(requireContext(), listData)

                    filterListView.adapter = listApapter

                   showFilterListView()

                }

            }


        }


        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                if(tab.position==0){
                    basicFltView.visibility = View.VISIBLE
                    advanceView.visibility = View.GONE
                    currentView = "Basic"
                    if(!isBasicFltrAvialble){
                        noBasicFltrTv.visibility = View.VISIBLE
                    }
                }else if (tab.position==1){
                    if(hashMapAdvanceFltrData.containsKey(selectedInshtFltrData.columnId)){
                        advaceFltData= hashMapAdvanceFltrData[selectedInshtFltrData.columnId]!!
                    }
                    if(::advaceFltData.isInitialized){


                        if(advaceFltData.isDataSet &&
                                selectedInshtFltrData.columnId.equals(advaceFltData.columId,true)){

                            spinnerOperOne.setSelection(advaceFltData.firstOperator)
                            spinnerOperTwo.setSelection(advaceFltData.secondOperator)

                            valueOneEditText.setText(advaceFltData.firstInput)
                            valueTwoEditText.setText(advaceFltData.SecondInput)

                            if(advaceFltData.mainOperator.equals("or")){
                                filterOrOperClicked()
                            }else{
                                filterAndOperClicked()
                            }
                        }else {

                            clearAdvanceFiltterView()

                        }


                    }else{

                        clearAdvanceFiltterView()
                    }


                    advanceView.visibility = View.VISIBLE
                    basicFltView.visibility = View.GONE
                    noBasicFltrTv.visibility = View.GONE
                    currentView = "Advance"
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })



            dialog.show()





        mViewModel.appInsightDtlFltrCoumResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    try {

                        ProgressDialogUtil.hideProgressDialog()
                        var result = it.value.string()


                        val jsonObj = JSONObject(result.toString())

                        var preSeletedList = ArrayList<String>()
                        if(hashMapIsRowValuesChecked.containsKey(selectedInshtFltrData.columnId)){

                            preSeletedList = hashMapIsRowValuesChecked[selectedInshtFltrData.columnId]!!
                        }

                        val dataArr = jsonObj.optJSONArray("data")
                        for(i in 0 until dataArr.length()){
                            val obj = dataArr.optJSONObject(i)
                            val name = obj.optString("name")
                            val cb = CheckBox(requireContext())

                            val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    getResources().getDimensionPixelSize(R.dimen.cb_height_ins_dtl_fltr))

                            cb.layoutParams = params
                            cb.background = ContextCompat.getDrawable(requireContext(),R.drawable.cb_basic_flr_bg);
                            cb.text = name

                            if(preSeletedList.contains(name)){
                                cb.isChecked = true
                                selectedBasicFlterValues.add(cb.text.toString())
                            }


                            cb.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                                if(isChecked){

                                    selectedBasicFlterValues.add(cb.text.toString())

                                }else {
                                    selectedBasicFlterValues.remove(cb.text.toString())

                                }
                            }
                            )

                            basicLay.addView(cb)
                        }

                        }catch (e : Exception){

                        }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }

        })


    }

    private fun clearAdvanceFiltterView() {
        spinnerTwoLock.visibility = View.INVISIBLE
        spinnerOneLock.visibility = View.INVISIBLE
        valueOneEditText.setText("")
        valueTwoEditText.setText("")
        filterTwoView.visibility = View.INVISIBLE
        filterOperView.visibility = View.INVISIBLE
        spinnerOperOne.isEnabled = true
        spinnerOperTwo.isEnabled = true
        spinnerOperOne.setSelection(0)
        spinnerOperTwo.setSelection(0)
    }


    private fun showShareView(bitmap: Bitmap) {

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.share_view_lay)
        val shareImage = dialog.findViewById<TextView>(R.id.share_img)
        val closeIcon = dialog.findViewById<ImageView>(R.id.close_icon)
        val addTextTv = dialog.findViewById<ImageView>(R.id.add_text)
        val addBrush = dialog.findViewById<ImageView>(R.id.add_brush)
        val clearAll = dialog.findViewById<TextView>(R.id.clear_all)
        val mPhotoEditorView: PhotoEditorView = dialog.findViewById(R.id.chart_shar_img)

        val addTextColorPickerRecyclerView: RecyclerView =
            dialog.findViewById(R.id.add_text_color_picker_recycler_view)

        var brushColorCode = 0
        var textColorCode = 0



        mPhotoEditorView.getSource().setImageBitmap(bitmap)
        var mPhotoEditor = PhotoEditor.Builder(requireContext(), mPhotoEditorView)
            .build()

        dialog.show()

        closeIcon.setOnClickListener {
            dialog.dismiss()
        }



        addTextTv.setOnClickListener {

            addTextColorPickerRecyclerView.visibility = View.GONE
            clearAll.visibility = View.VISIBLE

            val textEditorDialogFragment =
                TextEditorDialogFragment.show(requireActivity())
            textEditorDialogFragment.setOnTextEditorListener { inputText, colorCode ->
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                styleBuilder.withTextSize(22f)
                mPhotoEditor.addText(inputText, styleBuilder)
                textColorCode = colorCode
                updateImageViewColor(addTextTv,textColorCode)

            }

        }

        addBrush.setOnClickListener {

            mPhotoEditor.setBrushDrawingMode(true)
            addTextColorPickerRecyclerView.visibility= View.VISIBLE
            clearAll.visibility = View.VISIBLE
        }



        val layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        addTextColorPickerRecyclerView.layoutManager = layoutManager
        addTextColorPickerRecyclerView.setHasFixedSize(true)

        val colorPickerAdapter = ColorPickerAdapter(requireActivity())
        colorPickerAdapter.setOnColorPickerClickListener { colorCode ->
            brushColorCode = colorCode
            mPhotoEditor.brushColor = brushColorCode

            updateImageViewColor(addBrush,brushColorCode)

        }
        addTextColorPickerRecyclerView.adapter = colorPickerAdapter


        shareImage.setOnClickListener {

            onClickShare(screenShot(mPhotoEditorView)!!)
        }


        clearAll.setOnClickListener {

            mPhotoEditor.clearAllViews()
            clearAll.visibility = View.GONE
            addTextColorPickerRecyclerView.visibility = View.GONE

            updateImageViewColor(addBrush,Color.BLACK)
            updateImageViewColor(addTextTv,Color.BLACK)


        }


        mPhotoEditor.setOnPhotoEditorListener(object : OnPhotoEditorListener{

            override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

            }

            override fun onEditTextChangeListener(rootView: View?, text: String?, colorCode: Int) {
                val textEditorDialogFragment: TextEditorDialogFragment =
                    TextEditorDialogFragment.show(requireActivity(), text!!, colorCode)
                textEditorDialogFragment.setOnTextEditorListener({ inputText, newColorCode ->
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(newColorCode)
                    styleBuilder.withTextSize(22f)
                    mPhotoEditor.editText(rootView!!, inputText, styleBuilder)

                    textColorCode = newColorCode
                    updateImageViewColor(addTextTv,textColorCode)

                })
            }

            override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {

            }

            override fun onStartViewChangeListener(viewType: ViewType?) {

            }

            override fun onStopViewChangeListener(viewType: ViewType?) {

            }


        })

    }


    private fun getAdvancePayLoadObj(insightFilterHelper : InsightFilterHelper) : JsonObject {
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_DTL_DATA,"").toString()
        val gson = Gson()
        val chartP = gson.fromJson(json, ChartParams::class.java)
        val jsonObj = JSONObject(chartP.dataViewJsonStr)

        val dataViewJson = JSONObject()

        val firstFilter = JSONObject();
        firstFilter.put("columnId", selectedInshtFltrData.columnId)
        firstFilter.put("columnName", selectedInshtFltrData.columnName)
        firstFilter.put("columnType", selectedInshtFltrData.columnType)
        firstFilter.put("configZone", selectedInshtFltrData.configZone)

        if(selectedInshtFltrData.columnType ==3 ){
            firstFilter.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss")
        }


        firstFilter.put("operator",
                insightFilterHelper.getOperatorSelectedValue(selectedInshtFltrData.columnType,advaceFltData.firstOperator))

        val valuesArr = JSONArray();
        valuesArr.put(advaceFltData.firstInput)
        firstFilter.put("value", valuesArr)



        val secondFilter = JSONObject();
        secondFilter.put("columnId", selectedInshtFltrData.columnId)
        secondFilter.put("columnName", selectedInshtFltrData.columnName)
        secondFilter.put("columnType", selectedInshtFltrData.columnType)
        secondFilter.put("configZone", selectedInshtFltrData.configZone)
        secondFilter.put("operator",
                insightFilterHelper.getOperatorSelectedValue(selectedInshtFltrData.columnType,advaceFltData.secondOperator))

        if(selectedInshtFltrData.columnType ==3 ){
            secondFilter.put("dateFormat", "yyyy-MM-dd'T'HH:mm:ss")
        }

        val valuesArr1 = JSONArray();
        valuesArr1.put(advaceFltData.SecondInput)
        secondFilter.put("value", valuesArr1)



        val filterObj = JSONObject();
        filterObj.put("columnId", selectedInshtFltrData.columnId)
        filterObj.put("columnName", selectedInshtFltrData.columnName)
        filterObj.put("columnType", selectedInshtFltrData.columnType)
        filterObj.put("configZone", selectedInshtFltrData.configZone)
        filterObj.put("type", "advanced")
        filterObj.put("logicalOperator", advaceFltData.mainOperator)
        filterObj.put("firstFilter",firstFilter)
        if(advaceFltData.SecondInput.isNotEmpty()){
            filterObj.put("secondFilter",secondFilter)
        }


        jsonObj.optJSONObject("visualizations").optJSONArray("filters").put(filterObj)

        dataViewJson.put("dataViewJson", jsonObj)
        if (jsonObj.has("calculatedMeasures")) {
            dataViewJson.put("calculatedMeasures", jsonObj.optJSONArray("calculatedMeasures"))
        }

        if (chartParams.chartType.equals("Bubble", true) ||
                chartParams.chartType.equals("Heatmap", true)) {
            dataViewJson.put("parseData", true)
        }

        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)

        return payLoadObj

    }

    private fun getBasicPayLoadObj() : JsonObject {
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_DTL_DATA,"").toString()
        val gson = Gson()
        val chartP = gson.fromJson(json, ChartParams::class.java)
        val jsonObj = JSONObject(chartP.dataViewJsonStr)

        val dataViewJson = JSONObject()

        hashMapIsRowValuesChecked.forEach { (t, u) ->

            val columId = t
            lateinit var selectedInshtFltrData: InsightSortFilterData
            for (i in sortFilterList.indices) {
                if (columId.equals(sortFilterList[i].columnId, true)) {
                    selectedInshtFltrData = sortFilterList[i]
                }
            }
            val filterObj = JSONObject();
            filterObj.put("columnId", selectedInshtFltrData.columnId)
            filterObj.put("columnName", selectedInshtFltrData.columnName)
            filterObj.put("columnType", selectedInshtFltrData.columnType)
            filterObj.put("configZone", selectedInshtFltrData.configZone)
            filterObj.put("type", "basic")
            filterObj.put("operator", "in")

            val selValues = u
            val valuesArr = JSONArray();
            for (i in selValues.indices) {
                valuesArr.put(selValues[i])
            }
            filterObj.put("value", valuesArr)

            jsonObj.optJSONObject("visualizations").optJSONArray("filters").put(filterObj)


        }


        dataViewJson.put("dataViewJson", jsonObj)
        if (jsonObj.has("calculatedMeasures")) {
            dataViewJson.put("calculatedMeasures", jsonObj.optJSONArray("calculatedMeasures"))
        }

        if (chartParams.chartType.equals("Bubble", true) ||
                chartParams.chartType.equals("Heatmap", true)) {
            dataViewJson.put("parseData", true)
        }

        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)
        return payLoadObj
    }

    private fun getWithoutFilterPayLoadObj() : JsonObject {
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_DTL_DATA,"").toString()
        val gson = Gson()
        val chartP = gson.fromJson(json, ChartParams::class.java)
        val jsonObj = JSONObject(chartP.dataViewJsonStr)

        val dataViewJson = JSONObject()

        dataViewJson.put("dataViewJson", jsonObj)
        if (jsonObj.has("calculatedMeasures")) {
            dataViewJson.put("calculatedMeasures", jsonObj.optJSONArray("calculatedMeasures"))
        }

        if (chartParams.chartType.equals("Bubble", true) ||
                chartParams.chartType.equals("Heatmap", true)) {
            dataViewJson.put("parseData", true)
        }

        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)
        return payLoadObj
    }

    private fun showFilterListView() {
        title.setText("Filters")
        backImg.tag =1
        tabLayout.visibility = View.GONE
        noBasicFltrTv.visibility = View.GONE
        basicFltView.visibility = View.GONE
        advanceView.visibility = View.GONE
        subTtile.visibility= View.VISIBLE
        filterListView.visibility = View.VISIBLE
        backImg.setImageDrawable(requireContext().getDrawable(R.drawable.ic_close))
    }

    private fun filterOrOperClicked() {
        mainOperVal ="or"
        OrOperView.background = ContextCompat.getDrawable(requireContext(),R.drawable.selected_op_bg)
        OrOperView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

        andOperView.background = ContextCompat.getDrawable(requireContext(),R.drawable.un_selected_op_bg)
        andOperView.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
    }

    private fun filterAndOperClicked() {
        mainOperVal = "and"
        andOperView.background = ContextCompat.getDrawable(requireContext(),R.drawable.selected_op_bg)
        andOperView.setTextColor(ContextCompat.getColor(requireContext(),R.color.white))

        OrOperView.background = ContextCompat.getDrawable(requireContext(),R.drawable.un_selected_op_bg)
        OrOperView.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
    }


    fun openDatePickerDialog(v: View) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate =
                            year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()

                    (v as EditText).setText(selectedDate+"T00:00:00")
                    (v as EditText).setError(null)

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    fun spinnerClickListener(){
        spinnerOperOne?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if(valueOneEditText.tag == 3){
                    valueOneEditText.disableDatePkrForFltr()
                }

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if(spinnerOperOne.selectedItem.equals("Is blank")||
                        spinnerOperOne.selectedItem.equals("Is not blank")){
                       valueOneEditText.setFocusable(false)
                    valueOneEditText.disableDatePkrForFltr()

                    valueOneEditText.setText("")
                }else {
                    valueOneEditText.setFocusableInTouchMode(true);
                    valueOneEditText.setFocusable(true)
                    valueOneEditText.isEnabled = true

                    if(valueOneEditText.tag!=null){
                        if(valueOneEditText.tag == 3){
                            valueOneEditText.enableDatePkrForFltr()
                        }
                    }


                }

                if(position!=0){
                    filterTwoView.visibility = View.VISIBLE
                    filterOperView.visibility = View.VISIBLE
                    spinnerOneLock.visibility = View.VISIBLE
                    spinnerOperOne.isEnabled = false
                }

            }

        }

        spinnerOperTwo?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                if(valueOneEditText.tag == 3){
                    valueTwoEditText.disableDatePkrForFltr()
                }

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if(spinnerOperTwo.selectedItem.equals("Is blank")||
                        spinnerOperTwo.selectedItem.equals("Is not blank")){
                    valueTwoEditText.setFocusable(false)

                    valueTwoEditText.disableDatePkrForFltr()
                    valueTwoEditText.setText("")

                }else {
                    valueTwoEditText.setFocusableInTouchMode(true);
                    valueTwoEditText.setFocusable(true)
                    valueTwoEditText.isEnabled = true

                    if(valueTwoEditText.tag!=null){
                        if(valueTwoEditText.tag == 3){
                            valueTwoEditText.enableDatePkrForFltr()

                        }
                    }
                }

                if(position!=0){

                    spinnerTwoLock.visibility = View.VISIBLE
                    spinnerOperTwo.isEnabled = false
                }
            }

        }

    }

    fun screenShot(view: View): Bitmap? {
        val bitmap: Bitmap = Bitmap.createBitmap(
            view.width,
            view.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }



    fun onClickShare( bitmap: Bitmap) {
        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String = MediaStore.Images.Media.insertImage(
                requireContext().contentResolver,
                bitmap,
                "Eka",
                null
            )
            val imageUri = Uri.parse(path)

            val waIntent = Intent(Intent.ACTION_SEND)
            waIntent.type = "image/*"

            waIntent.putExtra(Intent.EXTRA_STREAM, imageUri)


            val chooser = Intent.createChooser(waIntent, "Share File")

            val resInfoList: List<ResolveInfo> = requireContext().getPackageManager()
                .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

            for (resolveInfo in resInfoList) {
                val packageName: String = resolveInfo.activityInfo.packageName
                requireContext().grantUriPermission(
                    packageName,
                    imageUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(chooser)


        } catch (e: java.lang.Exception) {

        }
    }


    fun updateImageViewColor(imageView: ImageView,color : Int){
        val porterDuffColorFilter = PorterDuffColorFilter(
            color,
            PorterDuff.Mode.SRC_ATOP
        )
        imageView.setColorFilter(porterDuffColorFilter)
    }

    private fun isBasicFilterAvaliable(columnType :String, configZone :String) : Boolean {
        return !(columnType.equals("3") || (configZone.isEmpty() && columnType.equals("2"))
                || (configZone.isEmpty() && columnType.equals("-1"))
                ||(columnType.equals("2")  && configZone.equals("columns-table"))
                ||configZone.equals("xaxis")  || configZone.equals("yaxis")  || configZone.equals("size"))


    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        findNavController().navigate(R.id.action_insightDetailFrag_self)

     /*   val currentChartType = chartParams?.chartType
//        mBinding.hightChartV.removeAllViews()
        val chartView = mBinding.hightChartV
        if (currentChartType.equals("Pie",true)
            || currentChartType.equals("Pie3D",true)
            ||currentChartType.equals("Donut",true)
            ||currentChartType.equals("Donut3D",true)

        ){
            chartParams.valueIdList.clear()
            val columnsArr = chartParams.dataViewJson.optJSONObject("visualizations")
                .optJSONArray("filters")
            for(i in 0 until columnsArr.length()){
                val fltrObj = columnsArr.optJSONObject(i)
                if(fltrObj.optString("configZone").equals("values")){
                    chartParams.valueIdList.add(fltrObj.optString("columnId"))
                }
            }
        }


        if(currentChartType.equals("Card",true)){

            val manager = InsightsViewsManager()
            manager.setCardTypeView(requireContext(),chartView, chartParams,true,false)
            disableBottomNavItem(intArrayOf(0,1),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE

        }else if (currentChartType.equals("Table",true)){
            val manager = InsightsViewsManager()
            manager.setTableTypeView(requireContext(),chartView, chartParams,false)
            disableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE
        }
        else if (currentChartType.equals("Pivot",true)
            ||currentChartType.equals("PointTime",true)
            ||currentChartType.equals("SplineTime",true)
            ||currentChartType.equals("AreaTime",true)
            ||currentChartType.equals("AreaSplineTime",true)
            ||currentChartType.equals("LineTime",true)
            ||currentChartType.equals("ColumnTime",true)){
            val manager = InsightsViewsManager()
            val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "")
            val deciveId= AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
            val token= AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()
            val appliedFltr= AppPreferences.getKeyValue(Constants.PrefCode.WBVIEW_APPLIED_FLTR,"").toString()
            manager.setChartWebView(requireContext(),chartView,
                chartParams,baseUrl,token,currentChartType,
                deciveId, appliedFltr)

            disableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            mBinding.moreImg.visibility = View.GONE
            mBinding.drillHeader.visibility = View.GONE

            mBinding.wbViewHdr.visibility = View.VISIBLE
            mBinding.header.visibility = View.GONE
            mBinding.bottomNavigationView.visibility = View.GONE
            mBinding.serviceTtlWv.setText(chartParams?.chartTitle)
        }

        else {
            enableBottomNavItem(intArrayOf(0,1,2),mBinding.bottomNavigationView)
            val hiOptions = OptionsProvider.provideOptionsForChartType(chartParams)
            val hiTitle = HITitle();
            hiTitle.text =""
            hiOptions.title = hiTitle

            chartView.options = hiOptions
            chartView.invalidate()

            val legend = HILegend()
            legend.enabled = legendStatus
            mBinding.hightChartV.options.legend = legend


        }*/
    }

    fun getScreenWidth(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = activity.windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }
}

