package com.eka.cacapp.ui.farmerConnect

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.R
import com.eka.cacapp.adapter.FarmerConnSpnrAdptr
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.farmconnect.ListOfOfferDataItem
import com.eka.cacapp.databinding.CreateOffrFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.FarmerConnectRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.AppUtil.Companion.getDateForFc
import com.eka.cacapp.utils.DialogUtil.showFcValidationError
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateOfferFrag : Fragment() {

    private lateinit var mViewModel: FarmerConnectViewModel
    private lateinit var mBinding : CreateOffrFragBinding

    var dropDownFieldsKey : ArrayList<String> = arrayListOf("product","quality","priceUnit",
            "quantityUnit","cropYear", "paymentTerms",
            "location","incoTerm","packingType","packingSize");
    private var drodownHashMap : HashMap<String,JSONArray> = HashMap()

    private var isOfferTypeAdvnce : Boolean = false

    private val PAGE_ONE = "page_one"
    private val PAGE_TWO = "page_two"
    private var currentPage = PAGE_ONE

    private val OFFER_TYPE_SALE = "Sale"
    private val OFFER_TYPE_PURCHASE = "Purchase"
    private var offerType = OFFER_TYPE_SALE


    private var isModifyReqMode = false
    private lateinit var dataItem: ListOfOfferDataItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.create_offr_frag, container, false)

            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()

            (activity as DashboardActivity).clearListView()

            val factory = ViewModelFactory(FarmerConnectRepository(), requireContext())
            mViewModel = ViewModelProvider(this, factory).get(FarmerConnectViewModel::class.java)


            isModifyReqMode = arguments?.getBoolean("modifyReq") ?: false

            if(isModifyReqMode) {

                modifyView()

            }else{
                  createOfferView()
            }



            enableVwForAdvncOffereType(isOfferTypeAdvnce)

            datePickerClickListener()

            initClickListeners()

            val fieldsParam = dropDownFieldsKey.joinToString(separator = ",")

            mViewModel.getFcOfferDropDownFields(fieldsParam)

            dropDownFieldsObserver()

            publishOfferObserver()

            updateOfferObserver()

        }catch (e : Exception){

        }
        return mBinding.root

    }

    private fun createOfferView() {
        selectSaleType()
        mBinding.ttl.setText(getString(R.string.new_offer))
        if (AppPreferences.getKeyValue(Constants.PrefCode.FC_OFFER_TYPE, "").toString().equals("advanced", true)) {
            isOfferTypeAdvnce = true
        }
    }

    private fun modifyView() {
        val gson = Gson()
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_OFFER, "").toString()
        dataItem = gson.fromJson(json, ListOfOfferDataItem::class.java)

        isOfferTypeAdvnce = !(dataItem.packingSize==null ||dataItem.packingType==null
                ||dataItem.paymentTerms==null)

        if(dataItem.offerType.equals("Sale")){
            selectSaleType()
        }else{
            selectPurchaseType()
        }
        mBinding.purchaseBtn.isEnabled = false
        mBinding.saleBtn.isEnabled = false
        mBinding.ttl.setText(dataItem.bidId)
    }

    private fun publishOfferObserver(){
        mViewModel.fcPubishNewOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        val result = it.value.string()

                        val resultJson = JSONObject(result)
                        val bidId = resultJson.optString("bidId")
                        showFcOfferSuccessDialog(requireContext(),
                                bidId+" is successfully published.")

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }


            }

        })


    }
    private fun updateOfferObserver(){
        mViewModel.fcPubishPutOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        val result = it.value.string()

                        val resultJson = JSONObject(result)
                        if(resultJson.optBoolean("success")){
                            DialogUtil.infoPopUp(requireContext(),
                                    getString(R.string.success), getString(R.string.offer_updated_msg),
                                    { handleBackPress() }
                            )
                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        handleApiError(it){null}
                    }
                }


            }

        })


    }

    private fun dropDownFieldsObserver(){
        mViewModel.fcOfferDropdownValues.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        val result = it.value.string()

                        val resultArr = JSONArray(result)
                        for(i in 0 until resultArr.length()){
                            val jsonObj = resultArr.optJSONObject(i)
                            val keyName = dropDownFieldsKey.get(i)
                            drodownHashMap.put(keyName,jsonObj.optJSONArray("data"))

                        }


                        setAdaptorsToDropDown()


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }


            }

        })

    }

    private fun datePickerClickListener(){
        mBinding.expryDtEdtTxt.setOnClickListener {
            openDatePickerDialog(it)
        }

        mBinding.delFromEdtTxt.setOnClickListener {
            openDatePickerDialog(it)
        }

        mBinding.delToEdtTxt.setOnClickListener {
            openDatePickerDialog(it)
        }
    }

    private fun initClickListeners(){
        mBinding.nextTv.setOnClickListener {
            if(currentPage.equals(PAGE_ONE)){
                secondPageView()
            }else{
                if(validateInputs()){
                    onPublish()
                }
                else{
                    showFcValidationError(requireContext(),"",getString(R.string.kindly_entr_mand_values))
                }

            }
        }
        mBinding.cancelTv.setOnClickListener {
            if(currentPage.equals(PAGE_TWO)){
                firstPageView()
            }else {
                handleBackPress()
            }
        }
        mBinding.closeIcon.setOnClickListener {
            handleBackPress()
        }

        mBinding.saleBtn.setOnClickListener {
            selectSaleType()
        }
        mBinding.purchaseBtn.setOnClickListener {
            selectPurchaseType()
        }
    }


    fun selectSaleType(){
        offerType = OFFER_TYPE_SALE
        selectedBtnbg(mBinding.saleBtn)
        deSelectBtnbg(mBinding.purchaseBtn)
    }
    fun selectPurchaseType(){
        offerType = OFFER_TYPE_PURCHASE
        selectedBtnbg(mBinding.purchaseBtn)
        deSelectBtnbg(mBinding.saleBtn)
    }


    fun handleBackPress(){
        requireActivity().onBackPressed()
    }

    fun firstPageView(){
        currentPage = PAGE_ONE
        mBinding.pageOne.visibility = View.VISIBLE
        mBinding.pageTwo.visibility = View.GONE
        mBinding.cancelTv.setText(getString(R.string.cancel))
        mBinding.nextTv.setText(getString(R.string.next))
    }
    fun secondPageView(){
        currentPage = PAGE_TWO
        mBinding.pageOne.visibility = View.GONE
        mBinding.pageTwo.visibility = View.VISIBLE
        mBinding.cancelTv.setText(getString(R.string.back))
        if(isModifyReqMode){
            mBinding.nextTv.setText(getString(R.string.update))
        }else{
            mBinding.nextTv.setText(getString(R.string.publish))
        }

    }

    fun setAdaptorsToDropDown(){


        val productsJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(0))!!)
        val qualityJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(1))!!)
        val priceUnitJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(2))!!)
        val quantityUnitJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(3))!!)
        val cropYearJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(4))!!)
        val locationJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(6))!!)
        val incoTermJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(7))!!)


        var productsDefIndex=-1
        var qualityDefIndex=-1
        var priceUnitDefIndex=-1
        var quantityDefIndex=-1
        var cropYearDefIndex=-1
        var locationDefIndex=-1
        var incoTermDefIndex=-1
        if(isModifyReqMode){
             productsDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(0))!!,dataItem.product)
             qualityDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(1))!!,dataItem.quality)
             priceUnitDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(2))!!,dataItem.priceUnit)
             quantityDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(3))!!,dataItem.quantityUnit)
             cropYearDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(4))!!,dataItem.cropYear)
             locationDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(6))!!,dataItem.location)
             incoTermDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(7))!!,dataItem.incoTerm)

             mBinding.quantityEdtTxt.setText(dataItem.quantity.toString())
            mBinding.delToEdtTxt.setText(getDateForFc(dataItem.deliveryToDate))
             mBinding.delFromEdtTxt.setText(getDateForFc(dataItem.deliveryFromDate))
            mBinding.pubPrcEdtTxt.setText(dataItem.publishedPrice.toString())
            mBinding.expryDtEdtTxt.setText(getDateForFc(dataItem.expiryDate))
        }


        setSpinnerAdapter(mBinding.productSpnr,productsJsonArr,productsDefIndex+1)
        setSpinnerAdapter(mBinding.qualitySpnr,qualityJsonArr,qualityDefIndex+1)
        setSpinnerAdapter(mBinding.pubPrcSpnr,priceUnitJsonArr,priceUnitDefIndex+1)
        setSpinnerAdapter(mBinding.quantitySpnr,quantityUnitJsonArr,quantityDefIndex+1)
        setSpinnerAdapter(mBinding.cropYrSpnr,cropYearJsonArr,cropYearDefIndex+1)
        setSpinnerAdapter(mBinding.locationSpnr,locationJsonArr,locationDefIndex+1)
        setSpinnerAdapter(mBinding.incoTermSpnr,incoTermJsonArr,incoTermDefIndex+1)

        if(isOfferTypeAdvnce){
            val paymentTermJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(5))!!)
            val packagingTypeJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(8))!!)
            val packingSizeJsonArr = getValuesForDropDown(drodownHashMap.get(dropDownFieldsKey.get(9))!!)

            var paymentTermDefIndex =-1
            var packingTypeDefIndex =-1
            var packingSizeDefIndex =-1
            if(isModifyReqMode){
                paymentTermDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(5))!!,dataItem.paymentTerms)
                packingTypeDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(8))!!,dataItem.packingType)
                packingSizeDefIndex = getSpnrDefaultValuIndex(drodownHashMap.get(dropDownFieldsKey.get(9))!!,dataItem.packingSize)

            }


            setSpinnerAdapter(mBinding.paymentTermSpnr,paymentTermJsonArr,paymentTermDefIndex+1)
            setSpinnerAdapter(mBinding.packingTypeSpnr,packagingTypeJsonArr,packingTypeDefIndex+1)
            setSpinnerAdapter(mBinding.packingSizeSpnr,packingSizeJsonArr,packingSizeDefIndex+1)
        }


    }


    fun setSpinnerAdapter(spnr : Spinner,spnrArr : ArrayList<String>,defVal : Int =0){

        val adapter =  FarmerConnSpnrAdptr(requireContext(), android.R.layout.simple_spinner_item, spnrArr)
        adapter.setDropDownViewResource(R.layout.spinner_drop_down_tv)
        spnr.setAdapter(adapter)
        spnr.setSelection(defVal)
    }


    fun getSpnrDefaultValuIndex(jsonArr : JSONArray, key : String):Int{
        var index =0
        for(i in 0 until jsonArr.length()){
            val jObj = jsonArr.optJSONObject(i)
            if(jObj.optString("value").equals(key,true)){
                return i
            }

        }
        return index
    }



    fun getValuesForDropDown(jsonArr : JSONArray):ArrayList<String>{
        var resultList : ArrayList<String> = ArrayList()
        for(i in 0 until jsonArr.length()){
            val jObj = jsonArr.optJSONObject(i)
            if(i==0){
                resultList.add("Select")
            }
            resultList.add(jObj.optString("value"))
        }
        return resultList
    }




    fun getSelectedSpnrKey(jsonArr: JSONArray,selPos : Int): String{
        val jsonObj = jsonArr.optJSONObject(selPos)
        return jsonObj.optString("value")
    }

    fun selectedBtnbg(btn : Button){
        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fc_btn_check, 0, R.drawable.ic_fc_check_deslct, 0);
        btn.background = ContextCompat.getDrawable(requireContext(),R.drawable.fc_btn_slct_bg)

    }
    fun deSelectBtnbg(btn : Button){
        btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_fc_check_deslct, 0, R.drawable.ic_fc_check_deslct, 0);
        btn.background = ContextCompat.getDrawable(requireContext(),R.drawable.fc_btn_deslct_bg)
    }

    fun enableVwForAdvncOffereType(isOfferTypeAdvnce : Boolean){
        mBinding.packingSizeSpnr.visibility =  if(isOfferTypeAdvnce) View.VISIBLE else View.GONE
        mBinding.packingSzLbl.visibility = if(isOfferTypeAdvnce) View.VISIBLE else View.GONE

        mBinding.packingTypeSpnr.visibility = if(isOfferTypeAdvnce) View.VISIBLE else View.GONE
        mBinding.packingTypeLbl.visibility = if(isOfferTypeAdvnce) View.VISIBLE else View.GONE

        mBinding.paymentTermSpnr.visibility = if(isOfferTypeAdvnce) View.VISIBLE else View.GONE
        mBinding.paymentTermLbl.visibility = if(isOfferTypeAdvnce) View.VISIBLE else View.GONE
    }

    fun openDatePickerDialog(v: View) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate =
                            year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()


                    (v as EditText).setText(selectedDate)
                  //  (v as EditText).setError(null)

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
        datePickerDialog.show()
    }

    fun convertDateToIsoStr(inputDt : String):String{
        val dateFormat =  SimpleDateFormat("yyyy-MM-dd");
        val inputdate = dateFormat.parse(inputDt)
        val tz = TimeZone.getTimeZone("UTC")

        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",Locale.US)
        df.setTimeZone(tz)
        val dateAsISO: String = df.format(inputdate)
        return dateAsISO
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    private fun onPublish(){

        val productValue =  getSpnrSelectedKeyVal( mBinding.productSpnr,dropDownFieldsKey.get(0))
        val qualityValue =  getSpnrSelectedKeyVal( mBinding.qualitySpnr,dropDownFieldsKey.get(1))
        val priceUnitValue =  getSpnrSelectedKeyVal( mBinding.pubPrcSpnr,dropDownFieldsKey.get(2))
        val quantityUnitValue =  getSpnrSelectedKeyVal( mBinding.quantitySpnr,dropDownFieldsKey.get(3))
        val cropYearValue =  getSpnrSelectedKeyVal( mBinding.cropYrSpnr,dropDownFieldsKey.get(4))
        val locationValue =  getSpnrSelectedKeyVal( mBinding.locationSpnr,dropDownFieldsKey.get(6))
        val incoTermValue =  getSpnrSelectedKeyVal( mBinding.incoTermSpnr,dropDownFieldsKey.get(7))

        var packingTypeValue = ""
        var packingSizeValue = ""
        var paymentTermsValue = ""
        if(isOfferTypeAdvnce){
            packingTypeValue =  getSpnrSelectedKeyVal( mBinding.packingTypeSpnr,dropDownFieldsKey.get(8))
             packingSizeValue =  getSpnrSelectedKeyVal( mBinding.packingSizeSpnr,dropDownFieldsKey.get(9))
             paymentTermsValue =  getSpnrSelectedKeyVal( mBinding.paymentTermSpnr,dropDownFieldsKey.get(5))
        }


        val quantityValue =  mBinding.quantityEdtTxt.text.toString().trim()
        val deliveryToDateValue =  mBinding.delToEdtTxt.text.toString().trim()
        val deliveryFromDateValue =  mBinding.delFromEdtTxt.text.toString().trim()
        val publishPriceValue =  mBinding.pubPrcEdtTxt.text.toString().trim()
        val expiryDateValue =  mBinding.expryDtEdtTxt.text.toString().trim()

       val reqParam= createSubReq(quantityValue,qualityValue,deliveryToDateValue,priceUnitValue,productValue,
        publishPriceValue,"",quantityUnitValue,locationValue,cropYearValue,
                expiryDateValue,incoTermValue,deliveryFromDateValue,
               paymentTermsValue,packingTypeValue,packingSizeValue)

        if(comapreFromAndToDate(deliveryFromDateValue,deliveryToDateValue)){
            sendPublishReq(reqParam)
        }else{
            showFcValidationError(requireContext(),"",getString(R.string.kindly_entr_valid_del_dates_fc))
        }

    }

    private fun validateInputs():Boolean{


        var basicTypeRes = checkSpinnerSelected(mBinding.productSpnr,mBinding.qualitySpnr,mBinding.pubPrcSpnr,
                mBinding.quantitySpnr,mBinding.cropYrSpnr,mBinding.locationSpnr,mBinding.incoTermSpnr)

        var advanceTypeRes = checkSpinnerSelected(mBinding.packingTypeSpnr,mBinding.packingSizeSpnr,
                mBinding.paymentTermSpnr)


        var textFieldVal = checkIfTextFieldEmpty(mBinding.quantityEdtTxt,mBinding.delToEdtTxt,
                mBinding.delFromEdtTxt,mBinding.pubPrcEdtTxt,mBinding.expryDtEdtTxt )

        val basicTypeResult = basicTypeRes && textFieldVal

        if(isOfferTypeAdvnce){
            return basicTypeResult && advanceTypeRes
        }else{
            return  basicTypeResult
        }


    }

    private fun checkIfTextFieldEmpty(vararg editText : EditText):Boolean{
        val result : Boolean = true
        for (item in editText) {
            if(item.text.toString().trim().isEmpty())
                return false
        }
        return result
    }

    private fun checkSpinnerSelected(vararg spinner: Spinner) : Boolean {
        val result : Boolean = true
        for (spnr in spinner) {
            if(!isSpinnerSelected(spnr))
                return false
        }
        return result
    }


    private fun isSpinnerSelected(spnr :Spinner): Boolean{
       return spnr.selectedItemPosition !=0
    }

    private fun getSpnrSelectedKeyVal (spnr: Spinner,hashMapKey :String):String{
        val selKey= getSelectedSpnrKey(drodownHashMap.get(hashMapKey)!!,
                spnr.selectedItemPosition-1)
        return selKey
    }

    private fun sendPublishReq(reqParams: JsonObject){
        if(isModifyReqMode){
            mViewModel.sendFcPublishPutOfferReq(reqParams,dataItem.bidId)
        }else{
            mViewModel.sendFcPublishNewOfferReq(reqParams)
        }

    }


    private fun createSubReq(quantity:String,quality:String,deliveryToDate:String,
                     priceUnit :String,product:String,publishedPrice:String,
                     bidId :String,quantityUnit:String,location:String,
                     cropYear:String, expiryDate:String,
                     incoTerm:String,deliveryFromDate:String,
                     paymentTermVal:String,packaingTypeVal:String,packaingSizeVal:String) : JsonObject{

        var bidIdVal = bidId
        if(isModifyReqMode){
            bidIdVal = dataItem.bidId
        }
        val subReqParam = JsonObject()
        subReqParam.addProperty("quantity",quantity)
        subReqParam.addProperty("quality",quality)
        subReqParam.addProperty("deliveryToDateISOString",convertDateToIsoStr(deliveryToDate))
        subReqParam.addProperty("priceUnit",priceUnit)

        subReqParam.addProperty("product",product)
        subReqParam.addProperty("publishedPrice",publishedPrice)
        subReqParam.addProperty("bidId",bidIdVal)
        subReqParam.addProperty("quantityUnit",quantityUnit)

        subReqParam.addProperty("location",location)
        if(!isModifyReqMode){
            subReqParam.addProperty("offerType",offerType)
        }
        subReqParam.addProperty("cropYear",cropYear)
        subReqParam.addProperty("expiryDateISOString",convertDateToIsoStr(expiryDate))
        subReqParam.addProperty("incoTerm",incoTerm)
        subReqParam.addProperty("deliveryFromDateISOString",convertDateToIsoStr(deliveryFromDate))

        if(isOfferTypeAdvnce){
            subReqParam.addProperty("paymentTerms",paymentTermVal)
            subReqParam.addProperty("packingType",packaingTypeVal)
            subReqParam.addProperty("packingSize",packaingSizeVal)

        }

        return subReqParam
    }


    fun showFcOfferSuccessDialog(context : Context, msg: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val metrics: DisplayMetrics = resources.displayMetrics


        dialog.setCancelable(false)
        dialog.setContentView(R.layout.fc_offr_success_dialog)
        val text = dialog.findViewById(R.id.text_msg) as TextView
        text.text = msg

        val blankOffer: Button = dialog.findViewById(R.id.new_blank_offer) as Button
        val duplicateOffer: Button = dialog.findViewById(R.id.duplicate_offer) as Button

        blankOffer.setOnClickListener {
            dialog.dismiss()
            refreshFrag()
        }

        duplicateOffer.setOnClickListener {
            dialog.dismiss()
            firstPageView()
        }

        val exitBtn: TextView = dialog.findViewById(R.id.exit_btn) as TextView
        exitBtn.setOnClickListener {
            dialog.dismiss()
            handleBackPress()
        }
        dialog.show()
        val width: Int = metrics.widthPixels
        dialog.getWindow()?.setLayout((6 * width)/7, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private fun refreshFrag(){
        findNavController().navigate(R.id.action_createOfferFrag_self)
    }





    fun comapreFromAndToDate(dateOneStr: String,dateTwoStr:String) : Boolean{

        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateOne = dateFormat.parse(dateOneStr)
            val dateTwo = dateFormat.parse(dateTwoStr)
            return dateTwo.after(dateOne) || dateOne == dateTwo
        }catch (e : Exception){
            return false
        }

    }
}