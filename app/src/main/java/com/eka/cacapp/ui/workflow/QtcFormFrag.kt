package com.eka.cacapp.ui.workflow

import android.Manifest
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
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
import com.eka.cacapp.data.qtcLayout.QtcFormValidation
import com.eka.cacapp.data.qtcLayout.WorkFlowMargin
import com.eka.cacapp.databinding.QtcFormFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.WorkFlowViews.MATCH_PARENT
import com.eka.cacapp.utils.WorkFlowViews.WRAP_CONTENT
import com.eka.cacapp.utils.WorkFlowViews.createEditText
import com.eka.cacapp.utils.WorkFlowViews.createLinearLay
import com.eka.cacapp.utils.WorkFlowViews.createTextView
import com.eka.cacapp.utils.jsevaluator.JsEvaluator
import com.eka.cacapp.utils.jsevaluator.interfaces.JsCallback
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


class QtcFormFrag : Fragment() {

    var callDependedOnlyOnce = false
    var audioNlpCall = false

    var hasRecordingPermission = false
    private lateinit var speechRecognizer: SpeechRecognizer

    private lateinit var mBinding: QtcFormFragBinding
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
    private var erroTvList: ArrayList<TextView> = ArrayList()
    private var editTextArrayList: ArrayList<EditText> = ArrayList()
    private var decisionList: ArrayList<QtcFormDecisionItem> = ArrayList()
    private var moreItemDecisionList: ArrayList<QtcFormDecisionItem> = ArrayList()
    private var hashMapSpinnerId: HashMap<String, Int> = HashMap()
    private var hashMapRowId: HashMap<String, Int> = HashMap()
    private var noOfPages = 0
    private val delimiter = "|~|"
    private var submitTaskName = ""
    private var externalEventName = ""

    private var reqParamObjectValue = ""
    private var reqParamSYS__UUID = ""
    private var reqParam_ID = ""
    private var reqParamSYS__UPDATEDON = ""
    private var reqParamREFTYPE = ""
    private var reqParamREFTYPEID = ""


    private var hashMapRecommendedKeys: HashMap<String, String> = HashMap()
    private var hashMapNlpRecommendationMap: HashMap<String, JSONObject> = HashMap()
    private var workflowRecommendationNeed = false

    private var currentNlpFieldKey = ""
    private var currentNlpField = ""
    var count = 0
    var depMaxCount = 0

