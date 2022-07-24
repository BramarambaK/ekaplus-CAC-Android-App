package com.eka.cacapp.adapter

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.insight.SlicerData
import com.eka.cacapp.data.qtcLayout.WorkFlowMargin
import com.eka.cacapp.databinding.SlicerListItemBinding
import com.eka.cacapp.utils.QtcSpinner
import com.eka.cacapp.utils.WorkFlowViews
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class SlicerScreenAdapter (context: Context,chartInfoObj : JSONObject,
                           dataViewNameMapJSON : JSONObject,listItems: List<SlicerData>,
                           lastSelectedValues : HashMap<String,ArrayList<String>>,
                           filterDefaultMap : HashMap<String,JSONObject>  ) : RecyclerView.Adapter<SlicerScreenAdapter.ViewHolder>()

        , DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private val categoryList: List<SlicerData> = listItems
    var onItemClick: ((SlicerData) -> Unit)? = null
    var mContext = context
    var chartInfoObj = chartInfoObj
    var dataViewNameMapJSON = dataViewNameMapJSON

    var hasMapSelectedValues : HashMap<String,ArrayList<String>> = HashMap()
    var hasMapLastSelValues =lastSelectedValues

    var hasMapSourceObj : HashMap<String,JSONObject> = HashMap()

    var defaultFltrMap : HashMap<String,JSONObject> = filterDefaultMap


    private lateinit var bottomSheetDialog: BottomSheetDialog

    private lateinit var wildCardSlicerAdapter:  WildCardSlicerAdapter

    var day = 0
    var month:Int = 0
    var year:Int = 0
    var hour:Int = 0
    var minute:Int = 0
    var selectedday = 0
    var selectedMonth:Int = 0
    var selectedYear:Int = 0
    var selectedHour:Int = 0
    var selectedMinute:Int = 0

    private lateinit var fromDateTv : TextView

    var hasMapDateRangeSelectedValues : HashMap<String,JSONObject> = HashMap()
    var hasMapDateRangeCustomViews : HashMap<String,ArrayList<LinearLayout>> = HashMap()
    var dateSlicerDelimitor   = "@@@"

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: SlicerListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.slicer_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try{
        val dataModel: SlicerData = categoryList[position]
        val dataViewJSON = dataModel.dataViewData
        hasMapSelectedValues[dataModel.chartId] = ArrayList()
        val chartName = dataViewJSON.optString("name")
        val visualizationsObj = dataViewJSON.optJSONObject("visualizations")
        val configurationObj = visualizationsObj.optJSONObject("configuration")
        val configArr = configurationObj.optJSONArray("config")
        var columnType = ""
        var columnId = ""
        for(i in 0 until configArr.length()){
            val configObj = configArr.optJSONObject(i)
            if(configObj.optString("zone").equals("column",true)){
                columnType = configObj.optJSONArray("columns").optJSONObject(0).optInt("columnType").toString()
                columnId = configObj.optJSONArray("columns").optJSONObject(0).optString("columnId").toString()
            }
        }


        var affectedCharts : StringBuilder = StringBuilder()
        val actionsArr = chartInfoObj.optJSONArray("actions")
        for(i in 0 until actionsArr.length()){
            val obj = actionsArr.optJSONObject(i)
            if(obj.optString("sourceDataViewId").equals(dataModel.chartId)){
                affectedCharts.append(dataViewNameMapJSON.optString(obj.optString("targetDataViewId")))
            }
        }


        var defaultVal = ""
        /*for(i in 0 until dataViewArr.length()){
            if(dataViewArr.optJSONObject(i).optString("dataViewId").equals(dataModel.chartId)){
                defaultVal = dataViewArr.optJSONObject(i)?.optJSONObject("default")?.optJSONArray("value")?.optString(0) ?: ""
            }
        }*/



        if(defaultFltrMap.containsKey(dataModel.chartId)){
            defaultVal = defaultFltrMap.get(dataModel.chartId)?.optJSONArray("value")?.optString(0) ?: ""
        }

        val dataArr = dataModel.visualizeData.optJSONArray("data")


        val chartType = visualizationsObj.optString("chartType")

        if(chartType.equals("CheckSlicer",true)){
            for (j in 0 until dataArr.length()) {
//                val idObj = dataArr.optJSONObject(j).optJSONObject("_id")
                val idObj = getIdObject(dataArr,j)
                val cb = CheckBox(mContext)
                cb.text =idObj.optString(columnId)
                cb.tag = dataModel.chartId
                if(hasMapLastSelValues.containsKey(dataModel.chartId)){
                    if(hasMapLastSelValues[dataModel.chartId]!!.contains(cb.text.toString())){
                        cb.isChecked = true
                        hasMapSelectedValues[dataModel.chartId] = hasMapLastSelValues[dataModel.chartId]!!
                    }
                }
               else if(defaultVal.equals(idObj.optString(columnId))){
                    cb.isChecked = true
                    var existingList = hasMapSelectedValues[dataModel.chartId]
                    existingList?.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList!!
                }

                cb.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked){
                        var existingList = hasMapSelectedValues[dataModel.chartId]
                        if(!existingList!!.contains(cb.text.toString())){
                            existingList?.add(cb.text.toString())
                        }
                        hasMapSelectedValues[dataModel.chartId] = existingList!!
                    }else {
                        var existingList = hasMapSelectedValues[dataModel.chartId]
                        existingList?.remove(cb.text.toString())
                        hasMapSelectedValues[dataModel.chartId] = existingList!!
                    }
                }
                )

                holder.dashCatItemBinding.dynLl.addView(cb)
            }

        }else if (chartType.equals("ComboSlicer",true)){

            var defaultSel =0
            var spinnerArray : ArrayList<String> = ArrayList()
            for (j in 0 until dataArr.length()) {
//                val idObj = dataArr.optJSONObject(j).optJSONObject("_id")
               val idObj = getIdObject(dataArr,j)
                spinnerArray.add(idObj.optString(columnId))
                if(hasMapLastSelValues.containsKey(dataModel.chartId)){
                    if(hasMapLastSelValues[dataModel.chartId]!!.contains(idObj.optString(columnId))){
                        defaultSel = j
                        var existingList = ArrayList<String>()
                        existingList.add(defaultVal)
                        hasMapSelectedValues[dataModel.chartId] = existingList
                    }
                }

               else if(defaultVal.equals(idObj.optString(columnId))){
                    defaultSel= j
                    var existingList = ArrayList<String>()
                    existingList.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList
                }

            }

            val params = TableLayout.LayoutParams(WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT, 0f)
            params.setMargins(10, 10, 10, 10)


            val spinner = QtcSpinner(mContext)
            spinner.layoutParams = params
            spinner.tag = dataModel.chartId
            spinner.setBackgroundResource(R.drawable.filer_spnr_bg);
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
            spinner.adapter = spinnerArrayAdapter
            spinner.setSelection(false,defaultSel)
            spinner.contentDescription = "def"


            spinnerClickListener(spinner)

            holder.dashCatItemBinding.dynLl.addView(spinner)

        }else if(chartType.equals("RadioSlicer",true)){
            val radioGroup = RadioGroup(mContext)
            var selectPos = -20
            for (j in 0 until dataArr.length()) {
                val idObj = getIdObject(dataArr,j)
                val cb = RadioButton(mContext)
                cb.text =idObj.optString(columnId)
                cb.tag = dataModel.chartId

                if(hasMapLastSelValues.containsKey(dataModel.chartId)){

                    if(hasMapLastSelValues[dataModel.chartId]!!.contains(cb.text.toString())){
                        selectPos = j
                        var existingList = ArrayList<String>()
                        existingList.add(cb.text.toString())
                        hasMapSelectedValues[dataModel.chartId] = existingList
                    }
                }

               else if(defaultVal.equals(idObj.optString(columnId))){
                   // cb.isChecked = true
                    selectPos = j
                    var existingList = ArrayList<String>()
                    existingList.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList
                }

                cb.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
                    if(isChecked){

                        var existingList = ArrayList<String>()
                        existingList.add(cb.text.toString())
                        hasMapSelectedValues[dataModel.chartId] = existingList!!
                    }
                }
                )

                radioGroup.addView(cb)


            }
            if(selectPos!= -20){
                (radioGroup.getChildAt(selectPos) as RadioButton).isChecked = true

            }

            holder.dashCatItemBinding.dynLl.addView(radioGroup)

        }
        else if (chartType.equals("TagSlicer",true)){

            var defaultSel =defaultFltrMap.get(dataModel.chartId)?.optJSONArray("value")
            var spinnerArray : ArrayList<String> = ArrayList()
            for (j in 0 until dataArr.length()) {
//                val idObj = dataArr.optJSONObject(j).optJSONObject("_id")
                val idObj = getIdObject(dataArr,j)
                spinnerArray.add(idObj.optString(columnId))
                if(hasMapLastSelValues.containsKey(dataModel.chartId)){
                    if(hasMapLastSelValues[dataModel.chartId]!!.contains(idObj.optString(columnId))){

                        var existingList = ArrayList<String>()
                        existingList.add(defaultVal)
                        hasMapSelectedValues[dataModel.chartId] = existingList
                    }
                }

                else if(defaultVal.equals(idObj.optString(columnId))){

                    var existingList = ArrayList<String>()
                    existingList.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList
                }

            }


            val params = TableLayout.LayoutParams(WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT, 0f)
            params.setMargins(10, 10, 10, 10)


            val textView = TextView(mContext)
            textView.setTextColor(mContext.resources.getColor(R.color.black))
            textView.layoutParams = params
            textView.tag = dataModel.chartId

            textView.setBackgroundResource(R.drawable.filer_spnr_bg);

            textView.setOnClickListener {
                showSortBottomSheet(textView,spinnerArray,chartName)
            }


            val finalString = java.lang.StringBuilder()
            var arrayListDef = hasMapSelectedValues[dataModel.chartId] ?: ArrayList<String>()
            for(i in 0 until  arrayListDef.size){
                if(i == arrayListDef.size-1){
                    finalString.append(arrayListDef[i]  )
                }else {
                    finalString.append(arrayListDef[i] + " , " )
                }

            }
            textView.setText(finalString)


            holder.dashCatItemBinding.dynLl.addView(textView)

        }
        else if (chartType.equals("DateRangeSlicer",true)){

            var defaultSel =0
            var cDefVal = ""
            var fromStr = "";
            var toStr = "";

            if(defaultVal.startsWith("Custom")){
                cDefVal = "Custom"
            }
            var spinnerArray : ArrayList<String> = ArrayList()
            spinnerArray.add("")
            for (j in 0 until dataArr.length()) {
                val idObj = dataArr.optJSONObject(j)
                spinnerArray.add(idObj.optString("name"))

                if(hasMapLastSelValues.containsKey(dataModel.chartId)){
                    val lastSelList = hasMapLastSelValues[dataModel.chartId]
                    var customCheckValue = ""
                    if(lastSelList!!.size>0){
                        customCheckValue = lastSelList.get(0)
                        if(customCheckValue.startsWith("Custom")){
                            defaultVal = customCheckValue
                            customCheckValue = "Custom"
                        }
                    }
                    if(hasMapLastSelValues[dataModel.chartId]!!.contains(idObj.optString("name"))){
                        defaultSel = j+1
                        var existingList = ArrayList<String>()
                        existingList.add(defaultVal)
                        hasMapSelectedValues[dataModel.chartId] = existingList
                    }else if(customCheckValue.equals(idObj.optString("name"),true)) {
                        defaultSel = j+1
                        var existingList = ArrayList<String>()
                        cDefVal = "Custom"
                        existingList.add(cDefVal)

                        hasMapSelectedValues[dataModel.chartId] = existingList
                        val parts = defaultVal.split(dateSlicerDelimitor)
                        if(parts.size>1){
                            fromStr = parts[1]
                            toStr = parts[2]
                        }

                    }
                }

                else if(defaultVal.equals(idObj.optString("name"))){
                    defaultSel= j+1
                    var existingList = ArrayList<String>()
                    existingList.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList
                }

                else if(cDefVal.equals(idObj.optString("name"))){
                    defaultSel= j+1
                    var existingList = ArrayList<String>()
                    existingList.add(defaultVal)
                    hasMapSelectedValues[dataModel.chartId] = existingList

                    val parts = defaultVal.split(dateSlicerDelimitor)
                    if(parts.size>1){
                        fromStr = parts[1]
                        toStr = parts[2]
                    }

                }

            }

            val params = TableLayout.LayoutParams(WorkFlowViews.MATCH_PARENT, WorkFlowViews.WRAP_CONTENT, 0f)
            params.setMargins(10, 10, 10, 10)


            val spinner = QtcSpinner(mContext)
            spinner.layoutParams = params
            spinner.tag = dataModel.chartId
            spinner.setBackgroundResource(R.drawable.filer_spnr_bg);
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, spinnerArray)
            spinner.adapter = spinnerArrayAdapter
            spinner.setSelection(false,defaultSel)
            spinner.contentDescription = "def"


            spinnerClickListener(spinner)

            val textLabelFrom = WorkFlowViews.createTextView(mContext, "From",
                    WorkFlowViews.WRAP_CONTENT, 100,
                    3f, WorkFlowMargin(10, 10, 0, 0), Gravity.LEFT,
                    R.color.black, R.dimen.learn_more_hdr_sz)

            val fromDateField = WorkFlowViews.createTextView(mContext, "From",
                    WorkFlowViews.WRAP_CONTENT, 100,
                    1f, WorkFlowMargin(10, 10, 0, 0), Gravity.CENTER,
                    R.color.date_range_clr, R.dimen.learn_more_hdr_sz)
            fromDateField!!.tag = dataModel.chartId
            fromDateField!!.setBackgroundResource(R.drawable.date_rnge_field_bg);

            fromDateField!!.setOnClickListener {
                val calendar: Calendar = Calendar.getInstance()
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(mContext, this, year, month, day)
                datePickerDialog.show()
                fromDateTv = fromDateField
                fromDateTv.contentDescription = "from"
            }

            val fromDateLayout = WorkFlowViews.createLinearLay(
                    mContext, LinearLayout.HORIZONTAL,
                    WorkFlowViews.MATCH_PARENT, 100, 4f,
                    WorkFlowMargin(10, 0, 15, 15)
            )
            fromDateLayout.addView(textLabelFrom)
            fromDateLayout.addView(fromDateField)
            if(fromStr.isEmpty()){
                fromDateField.setText("DD/MM/YYYY  hh:mm:ss")
                fromDateLayout.visibility = View.GONE
            }else {
                fromDateField.setText(fromStr)
                fromDateLayout.visibility = View.VISIBLE
            }





            val textLabelto = WorkFlowViews.createTextView(mContext, "To",
                    WorkFlowViews.WRAP_CONTENT, 100,
                    3f, WorkFlowMargin(10, 10, 0, 0), Gravity.LEFT,
                    R.color.black, R.dimen.learn_more_hdr_sz)

            var toDateField = WorkFlowViews.createTextView(mContext, "To",
                    WorkFlowViews.WRAP_CONTENT, 100,
                    1f, WorkFlowMargin(10, 10, 0, 0), Gravity.CENTER,
                    R.color.date_range_clr, R.dimen.learn_more_hdr_sz)
            toDateField!!.tag = dataModel.chartId
            toDateField!!.setBackgroundResource(R.drawable.date_rnge_field_bg);

            val toDateLayout = WorkFlowViews.createLinearLay(
                    mContext, LinearLayout.HORIZONTAL,
                    WorkFlowViews.MATCH_PARENT, 100, 4f,
                    WorkFlowMargin(10, 0, 15, 15)
            )
            toDateLayout.addView(textLabelto)
            toDateLayout.addView(toDateField)

            toDateField.setOnClickListener {
                val calendar: Calendar = Calendar.getInstance()
                year = calendar.get(Calendar.YEAR)
                month = calendar.get(Calendar.MONTH)
                day = calendar.get(Calendar.DAY_OF_MONTH)
                val datePickerDialog = DatePickerDialog(mContext, this, year, month, day)
                datePickerDialog.show()
                fromDateTv = toDateField
                fromDateTv.contentDescription = "to"
            }
            if(toStr.isEmpty()){
                toDateField.setText("DD/MM/YYYY  hh:mm:ss")
                toDateLayout.visibility = View.GONE
            }else {
                toDateField.setText(toStr)
                toDateLayout.visibility = View.VISIBLE
            }



            var customViewList = ArrayList<LinearLayout>()
            customViewList.add(toDateLayout)
            customViewList.add(fromDateLayout)


            hasMapDateRangeCustomViews[dataModel.chartId] = customViewList
            hasMapDateRangeSelectedValues[dataModel.chartId] = JSONObject().put("isValidationError",false)


            holder.dashCatItemBinding.dynLl.addView(spinner)
            holder.dashCatItemBinding.dynLl.addView(toDateLayout)
            holder.dashCatItemBinding.dynLl.addView(fromDateLayout)

        }


        val fltrObj = JSONObject();
        if(chartType.equals("DateRangeSlicer",true)){
            fltrObj.put("columnType",3)
            fltrObj.put("columnId","_2")
            fltrObj.put("operator","advanced")
            fltrObj.put("source","actions")
        }else {
            fltrObj.put("columnType",columnType)
            fltrObj.put("columnId",columnId)
            fltrObj.put("operator","in")
            fltrObj.put("source","actions")
        }

        hasMapSourceObj[dataModel.chartId] = fltrObj



            holder.dashCatItemBinding.title.text = chartName
            holder.dashCatItemBinding.affectedItems.text = affectedCharts.toString()

        } catch (e: Exception) {

        }
    }

    private fun getIdObject(dataArr: JSONArray?,pos : Int): JSONObject {
      var idObj=  dataArr?.optJSONObject(pos)?.optJSONObject("_id")
        if (idObj == null) {
            idObj = dataArr?.optJSONObject(pos);
        }
        return idObj?:JSONObject()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(var dashCatItemBinding: SlicerListItemBinding) : RecyclerView.ViewHolder(dashCatItemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }
        }
    }

    fun checkForDateRangeValidError() : Boolean{
        if(hasMapDateRangeSelectedValues.isEmpty()){
            return false
        }else{
            var hasError = false
            hasMapDateRangeSelectedValues.forEach { (t, u) ->
                if(u.optBoolean("isValidationError")){
                    hasError = true
                }

            }
            return hasError
        }
    }

    fun getSlicerFltrData() : HashMap<String,JSONObject>{
        hasMapSourceObj.forEach { (t, u) ->
            val list = hasMapSelectedValues[t]
            val jObj = u
            val valueArr = JSONArray(list);
            jObj.put("value",valueArr)
            if(hasMapDateRangeSelectedValues.containsKey(t) &&
                    jObj.optString("operator").equals("range",true)){
                val existObj = hasMapDateRangeSelectedValues[t]
                var dataArr = JSONArray()
                dataArr.put(existObj!!.optString("from"))
                dataArr.put(existObj!!.optString("to"))
                jObj.put("value",dataArr)
                var customList = ArrayList<String>();
                customList.add("Custom"+dateSlicerDelimitor+existObj!!.optString("from")+
                        dateSlicerDelimitor+existObj!!.optString("to"))
                hasMapSelectedValues[t] = customList

            }

            hasMapSourceObj[t] = jObj
        }
        return hasMapSourceObj


    }

    fun getLastSelectedValues()  : HashMap<String,ArrayList<String>>{
          return hasMapSelectedValues
    }


    fun spinnerClickListener(spnr : QtcSpinner){
        spnr?.onItemSelectedSpinner2Listener = object : QtcSpinner.OnItemSelectedSpinner2Listener{


            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                var existingList = ArrayList<String>();
                existingList.add(spnr.selectedItem.toString().trim())
                hasMapSelectedValues[spnr.tag.toString()!!] = existingList
                if(hasMapDateRangeSelectedValues.containsKey(spnr.tag.toString())){
                    if(spnr.selectedItem.toString().trim().equals("Custom",true)){
                        hasMapDateRangeSelectedValues[spnr.tag.toString()] = JSONObject().put("isValidationError",true)
                        hasMapSourceObj[spnr.tag.toString()]!!.put("operator","range")
                        val custViewList =   hasMapDateRangeCustomViews[spnr.tag.toString()]
                        for(i in 0 until custViewList!!.size){
                            custViewList[i].visibility = View.VISIBLE
                        }
                    }else {
                        hasMapSourceObj[spnr.tag.toString()]!!.put("operator","advanced")
                        hasMapDateRangeSelectedValues[spnr.tag.toString()] = JSONObject().put("isValidationError",false)
                        val custViewList =   hasMapDateRangeCustomViews[spnr.tag.toString()]
                        for(i in 0 until custViewList!!.size){
                            custViewList[i].visibility = View.GONE
                        }
                    }
                }

            }

        }

    }




    private fun showSortBottomSheet(parentEdtTxt :TextView, dataList : ArrayList<String>,title :String) {
        val view: View = (mContext as FragmentActivity).layoutInflater.inflate(R.layout.wildcard_slicer_filter_lay, null)
        bottomSheetDialog = BottomSheetDialog(mContext)
        val listView = view.findViewById<ListView>(R.id.lst_view)
        val doneTv = view.findViewById<TextView>(R.id.done_tv)
        val cancelTv = view.findViewById<TextView>(R.id.cancel_tv)
        val titleTv = view.findViewById<TextView>(R.id.tv1)

        titleTv.setText(title)

        var selectedListOfData = ArrayList<String>()
        val existingList = hasMapSelectedValues[parentEdtTxt.tag.toString()] ?: ArrayList<String>()

        wildCardSlicerAdapter = WildCardSlicerAdapter(mContext, dataList,
            bottomSheetDialog,existingList)
        listView?.adapter = wildCardSlicerAdapter
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        cancelTv.setOnClickListener {
            bottomSheetDialog.dismiss()
        }

        doneTv.setOnClickListener {
            bottomSheetDialog.dismiss()
            hasMapSelectedValues[parentEdtTxt.tag.toString()] =selectedListOfData

            val finalString = java.lang.StringBuilder()
            for(i in 0 until  selectedListOfData.size){
                if(i == selectedListOfData.size-1){
                    finalString.append(selectedListOfData[i]  )
                }else {
                    finalString.append(selectedListOfData[i] + " , " )
                }

            }
            parentEdtTxt.setText(finalString)

        }

        wildCardSlicerAdapter.onItemClick = {
                selectedItem ->

            if(selectedItem.isAdded){
                if(!selectedListOfData.contains(selectedItem.dataItem)){
                    selectedListOfData.add(selectedItem.dataItem)
                }
            }else{
                if(selectedListOfData.contains(selectedItem.dataItem))
                selectedListOfData.remove(selectedItem.dataItem)
            }

        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        selectedYear = year
        selectedday = dayOfMonth
        selectedMonth = month
        val c = Calendar.getInstance()
        hour = c[Calendar.HOUR]
        minute = c[Calendar.MINUTE]
        val timePickerDialog = TimePickerDialog(mContext,this , hour, minute,true)
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        selectedHour = hourOfDay
        selectedMinute = minute
        selectedMonth = selectedMonth+1
        fromDateTv.setText("" + selectedday + "/" + selectedMonth + "/" + selectedYear + "  " + selectedHour + ":" + selectedMinute + ":00")

        if(fromDateTv.contentDescription.equals("from")){
            var existingObj = hasMapDateRangeSelectedValues[fromDateTv.tag.toString()]
            existingObj!!.put("from","" + selectedday + "-" + selectedMonth + "-" + selectedYear + " " + selectedHour + ":" + selectedMinute + ":00".trim())

            if(existingObj.optString("to").isNotEmpty()){
                existingObj!!.put("isValidationError",false)
            }

        }else{
            var existingObj = hasMapDateRangeSelectedValues[fromDateTv.tag.toString()]
            existingObj!!.put("to","" + selectedday + "-" + selectedMonth + "-" + selectedYear + " " + selectedHour + ":" + selectedMinute + ":00".trim())
            if(existingObj.optString("from").isNotEmpty()){
                existingObj!!.put("isValidationError",false)
            }

        }



    }

}

