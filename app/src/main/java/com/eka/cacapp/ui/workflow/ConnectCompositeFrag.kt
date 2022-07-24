package com.eka.cacapp.ui.workflow

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.R
import com.eka.cacapp.adapter.QtcSpinnerAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.qtcLayout.QtcFormDecisionItem
import com.eka.cacapp.data.qtcLayout.QtcFormModel
import com.eka.cacapp.data.qtcLayout.WorkFlowMargin
import com.eka.cacapp.databinding.CcCompositeFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.WorkFlowViews.MATCH_PARENT
import com.eka.cacapp.utils.WorkFlowViews.WRAP_CONTENT
import com.eka.cacapp.utils.WorkFlowViews.createLinearLay
import com.eka.cacapp.utils.jsevaluator.JsEvaluator
import com.eka.cacapp.utils.jsevaluator.interfaces.JsCallback
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

class ConnectCompositeFrag : Fragment() {


    private lateinit var mBinding: CcCompositeFragBinding
    private lateinit var mViewModel: DashboardViewModel

    private val serviceKeyList: ArrayList<String> = ArrayList()
    private val pareentKeyList: ArrayList<String> = ArrayList()

    private var hashMapDropDown: HashMap<String, JSONArray> = HashMap()
    private var hashMapDependsOn: HashMap<String, JSONArray> = HashMap()
    private var hashMapSpinner: HashMap<String, QtcSpinner> = HashMap()


    private var hashMapItemKeyServiceKey: HashMap<String, String> = HashMap()
    private var hashMapItemKeyDropDownKey: HashMap<String, String> = HashMap()

    private var hashMapQtcFormData: LinkedHashMap<String, QtcFormModel> = LinkedHashMap()

    private var hasMapLayoutRes: HashMap<String, String> = HashMap()

    private var currentWorkFLowName = ""

    private var spinnerArrayList: ArrayList<QtcSpinner> = ArrayList()
    private var rowArrayList: ArrayList<LinearLayout> = ArrayList()

    private var decisionList: ArrayList<QtcFormDecisionItem> = ArrayList()
    private var moreItemDecisionList: ArrayList<QtcFormDecisionItem> = ArrayList()
    private var hashMapSpinnerId: HashMap<String, Int> = HashMap()
    private var hashMapRowId: HashMap<String, Int> = HashMap()

    private val delimiter = "|~|"
    private var submitTaskName = ""
    private var externalEventName = ""