    enum class RequestType {
        DEFAULT,EXTERNAL_DEFAULT_VALUES, NLP_RECOMM_FOR_ALL_FIELDS, NLP_RECOMM_WITH_FIELDS_ID
    }
    private  var requestType = RequestType.DEFAULT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        try {
            allowOnlyPortrait()
            mBinding = DataBindingUtil.inflate(inflater, R.layout.qtc_form_frag, container, false)

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


            mViewModel.connectNlpSentenceResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {
                    activity?.let { it1 ->
                        ProgressDialogUtil.showProgressDialog(it1)
                    }
                }
                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val mainObject = JSONObject(result)
                        val jsonObject = mainObject.optJSONObject("tags")

                        val iter: Iterator<String> = jsonObject.keys()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            if (hashMapQtcFormData.containsKey(key)) {
                                try {
                                    val value: Any = jsonObject.get(key)
                                    hashMapRecommendedKeys[key] = value.toString()
                                    hashMapQtcFormData[key].apply {
                                        this?.nlpRecommendValue = value.toString()
                                    }

                                    val obj = hashMapQtcFormData[key]
                                    if(obj?.type.toString().equals("dropdown",true)){

                                        val spnrId = hashMapSpinnerId[obj?.key]
                                        val spnr = spinnerArrayList[spnrId!!]
                                        spnr.setSelection(false,0)
                                        changeRowBg(spinnerArrayList[spnrId!!].tag.toString(),false)
                                    }


                                } catch (e: JSONException) {

                                }

                            }

                        }
                        setNLPRecommendedValues()


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            })


            mViewModel.connectAppRecommendationResponse.observe(viewLifecycleOwner, Observer {

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
                        val iter: Iterator<String> = jsonObject.keys()
                        while (iter.hasNext()) {
                            val key = iter.next()
                            if (hashMapQtcFormData.containsKey(key)||
                                    hashMapItemKeyDropDownKey.containsKey(key)) {
                                try {

                                    if(hashMapQtcFormData.containsKey(key)){

                                        val value: Any = jsonObject.get(key)
                                        hashMapRecommendedKeys[key] = value.toString()
                                        hashMapQtcFormData[key].apply {
                                            this?.nlpRecommendValue = value.toString()
                                        }


                                    }else if(!hashMapQtcFormData.containsKey(key)
                                            &&hashMapItemKeyDropDownKey.containsKey(key)){
                                        val value: Any = jsonObject.get(key)

                                            val serKey = hashMapItemKeyDropDownKey[key].toString()
                                            if(!hashMapRecommendedKeys.containsKey(serKey)) {
                                                hashMapRecommendedKeys[serKey] = value.toString()
                                                hashMapQtcFormData[serKey].apply {
                                                    this?.nlpRecommendValue = value.toString()
                                                }
                                            }



                                    }
                                } catch (e: JSONException) {

                                }

                            } else {
                                val value: Any = jsonObject.get(key)
                                if (value is JSONObject) {
                                    hashMapNlpRecommendationMap[key] = value
                                    if (currentNlpFieldKey.equals(key, true)) {
                                        setRecommendedWithSelectedFieldID(value)
                                    }
                                }else if (value is String){
                                    if(!key.equals("msg",true)){
                                        hashMapRecommendedKeys[key] = value.toString()
                                    }
                                }
                            }

                        }

                        if (requestType.equals(RequestType.NLP_RECOMM_WITH_FIELDS_ID)) {
                            if (serviceKeyList.isNotEmpty()) {
                                makeMdmCall()
                            } else {
                                requestType = RequestType.DEFAULT
                            }
                        } else {
                            requestType = RequestType.NLP_RECOMM_FOR_ALL_FIELDS
                            makeMdmCall()

                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it) { null }
                    }
                }
            })


            mViewModel.connectAppDecisionResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {
                    activity?.let { it1 ->
                        ProgressDialogUtil.showProgressDialog(it1)
                    }
                }
                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()

                        if (requestType.equals(RequestType.EXTERNAL_DEFAULT_VALUES)) {

                            val jsonObj = JSONObject(result)
                            val dataObj = jsonObj.optJSONObject("data")

                            hashMapQtcFormData.forEach { (key, value) ->

                                val recommValue = dataObj.optString(key) ?: ""
                              //  if (recommValue.isNotEmpty()) {
                                    hashMapQtcFormData[key]?.let {
                                        it.recommendedDefaultValue = recommValue.trim()
                                    }
                               // }

                            }
                            val iter: Iterator<String> = dataObj.keys()
                            while (iter.hasNext()) {
                                val key = iter.next()
                                if (!hashMapQtcFormData.containsKey(key)) {
                                    try {
                                        val value: Any = dataObj.get(key)
                                        hashMapRecommendedKeys[key] = value.toString()
                                    } catch (e: JSONException) {

                                    }

                                }

                            }

                            makeMdmCall()
                        } else {
                            val jsonObject = JSONObject(result)
                            if (jsonObject.optBoolean("showPopUp")) {
                                DialogUtil.infoPopUp(requireContext(),
                                        "", jsonObject.optString("message"),
                                        { findNavController().navigate(R.id.action_qtcFormFrag_to_appDetailFrag) }
                                )
                            } else {
                                val action = jsonObject.optString("action")
                                if (action.equals("Cancel", true)) {

                                    if (jsonObject.optBoolean("showToaster")) {
                                        Toast.makeText(requireContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show()
                                    }

                                       navBack()
                                   // (activity as AppCompatActivity?)!!.supportActionBar!!.show()
                                }else {
                                    if(!jsonObject.optBoolean("showPopUp")){
                                        if (jsonObject.optBoolean("showToaster")) {
                                            Toast.makeText(requireContext(), jsonObject.optString("message"), Toast.LENGTH_SHORT).show()
                                        }
                                        navBack()
                                       // findNavController().navigate(R.id.action_qtcFormFrag_to_appDetailFrag)
                                    }

                                }

                            }
                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                      //  handleApiError(it) { null }
                        showErrorOnFields(it)
                    }
                }
            })




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
                                            changeRowBg(spinnerArrayList[spinnerId!!].tag.toString(),false)
                                        }
                                    }

                                } else {
                                    hashMapDropDown[itemKey!!] = value

                                    if (hashMapSpinnerId.containsKey(itemKey)) {
                                        val spinnerId = hashMapSpinnerId[itemKey]
                                        spinnerArrayList[spinnerId!!].adapter = spinnerArrayAdapter

                                        //for setting default trader name in default settings
                                        if(itemKey.equals("traderUserId")){
                                            val defaultTraderKey= (activity as DashboardActivity).getDefaultTraderKey()
                                            if(defaultTraderKey.isNotEmpty()){
                                            for(t in 0 until value.length()){
                                                val jsonObj = value.getJSONObject(t)
                                               if( jsonObj.optString("key").trim().equals(defaultTraderKey,true)){
                                                   spinnerArrayList[spinnerId!!].setSelection(true,t+1)
                                               }
                                            }
                                            }


                                        }




                                        changeRowBg(spinnerArrayList[spinnerId!!].tag.toString(),false)

                                    }
                                }

                            }


                        }

                        if (requestType.equals(RequestType.EXTERNAL_DEFAULT_VALUES)) {
                            setExternalDefaultValues()
                        }

                       else  if (requestType.equals(RequestType.NLP_RECOMM_FOR_ALL_FIELDS)) {
                            setNLPRecommendedValues()
                        }
                      else   if (requestType.equals(RequestType.NLP_RECOMM_WITH_FIELDS_ID)) {
                            serviceKeyList.clear()
                            pareentKeyList.clear()
                            mViewModel.connectRecommendation(currentWorkFLowName, currentNlpField,reqParamObjectValue)
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

    private fun showErrorOnFields(it: Resource.Failure) {

        try {


        val res = it.errorBody?.string() ?: ""

        val jsonObject = JSONObject(res)

           val toastErrorMsg = jsonObject.optString("errorMessage") ?: "Something went wrong, Please try again later"

            Toast.makeText(requireContext(), toastErrorMsg, Toast.LENGTH_SHORT).show()

        if (jsonObject.has("errorCode")) {
            val errorCode = jsonObject.optString("errorCode")
            if ((errorCode.equals("022", true) ||
                            errorCode.equals("125", true) ||
                            errorCode.equals("004", true)) && jsonObject.has("errors")) {

                val errors = jsonObject.optJSONArray("errors") ?: JSONArray()
                for (i in 0 until errors.length()) {
                    val jObj = errors.optJSONObject(i)
                    var errorField = jObj.optString("errorContext")
                    errorField = errorField.substringAfter(":")
                    errorField = errorField.replace("}", "")
                    var errorMsg = jObj.optString("errorLocalizedMessage")
                    if (hashMapQtcFormData.containsKey(errorField)) {

                        highlightFieldWithError(errorField,errorMsg)

                    }


                }


            }
        }
    }catch (e : java.lang.Exception){

        }
    }

    private fun setRecommendedWithSelectedFieldID(value: JSONObject) {
        val iter: Iterator<String> = value.keys()
        while (iter.hasNext()) {
            val key = iter.next()
            var keyValue = value.getString(key)
            if (hashMapQtcFormData.containsKey(key)) {
                val obj = hashMapQtcFormData[key]

                if (obj?.type.equals("dropdown")) {
                    var value = JSONArray()
                    if (obj?.properyKeyArray?.length() != 0) {
                        value = obj?.properyKeyArray ?: JSONArray()
                    } else {
                        val objKey = obj?.key
                        value = hashMapDropDown[objKey] ?: JSONArray()
                    }


                    for (j in 0 until value.length()) {
                        val jsonObject = value.optJSONObject(j)
                        if (jsonObject.optString("key").trim()
                                        .equals(keyValue, true)
                        ) {
                            val spinnerId = hashMapSpinnerId[key]
                            spinnerArrayList[spinnerId!!].setSelection(false, j + 1, false)
                            changeRowBg(spinnerArrayList[spinnerId!!].tag.toString(),true)
                            checkUiVisibility(spinnerArrayList[spinnerId!!].tag.toString(),spinnerArrayList[spinnerId!!].selectedItem.toString())

                            break
                        }
                    }

                } else {

                    for (i in editTextArrayList.indices) {
                        val spnrTag = editTextArrayList[i].tag.toString()
                        if(obj?.type.equals("datepicker",true)){
                            keyValue = getDateString(keyValue)
                        }

                        if (key.equals(spnrTag, true)) {
                            editTextArrayList[i].setText(keyValue.toString().trim())
                            changeRowBg(editTextArrayList[i].tag.toString(),true)
                        }

                    }
                }

            }

        }
    }

    fun changeRowBg(rowTag: String,isRecommended: Boolean){
        val rowId = hashMapRowId[rowTag]
        if(isRecommended){
            rowArrayList[rowId!!].background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.recommended_row_bg)
        }else{
            rowArrayList[rowId!!].background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.create_row_bg)
        }


    }

    private fun checkForDependendMdm(reqType :RequestType){
        if(!callDependedOnlyOnce&& count<depMaxCount) {
            callDependedOnlyOnce = true
            count++
            hashMapQtcFormData.forEach { (indexKey, objVal) ->

                if(objVal.type.equals("dropdown")){
                    val spnrId = hashMapSpinnerId[indexKey]
                    val spnr = spinnerArrayList[spnrId!!]

                    if(objVal.parent.length()>0
                            && spnr.selectedItemPosition<1){
                        val parentkey = objVal.parentKeys
                        val parentValueArr = objVal.parent
                        val jsonrArray = JSONArray()
                        for(i in 0 until  parentkey.length()){
                            val key = parentkey.optString(i)
                            if(key.equals(parentValueArr.optString(i))){

                                val parentKey = key
                                val spnrId = hashMapSpinnerId[parentKey]
                                var spnrPos = 0
                                if(spnrId!=null){
                                    spnrPos = spinnerArrayList.get(spnrId!!).selectedItemPosition
                                }
                                if(spnrPos>0){
                                    val objParent = hashMapQtcFormData[key]
                                    var valueParent = JSONArray()
                                    if (objParent?.properyKeyArray?.length() != 0) {
                                        valueParent = objParent?.properyKeyArray ?: JSONArray()
                                    } else {
                                        val objKeyParent = objParent?.key
                                        valueParent = hashMapDropDown[objKeyParent] ?: JSONArray()
                                    }
                                    val jsonObjectParent = valueParent.getJSONObject(spnrPos - 1)
                                    val parentKey = jsonObjectParent.optString("key")

                                    jsonrArray.put(parentKey)
                                }else{
                                    jsonrArray.put(key)
                                }


                            }else {
                                jsonrArray.put(parentValueArr.optString(i))
                            }

                        }


                        objVal.apply {
                            this.parent = jsonrArray
                        }

                    }
                }


            }


            hashMapQtcFormData.forEach { (indexKey, objVal) ->

                if (objVal.type.equals("dropdown")) {
                    val spnrId = hashMapSpinnerId[indexKey]
                    val spnr = spinnerArrayList[spnrId!!]

                    if (objVal.parent.length() > 0
                            && spnr.selectedItemPosition < 1) {

                        var isHasAllValues = true
                        val parentkey = objVal.parentKeys
                        val parentValueArr = objVal.parent
                        val jsonrArray = JSONArray()
                        for(i in 0 until  parentkey.length()) {
                            val key = parentkey.optString(i)
                            if (key.equals(parentValueArr.optString(i))) {
                              isHasAllValues = false
                            }
                        }

                        if(isHasAllValues){
                            serviceKeyList.add(objVal.serviceKey)
                            pareentKeyList.add(objVal.key)
                            hashMapDependsOn[objVal.serviceKey] = objVal.parent
                        }


                    }
                }
            }
            if (serviceKeyList.isNotEmpty()) {
                requestType = reqType
                makeMdmCall()
            }

        }
    }

    private fun setExternalDefaultValues() {
        setDefaultValues("EXT_DEFAULT",false)
        if (serviceKeyList.isNotEmpty()) {
            requestType = RequestType.EXTERNAL_DEFAULT_VALUES
            makeMdmCall()
        }else {
            checkForDependendMdm(RequestType.EXTERNAL_DEFAULT_VALUES)
        }

    }

    private fun setNLPRecommendedValues() {

        setDefaultValues("NLP",true)
        if (serviceKeyList.isNotEmpty()) {
            requestType = RequestType.NLP_RECOMM_FOR_ALL_FIELDS
            makeMdmCall()
        }else
        {
            checkForDependendMdm(RequestType.NLP_RECOMM_FOR_ALL_FIELDS)
        }

    }

    private fun setDefaultValues(key : String,changeRowBg : Boolean){
        serviceKeyList.clear()
        pareentKeyList.clear()
        requestType = RequestType.DEFAULT
        for (i in spinnerArrayList.indices) {
            val spnrTag = spinnerArrayList[i].tag
            var spnrKey = ""
            val obj = hashMapQtcFormData[spnrTag]

            var recommendedValue = ""
            if(key.equals("NLP",true)){
                recommendedValue =obj?.nlpRecommendValue.toString()
            }else if (key.equals("EXT_DEFAULT",true)){
                recommendedValue= obj?.recommendedDefaultValue.toString()
            }

            if (recommendedValue.isNotEmpty() && spinnerArrayList[i].selectedItemPosition < 1

            ) {

                var value = JSONArray()
                if (obj?.properyKeyArray?.length() != 0) {
                    value = obj?.properyKeyArray ?: JSONArray()
                } else {
                    val objKey = obj?.key
                    value = hashMapDropDown[objKey] ?: JSONArray()
                }



                for (j in 0 until value.length()) {
                    val jsonObject = value.optJSONObject(j)
                    if (jsonObject.optString("key").trim()
                                    .equals(recommendedValue.trim(), true)
                            ||jsonObject.optString("value").trim()
                                    .equals(recommendedValue.trim(), true)
                    ) {
                        callDependedOnlyOnce = false
                        spinnerArrayList[i].setSelection(false, j + 1, false)

                        if(changeRowBg){

                            changeRowBg(spinnerArrayList[i].tag.toString(),true)

                        }

                        removeErrorFromField(spinnerArrayList[i].tag.toString())


                        checkUiVisibility(spinnerArrayList[i].tag.toString(),spinnerArrayList[i].selectedItem.toString())

                        if (j != 0) {
                            addValuesForDependentSpnr(spinnerArrayList[i], j + 1)
                        }

                        break
                    }
                }

            }

        }

        for (i in editTextArrayList.indices) {
            val spnrTag = editTextArrayList[i].tag
            var spnrKey = ""
            val obj = hashMapQtcFormData[spnrTag]

            var recommendedValue = ""
            if(key.equals("nlp",true)){
                recommendedValue =obj?.nlpRecommendValue.toString()
            }
            else if (key.equals("EXT_DEFAULT",true)){
                recommendedValue= obj?.recommendedDefaultValue.toString()
            }

            if (recommendedValue.isNotEmpty()
                    /*&& editTextArrayList[i].text.toString().trim().isEmpty()*/) {

                if(obj?.type.equals("datepicker",true)){
                    recommendedValue = getDateString(recommendedValue)

                    recommendedValue=  getFormattedDate(recommendedValue,editTextArrayList[i].tag.toString())

                }




                editTextArrayList[i].setText(recommendedValue.trim())
                if(changeRowBg) {
                    changeRowBg(editTextArrayList[i].tag.toString(),true)
                }
            }

        }
    }

    private fun getDateString(recommendedValue: String): String {

       var  result = recommendedValue.substringBefore("T")
        return result
    }

    private fun clearAllValues(){

        for(i in 0 until spinnerArrayList.size){
            spinnerArrayList[i].setSelection(false,0)
        }
        for(i in 0 until editTextArrayList.size){


            val doNotcleanField =editTextArrayList[i].text.toString().trim().equals(getCurrentDate().toString(),true)
                    &&hashMapQtcFormData[editTextArrayList[i].tag!!]?.type.equals("datepicker", true)
                    &&hashMapQtcFormData[editTextArrayList[i].tag!!]?.itemType.equals("hidden", true)
            if(!doNotcleanField){
                editTextArrayList[i].setText("")
            }

        }

    }
    private fun clearAllExcept(selectSpnr : String){

        for(i in 0 until spinnerArrayList.size){
            if(!spinnerArrayList[i].tag.toString().equals(selectSpnr)){
                spinnerArrayList[i].setSelection(false,0)
            }

        }
        for(i in 0 until editTextArrayList.size){
            val doNotcleanField =editTextArrayList[i].text.toString().trim().equals(getCurrentDate().toString(),true)
                    &&hashMapQtcFormData[editTextArrayList[i].tag!!]?.type.equals("datepicker", true)
                    &&hashMapQtcFormData[editTextArrayList[i].tag!!]?.itemType.equals("hidden", true)
            if(!doNotcleanField){
                editTextArrayList[i].setText("")
            }
        }

    }

    private fun cancelBtnAction() {
        if (mBinding.cancelTxt.text.toString().equals("Prev", true)) {
            mBinding.detailsView.visibility = View.VISIBLE
            mBinding.detailsView1.visibility = View.GONE
            mBinding.cancelTxt.text = "Cancel"
            mBinding.nextTxt.text = "Next"
        } else {

            navBack()


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
//            activity?.onBackPressed()
            findNavController().navigate(R.id.action_qtcFormFrag_to_appDetailFrag)
        }
    }


    private fun submitBtnAction() {
        requestType = RequestType.DEFAULT

        if (mBinding.nextTxt.text.toString().equals("Next", true)) {
            mBinding.detailsView.visibility = View.GONE
            mBinding.detailsView1.visibility = View.VISIBLE
            mBinding.cancelTxt.text = "Prev"
            mBinding.nextTxt.text = "Save"
        } else {


            val dataObjRes = getDataObjectWithValidation(true)

            var dataObj = dataObjRes.dataObj

            if(!dataObjRes.hasValidationErro){

                dataObj.addProperty("object", reqParamObjectValue)
                dataObj.addProperty("sys__UUID", reqParamSYS__UUID)
                dataObj.addProperty("_id", reqParam_ID)
                dataObj.addProperty("sys__updatedOn", reqParamSYS__UPDATEDON)
                dataObj.addProperty("refType", reqParamREFTYPE)
                dataObj.addProperty("refTypeId", reqParamREFTYPEID)

                val dataJsonObj = JsonObject()
                dataJsonObj.add(submitTaskName, dataObj)
                mViewModel.connectAppDecision(submitTaskName, submitTaskName, dataJsonObj)
            }

        }

    }

    private fun getDataObjectWithValidation(hasFieldValCheck :Boolean): QtcFormValidation {
        val dataObj = JsonObject()

        var hasValErrorFields = false
        var hasValError = false
        var hashMapFieldsValidation = HashMap<String,String>()
        var valCheckCount =0
        var valTestedCount =0

        for (i in spinnerArrayList.indices) {
            val spnrTag = spinnerArrayList[i].tag
            var spnrKey = ""
            val obj = hashMapQtcFormData[spnrTag]
            val selectItemPos = spinnerArrayList[i].selectedItemPosition
            if (selectItemPos > 0) {
                var value = JSONArray()
                if (obj?.properyKeyArray?.length() != 0) {
                    value = obj?.properyKeyArray ?: JSONArray()
                } else {
                    val objKey = obj?.key
                    value = hashMapDropDown[objKey] ?: JSONArray()

                }
                val jsonObject = value.optJSONObject(selectItemPos - 1)
                spnrKey = jsonObject.optString("key")

            }
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.defaultValue ?: ""
            }
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.recommendedDefaultValue ?: ""
            }

            dataObj.addProperty(obj?.key.toString(), spnrKey)
        }

        for (i in editTextArrayList.indices) {
            val spnrTag = editTextArrayList[i].tag
            var spnrKey = ""
            spnrKey = editTextArrayList[i].text.toString().trim()
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.defaultValue ?: ""
            }




            if(hashMapQtcFormData.containsKey(spnrTag) &&hasFieldValCheck){

                val formModel = hashMapQtcFormData[spnrTag]
                if(formModel?.event!!.isNotEmpty()){
                    valCheckCount++
                    hasValErrorFields = true
                    var value = formModel?.event
                    var keysArrayList = findKeysToReplace(value)
                    for(k in keysArrayList.indices){
                        val key = keysArrayList[k]
                        var compareToValue =""
                        for(j in editTextArrayList.indices){
                            val edtTxt = editTextArrayList[j]
                            if(edtTxt.tag.toString().equals(key)){
                                compareToValue = edtTxt.text.toString()
                            }
                        }
                        value = value.replace("\${"+key+"}",compareToValue)

                    }
                    value = value.replace("return", "")


                    val expr = value
                    val jsEvaluator = JsEvaluator(requireContext())
                    jsEvaluator.evaluate(expr, object : JsCallback {
                        override fun onResult(result: String) {
                            valTestedCount++
                            if(!result.equals("true",true)){
                                hasValError = true
                                hashMapFieldsValidation[spnrTag.toString()] = result
                                editTextArrayList[i].error = result
                            }
                            if(valCheckCount==valTestedCount){
                                if(!hasValError){
                                    sendReqAfterValidation(dataObj)
                                }
                            }


                        }

                        override fun onError(errorMessage: String) {
                            valTestedCount++
                            if(valCheckCount==valTestedCount){
                                if(!hasValError){
                                    sendReqAfterValidation(dataObj)
                                }
                            }
                        }
                    })

                }

            }
            dataObj.addProperty(spnrTag.toString(), spnrKey)
        }

        return QtcFormValidation(hasValErrorFields,hashMapFieldsValidation,dataObj)
    }

    private fun sendReqAfterValidation(dataObject : JsonObject){
        var dataObj = dataObject

        dataObj.addProperty("object", reqParamObjectValue)
        dataObj.addProperty("sys__UUID", reqParamSYS__UUID)
        dataObj.addProperty("_id", reqParam_ID)
        dataObj.addProperty("sys__updatedOn", reqParamSYS__UPDATEDON)
        dataObj.addProperty("refType", reqParamREFTYPE)
        dataObj.addProperty("refTypeId", reqParamREFTYPEID)

        val dataJsonObj = JsonObject()
        dataJsonObj.add(submitTaskName, dataObj)
        mViewModel.connectAppDecision(submitTaskName, submitTaskName, dataJsonObj)

    }


    private fun externalWorkFlowCall() {
        requestType = RequestType.EXTERNAL_DEFAULT_VALUES
        val dataObj = getDataObjectValue()

        val dataJsonObj = JsonObject()
        dataJsonObj.add(submitTaskName, dataObj)
        mViewModel.connectAppDecision(submitTaskName, externalEventName, dataJsonObj)

    }

    private fun getDataObjectValue(): JsonObject {
        val dataObj = JsonObject()

        for (i in spinnerArrayList.indices) {
            val spnrTag = spinnerArrayList[i].tag
            var spnrKey = ""
            val obj = hashMapQtcFormData[spnrTag]
            val selectItemPos = spinnerArrayList[i].selectedItemPosition
            if (selectItemPos > 0) {
                var value = JSONArray()
                if (obj?.properyKeyArray?.length() != 0) {
                    value = obj?.properyKeyArray ?: JSONArray()
                } else {
                    val objKey = obj?.key
                    value = hashMapDropDown[objKey] ?: JSONArray()

                }
                val jsonObject = value.optJSONObject(selectItemPos - 1)
                spnrKey = jsonObject.optString("key")

            }
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.defaultValue ?: ""
            }
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.recommendedDefaultValue ?: ""
            }

            dataObj.addProperty(obj?.key.toString(), spnrKey)
        }

        for (i in editTextArrayList.indices) {
            val spnrTag = editTextArrayList[i].tag
            var spnrKey = ""
            spnrKey = editTextArrayList[i].text.toString().trim()
            if (spnrKey.equals("", true)) {
                spnrKey = hashMapQtcFormData[spnrTag]?.defaultValue ?: ""
            }

            dataObj.addProperty(spnrTag.toString(), spnrKey)
        }
        return dataObj
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
        //  val layOutRes = getJsonDataFromAsset(requireActivity(), "lay_res.json").toString()
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

            workflowRecommendationNeed = layoutObj.optBoolean("recommedationNeed")


            var fieldsArr = JSONArray()


            val objectMeta = res.getJSONObject("objectMeta")
            reqParamSYS__UUID = objectMeta.optString("sys__UUID")
            reqParam_ID = objectMeta.optString("_id")

            val fields = objectMeta.getJSONObject("fields")

            var mainFieldsArr = workFowObj.getJSONArray("fields")

            noOfPages = mainFieldsArr.length()
            depMaxCount = noOfPages*2+1
            for (m in 0 until mainFieldsArr.length()) {
                fieldsArr = mainFieldsArr.getJSONArray(m)
                for (i in 0 until fieldsArr.length()) {

                    var item = fieldsArr.getJSONObject(i)

                    val key = item.optString("key")
                    val placement = item.optString("placement")
                    val label = item.optString("label")
                    val hasSort = item.optBoolean("sort")
                    val hasFilter = item.optBoolean("filter")
                    val itemType = item.optString("type")
                    val defaultValue = item.optString("value") ?: ""
                    val event = item.optString("event") ?: ""


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
                            // properyKeyArr = propertyKey.names()?.get(0) as JSONArray
                            properyKeyArr = propertyKey.optJSONArray(pkey) ?: JSONArray()
                        }


                        if (type.equals("dropdown", true)
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
                                m,
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
                                type, displayName, m, JSONArray(),
                                "", "", "", "", "", ""
                                , "")
                        hashMapQtcFormData[hashMapQtcFormData.size.toString()] = qtcFormModel
                    }

                }
            }

            mBinding.serviceTtl.setText(title)


            if (noOfPages > 1) {
                mBinding.nextTxt.text = "Next"
            }

            if (layOutName.equals("create", true)) {
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
                        parentRow.minimumHeight = 150
                      //  textInputLayout.minimumHeight = 150
                        parentRow.background =
                                ContextCompat.getDrawable(requireContext(), R.drawable.create_row_bg)

                        var labelSuffix = ""
                        if (itemValue.isRequired) {
                            labelSuffix = " *"
                        }

                        val tvLabel = createTextView(
                                requireActivity(), itemValue.displayName + labelSuffix, 0, WRAP_CONTENT,
                                1f, WorkFlowMargin(20, 10, 10, 10), Gravity.LEFT
                        )

                        val params = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)
                        params.setMargins(0, 0, 0, 0)
                        row.addView(tvLabel)
                        if (itemValue.type.equals("dropdown")) {
                            val spinner = QtcSpinner(requireContext())
                            spinner.layoutParams = params

                            //To remove down arrow from spinner
                            // spinner.getBackground().setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

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
                                                R.layout.spnner_item_lay,
                                                spinnerArray
                                        )

                                spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down_tv);
                                spinner.adapter = spinnerArrayAdapter

                            }

                            val id = spinnerArrayList.size

                            spinnerArrayList.add(id, spinner)

                            hashMapSpinnerId[itemValue.key] = id
                            hashMapSpinner[itemValue.key] = spinner
                            row.addView(spinner)
                            setItemSelectListener(spinner)
                        } else {
                            val editText = createEditText(
                                    requireActivity(), "  ", MATCH_PARENT, WRAP_CONTENT,
                                    1f, WorkFlowMargin(20, 60, 5, 0), Gravity.RIGHT
                            )
                            editText.tag = itemValue.key
                            editText.setBackgroundResource(android.R.color.transparent)

                            if (itemValue.type.equals("datepicker", true)) {

                               // editText.setFocusable(false)
                                editText.setClickable(true)
                                disableEditForDateField(editText)

                                if (itemValue.itemType.equals("hidden", true)) {
                                    //TODO need to discuss
                                    editText.setText(getCurrentDate())
                                   // editText.setText("null")
                                }

                                /*editText.setOnClickListener {

                                 //   editText.setKeyListener(EditText(requireContext()).getKeyListener());
                                    openDatePickerDialog(it)
                                }*/

                                editText.setOnTouchListener(object : View.OnTouchListener {
                                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                                        when (event?.action) {
                                            MotionEvent.ACTION_UP ->
                                                openDatePickerDialog(v!!)
                                        }

                                        return v?.onTouchEvent(event) ?: true
                                    }
                                })




                            }

                            if (itemValue.defaultValue.isNotEmpty()) {
                                editText.setText(itemValue.defaultValue)
                            }

                            editText.doOnTextChanged { text, start, count, after ->
                                changeRowBg(editText.tag.toString(),false)

                                removeErrorFromField(editText.tag.toString())

                            }

                            editTextArrayList.add(editText)
                            row.addView(editText)
                        }

                        parentRow.addView(row)
                        val tvError = createTextView(
                                requireActivity(), itemValue.displayName + labelSuffix, 0, WRAP_CONTENT,
                                1f, WorkFlowMargin(20, 10, 0, 0), Gravity.RIGHT,
                                R.color.error_txt_clr,R.dimen.error_text_sz
                        )

                        val paramsErro = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f)
                        paramsErro.setMargins(0, 0, 0, 0)
                        tvError!!.visibility   = View.GONE
                        parentRow.addView(tvError)

                        val rowId = rowArrayList.size
                        rowArrayList.add(rowId, parentRow)
                        erroTvList.add(rowId, tvError)
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
                if (workflowRecommendationNeed) {
                    requestType = RequestType.NLP_RECOMM_FOR_ALL_FIELDS
                    mViewModel.connectRecommendation(currentWorkFLowName, "",reqParamObjectValue)
                } else {
                    makeMdmCall()
                }
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
                            cancelBtnAction()
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
                            submitBtnAction()
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
                changeRowBg(spinner.tag.toString(),false)

                removeErrorFromField(spinner.tag.toString())


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

                            externalWorkFlowCall()
                        } else {
                            requestType = RequestType.DEFAULT
                            if (workflowRecommendationNeed) {
                                    requestType = RequestType.NLP_RECOMM_WITH_FIELDS_ID
                                    currentNlpFieldKey = spnrKey
                                    currentNlpField = spnrTag.toString()
                                    makeMdmCall()
                            } else {
                                requestType = RequestType.DEFAULT
                                makeMdmCall()
                            }


                        }
                    } else {
                        if (workflowRecommendationNeed) {
                            currentNlpFieldKey = spnrKey
                            requestType = RequestType.NLP_RECOMM_WITH_FIELDS_ID
                            mViewModel.connectRecommendation(currentWorkFLowName, spnrTag.toString(),reqParamObjectValue)
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


    fun openDatePickerDialog(v: View) {

        removeErrorFromField(v.tag.toString())

        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
                OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->

                    val month = monthOfYear + 1
                    var fm = "" + month
                    var fd = "" + dayOfMonth
                    if (month < 10) {
                        fm = "0$month"
                    }
                    if (dayOfMonth < 10) {
                        fd = "0$dayOfMonth"
                    }
                    val selectedDate =
                            year.toString() + "-" + fm + "-" + fd


                    val formData = hashMapQtcFormData[v.tag.toString()]
                    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
                    val outputFormat = SimpleDateFormat(formData?.dateFormat?:"yyyy-MM-dd")
                    val inputDateStr = selectedDate
                    val date: Date = inputFormat.parse(inputDateStr)
                    val outputDateStr: String = outputFormat.format(date)


                    (v as EditText).setText(outputDateStr)
                    (v as EditText).setError(null)

                    dateValidationOnSelection(v.tag.toString(),v as EditText)


                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
    private fun dateValidationOnSelection(tag :String,editText: EditText){
        if(hashMapQtcFormData.containsKey(tag)){

            val formModel = hashMapQtcFormData[tag]
            if(formModel?.event!!.isNotEmpty()){
                var value = formModel?.event
                var keysArrayList = findKeysToReplace(value)
                for(k in keysArrayList.indices){
                    val key = keysArrayList[k]
                    var compareToValue =""
                    for(j in editTextArrayList.indices){
                        val edtTxt = editTextArrayList[j]
                        if(edtTxt.tag.toString().equals(key)){
                            compareToValue = edtTxt.text.toString()
                        }
                    }

                    value = value.replace("\${"+key+"}",compareToValue)

                }
                value = value.replace("return", "")


                val expr = value
                val jsEvaluator = JsEvaluator(requireContext())
                jsEvaluator.evaluate(expr, object : JsCallback {
                    override fun onResult(result: String) {
                        if(!result.equals("true",true)){
                       //     editText.setError(result)
                           highlightFieldWithError(tag,result)

                        }

                    }

                    override fun onError(errorMessage: String) {
                    }
                })

            }

        }
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
            if(action.equals("Audio",true)){
                showAudioNlpDialog(requireContext(),"")
            }
            else if(type.equals("client",true)
                    && outComeObj.has("data")){
                val jsonObject = outComeObj.optJSONObject("data")


                val iter: Iterator<String> = jsonObject.keys()
                while (iter.hasNext()) {
                    val key = iter.next()
                    if (hashMapQtcFormData.containsKey(key)) {
                        try {

                            val value: Any = jsonObject.get(key)
                            hashMapRecommendedKeys[key] = value.toString()
                            hashMapQtcFormData[key].apply {
                                this?.nlpRecommendValue = value.toString()
                            }
                        } catch (e: JSONException) {

                        }

                    }

                }
                callDependedOnlyOnce = false
                count = 0
                clearAllValues()
                setNLPRecommendedValues()

            }
            else if (name.trim().isNotEmpty()){
                //for setting default trader Name

                if(name.startsWith("defaultsetting",true)){
                    var selecedTraderNameKey =""
                    if(hashMapQtcFormData.containsKey("traderUserId")
                            &&hashMapSpinnerId.containsKey("traderUserId")) {
                        val obj = hashMapQtcFormData["traderUserId"]
                        val spinnerId = hashMapSpinnerId["traderUserId"]
                        val selectItemPos = spinnerArrayList[spinnerId!!].selectedItemPosition
                        if (selectItemPos > 0) {
                            var value = JSONArray()
                            if (obj?.properyKeyArray?.length() != 0) {
                                value = obj?.properyKeyArray ?: JSONArray()
                            } else {
                                val objKey = obj?.key
                                value = hashMapDropDown[objKey] ?: JSONArray()

                            }
                            val jsonObject = value.optJSONObject(selectItemPos - 1)
                            selecedTraderNameKey = jsonObject.optString("key")
                        }
                        (activity as DashboardActivity).setDefaultTraderName(selecedTraderNameKey)
                    }
                }
                (activity as DashboardActivity).setLastFromTag(currentWorkFLowName)
                (activity as DashboardActivity).listClickHandler(name)
            }



        }


        return false
    }


    fun getCurrentDate(): String? {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("yyyy-MM-dd")
        return df.format(c.time)
    }

    fun addValuesForDependentSpnr(spnr: QtcSpinner, pos: Int) {
        val spnrTag = spnr.tag

        val obj = hashMapQtcFormData[spnrTag]

        checkUiVisibility(spnrTag.toString(),spnr.selectedItem.toString())
        if (obj != null) {
            addValuesToServiceKeys(obj,spnrTag.toString() ,pos )
        }


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


    fun showAudioNlpDialog(context : Context, msg: String?) {
        val dialog = Dialog(context)
        val sb = StringBuilder()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.audio_nlp_dlg)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        var isStopPressed = false

        val startRecording = dialog.findViewById<TextView>(R.id.start_record)
        val contentText = dialog.findViewById<EditText>(R.id.content_txt)

        val skipBtn = dialog.findViewById<TextView>(R.id.skip)
        skipBtn.setOnClickListener {
            speechRecognizer.destroy()
            startRecording.setText("Start Recording")
            dialog.dismiss()
        }

        val clearBtn = dialog.findViewById<TextView>(R.id.clear)
        clearBtn.setOnClickListener {
            sb.clear()
            contentText.setText(sb)
        }

        val procedBtn = dialog.findViewById<TextView>(R.id.proceed)
        procedBtn.setOnClickListener {
            speechRecognizer.destroy()
            dialog.dismiss()
            audioNlpCall = true
            mViewModel.connectNlpSentenceProcess(currentWorkFLowName, contentText.text.toString(),reqParamObjectValue)

        }



        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
            }
            override fun onBeginningOfSpeech() {
                contentText.setText(sb.toString())
            }

            override fun onRmsChanged(v: Float) {
            }
            override fun onBufferReceived(bytes: ByteArray) {
            }
            override fun onEndOfSpeech(

            ) {
            }
            override fun onError(i: Int) {
                if(!isStopPressed){
                    speechRecognizer.startListening(speechRecognizerIntent)
                }else {
                    startRecording.setText("Start Recording")
                }
            }
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                sb.append(data!![0]+" ")
                contentText.setText(sb.toString())

                if(!isStopPressed){
                    speechRecognizer.startListening(speechRecognizerIntent)
                }else {
                    startRecording.setText("Start Recording")
                }

            }

            override fun onPartialResults(bundle: Bundle) {
            }
            override fun onEvent(i: Int, bundle: Bundle) {
            }
        })

        startRecording.setOnClickListener {

            if(startRecording.text.toString().equals("Start Recording",true)){
                val recordPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                if(recordPermission == PackageManager.PERMISSION_GRANTED){
                    speechRecognizer.startListening(speechRecognizerIntent)
                    startRecording.setText("Stop Recording")
                    isStopPressed = false
                    contentText.setHint("What contract you want to create")
                }else {
                    checkPermission()
                }

            }else {
                isStopPressed = true
                speechRecognizer.stopListening()
                startRecording.setText("Start Recording")
            }




        }
        dialog.show()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), RecordAudioRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            hasRecordingPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        const val RecordAudioRequestCode = 1
    }

    fun disableEditForDateField(editText: EditText){
        editText.isCursorVisible = false
        editText.showSoftInputOnFocus = false
    }

    /**
     * used to find the keys to be replaced from value expression*/
    fun findKeysToReplace(str: String?) : java.util.ArrayList<String> {
        val arraList = java.util.ArrayList<String>()
        try {

            val regex =   "\\$\\{(.*?)\\}"
            val p: Pattern = Pattern.compile(regex)
            val m: Matcher = p.matcher(str)
            while (m.find()) {
                arraList.add(m.group(1))
            }

        }catch (e : Exception){

        }
        return arraList

    }



    private fun highlightFieldWithError(fieldTagId : String,errorMessage :String){
        try {
            val rowId = hashMapRowId[fieldTagId]
            rowArrayList[rowId!!].background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.error_row_bg)
            erroTvList[rowId!!].visibility =
                    View.VISIBLE
            erroTvList[rowId!!].setText(errorMessage)
        }catch (e : java.lang.Exception ){

        }

    }

    private fun removeErrorFromField(fieldTagId: String) {
        val rowId = hashMapRowId[fieldTagId]
        erroTvList[rowId!!].visibility =
                View.GONE
    }


    private fun getFormattedDate(inputStr: String, tagValue: String?) : String{
        try {
            val formData = hashMapQtcFormData[tagValue]
            val inputFormat = SimpleDateFormat("yyyy-MM-dd")
            val outputFormat = SimpleDateFormat(formData?.dateFormat?:"yyyy-MM-dd")
            val inputDateStr = inputStr.trim()
            val date: Date = inputFormat.parse(inputDateStr)
            val outputDateStr: String = outputFormat.format(date)
            return outputDateStr
        }catch (e : Exception){

            return inputStr
        }
    }
}





