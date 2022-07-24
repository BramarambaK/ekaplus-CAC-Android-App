package com.eka.cacapp.ui.insight

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.adapter.SlicerScreenAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.insight.ChartParams
import com.eka.cacapp.data.insight.InsightDetailData
import com.eka.cacapp.data.insight.SlicerData
import com.eka.cacapp.databinding.InsightChartFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.InsightRepository
import com.eka.cacapp.utils.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.highsoft.highcharts.common.hichartsclasses.HIColumn
import com.highsoft.highcharts.common.hichartsclasses.HIOptions
import com.highsoft.highcharts.core.HIChartView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class InsightChartFragment : Fragment()  {

    private var chartinfo: String? = null
    private lateinit var mViewModel: InsightViewModel
    private lateinit var mBinding : InsightChartFragBinding
    private var hashMapDataViewIds : HashMap<String,String> = HashMap()
    private var hashMapDataViewDtl : HashMap<String,JSONObject> = HashMap()
    private var dataViewIdList : ArrayList<String> = ArrayList()
    private var slicerViewId : ArrayList<String> = ArrayList()
    private var chartViewList : ArrayList<HIChartView> = ArrayList()
    private var currentDataViewIndex = 0
    private var currentSlicerDataViewIndex = 0
    var series1 = HIColumn()
    val options = HIOptions()
    var currentChartType = ""
    var currentCollectionId = ""
    var currentAxisId = ""
    var currentValueId = ""
    private var currentValueIdList : ArrayList<String> = ArrayList()
    private var slicerDataList  : ArrayList<SlicerData> = ArrayList()
    var currentXAxisTitle = ""
    var currentYAxisTitle = ""
    var currentPayLoad : JsonObject = JsonObject()
    var currentColumnMapDataJson : JSONObject = JSONObject()

    var slicerFilterArr : JSONArray = JSONArray()

    private var charDataSupportedMap : HashMap<Int,Boolean> = HashMap()
    private var slicerId = ""
    private var booleanIsSlicerReq = false
    var dataViewNameMapJSON : JSONObject = JSONObject()
    var slicerDataViewJSON : JSONObject = JSONObject()
    var slicerVisulizeJSON : JSONObject = JSONObject()
    var slicerFltrHashMap : HashMap<String,JSONObject> = HashMap()
    var insightDtlList : HashMap<Int,InsightDetailData> = HashMap()
    var slicerLastSeleValueMap : HashMap<String,ArrayList<String>> = HashMap()

    private var isNavViaSearch = false
    private var cacheJsonArray = JSONArray()
    private var cacheJsonObj = JSONObject()

    var slicerActionArr = JSONArray()
    var slicerWvFltrMap : HashMap<String,String> = HashMap()

    lateinit var progressBar : ProgressBar
    private var pbViewList : ArrayList<ProgressBar> = ArrayList()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        allowAllOrientation()

        mBinding = DataBindingUtil.inflate(inflater, R.layout.insight_chart_frag, container, false)
        val factory  = ViewModelFactory(InsightRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(InsightViewModel::class.java)

        mViewModel.appInsightDataViewResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    if(currentDataViewIndex==0 ){
                        //      ProgressDialogUtil.showProgressDialog(it1)
                    }
                    if(!ProgressDialogUtil.isShowing()){
                        //     ProgressDialogUtil.hideProgressDialog()
                        //     ProgressDialogUtil.showProgressDialog(it1)
                    }


                }
            }
            when (it) {
                is Resource.Success -> {
            try {

                    cacheJsonObj = JSONObject()
                    val result = it.value.string()
                    val jsonObj = JSONObject(result)

                    cacheJsonObj.put("id",dataViewIdList[currentDataViewIndex])
                    cacheJsonObj.put("dataviewResponse",jsonObj)

                    dataViewResponseParsing(jsonObj)


                    mViewModel.getInsightCollectionMapping(currentCollectionId)




                }catch (e : Exception){
                     ProgressDialogUtil.hideProgressDialog()

                }
                }
                is Resource.Failure -> {
                    charDataSupportedMap[currentDataViewIndex] = false
                    if(currentDataViewIndex+1 ==dataViewIdList.size){
                           ProgressDialogUtil.hideProgressDialog()
                        var cusMsg = ""
                        if(it.errorCode==403){
                            cusMsg =getString(R.string.user_unauth_error)
                        }

                        enableChartOrErrorView(cusMsg)
                    }else{
                        currentDataViewIndex++
                        mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                    }
                }
            }

        })


        mViewModel.appSLicerInsightDataVisulizeResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    if(!ProgressDialogUtil.isShowing()){
                        ProgressDialogUtil.hideProgressDialog()
                        ProgressDialogUtil.showProgressDialog(it1)
                    }

                }
            }
            when (it) {
                is Resource.Success -> {
                    try {


                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        slicerVisulizeJSON = jsonObj


                        slicerDataList.add(SlicerData(
                                slicerVisulizeJSON,slicerDataViewJSON,
                                slicerViewId[currentSlicerDataViewIndex]
                        ))

                        currentSlicerDataViewIndex++
                        if(currentSlicerDataViewIndex==slicerViewId.size){
                            ProgressDialogUtil.hideProgressDialog()
                            showInsightSelectFilter(slicerLastSeleValueMap)
                        }else{
                            mViewModel.getSlicerInsightDataView(slicerViewId[currentSlicerDataViewIndex])
                        }

                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }

        })


        mViewModel.appDateSlicerInfoResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    if(!ProgressDialogUtil.isShowing()){
                        ProgressDialogUtil.hideProgressDialog()
                        ProgressDialogUtil.showProgressDialog(it1)
                    }

                }
            }
            when (it) {
                is Resource.Success -> {
                    try {

                        val result = it.value.string()
                        val dataArr = JSONArray(result)
                        val jsonObj = JSONObject()
                        jsonObj.put("data",dataArr)
                        slicerVisulizeJSON = jsonObj


                        slicerDataList.add(SlicerData(
                                slicerVisulizeJSON,slicerDataViewJSON,
                                slicerViewId[currentSlicerDataViewIndex]
                        ))

                        currentSlicerDataViewIndex++
                        if(currentSlicerDataViewIndex==slicerViewId.size){
                            ProgressDialogUtil.hideProgressDialog()
                            showInsightSelectFilter(slicerLastSeleValueMap)
                        }else{
                            mViewModel.getSlicerInsightDataView(slicerViewId[currentSlicerDataViewIndex])
                        }

                    }catch (e : Exception){
                        e.printStackTrace()
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }

        })



        mViewModel.slicerInsightDataViewResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->


                }
            }
            when (it) {
                is Resource.Success -> {
                    try {


                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                         slicerDataViewJSON = jsonObj

                        val dataViewJson  = JSONObject()
                        val sourceType = jsonObj.optJSONObject("dataSource").optString("sourceType") ?: ""

                        if(sourceType.equals("Joined",true)
                            ||sourceType.equals("Realtime",true)){
                            jsonObj.put("inMemoryCollection",true)
                        }

                        dataViewJson.put("dataViewJson",jsonObj)
                        if(jsonObj.has("calculatedMeasures")){
                            dataViewJson.put("calculatedMeasures",jsonObj.optJSONArray("calculatedMeasures"))
                        }

                        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)


                        if(slicerDataViewJSON.optJSONObject("visualizations")
                                        .optString("chartType").equals("DateRangeSlicer",true)){
                            mViewModel.getDateSlicerInfo()
                        }else {
                            mViewModel.getSlicerInsightDataVisulize(payLoadObj)
                        }

                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }

        })




        mViewModel.appSlicerDataMapResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)

                }
            }
            when (it) {
                is Resource.Success -> {
                    try {


                        val result = it.value.string()
                        dataViewNameMapJSON = JSONObject(result)
                        mViewModel.getSlicerInsightDataView(slicerViewId[0])


                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }

        })


        mViewModel.appCollectionColumMapingResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->


                }
            }
            when (it) {
                is Resource.Success -> {
                    try {


                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        currentColumnMapDataJson = jsonObj

                        cacheJsonObj.put("columnMapping",currentColumnMapDataJson)
                        currentXAxisTitle = jsonObj.optString(currentAxisId)
                        currentYAxisTitle = jsonObj.optString(currentYAxisTitle)

                        mViewModel.getInsightQuickEditInfo(currentCollectionId)
                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    charDataSupportedMap[currentDataViewIndex] = false
                    if(currentDataViewIndex+1 ==dataViewIdList.size){
                        ProgressDialogUtil.hideProgressDialog()
                        enableChartOrErrorView()
                    }else{
                        currentDataViewIndex++
                        mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                    }
                }
            }

        })

        mViewModel.appInsightEditInfoResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->


                }
            }
            when (it) {
                is Resource.Success -> {
                    try {


                        val result = it.value.string()
                        val jObj = JSONObject(result)


                        cacheJsonObj.put("editingRes",jObj)

                        editInfoResponseParsing(jObj)


                                        if(currentChartType.equals("Pivot",true)
                        ||currentChartType.equals("PointTime",true)
                        ||currentChartType.equals("SplineTime",true)
                        ||currentChartType.equals("AreaTime",true)
                        ||currentChartType.equals("AreaSplineTime",true)
                        ||currentChartType.equals("LineTime",true)
                        ||currentChartType.equals("ColumnTime",true)
                                            ||currentChartType.equals("DotMap",true)){
                    charDataSupportedMap[currentDataViewIndex] = true


                    visualizeResponseParsing(JSONObject())

                    if(currentDataViewIndex+1 ==dataViewIdList.size){
                        ProgressDialogUtil.hideProgressDialog()
                        enableChartOrErrorView()

                    }else{
                        currentDataViewIndex++
                        mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                    }

                }else {

                                            mViewModel.getInsightDataVisulize(currentPayLoad)
                                        }

                    }catch (e : Exception){
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    charDataSupportedMap[currentDataViewIndex] = false
                    if(currentDataViewIndex+1 ==dataViewIdList.size){
                        ProgressDialogUtil.hideProgressDialog()
                        enableChartOrErrorView()
                    }else{
                        currentDataViewIndex++
                        mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                    }
                }
            }

        })




        mViewModel.appInsightDataVisulizeResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 ->
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
            when (it) {
                is Resource.Success -> {
                    try {

                        val result = it.value.string()
                        val jsonObj = JSONObject(result)

                        cacheJsonObj.put("visuResponse", jsonObj)
                        val existingArrStr = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                                "")
                        if (existingArrStr!!.isNotEmpty()) {
                            val existingArr = JSONArray(existingArrStr)
                            cacheJsonArray = existingArr
                        }

                        cacheJsonArray.put(cacheJsonObj)
                        AppPreferences.saveValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                                cacheJsonArray.toString())

                        visualizeResponseParsing(jsonObj)


                        if (currentDataViewIndex + 1 == dataViewIdList.size) {
                            ProgressDialogUtil.hideProgressDialog()
                            enableChartOrErrorView()

                        } else {
                            currentDataViewIndex++
                            mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                        }

                    } catch (e: Exception) {
                        ProgressDialogUtil.hideProgressDialog()

                    }
                }
                is Resource.Failure -> {
                    charDataSupportedMap[currentDataViewIndex] = false
                    if (currentDataViewIndex + 1 == dataViewIdList.size) {
                        ProgressDialogUtil.hideProgressDialog()
                        enableChartOrErrorView()
                    } else {
                        currentDataViewIndex++
                        mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
                    }

                    // handleError(it) {null}
                    //  handleApiError(it){null}
                }
            }
        }

        })


        return mBinding.root
    }

    private fun visualizeResponseParsing(jsonObj: JSONObject) {
        if( currentChartType.equals("column",true)
                ||currentChartType.equals("Bar",true)
                ||currentChartType.equals("StackedBar",true)
                ||currentChartType.equals("StackedPercentageColumn",true)
                ||currentChartType.equals("StackedColumn",true)
                ||currentChartType.equals("Area",true)
                ||currentChartType.equals("AreaSpline",true)
                ||currentChartType.equals("StackedPercentageArea",true)
                ||currentChartType.equals("StackedArea",true)
                ||currentChartType.equals("Pie",true)
                || currentChartType.equals("Pie3D",true)
                || currentChartType.equals("Heatmap",true)
                || currentChartType.equals("Spline",true)
                || currentChartType.equals("Line",true)
                || currentChartType.equals("ScatterPlot",true)
                || currentChartType.equals("Polar",true)
                || currentChartType.equals("Bubble",true)
                ||currentChartType.equals("Donut",true)
                ||currentChartType.equals("Donut3D",true)
                ||currentChartType.equals("Card",true)
                ||currentChartType.equals("Table",true)
                || currentChartType.equals("Pivot",true)
                ||currentChartType.equals("PointTime",true)
                ||currentChartType.equals("SplineTime",true)
                ||currentChartType.equals("AreaTime",true)
                ||currentChartType.equals("AreaSplineTime",true)
                ||currentChartType.equals("LineTime",true)
                ||currentChartType.equals("ColumnTime",true)
                ||currentChartType.equals("DotMap",true)
                ||currentChartType.equals("Column3D",true)
                ||currentChartType.equals("LineColumn",true)
                ||currentChartType.equals("LineColumnStacked",true)
                ||currentChartType.equals("LineArea",true)
                ||currentChartType.equals("LineAreaStacked",true)
                ||currentChartType.equals("Bar3D",true)
                ||currentChartType.equals("SemiPie",true)
              ||currentChartType.equals("StackedPercentageBar",true)
            ||currentChartType.equals("Scatter",true)

                ||currentChartType.equals("LineLine",true)
        //
        //
        ) {

            val jsonData = hashMapDataViewDtl[currentDataViewIndex.toString()] ?: JSONObject()

            val dataViewId = dataViewIdList[currentDataViewIndex]
            val charParams = ChartParams(
                    jsonData?.optJSONObject("visualizations")?.optString("chartType")?:"",
                    jsonData?.optString("name")?:"",
                    "",false, currentXAxisTitle, currentYAxisTitle,
                    currentAxisId,currentValueIdList,jsonObj,currentColumnMapDataJson,jsonData,
                    "","","",dataViewId )


            pbViewList[currentDataViewIndex].visibility = View.GONE

            if(currentChartType.equals("Card",true)){

                val manager = InsightsViewsManager()
                manager.setCardTypeView(requireContext(),chartViewList[currentDataViewIndex], charParams,false,true)

                insightDtlList[chartViewList[currentDataViewIndex].tag as Int] = InsightDetailData(true,charParams
                )


            }else if (currentChartType.equals("Table",true)){
                val manager = InsightsViewsManager()
                manager.setTableTypeView(requireContext(),chartViewList[currentDataViewIndex], charParams,true)
                insightDtlList[chartViewList[currentDataViewIndex].tag as Int] = InsightDetailData(true,charParams)
            }
            else if (currentChartType.equals("Pivot",true)
                    ||currentChartType.equals("PointTime",true)
                    ||currentChartType.equals("SplineTime",true)
                    ||currentChartType.equals("AreaTime",true)
                    ||currentChartType.equals("AreaSplineTime",true)
                    ||currentChartType.equals("LineTime",true)
                    ||currentChartType.equals("DotMap",true)
                    ||currentChartType.equals("ColumnTime",true)){

                val manager = InsightsViewsManager()
                val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "")
                val deciveId= AppPreferences.getKeyValue(Constants.PrefCode.DEVICE_ID,"").toString()
                val token= AppPreferences.getKeyValue(Constants.PrefCode.USER_TOKEN,"").toString()

                var filtersValue = JSONArray()

                filtersValue = getWebViewFilterValue(charParams.dataViewId)

                slicerWvFltrMap[charParams.dataViewId] = filtersValue.toString()
                manager.setChartWebView(requireContext(),chartViewList[currentDataViewIndex],
                        charParams,baseUrl,token,currentChartType,
                        deciveId,filtersValue.toString(),false)

                insightDtlList[chartViewList[currentDataViewIndex].tag as Int] = InsightDetailData(true,charParams)
            }

            else {
                val manager = ChartsManager()
                manager.setChart(chartViewList[currentDataViewIndex], charParams)
                insightDtlList[chartViewList[currentDataViewIndex].tag as Int] = InsightDetailData(true,charParams)
            }


        }else {
            insightDtlList[chartViewList[currentDataViewIndex].tag as Int] = InsightDetailData(false,null
            )

            charDataSupportedMap[currentDataViewIndex] =false
        }
    }

    private fun getWebViewFilterValue(dataViewId : String): JSONArray {
        val filtersValueArr = JSONArray()
        for (t in 0 until slicerActionArr.length()) {
            val currObject: JSONObject = slicerActionArr.optJSONObject(t)
            if (currObject.optString("targetDataViewId").equals(dataViewId, ignoreCase = true)) {
                val sourceId = currObject.optString("sourceDataViewId")
                if (slicerFltrHashMap.containsKey(sourceId)) {
                    val fltrObj: JSONObject? = slicerFltrHashMap.get(sourceId)
                    if (fltrObj?.optJSONArray("value")!!.length() > 0) {
                        try {
                            fltrObj?.put("columnId", currObject.optJSONArray("columnMapping").optJSONObject(0).optString("targetColumn"))
                            if (fltrObj?.optString("columnType") == "") {
                                fltrObj?.put("columnType", currObject.optJSONArray("columnMapping").optJSONObject(0).optString("targetColumnType"))
                                fltrObj?.put("operator", "in")
                                fltrObj?.put("source", "actions")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                        filtersValueArr.put(fltrObj)
                    }
                }
            }
        }
        return filtersValueArr

    }

    private fun editInfoResponseParsing(jObj: JSONObject) {
        val dataObj = jObj.optJSONObject("data")


        val  jsonObj = hashMapDataViewDtl[currentDataViewIndex.toString()]
        val dataViewJson  = JSONObject()
        dataViewJson.put("dataViewJson",jsonObj)
        if(dataObj.has("calculatedMeasures")){
            dataViewJson.put("calculatedMeasures",dataObj.optJSONArray("calculatedMeasures"))
        }
        if(currentChartType.equals("Bubble",true)||
                currentChartType.equals("Heatmap",true)){
            dataViewJson.put("parseData",true)
        }

        val chartinfoObj = JSONObject(chartinfo)
        val actionArr = chartinfoObj.optJSONArray("actions") ?: JSONArray()
        slicerActionArr =actionArr

        val currentChartId = dataViewIdList[currentDataViewIndex]


        for(t in 0 until actionArr.length()){
            val currObject = actionArr.optJSONObject(t)
            if(currObject.optString("targetDataViewId").equals(currentChartId)){
                val sourceId = currObject.optString("sourceDataViewId")
                if(slicerFltrHashMap.containsKey(sourceId)){
                    val fltrObj = slicerFltrHashMap[sourceId]
                    if (fltrObj!!.optJSONArray("value").length() > 0) {
                        fltrObj!!.put("columnId", currObject.optJSONArray("columnMapping").optJSONObject(0).optString("targetColumn"))

                        if (fltrObj?.optString("columnType") == "") {
                            fltrObj?.put("columnType", currObject.optJSONArray("columnMapping").optJSONObject(0).optString("targetColumnType"))
                            fltrObj?.put("operator", "in")
                            fltrObj?.put("source", "actions")
                        }

                        jsonObj!!.optJSONObject("dataSource").optJSONArray("filters").put(fltrObj)

                    }
                }

            }



        }
        val payLoadObj: JsonObject = Gson().fromJson(dataViewJson.toString(), JsonObject::class.java)

        currentPayLoad = payLoadObj

    }

    private fun dataViewResponseParsing(jsonObj: JSONObject) {
        currentValueIdList.clear()


        hashMapDataViewDtl[currentDataViewIndex.toString()] = jsonObj

        val filtersArr = jsonObj.optJSONObject("visualizations")?.optJSONArray("filters")?: JSONArray()
        currentChartType = jsonObj.optJSONObject("visualizations")?.optString("chartType") ?: ""
        currentAxisId = jsonObj.optString("axisId")

        val isSlicerChart = currentChartType.equals("RadioSlicer", true)
                || currentChartType.equals("ComboSlicer", true)
                || currentChartType.equals("CheckSlicer", true)
                || currentChartType.equals("DateRangeSlicer", true)
                || currentChartType.equals("TagSlicer", true)

        if(isNavViaSearch && isSlicerChart){
            chartViewList[currentDataViewIndex].visibility = View.GONE
        }


        if (currentChartType.equals("Pie",true)
                || currentChartType.equals("Pie3D",true)
                ||currentChartType.equals("Donut",true)
                ||currentChartType.equals("Donut3D",true)

        ){
            currentAxisId = jsonObj.optString("sliceId")
        }

        for(k in 0 until filtersArr.length()){
            val obj  = filtersArr.optJSONObject(k)
            val configZone = obj.optString("configZone")
            if(configZone.equals("values",true)){
                currentValueIdList.add(obj.optString("columnId"))
            }

        }

        currentCollectionId = jsonObj.optJSONObject("dataSource")?.optString("collectionId")?:""
    }

    private fun enableChartOrErrorView(custErroMsg : String = "Unable to Render Chart"){
        charDataSupportedMap.forEach { (t, u) ->

            if(!u){
                val textView = TextView(requireContext())
                textView.text = custErroMsg//"Unable to Render Chart"
                textView.setTextColor(resources.getColor(R.color.black))
                textView.background = requireContext().resources.getDrawable(R.drawable.error_chart_bg)
                val paramsR = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)

                paramsR.addRule(RelativeLayout.CENTER_HORIZONTAL)
                paramsR.addRule(RelativeLayout.CENTER_VERTICAL)
                textView.layoutParams = paramsR
                textView.gravity = Gravity.CENTER

                chartViewList[t].addView(textView, paramsR)
            }


        }
    }

    fun newInstance( chartinfo: String?,isNavViaSearch :Boolean): InsightChartFragment? {
        val fragmentFirst = InsightChartFragment()
        val args = Bundle()
        args.putString("chartInfo", chartinfo)
        args.putBoolean("navViaSearch", isNavViaSearch)
        fragmentFirst.setArguments(args)
        return fragmentFirst
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chartinfo = requireArguments().getString("chartInfo")
        isNavViaSearch = requireArguments().getBoolean("navViaSearch")

    }

    fun onSlicerClicked(){
        try {
        slicerDataList.clear()
        currentSlicerDataViewIndex = 0
        mViewModel.getSlicerDataViewMap(slicerId)
        }catch (e : Exception){

        }
    }

    fun downloadInsight(){
        try {
            var bitmap = getBitmapFromView(
                mBinding.parentChartLay,
                mBinding.parentChartLay.getChildAt(0).getHeight(),
                mBinding.parentChartLay.getChildAt(0).getWidth()
            )

            createPdfFile(bitmap)
        }catch (e : Exception){
             e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        try {


            chartViewList.clear()
            dataViewIdList.clear()
            slicerViewId.clear()
            mBinding.mainLlLay.removeAllViews()
            val jsonObj = JSONObject(chartinfo)
            val name = jsonObj.optString("name")
            val chartType = jsonObj.optString("chartType")
            val selDataViewIds = jsonObj.optJSONArray("selectedDataviewIds")

            if(isNavViaSearch){
                for(i in 0 until selDataViewIds.length()){
                    dataViewIdList.add(i, selDataViewIds[i].toString())
                    addChartsToView(i)
                }
            }else{
            val dataViews = jsonObj.optJSONObject("contents").optJSONArray("dataviews")

                for(i in 0 until dataViews.length()){
                    if(dataViews.optJSONObject(i).has("default")){
                        slicerFltrHashMap.put(dataViews.optJSONObject(i).optString("dataViewId"),
                        dataViews.optJSONObject(i).optJSONObject("default"))
                    }
                }



            slicerId = jsonObj.optInt("_id").toString()

            for (i in 0 until dataViews.length()) {
                val dataViewId = dataViews.optJSONObject(i).optString("dataViewId")
                val chartType = dataViews.optJSONObject(i).optString("chartType")

                if (chartType.equals("RadioSlicer", true)
                        || chartType.equals("ComboSlicer", true)
                        || chartType.equals("CheckSlicer", true)
                        || chartType.equals("DateRangeSlicer", true)
                        || chartType.equals("TagSlicer", true)) {
                    slicerViewId.add(dataViewId)

                } else {

                    var index = dataViewIdList.size

                    dataViewIdList.add(index, dataViewId)


                    addChartsToView(index)
                }


            }
            }



            if (dataViewIdList.isNotEmpty()) {
                currentDataViewIndex = 0

              //  checkIfDataIsPresentinCache(dataViewIdList[0])

                mViewModel.getInsightDataView(dataViewIdList[0])
            }
        }catch (e : java.lang.Exception){

        }

    }

    private fun checkIfDataIsPresentinCache(dataViewId : String){
       val cacheStr = AppPreferences.getKeyValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                "")
        var isFound = false
        if(cacheStr!!.isNotEmpty()){
            val cacheArray = JSONArray(cacheStr)
            for(i in 0 until cacheArray.length()){
                val cacheObj = cacheArray.getJSONObject(i)
                val dataId = cacheObj.optString("id")
                if(dataId.equals(dataViewId)){

                    isFound = true

                    val dataViewResObj = cacheObj.optJSONObject("dataviewResponse")
                    dataViewResponseParsing(dataViewResObj)


                    val coulumnMappingObj = cacheObj.optJSONObject("columnMapping")
                    currentColumnMapDataJson = coulumnMappingObj
                    currentXAxisTitle = coulumnMappingObj.optString(currentAxisId)
                    currentYAxisTitle = coulumnMappingObj.optString(currentYAxisTitle)


                    val editingResObj = cacheObj.optJSONObject("editingRes")
                    editInfoResponseParsing(editingResObj)


                    val visuObj = cacheObj.optJSONObject("visuResponse")

                    visualizeResponseParsing(visuObj)

                    if(currentDataViewIndex+1 ==dataViewIdList.size){
                        enableChartOrErrorView()

                    }else{
                        currentDataViewIndex++
                        checkIfDataIsPresentinCache(dataViewIdList[currentDataViewIndex])
                    }

                }
            }

            if(!isFound){
                mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
            }



        }else {
            mViewModel.getInsightDataView(dataViewIdList[currentDataViewIndex])
        }



    }

    private fun addChartsToView(i : Int){
        val chartView = HIChartView(requireContext())

        progressBar = ProgressBar(requireContext(), null, android.R.attr.progressBarStyleLarge)
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        val paramsT = RelativeLayout.LayoutParams(100, 100)
        paramsT.addRule(RelativeLayout.CENTER_IN_PARENT)
        chartView.addView(progressBar, paramsT)
        pbViewList.add(i,progressBar)

        chartView.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.white))
        val r: Resources = resources
        val height = Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 400f, r.getDisplayMetrics()))
        val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height)
        params.setMargins(0, 10, 0, 10)
        chartView.layoutParams = params
        chartViewList.add(i,chartView)
        chartView.setTag(i)

        val relativeLayout = RelativeLayout(requireContext())
        val linearLayout = RelativeLayout(requireContext())
        relativeLayout.layoutParams = params
        linearLayout.layoutParams = params
        linearLayout.setTag(i)
        relativeLayout.addView(chartView)
        relativeLayout.addView(linearLayout)

        linearLayout.setOnClickListener {
            chartViewClickListener(it)
        }

        mBinding.mainLlLay.addView(relativeLayout)
    }

    private fun chartViewClickListener(it: View){
        val tag = it.tag
        if(insightDtlList.containsKey(tag as Int)){

            val chartParams = insightDtlList[tag as Int]?.chartParam
            chartParams.apply {
                this?.dataJsonStr = this?.dataJson.toString()
                this?.columnMapDataJsonStr = this?.columnMapDataJson.toString()
                this?.dataViewJsonStr = this?.dataViewJson.toString()
            }
            val gson = Gson()
            val json = gson.toJson(chartParams)
            AppPreferences.saveValue(Constants.PrefCode.INSIGHT_DTL_DATA,json);
            var appliedFltrVal = ""
            if(slicerWvFltrMap.containsKey(chartParams?.dataViewId)){
                appliedFltrVal = slicerWvFltrMap[chartParams?.dataViewId]!!
            }

            AppPreferences.saveValue(Constants.PrefCode.WBVIEW_APPLIED_FLTR,appliedFltrVal)

            if(chartParams !=null){
                findNavController().navigate(R.id.action_appDetailFrag_to_insightDetailFrag)
            }

/*            var bitmap = getBitmapFromView(
                mBinding.parentChartLay,
                mBinding.parentChartLay.getChildAt(0).getHeight(),
                mBinding.parentChartLay.getChildAt(0).getWidth()
            )

            createPdfFile(bitmap)*/
        }


    }


    override fun onStop() {
        super.onStop()
        ProgressDialogUtil.hideProgressDialog()
    }


    private fun showInsightSelectFilter(slicerLastSeleValue :HashMap<String,ArrayList<String>>) {
        lateinit var slicerRecyclerView: RecyclerView

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.slicer_view_lay)

        dialog.findViewById<ImageView>(R.id.close_icon).setOnClickListener {
            dialog.dismiss()
        }

        slicerRecyclerView = dialog.findViewById(R.id.slicer_rv)
        slicerRecyclerView.layoutManager = LinearLayoutManager(slicerRecyclerView.context)
        slicerRecyclerView.setHasFixedSize(true)

        val chartInfoObj = JSONObject(chartinfo)
        var adapter = SlicerScreenAdapter(requireContext(),chartInfoObj, dataViewNameMapJSON,slicerDataList,
            slicerLastSeleValue,slicerFltrHashMap)
        slicerRecyclerView.adapter = adapter

        val applyTv = dialog.findViewById<TextView>(R.id.apply_tv)
        val resetTv = dialog.findViewById<TextView>(R.id.reset_tv)

        resetTv.setOnClickListener {
            slicerFltrHashMap.clear()
            slicerLastSeleValueMap.clear()
            dialog.dismiss()
            reloadChartsWithSlicer()
        }

        applyTv.setOnClickListener {
            if(adapter.checkForDateRangeValidError()){
                Toast.makeText(context,"Please select values",Toast.LENGTH_SHORT).show()
            }else {
                slicerFltrHashMap = adapter.getSlicerFltrData()
                slicerLastSeleValueMap = adapter.getLastSelectedValues()
                dialog.dismiss()
                reloadChartsWithSlicer()
            }


        }
        dialog.show()
    }
    private fun reloadChartsWithSlicer() {
         currentDataViewIndex = 0
        currentSlicerDataViewIndex = 0
        chartViewList.clear()
        dataViewIdList.clear()
        slicerViewId.clear()
        mBinding.mainLlLay.removeAllViews()
        charDataSupportedMap.clear()
        val jsonObj = JSONObject(chartinfo)
        val dataViews = jsonObj.optJSONObject("contents").optJSONArray("dataviews")

        slicerId = jsonObj.optInt("_id").toString()

        for (i in 0 until dataViews.length()) {
            val dataViewId = dataViews.optJSONObject(i).optString("dataViewId")
            val chartType = dataViews.optJSONObject(i).optString("chartType")

            if (chartType.equals("RadioSlicer", true)
                    || chartType.equals("ComboSlicer", true)
                    || chartType.equals("CheckSlicer", true)

                || chartType.equals("DateRangeSlicer", true)
                || chartType.equals("TagSlicer", true)) {
                slicerViewId.add(dataViewId)
            } else {

                var index = dataViewIdList.size

                dataViewIdList.add(index, dataViewId)

                addChartsToView(index)
            }

        }
        if(dataViewIdList.isNotEmpty()){
            currentDataViewIndex = 0
            mViewModel.getInsightDataView(dataViewIdList[0])
        }
    }

    //create bitmap from the ScrollView
    private fun getBitmapFromView(view: View, height: Int, width: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val bgDrawable = view.background
        if (bgDrawable != null) bgDrawable.draw(canvas) else canvas.drawColor(Color.WHITE)
        view.draw(canvas)
        return bitmap
    }




    fun createPdfFile(bitmap1: Bitmap) {
        val pdfFile = File(
            requireContext().externalCacheDir!!.absolutePath + File.separator.toString() + "Insight_" + System.currentTimeMillis()
                .toString() + ".pdf"
        )
        object : AsyncTask<Void?, Void?, Void?>() {

            override fun doInBackground(vararg p0: Void?): Void? {
                val document = PdfDocument()
                try {

                    val bitmap = bitmap1
                    val pageInfo = PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                    val page = document.startPage(pageInfo)
                    val canvas = page.canvas
                    val paint = Paint()
                    paint.setColor(Color.parseColor("#ffffff"))
                    canvas.drawPaint(paint)
                    canvas.drawBitmap(bitmap, 0f, 0f, null)
                    document.finishPage(page)

                    document.writeTo(FileOutputStream(pdfFile))
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    document.close()
                }
                return null
            }
            override fun onPostExecute(unused: Void?) {
                super.onPostExecute(unused)
                if (pdfFile.exists() && pdfFile.length() > 0) {
                    openFile(
                        context,
                        pdfFile.absolutePath
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Something went wrong creating the PDF :(",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }.execute()

    }


    fun openFile(context: Context?, filepath: String?): Boolean {
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Uri.fromFile(File(filepath))
        } else {
            FileProvider.getUriForFile(requireContext(), "com.eka.CACApp.fileprovider", File(filepath))


        }
        return openUriForShare(requireContext(), uri)
    }

    fun openUri(context: Context, uri: Uri?): Boolean {
        return openUri(context, getOpenIntent(uri, context.contentResolver.getType(uri!!)))
    }

    fun openUri(context: Context, intent: Intent?): Boolean {
        try {
            context.startActivity(intent)
            return true
        } catch (e: Throwable) {

        }
        return false
    }


    fun getOpenIntent(url: Uri?, type: String?): Intent? {
        return Intent(Intent.ACTION_VIEW).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .setDataAndType(url, type)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        onResume()
    }


    fun openUriForShare(context: Context, uri: Uri?): Boolean {
        return openUri(context, sharePdf(uri, context.contentResolver.getType(uri!!)))
    }

    fun sharePdf(url: Uri?, type: String?): Intent? {
        return Intent(Intent.ACTION_SEND).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .putExtra(Intent.EXTRA_STREAM,url)
            .setType("application/pdf")
    }

}