    private var reqParamObjectValue = ""
    private var reqParamSYS__UUID = ""
    private var reqParam_ID = ""
    private var reqParamREFTYPE = ""
    private var reqParamREFTYPEID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {

            mBinding = DataBindingUtil.inflate(inflater, R.layout.cc_composite_frag, container, false)

            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()

            mBinding.root.rootView.isFocusableInTouchMode = true
            mBinding.root.rootView.requestFocus()

            mBinding.root.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action === KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            navBack()
                            return true
                        }
                    }
                    return false
                }
            })

            val factory = ViewModelFactory(DashboardRepository(),requireContext())

            mViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

            parseMapLayoutRes()

            addValuesToForm(currentWorkFLowName)

            (activity as DashboardActivity).clearListView()

            (activity as DashboardActivity).makeAppTabSelected()



            mViewModel.connectAppMdmResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {
                    activity?.let { it1 ->
                        ProgressDialogUtil.showProgressDialog(it1)
                    }
                }
                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()

                        val jsonObject = JSONObject(result)

                        for (i in serviceKeyList.indices) {
                            val key = serviceKeyList[i].trim()
                            val value = jsonObject.optJSONArray(key) ?: JSONArray()

                            val spinnerArray = ArrayList<String>()
                            for (j in 0 until value.length()) {
                                val jsonObj = value.getJSONObject(j)

                                //for empty array
                                if(j==0 && jsonObj.optString("value").trim().isNotEmpty()
                                        &&jsonObj.optString("key").trim().isNotEmpty())
                                {
                                    spinnerArray.add(" ")
                                }

                                if(jsonObj.optString("value").trim().isNotEmpty()
                                        &&jsonObj.optString("key").trim().isNotEmpty())
                                {
                                    spinnerArray.add(jsonObj.optString("value"))
                                }

                            }

                            val spinnerArrayAdapter = QtcSpinnerAdapter(
                                    requireContext(),
                                    R.layout.spnner_item_lay,
                                    spinnerArray
                            )

                            spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_tv);
                            if (hashMapItemKeyServiceKey.containsKey(key)) {

                                val itemKey = hashMapItemKeyServiceKey[key]
                                if (itemKey.toString().contains(delimiter)) {
                                    val splttedKey = itemKey?.split(delimiter)
                                    for (k in splttedKey!!.indices) {
                                        if (hashMapSpinnerId.containsKey(splttedKey[k])
                                                && pareentKeyList.contains(splttedKey[k])
                                        ) {
                                            hashMapDropDown[splttedKey[k]] = value
                                            val spinnerId = hashMapSpinnerId[splttedKey[k]]
                                            spinnerArrayList[spinnerId!!].adapter =
                                                    spinnerArrayAdapter

                                        }
                                    }

                                } else {
                                    hashMapDropDown[itemKey!!] = value

                                    if (hashMapSpinnerId.containsKey(itemKey)) {
                                        val spinnerId = hashMapSpinnerId[itemKey]
                                        spinnerArrayList[spinnerId!!].adapter = spinnerArrayAdapter


                                    }
                                }

                            }


                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        handleApiError(it) { null }
                    }
                }

            })


        } catch (e: Exception) {

        }
        return mBinding.root
    }





    private fun clearAllValues(){

        for(i in 0 until spinnerArrayList.size){
            spinnerArrayList[i].setSelection(false,0)
        }

    }
    private fun clearAllExcept(selectSpnr : String){

        for(i in 0 until spinnerArrayList.size){
            if(!spinnerArrayList[i].tag.toString().equals(selectSpnr)){
                spinnerArrayList[i].setSelection(false,0)
            }

        }

    }



    private fun navBack(){
        val dataRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_SELF, "Y")

        val lastFormTag = (activity as DashboardActivity).getLastFormTag()

        if(!lastFormTag.equals("")){
            (activity as DashboardActivity).setLastFromTag("")
            (activity as DashboardActivity).setDefaultTraderName("")
            (activity as DashboardActivity).listClickHandler(lastFormTag)
        }
        else if(dataRes.toString().equals("Y",true)){
            findNavController().navigate(R.id.action_qtcFormFrag_to_appDetailFrag)
        }else{
            findNavController().navigate(R.id.action_qtcFormFrag_to_appDetailFrag)
        }
    }






    private fun parseMapLayoutRes() {
        val dataRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_LAY_MAP, "")

        val jsonArrayData = JSONArray(dataRes)
        if (jsonArrayData.length() != 0) {

            for (i in 0 until jsonArrayData.length()) {
                val jsonObject = jsonArrayData.getJSONObject(i)
                val key = jsonObject.getString("key")
                val value = jsonObject.getString("value")
                hasMapLayoutRes[key] = value
                if (i == 0) {
                    currentWorkFLowName = key
                }
            }

        }
    }


    private fun addValuesToForm(workFlowName: String) {

        val layOutRes = hasMapLayoutRes[workFlowName] ?: ""
        parseData(layOutRes, workFlowName)
    }


    private fun parseData(layOutRes: String, workFlowName: String) {

        try {

            val res = JSONObject(layOutRes)

            reqParamObjectValue = res.optString("object")

            val flowObject = res.getJSONObject("flow")


            var workFowObj = flowObject.optJSONObject(workFlowName)
            if (workFowObj == null) {
                workFowObj = flowObject.getJSONObject("composite").getJSONObject(workFlowName)
                        .getJSONObject("flow").getJSONObject(workFlowName)
            }
            reqParamREFTYPE = workFowObj.optString("refType")
            reqParamREFTYPEID = workFowObj.optString("refTypeId")


            val decisionArr = workFowObj.getJSONArray("decisions")
            createDecision(decisionArr)


            val title = workFowObj.getString("label");
            val layoutObj = workFowObj.getJSONObject("layout")
            val optionsObj = layoutObj.optJSONObject("options")

            val layOutName = layoutObj.optString("name")

            val objectMeta = res.getJSONObject("objectMeta")
            reqParamSYS__UUID = objectMeta.optString("sys__UUID")
            reqParam_ID = objectMeta.optString("_id")

            val fields = objectMeta.getJSONObject("fields")

            var mainFieldsArr = workFowObj.getJSONArray("fields")

                for (i in 0 until mainFieldsArr.length()) {

                    var item = mainFieldsArr.getJSONObject(i)

                    val key = item.optString("key")
                    val placement = item.optString("placement")
                    val label = item.optString("label")
                    val hasSort = item.optBoolean("sort")
                    val hasFilter = item.optBoolean("filter")
                    val itemType = item.optString("type")
                    val defaultValue = item.optString("value") ?: ""
                    val event = item.optString("event") ?: ""
                    var filterType = item.optString("filterType")

                    var displayName = ""
                    var dropDownValue = ""
                    var type = ""

                    if (!key.equals("")) {
                        val keyObject = fields.getJSONObject(key)

                        if (label.equals("", true)) {
                            displayName = keyObject.optString(key)
                        } else {
                            displayName = label
                        }

                        val labelKey = keyObject.optString("labelKey")
                        type = keyObject.optString("type")

                        dropDownValue = keyObject.optString("dropdownValue")

                        val dateFormat = keyObject.optString("format")
                        val isRequired = keyObject.optBoolean("isRequired")
                        val uiUpdatedObj = keyObject.optJSONObject("UIupdates")
                        val uiVisibilityExpr = uiUpdatedObj?.getString("visibility") ?: ""
                        var uiVisibilityKey = ""
                        if (uiVisibilityExpr.isNotEmpty()) {
                            uiVisibilityKey =
                                    uiVisibilityExpr.substringAfter("\${").substringBefore("}")
                        }

                        val dataType = keyObject.optString("dataType")
                        val serviceKey = keyObject.optString("serviceKey")
                        val propertyKey = keyObject.optJSONObject("propertyKey")
                        var properyKeyArr = JSONArray()
                        if (propertyKey != null) {
                            val pkey = propertyKey.names()?.opt(0).toString()
                            properyKeyArr = propertyKey.optJSONArray(pkey) ?: JSONArray()
                        }


                        if ((type.equals("dropdown", true) ||
                                        (filterType.equals("dropdown", true)))
                                && serviceKey.trim().isNotEmpty()
                        ) {
                            pareentKeyList.add(key)
                            serviceKeyList.add(serviceKey)
                            if (hashMapItemKeyServiceKey.containsKey(serviceKey)) {

                                var exstingKey = hashMapItemKeyServiceKey[serviceKey]
                                hashMapItemKeyServiceKey[serviceKey] = exstingKey + delimiter + key


                            } else {
                                hashMapItemKeyServiceKey[serviceKey] = key
                            }


                            if (keyObject.has("dependsOn")) {
                                hashMapDependsOn[serviceKey] =
                                        keyObject.optJSONArray("dependsOn") ?: JSONArray()
                            }


                            if(keyObject.has("parent")){
                                serviceKeyList.remove(serviceKey)
                            }


                        }
                        hashMapItemKeyDropDownKey[dropDownValue] = key
                        if(type.isEmpty()){
                           type = filterType
                        }
                        val qtcFormModel = QtcFormModel(
                                key,
                                keyObject.optJSONArray("dependsOn")
                                        ?: JSONArray(),
                                keyObject.optJSONArray("children") ?: JSONArray(),
                                keyObject.optString("dropdownValue"),
                                keyObject.optBoolean("isRequired"),
                                keyObject.optString("labelKey"),
                                keyObject.optString("originId"),
                                keyObject.optJSONArray("parent") ?: JSONArray(),
                                keyObject.optJSONArray("parent") ?: JSONArray(),
                                serviceKey,
                                type,
                                displayName,
                                i,
                                properyKeyArr,
                                itemType,
                                uiVisibilityExpr,
                                uiVisibilityKey,
                                defaultValue,
                                event,
                                "",
                                "",
                                dateFormat
                        )
                        hashMapQtcFormData[key] = qtcFormModel
                    } else {
                        val qtcFormModel = QtcFormModel(
                                "", JSONArray(),
                                JSONArray(),
                                "", false,
                                "", "",
                                JSONArray(), JSONArray(), "",
                                type, displayName, i, JSONArray(),
                                "", "", "", "", "", ""
                                , "")
                        hashMapQtcFormData[hashMapQtcFormData.size.toString()] = qtcFormModel
                    }

                }
            //}

            mBinding.serviceTtl.setText(title)



            if (layOutName.equals("query", true)) {
                mBinding.detailsView.visibility = View.VISIBLE

                val mainPageLayout = createLinearLay(
                        requireActivity(), LinearLayout.VERTICAL,
                        MATCH_PARENT, WRAP_CONTENT, 1f,
                        WorkFlowMargin(10, 0, 5, 5)
                )

                val nextPageLayout = createLinearLay(
                        requireActivity(), LinearLayout.VERTICAL,
                        MATCH_PARENT, WRAP_CONTENT, 1f,
                        WorkFlowMargin(10, 0, 5, 5)
                )



                hashMapQtcFormData.forEach { (key, value) ->
                    val itemValue = value

                    val parentRow = createLinearLay(
                            requireActivity(), LinearLayout.VERTICAL,
                            MATCH_PARENT, WRAP_CONTENT, 1f,
                            WorkFlowMargin(0, 0, 0, 0), Gravity.CENTER_VERTICAL
                    )

                    val row = createLinearLay(
                            requireActivity(), LinearLayout.HORIZONTAL,
                            MATCH_PARENT, WRAP_CONTENT, 2f,
                            WorkFlowMargin(0, 0, 0, 0), Gravity.CENTER_VERTICAL
                    )


                    if (itemValue.key.isEmpty()) {
                        parentRow.minimumHeight = 120
                        parentRow.background =
                                ContextCompat.getDrawable(requireContext(), R.drawable.space_row_bg)
                        //   mainPageLayout.addView(row)

                        if (itemValue.pageNo == 0) {
                            mainPageLayout.addView(parentRow)
                        } else {
                            nextPageLayout.addView(parentRow)
                        }

                    } else {
                        parentRow.minimumHeight = 200

                        parentRow.background =
                                ContextCompat.getDrawable(requireContext(), R.drawable.create_row_bg)

                        val params = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)
                        params.setMargins(0, 0, 0, 0)

                        if (itemValue.type.equals("dropdown")||itemValue.type.equals("multiselect")) {
                            val spinner = QtcSpinner(requireContext())
                            spinner.layoutParams = params

                            spinner.tag = itemValue.key
                            spinner.gravity = Gravity.RIGHT


                            val value = itemValue.properyKeyArray
                            if (value.length() != 0) {

                                val spinnerArray = ArrayList<String>()
                                for (j in 0 until value.length()) {
                                    val jsonObject = value.getJSONObject(j)
                                    if (j == 0) {
                                        spinnerArray.add(" ")
                                    }
                                    spinnerArray.add(jsonObject.optString("value"))
                                }

                                val spinnerArrayAdapter =
                                        QtcSpinnerAdapter(
                                                requireContext(),
                                                R.layout.query_spnr_item_lay,
                                                spinnerArray
                                        )

                                spinnerArrayAdapter.setDropDownViewResource(R.layout.query_comp_spnr_drp_down_tv);
                                spinner.adapter = spinnerArrayAdapter

                            }

                            val id = spinnerArrayList.size

                            spinnerArrayList.add(id, spinner)

                            hashMapSpinnerId[itemValue.key] = id
                            hashMapSpinner[itemValue.key] = spinner
                            row.addView(spinner)
                            setItemSelectListener(spinner)
                        }

                        parentRow.addView(row)


                        val rowId = rowArrayList.size
                        rowArrayList.add(rowId, parentRow)

                        hashMapRowId[itemValue.key] = rowId

                        if (itemValue.itemType.equals("hidden", true)) {
                            parentRow.visibility = View.GONE
                        }


                        if (itemValue.pageNo == 0) {
                            mainPageLayout.addView(parentRow)
                        } else {
                            nextPageLayout.addView(parentRow)
                        }


                    }
                    //}

                }


                mBinding.detailCard.addView(mainPageLayout)
                mBinding.detailCard1.addView(nextPageLayout)

            }
            if (checkInternet()) {

                makeMdmCall()

            }

        } catch (e: Exception) {

        }
    }

    private fun createDecision(decisionArr: JSONArray) {
        if (decisionArr.length() > 0) {

            for (d in 0 until decisionArr.length()) {
                val decisionObj = decisionArr.getJSONObject(d)
                val selection = decisionObj.optString("selection")
                val position = decisionObj.optString("position")
                val type = decisionObj.optString("type")

                if (selection.toString().equals("external", true) &&
                        type.equals("submit", true)) {
                    externalEventName = decisionObj.optString("task")
                }
                val outcomeArr = decisionObj.optJSONArray("outcomes") ?: JSONArray()
                for (i in 0 until outcomeArr.length()) {
                    val jsonObject = outcomeArr.getJSONObject(i)
                    val outcomeAction = jsonObject.optString("action")

                    if (position.toString().equals("TopLeft", true) &&
                            outcomeAction.equals("Cancel", true)) {

                        mBinding.cancelTxt.visibility = View.VISIBLE
                        mBinding.cancelTxt.setOnClickListener {

                        }
                    }

                }
                val qtcFormDecisionItem = QtcFormDecisionItem(
                        decisionObj.optString("label"),
                        decisionObj.optString("labelkey")
                        , outcomeArr, decisionObj.optString("position"), decisionObj.optString("task"),
                        decisionObj.optString("type")
                )

                if (decisionObj.optString("type").equals("submit", true)
                        && decisionObj.optString("position").equals("TopRight", true)) {
                    submitTaskName = decisionObj.optString("task")
                    mBinding.nextTxt.visibility = View.VISIBLE
                    mBinding.nextTxt.setOnClickListener {
                        if (checkInternet()) {
                            it.hideKeyboard()

                        }

                    }

                } else if (decisionObj.optString("position").equals("TopRight", true)) {

                    if (moreItemDecisionList.isEmpty()) {
                        mBinding.moreImg.visibility = View.VISIBLE
                        mBinding.moreImg.setOnClickListener {
                            showPopup(it)
                        }
                    }
                    moreItemDecisionList.add(qtcFormDecisionItem)

                }

                decisionList.add(qtcFormDecisionItem)
            }

        }
    }

    private fun makeMdmCall() {
        val jsonArray = JsonArray()
        for (i in serviceKeyList.indices) {

            val jsonObject = JsonObject()
            jsonObject.addProperty("serviceKey", serviceKeyList[i])
            if (hashMapDependsOn.containsKey(serviceKeyList[i])) {
                val dependsOnArr = JsonArray()
                for (j in 0 until (hashMapDependsOn[serviceKeyList[i]]?.length() ?: 0)) {
                    dependsOnArr.add(hashMapDependsOn[serviceKeyList[i]]?.getString(j))
                }
                jsonObject.add("dependsOn", dependsOnArr)
            }
            jsonArray.add(jsonObject)

        }

        mViewModel.connectMdmDataCall(currentWorkFLowName, jsonArray)

    }


    fun setItemSelectListener(spinner: QtcSpinner) {

        spinner.onItemSelectedSpinner2Listener = object : QtcSpinner.OnItemSelectedSpinner2Listener {
            override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
            ) {



                if (position != 0 && checkInternet()) {
                    serviceKeyList.clear()
                    pareentKeyList.clear()
                    val spnrTag = spinner.tag
                    var spnrKey = ""

                    val obj = hashMapQtcFormData[spnrTag]
                    val objKey = obj?.key
                    var value = JSONArray()
                    if (obj?.properyKeyArray?.length() != 0) {
                        value = obj?.properyKeyArray ?: JSONArray()
                    } else {
                        val objKey = obj?.key
                        value = hashMapDropDown[objKey] ?: JSONArray()
                    }
                    val jsonObject = value.getJSONObject(position - 1)
                    spnrKey = jsonObject.optString("key")

                    checkUiVisibility(spnrTag.toString(),spinner.selectedItem.toString())

                    obj?.let { addValuesToServiceKeys(it,spnrTag.toString(),position) }

                    if (obj?.children?.length() != 0) {
                        if (!obj?.event.equals("")) {

                            clearAllExcept(spnrTag.toString())


                        }
                    }

                }


            }

        }
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }



    fun showPopup(v: View?) {
        val popup = PopupMenu(requireContext(), v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.dynam_menu, popup.menu)
        val menu = popup.menu

        for (i in moreItemDecisionList.indices) {

            menu.add(0, i, 0, moreItemDecisionList[i].label)
        }
        popup.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        popup.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        val menuAction = moreItemDecisionList[itemId]
        val outComeArr = menuAction.outcomes
        if(outComeArr.length()>0){
            val outComeObj = outComeArr.optJSONObject(0)
            val action = outComeObj.optString("action")

            val name = outComeObj.optString("name")
            val type = outComeObj.optString("type")

             if(type.equals("client",true)
                    && outComeObj.has("data")){
                val jsonObject = outComeObj.optJSONObject("data")


                val iter: Iterator<String> = jsonObject.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    if (hashMapQtcFormData.containsKey(key)) {
                        try {

                            val value: Any = jsonObject.get(key)

                            hashMapQtcFormData[key].apply {
                                this?.nlpRecommendValue = value.toString()
                            }
                        } catch (e: JSONException) {

                        }

                    }

                }

                clearAllValues()


            }




        }


        return false
    }



    private fun addValuesToServiceKeys(obj : QtcFormModel,spnrTag : String,pos : Int){
        if (obj?.children?.length() != 0) {
            var spnrKey = ""
            val sprArr = obj?.children
            val objKey = obj?.key
            var value = JSONArray()
            if (obj?.properyKeyArray?.length() != 0) {
                value = obj?.properyKeyArray ?: JSONArray()
            } else {
                val objKey = obj?.key
                value = hashMapDropDown[objKey] ?: JSONArray()
            }
            val jsonObject = value.getJSONObject(pos - 1)
            spnrKey = jsonObject.optString("key")
            for (i in 0 until sprArr!!.length()) {
                val key = sprArr[i].toString()
                if (hashMapQtcFormData.containsKey(key)) {
                    val childObj = hashMapQtcFormData[key]
                    val jArry = childObj?.parent
                    val jArryKeys = childObj?.parentKeys
                    val jsonArray = JSONArray()
                    for (t in 0 until jArry!!.length()) {


                        if (jArryKeys!![t].toString().trim()
                                        .equals(spnrTag.trim(), true)) {
                            jsonArray.put(spnrKey)
                        } else {

                            val objParent = hashMapQtcFormData[jArryKeys[t]]
                            val spnrValKey = objParent?.key
                            val spnrId = hashMapSpinnerId[spnrValKey]
                            var spnrPos = 0
                            if(spnrId!=null){
                                spnrPos = spinnerArrayList.get(spnrId!!).selectedItemPosition
                            }

                            if(spnrPos>0){
                                var valueParent = JSONArray()
                                if (objParent?.properyKeyArray?.length() != 0) {
                                    valueParent = objParent?.properyKeyArray ?: JSONArray()
                                } else {
                                    val objKeyParent = objParent?.key
                                    valueParent = hashMapDropDown[objKeyParent] ?: JSONArray()
                                }
                                val jsonObjectParent = valueParent.getJSONObject(spnrPos - 1)
                                val parentKey = jsonObjectParent.optString("key")

                                jsonArray.put(parentKey)
                            }else{
                                jsonArray.put(jArry[t])
                            }



                        }

                    }

                    childObj.apply {
                        this.parent = jsonArray
                    }
                    val size = serviceKeyList.size
                    serviceKeyList.add(size,childObj.serviceKey)
                    pareentKeyList.add(childObj.key)
                    hashMapDependsOn[childObj.serviceKey] = childObj.parent


                }
            }
        }
    }

    private fun checkUiVisibility(spnrTag : String ,selectedValue :String){
        hashMapQtcFormData.forEach { (key, value) ->

            if (value.uiVisibilityKey.equals(spnrTag,true)) {
                var expr = value.itemVisibilityExpr
                if (expr.startsWith("return")) {
                    expr = expr.replace("return", "")
                }
                expr = expr.replace("\${", "")
                expr = expr.replace("}", "")
                expr = expr.replace(spnrTag, selectedValue)

                val jsEvaluator = JsEvaluator(requireContext())
                jsEvaluator.evaluate(expr, object : JsCallback {
                    override fun onResult(result: String) {
                        if (result.equals("true", true)) {
                            val rowid = hashMapRowId[value.key]
                            val row = rowArrayList[rowid!!]
                            row.visibility = View.VISIBLE
                        } else {
                            val rowid = hashMapRowId[value.key]
                            val row = rowArrayList[rowid!!]
                            row.visibility = View.GONE
                        }

                    }

                    override fun onError(errorMessage: String) {
                        val rowid = hashMapRowId[value.key]
                        val row = rowArrayList[rowid!!]
                        row.visibility = View.GONE
                    }
                })

            }


        }

    }





}





