package com.eka.cacapp.ui.farmerConnect

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.eka.cacapp.R
import com.eka.cacapp.adapter.FcBasicFltrAdapter
import com.eka.cacapp.adapter.InsightBasicFltrAdapter
import com.eka.cacapp.data.farmconnect.FCSortData
import com.eka.cacapp.data.insight.AdvanceFltrData
import com.eka.cacapp.data.insight.BasicFltrData
import com.eka.cacapp.data.insight.InsightFilterHelper
import com.eka.cacapp.network.RequestParams
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.FarmerConnectUtils.PUBLISED_PRICE_TAB
import com.eka.cacapp.utils.FarmerConnectUtils.getFcSortFields
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class FarmerConnectFilter(
    mContext: Context, themeId: Int,
    filterSelcListener : FilterSelectListener,
     hashMapIsRowCheck : HashMap<String,Boolean>,
    hashMapIsRowValuesCheck : HashMap<String,ArrayList<String>>,
     advaceFltrData : AdvanceFltrData,
     activeTab : String,
    hMapAdvanceFltrData : HashMap<String,AdvanceFltrData>
): Dialog(mContext,themeId) {

    //for filter
    private  var hashMapIsRowChecked = HashMap<String,Boolean>()
    private  var hashMapAdvanceFltrData = HashMap<String,AdvanceFltrData>()
    private  var hashMapIsRowValuesChecked = HashMap<String,ArrayList<String>>()
    private  var selectedBasicFlterValues = ArrayList<String>()
    private lateinit var advaceFltData : AdvanceFltrData

    lateinit var filterListView: ListView
    var currentView = "Basic"
    var mainOperVal ="and"
    lateinit var selectedFltrData : FCSortData
    var seletedPos = -1
    private lateinit var tabLayout : TabLayout
    private lateinit var basicLay : LinearLayout
    private lateinit var basicFltView : ScrollView
    private lateinit var advanceView : LinearLayout

    private lateinit var andOperView : TextView
    private lateinit var OrOperView : TextView

    private lateinit var valueOneEditText : EditText
    private lateinit var valueTwoEditText : EditText
    private lateinit var spinnerOperOne : Spinner
    private lateinit var spinnerOperTwo : Spinner

    private lateinit var title : TextView
    private lateinit var backImg : ImageView
    private lateinit var subTtile : TextView
    private lateinit var spinnerOneLock : ImageView
    private lateinit var spinnerTwoLock : ImageView
    private lateinit var filterTwoView : LinearLayout
    private lateinit var filterOperView : LinearLayout
    private val filterSelectListener : FilterSelectListener
    private var sortFilterList : ArrayList<FCSortData>  = ArrayList()
    private var selectedTab =""

    init {
        setCancelable(false)
        sortFilterList =  getFcSortFields()
        filterSelectListener = filterSelcListener
        hashMapIsRowChecked = hashMapIsRowCheck
        hashMapIsRowValuesChecked = hashMapIsRowValuesCheck
        advaceFltData  = advaceFltrData
        selectedTab = activeTab
        hashMapAdvanceFltrData =hMapAdvanceFltrData



        //getStoredFilterData()


    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fc_filter_scrn)


        try {


            title =  findViewById<TextView>(R.id.ttl)
            backImg =  findViewById<ImageView>(R.id.close_icon)
            subTtile =  findViewById<TextView>(R.id.actions_txt)

            tabLayout =  findViewById<TabLayout>(R.id.tabLayout)
            basicLay =  findViewById<LinearLayout>(R.id.basic_dyn_ll)
            basicFltView =  findViewById<ScrollView>(R.id.basic_view)
            advanceView =  findViewById<LinearLayout>(R.id.advance_view)

            andOperView =  findViewById<TextView>(R.id.and_op_txt);
            OrOperView =  findViewById<TextView>(R.id.or_op_txt);

            valueOneEditText =  findViewById<EditText>(R.id.value_one)
            valueTwoEditText =  findViewById<EditText>(R.id.value_two)

            spinnerOneLock =  findViewById(R.id.spiner_one_lock)
            spinnerTwoLock =  findViewById(R.id.spiner_two_lock)
            filterOperView =  findViewById(R.id.filter_opertor_view)
            filterTwoView =  findViewById(R.id.filer_two_view)


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


            spinnerOperOne =  findViewById(R.id.oper_one)

            spinnerOperTwo =  findViewById<Spinner>(R.id.oper_two)
            filterListView =  findViewById(R.id.slicer_rv)

            backImg.tag = 1
            backImg.setOnClickListener {
                if (backImg.tag == 1) {
                    this.dismiss()
                } else {
                    showFilterListView()
                }

            }


            var listData = ArrayList<BasicFltrData>();
            for (i in 0 until sortFilterList.size) {

                var isChecked = false
                if (hashMapIsRowChecked.containsKey(sortFilterList[i].columnId)) {
                    isChecked = true
                }
                listData.add(BasicFltrData(sortFilterList[i].columnName, isChecked))
            }


            val listApapter = FcBasicFltrAdapter(context, listData)

            filterListView.adapter = listApapter

            filterListView.setOnItemClickListener { adapterView, view, position: Int, id: Long ->

                selectedFltrData = sortFilterList[position]
                title.setText(sortFilterList[position].columnName.toString())
                backImg.tag = 2
                seletedPos = position
                currentView = "Basic"

                if (hashMapIsRowValuesChecked.containsKey(selectedFltrData.columnId)) {

                }
                basicLay.removeAllViews()
                selectedBasicFlterValues.clear()

                tabLayout.getTabAt(0)!!.select()


                val advanceFltrValues =
                    insightFilterHelper.getOperatorDisplayValues(selectedFltrData.columType)


                val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                    context,
                    android.R.layout.simple_spinner_item, advanceFltrValues
                )
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view

                spinnerOperOne.adapter = spinnerArrayAdapter
                spinnerOperTwo.adapter = spinnerArrayAdapter

                spinnerClickListener()

                valueOneEditText.resetFltrFieldToDefault()
                valueTwoEditText.resetFltrFieldToDefault()

                val columType = selectedFltrData.columType
                if (columType == -1 || columType == 2 || columType == 5) {
                    valueOneEditText.setInputType(InputType.TYPE_CLASS_NUMBER)
                    valueTwoEditText.setInputType(InputType.TYPE_CLASS_NUMBER)
                } else if (columType == 1) {
                    valueOneEditText.setInputType(InputType.TYPE_CLASS_TEXT)
                    valueTwoEditText.setInputType(InputType.TYPE_CLASS_TEXT)
                } else if (columType == 3) {
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


                getPublishBidColumnValues(selectedFltrData.columnId)
                subTtile.visibility = View.GONE
                tabLayout.visibility = View.VISIBLE
                filterListView.visibility = View.GONE
                backImg.setImageDrawable(context.getDrawable(R.drawable.ic_arrow_back_gray))
                basicFltView.visibility = View.VISIBLE

            }





            val applyTv = findViewById<TextView>(R.id.apply_tv)
            val resetTv = findViewById<TextView>(R.id.reset_tv)

            resetTv.setOnClickListener {

                hashMapIsRowValuesChecked.clear()
                hashMapIsRowChecked.clear()
                hashMapAdvanceFltrData.clear()

                advaceFltData = AdvanceFltrData(
                    0, 0, "and",
                    "", "", false, ""
                )

                storeFilterData(hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData)

                this.dismiss()
                val payLoadObj = JsonArray()
                filterSelectListener.onBasicFilterSelected(payLoadObj,
                        hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData,hashMapAdvanceFltrData)


            }

            applyTv.setOnClickListener {


                if (backImg.tag == 1) {

                    if (currentView.equals("Basic", true)) {

                        val payLoadObj = getBasicPayLoadObj()




                        storeFilterData(hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData)

                        filterSelectListener.onBasicFilterSelected(payLoadObj,hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData,hashMapAdvanceFltrData)
                        this.dismiss()


                    } else {

                        val payLoadObj = getAdvancePayLoadObj(insightFilterHelper)
                        storeFilterData(hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData)
                        filterSelectListener.onBasicFilterSelected(payLoadObj,hashMapIsRowChecked,hashMapIsRowValuesChecked,advaceFltData,hashMapAdvanceFltrData)
                        this.dismiss()
                    }


                } else {

                    if (currentView.equals("Basic", true)) {

                        if (selectedBasicFlterValues.isNotEmpty()) {


                            var arrList = ArrayList<String>()
                            arrList.addAll(selectedBasicFlterValues)
                            hashMapIsRowValuesChecked[selectedFltrData.columnId] = arrList

                            hashMapIsRowChecked[selectedFltrData.columnId] = true
                            listData[seletedPos].apply {
                                this.isChecked = true
                            }

                        } else {
                            if (hashMapIsRowValuesChecked.containsKey(selectedFltrData.columnId)) {
                                hashMapIsRowValuesChecked.remove(selectedFltrData.columnId)
                                hashMapIsRowChecked.remove(selectedFltrData.columnId)
                            }

                            listData[seletedPos].apply {
                                this.isChecked = false
                            }
                        }

                        val listApapter = InsightBasicFltrAdapter(context, listData)

                        filterListView.adapter = listApapter

                        showFilterListView()
                    } else {


                        val fistOperVal = spinnerOperOne.selectedItemPosition
                        val secondOperVal = spinnerOperTwo.selectedItemPosition
                        val mainOper = mainOperVal
                        val valueOne = valueOneEditText.text.toString().trim()
                        val valueTwo = valueTwoEditText.text.toString().trim()

                        if (fistOperVal != 0 || secondOperVal != 0 || valueOne.isNotEmpty() || valueTwo.isNotEmpty()) {
                            hashMapIsRowChecked[selectedFltrData.columnId] = true
                            listData[seletedPos].apply {
                                this.isChecked = true
                            }
                            advaceFltData = AdvanceFltrData(
                                fistOperVal, secondOperVal, mainOper,
                                valueOne, valueTwo, true, selectedFltrData.columnId
                            )
                            hashMapAdvanceFltrData[selectedFltrData.columnId] = advaceFltData
                        } else {
                            if (hashMapIsRowChecked.containsKey(selectedFltrData.columnId)) {
                                hashMapIsRowChecked.remove(selectedFltrData.columnId)
                                listData[seletedPos].apply {
                                    this.isChecked = false
                                }
                                advaceFltData = AdvanceFltrData(
                                    fistOperVal, secondOperVal, mainOper,
                                    valueOne, valueTwo, false, selectedFltrData.columnId
                                )
                                hashMapAdvanceFltrData[selectedFltrData.columnId] = advaceFltData
                            }
                        }


                        val listApapter = FcBasicFltrAdapter(context, listData)

                        filterListView.adapter = listApapter

                        showFilterListView()

                    }

                }


            }


            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    if (tab.position == 0) {
                        basicFltView.visibility = View.VISIBLE
                        advanceView.visibility = View.GONE
                        currentView = "Basic"
                    } else if (tab.position == 1) {

                        if(hashMapAdvanceFltrData.containsKey(selectedFltrData.columnId)){
                            advaceFltData= hashMapAdvanceFltrData[selectedFltrData.columnId]!!
                        }

                        if (::advaceFltData.isInitialized) {

                            if (advaceFltData.isDataSet &&
                                selectedFltrData.columnId.equals(advaceFltData.columId, true)
                            ) {

                                spinnerOperOne.setSelection(advaceFltData.firstOperator)
                                spinnerOperTwo.setSelection(advaceFltData.secondOperator)

                                valueOneEditText.setText(advaceFltData.firstInput)
                                valueTwoEditText.setText(advaceFltData.SecondInput)

                                if (advaceFltData.mainOperator.equals("or")) {
                                    filterOrOperClicked()
                                } else {
                                    filterAndOperClicked()
                                }
                            } else {

                                clearAdvanceFiltterView()

                            }


                        } else {

                            clearAdvanceFiltterView()
                        }


                        advanceView.visibility = View.VISIBLE
                        basicFltView.visibility = View.GONE
                        currentView = "Advance"
                    }

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })




        }catch ( e :Exception){

        }
    }




        private fun getAdvancePayLoadObj(insightFilterHelper : InsightFilterHelper) : JsonArray {

            val filterArr = JsonArray()


            val firstFilter = JsonObject();
            firstFilter.addProperty("columnId", selectedFltrData.columnId)
            firstFilter.addProperty("columnName", selectedFltrData.columnName)
            firstFilter.addProperty("columnType", selectedFltrData.columType)

            if(selectedFltrData.columType ==3 ){
                firstFilter.addProperty("dateFormat", "yyyy-MM-dd'T'HH:mm:ss")
            }

            firstFilter.addProperty("operator",
                insightFilterHelper.getOperatorSelectedValue(selectedFltrData.columType,advaceFltData.firstOperator))

            val valuesArr = JsonArray();
            valuesArr.add(advaceFltData.firstInput)
            firstFilter.add("value", valuesArr)



            val secondFilter = JsonObject();
            secondFilter.addProperty("columnId", selectedFltrData.columnId)
            secondFilter.addProperty("columnName", selectedFltrData.columnName)
            secondFilter.addProperty("columnType", selectedFltrData.columType)

            if(selectedFltrData.columType ==3 ){
                secondFilter.addProperty("dateFormat", "yyyy-MM-dd'T'HH:mm:ss")
            }

            secondFilter.addProperty("operator",
                insightFilterHelper.getOperatorSelectedValue(selectedFltrData.columType,advaceFltData.secondOperator))

            val valuesArr1 = JsonArray();
            valuesArr1.add(advaceFltData.SecondInput)
            secondFilter.add("value", valuesArr1)



            val filterObj = JsonObject();
            filterObj.addProperty("columnId", selectedFltrData.columnId)
            filterObj.addProperty("columnName", selectedFltrData.columnName)
            filterObj.addProperty("columnType", selectedFltrData.columType)
            filterObj.addProperty("type", "advanced")
            filterObj.addProperty("logicalOperator", advaceFltData.mainOperator)
            filterObj.add("firstFilter",firstFilter)

            if(advaceFltData.SecondInput.isNotEmpty()){
                filterObj.add("secondFilter",secondFilter)
            }


            filterArr.add(filterObj)


            return filterArr

        }

        private fun getBasicPayLoadObj() : JsonArray {

            val filterArr = JsonArray()

            hashMapIsRowValuesChecked.forEach { (t, u) ->

                val columId = t
                lateinit var selectedInshtFltrData: FCSortData
                for (i in sortFilterList.indices) {
                    if (columId.equals(sortFilterList[i].columnId, true)) {
                        selectedInshtFltrData = sortFilterList[i]
                    }
                }
                val filterObj = JsonObject();
                filterObj.addProperty("columnId", selectedInshtFltrData.columnId)
                filterObj.addProperty("columnName", selectedInshtFltrData.columnName)
                filterObj.addProperty("columnType", selectedInshtFltrData.columType)
                filterObj.addProperty("type", "basic")
                filterObj.addProperty("operator", "in")

                val selValues = u
                val valuesArr = JsonArray();
                for (i in selValues.indices) {
                    valuesArr.add(selValues[i])
                }
                filterObj.add("value", valuesArr)
                filterArr.add(filterObj)


            }

            return filterArr
        }



        private fun showFilterListView() {
            title.setText("Filters")
            backImg.tag =1
            tabLayout.visibility = View.GONE
            basicFltView.visibility = View.GONE
            advanceView.visibility = View.GONE
            subTtile.visibility= View.VISIBLE
            filterListView.visibility = View.VISIBLE
            backImg.setImageDrawable(context.getDrawable(R.drawable.ic_close))
        }

        private fun filterOrOperClicked() {
            mainOperVal ="or"
            OrOperView.background = ContextCompat.getDrawable(context,R.drawable.selected_op_bg)
            OrOperView.setTextColor(ContextCompat.getColor(context,R.color.white))

            andOperView.background = ContextCompat.getDrawable(context,R.drawable.un_selected_op_bg)
            andOperView.setTextColor(ContextCompat.getColor(context,R.color.black))
        }

        private fun filterAndOperClicked() {
            mainOperVal = "and"
            andOperView.background = ContextCompat.getDrawable(context,R.drawable.selected_op_bg)
            andOperView.setTextColor(ContextCompat.getColor(context,R.color.white))

            OrOperView.background = ContextCompat.getDrawable(context,R.drawable.un_selected_op_bg)
            OrOperView.setTextColor(ContextCompat.getColor(context,R.color.black))
        }


        fun openDatePickerDialog(v: View) {
            val cal = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(context,
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

    fun getRestClient( context : Context) : RestClient {
        val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL, "").toString();
        return RestClient.getInstance(
            context, baseUrl)
    }

    fun getPublishBidColumnValues(columnId : String){
        val headers = RequestParams.getPostLoginHeaderParams()
        headers[Constants.RequestParamCode.X_LOCALE] = AppUtil.getLangCode()
        headers[Constants.RequestParamCode.X_TENANTID] = AppUtil.getXTenanatId()

        var apiInterface : Call<ResponseBody>
        if(selectedTab.equals(PUBLISED_PRICE_TAB)){
             apiInterface = getRestClient(context)
                    .getApiService().getPublishedBidColumnsNormal(
                            headers,columnId)
        }else {
             apiInterface = getRestClient(context)
                    .getApiService().getFcBidsColumnsNormal(
                            headers,columnId)
        }

            apiInterface.enqueue( object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {

                val result = response?.body()?.string()


                addColumnsToBasicFltr(result?:"")

            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {

            }
        })
    }

    private fun addColumnsToBasicFltr(result :String) {

        try {
            var preSeletedList = ArrayList<String>()
            if (hashMapIsRowValuesChecked.containsKey(selectedFltrData.columnId)) {

                preSeletedList =
                        hashMapIsRowValuesChecked[selectedFltrData.columnId]!!
            }

            val dataArr = JSONArray(result)
            for (i in 0 until dataArr.length()) {

                val name = dataArr.opt(i).toString()
                val cb = CheckBox(context)

                val params = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        context.getResources().getDimensionPixelSize(R.dimen.cb_height_ins_dtl_fltr)
                )

                cb.layoutParams = params
                cb.background = ContextCompat.getDrawable(
                        context,
                        R.drawable.cb_basic_flr_bg
                );
                cb.text = name

                if (preSeletedList.contains(name)) {
                    cb.isChecked = true
                    selectedBasicFlterValues.add(cb.text.toString())
                }


                cb.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {

                        selectedBasicFlterValues.add(cb.text.toString())

                    } else {
                        selectedBasicFlterValues.remove(cb.text.toString())

                    }
                }
                )

                basicLay.addView(cb)
            }

        } catch (e: java.lang.Exception) {

        }
    }

    private fun saveMap(inputMap: Map<String, Any>, context: Context,mapKey : String) {
        val pSharedPref: SharedPreferences = context.getApplicationContext().getSharedPreferences("",
                Context.MODE_PRIVATE)
        if (pSharedPref != null) {
            val jsonObject = JSONObject(inputMap)
            val jsonString: String = jsonObject.toString()
            val editor = pSharedPref.edit()
            editor.remove(mapKey).apply()
            editor.putString(mapKey, jsonString)
            editor.commit()
        }
    }

    private fun loadMap(context: Context,mapKey : String): Map<String, Any>? {
        val outputMap: MutableMap<String, Any> = HashMap()
        val pSharedPref: SharedPreferences = context.getApplicationContext().getSharedPreferences("",
                Context.MODE_PRIVATE)
        try {
            if (pSharedPref != null) {
                val jsonString = pSharedPref.getString(mapKey, JSONObject().toString())
                val jsonObject = JSONObject(jsonString)
                val keysItr: Iterator<String> = jsonObject.keys()
                while (keysItr.hasNext()) {
                    val key = keysItr.next()
                    outputMap[key] = jsonObject.get(key)
                }
            }
        } catch (e: java.lang.Exception) {

        }
        return outputMap
    }

    private fun loadMapData(jsonString : String): Map<String, Any>? {
        val outputMap: MutableMap<String, Any> = HashMap()

        try {
                val jsonObject = JSONObject(jsonString)
                val keysItr: Iterator<String> = jsonObject.keys()
                while (keysItr.hasNext()) {
                    val key = keysItr.next()
                    outputMap[key] = jsonObject.get(key)
                }

        } catch (e: java.lang.Exception) {

        }
        return outputMap
    }

    private fun updateValuesChecked(inputMap: Map<String, JSONArray>) :  HashMap<String,ArrayList<String>>
    {
        val updatedMap = HashMap<String,ArrayList<String>>()
        inputMap.forEach { (key, value) ->
            val listdata = ArrayList<String>()
            println("$key = $value")
            if (value != null) {
                for (i in 0 until value.length()) {
                    listdata.add(value.getString(i))
                }
                updatedMap[key] = listdata
            }

        }

        return updatedMap


    }

    private fun storeFilterData(hMapRowChecked:Map<String,Any?>,
                                valuesCheckedData :Map<String,Any?>,advanceFtlrData:AdvanceFltrData){

        val rowCheckObj = JSONObject(hMapRowChecked)
        val rowCheckString: String = rowCheckObj.toString()

        val valuesObj = JSONObject(valuesCheckedData)
        val valuesDataString: String = valuesObj.toString()
        val gson = Gson()
        val advFltData = gson.toJson(advanceFtlrData)

        val userId = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"").toString()
        val json = JSONObject()
        json.put("rowCheck",rowCheckString)
        json.put("rowValueData",valuesDataString)
        json.put("advnceFltrData",advFltData)
        json.put("filterType",selectedTab)
        json.put("userId",userId)

        val existingData: String = AppPreferences.getKeyValue("filter_data", "").toString()
        if(existingData.trim().isNotEmpty()) {
            val existingArr = JSONArray(existingData)
            var removeIndex =0
            var hasRecord = false
            for(i in 0 until existingArr.length()){
                val jsonObject = existingArr.optJSONObject(i)
                if(jsonObject.optString("userId").equals(userId)){
                    removeIndex = i
                    hasRecord = true
                }
            }
            if(hasRecord){
                existingArr.remove(removeIndex)
                existingArr.put(json)
            }else{
                if (existingArr.length() < 5) {
                    existingArr.put(json)
                }else{
                    existingArr.remove(0)
                    existingArr.put(json)
                }
            }

            AppPreferences.saveValue("filter_data", existingArr.toString())
        }else{
            val jsonArray = JSONArray()
            jsonArray.put(json)
            AppPreferences.saveValue("filter_data", jsonArray.toString())
        }


    }


    private fun getStoredFilterData(){
       val userId = AppPreferences.getKeyValue(Constants.PrefCode.USER_ID,"").toString()

        val existingData: String = AppPreferences.getKeyValue("filter_data", "").toString()
        if(existingData.trim().isNotEmpty()) {
            val existingArr = JSONArray(existingData)

            for(i in 0 until existingArr.length()){

                val jsonObject = existingArr.optJSONObject(i)
                if(jsonObject.optString("userId").equals(userId)
                        && jsonObject.optString("filterType").equals(selectedTab)){
                    hashMapIsRowChecked = loadMapData(jsonObject.optString("rowCheck")) as HashMap<String, Boolean>

                    hashMapIsRowValuesChecked =  updateValuesChecked(loadMapData(jsonObject.optString("rowValueData")) as HashMap<String, JSONArray> )
                    val gson = Gson()
                    val advFLtData = jsonObject.optString("advnceFltrData")
                    if(advFLtData.isNotEmpty()){
                        advaceFltData = gson.fromJson(advFLtData, AdvanceFltrData::class.java)
                    }

                    return
                }
            }

        }

    }
}