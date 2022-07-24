package com.eka.cacapp.ui.farmerConnect

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.eka.cacapp.R
import com.eka.cacapp.adapter.BidLogsAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.farmconnect.BidLogData
import com.eka.cacapp.data.farmconnect.ListOfOfferDataItem
import com.eka.cacapp.databinding.BidDtlFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.FarmerConnectRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.FarmerConnectUtils.milliSecToDate
import com.eka.cacapp.utils.FarmerConnectUtils.milliSecToFcUpdtDate
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BidDetailsFrag : Fragment() {

    private lateinit var mBinding: BidDtlFragBinding
    private lateinit var dataItem: ListOfOfferDataItem
    private lateinit var mViewModel: FarmerConnectViewModel
    private  var moreMenuList : ArrayList<String> = ArrayList()
    private var fc_userAccess = ""
    private  val BIDER = "bider"
    private  val OFFERER = "offrer"
    private  val BOTH = "both"
    private var bidderViaPubPrc = false
    private lateinit var cancelRejctDlg : Dialog
    var bidLogsDataArrayList  = ArrayList<BidLogData>()
    private var bidLogTtl = ""
    private var ratingTypeArrList = ArrayList<String>()
    private lateinit var ratingsDlg : Dialog


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


            mBinding = DataBindingUtil.inflate(inflater, R.layout.bid_dtl_frag, container, false)

            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()


            (activity as DashboardActivity).clearListView()

            val factory = ViewModelFactory(FarmerConnectRepository(), requireContext())
            mViewModel = ViewModelProvider(this, factory).get(FarmerConnectViewModel::class.java)

            bidderViaPubPrc = arguments?.getBoolean("bdr_pub_prc") ?: false

            getSelectedData()

            if(bidderViaPubPrc){
                mBinding.moreImg.visibility = View.GONE
                mBinding.ttl.setText(dataItem.bidId)
            }else{
                mBinding.ttl.setText(dataItem.refId)
            }


            mBinding.backIcon.setOnClickListener {
                requireActivity().onBackPressed()
            }

            mBinding.moreImg.setOnClickListener {
                showPopup(it)
            }



            counterCheckBoxHandler()

            checkPerCodesDtl()

            checkBidStatusAndUpdateUI()

            cancelBidObserver()
            rejectBidObserver()

            accpeptCounterListerner()

            datePickerClickListener()

            acceptCounterObserver()

            acceptByOfferorObserver()

            bidQuantityLockCheck()

            bidLogsObserver()

            bidRatingObserver()


            mBinding.resetTv.setOnClickListener {
                requireActivity().onBackPressed()
            }

        } catch (e: Exception) {

        }

        return mBinding.root

    }

    private fun bidRatingObserver() {
        mViewModel.fcOfferRatingResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        try {

                            val result = it.value.string()
                            val jsonObj = JSONObject(result)
                            if(jsonObj.optBoolean("success")){
                                ratingsDlg.dismiss()
                                requireActivity().onBackPressed()
                            }


                        }catch (e : java.lang.Exception){

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

    private fun bidQuantityLockCheck() {
        mBinding.quantityEdtTxt.isEnabled =
            !AppPreferences.getKeyValue(Constants.PrefCode.FC_BID_QUANTITY_LOCKED,"").equals("true",true)
    }

    private fun accpeptCounterListerner() {
        mBinding.applyTv.setOnClickListener {
            val btnStatus = mBinding.applyTv.text.toString().trim()

            val remarks = mBinding.remarksEdtTxt.text.toString().trim()

            if(btnStatus.equals("Send")){

                val counterPrice = mBinding.counterEdtTxt.text.toString().trim()
                if(fc_userAccess.equals(OFFERER)){
                    //Counter by Offeror
                    if(!counterPrice.isEmpty()){
                        offerorCounterTheDeal(remarks,counterPrice)
                    }else{
                        DialogUtil.showFcValidationError(requireContext(), "", getString(R.string.kindly_entr_mand_values))
                 //       Toast.makeText(requireContext(),getString(R.string.pls_provide_all_values),Toast.LENGTH_SHORT).show()
                    }

                }else{
                    //Counter By Bidder

                    if(dataItem.status != null){
                        if(dataItem.status.equals("In-Progress")){
                            //for manage Bids Counter
                            if(!counterPrice.isEmpty()){
                                offerorCounterTheDeal(remarks,counterPrice)
                            }else{
                                DialogUtil.showFcValidationError(requireContext(), "", getString(R.string.kindly_entr_mand_values))
                             //   Toast.makeText(requireContext(),getString(R.string.pls_provide_all_values),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else {
                        // for published price counter

                        val toDateMilli = convertDateToMilliSec(mBinding.delToEdtTxt.text.toString().trim()).toString()
                        val fromDateMilli = convertDateToMilliSec(mBinding.delFromEdtTxt.text.toString().trim()).toString()
                        val quantity = mBinding.quantityEdtTxt.text.toString().trim()

                        if(counterPrice.isEmpty() || quantity.isEmpty()){
                            DialogUtil.showFcValidationError(requireContext(), "", getString(R.string.kindly_entr_mand_values))
//                            Toast.makeText(requireContext(),getString(R.string.pls_provide_all_values),Toast.LENGTH_SHORT).show()
                        }else{
                            bidderCounterTheDeal(fromDateMilli,toDateMilli,
                                    quantity,remarks,counterPrice)
                        }
                    }


                }
            }else{

                if(fc_userAccess.equals(OFFERER)){
                    //Accept by Offeror
                    var agreedPrice = dataItem.latestBidderPrice.toString()
                    if(agreedPrice.isEmpty()){
                        agreedPrice = dataItem.publishedPrice.toString()
                    }
                    DialogUtil.confirmationPopUp(requireContext(),
                            getString(R.string.confirmation),
                            getString(R.string.yor_are_accepting)+agreedPrice+" "+dataItem.priceUnit,
                            getString(R.string.accept),
                            getString(R.string.cancel),
                            { offerorAcceptTheDeal(remarks)} )

                }else{
                    //Accepted by Bidder

                    val quantity = mBinding.quantityEdtTxt.text.toString().trim()
                    if(dataItem.status != null){
                        if(dataItem.status.equals("In-Progress")){
                            //for manage Bids Accept
                            var agreedPrice = dataItem.latestOfferorPrice.toString()
                            if(agreedPrice.isEmpty()){
                                agreedPrice = dataItem.publishedPrice.toString()
                            }
                            DialogUtil.confirmationPopUp(requireContext(),
                                getString(R.string.confirmation),
                                getString(R.string.yor_are_accepting)+agreedPrice+" "+dataItem.priceUnit,
                                getString(R.string.accept),
                                getString(R.string.cancel),
                                    { offerorAcceptTheDeal(remarks)} )
                        }
                    }else{
                        //for published price accept
                        val toDateMilli = convertDateToMilliSec(mBinding.delToEdtTxt.text.toString().trim()).toString()
                        val fromDateMilli = convertDateToMilliSec(mBinding.delFromEdtTxt.text.toString().trim()).toString()

                        if(quantity.isEmpty()){
                            Toast.makeText(requireContext(),getString(R.string.pls_provide_all_values),Toast.LENGTH_SHORT).show()
                        }else{
                            DialogUtil.confirmationPopUp(requireContext(),
                                getString(R.string.confirmation),
                                getString(R.string.yor_are_accepting)+dataItem.publishedPrice+" "+dataItem.priceUnit,
                                getString(R.string.accept),
                                getString(R.string.cancel),
                                    { bidderAcceptTheDeal(fromDateMilli,toDateMilli,
                                            quantity,remarks)} )
                        }
                    }

                }

            }


        }
    }
    private fun offerorCounterTheDeal(remarks:String,counterPrice: String){
        val reqParam = JsonObject()
        reqParam.addProperty("price",counterPrice)
        reqParam.addProperty("remarks",remarks)
        reqParam.addProperty("status","In-Progress")
        val isOfferor = fc_userAccess.equals(OFFERER)
        mViewModel.sendAcceptCounterByOfferor(reqParam,dataItem.refId,isOfferor)
    }

    private fun offerorAcceptTheDeal(remarks:String){
        val reqParam = JsonObject()
        reqParam.addProperty("remarks",remarks)
        reqParam.addProperty("status","Accepted")
        val isOfferor = fc_userAccess.equals(OFFERER)
        mViewModel.sendAcceptCounterByOfferor(reqParam,dataItem.refId,isOfferor)
    }


    private fun bidderAcceptTheDeal(fromDateMillis:String,toDateMillis:String,
                             quantity:String,remarks:String){

        val reqParams = JsonObject()
        reqParams.addProperty("quantity",quantity)
        reqParams.addProperty("bidId",dataItem.bidId)
        reqParams.addProperty("status","Accepted")
        reqParams.addProperty("deliveryFromDateInMillis",fromDateMillis)
        reqParams.addProperty("deliveryToDateInMillis",toDateMillis)
        reqParams.addProperty("remarks",remarks)

        mViewModel.acceptOfferReq(reqParams)
    }

    private fun bidderCounterTheDeal(fromDateMillis:String,toDateMillis:String,
                                     quantity:String,remarks:String,counterPrice:String){
        val reqParams = JsonObject()
        reqParams.addProperty("quantity",quantity)
        reqParams.addProperty("bidId",dataItem.bidId)
        reqParams.addProperty("status","In-Progress")
        reqParams.addProperty("deliveryFromDateInMillis",fromDateMillis)
        reqParams.addProperty("deliveryToDateInMillis",toDateMillis)
        reqParams.addProperty("remarks",remarks)
        reqParams.addProperty("price",counterPrice)
        mViewModel.acceptOfferReq(reqParams)
    }


    private fun acceptCounterObserver(){
        mViewModel.acceptOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        DialogUtil.infoPopUp(requireContext(),
                                getString(R.string.success), getString(R.string.bid_placed_successfully),
                                {requireActivity().onBackPressed()} )

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        handleApiError(it){null}
                    }
                }


            }

        })


    }


    private fun acceptByOfferorObserver(){
        mViewModel.fcAcceptCountrByOfferorResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        DialogUtil.infoPopUp(requireContext(),
                            getString(R.string.success), getString(R.string.your_msg_hs_sent),
                                {requireActivity().onBackPressed()} )

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        handleApiError(it){null}
                    }
                }


            }

        })


    }

    private fun bidLogsObserver(){
        mViewModel.bidLogsResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){

                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()

                        try {

                            val result = it.value.string()
                            val jsonObj = JSONObject(result)
                            val priceUnitBidLogs = jsonObj.optString("priceUnit")
                            val negotiationLogsArr = jsonObj.optJSONArray("negotiationLogs")
                            val rowCount = negotiationLogsArr.length()-1
                            for(i in 0 until negotiationLogsArr.length()){
                                val indx = rowCount-i
                                val negoLogObj = negotiationLogsArr.optJSONObject(indx)
                                var logType = -5
                                if(negoLogObj.has("logType")){
                                    logType = negoLogObj.optInt("logType")
                                }
                                bidLogsDataArrayList.add(BidLogData(negoLogObj.optString("by"),
                                        negoLogObj.optLong("date"),logType,
                                        negoLogObj.optString("name"),negoLogObj.optString("remarks"),
                                        negoLogObj.optString("userId"), negoLogObj.optInt("price")
                                ))

                            }


                            showBidLogsView(bidLogsDataArrayList,priceUnitBidLogs)
                        }catch (e : java.lang.Exception){

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

    private fun bidderPubPriceView(){
        mBinding.fcTvR1.visibility = View.VISIBLE
        mBinding.fcTvR2.visibility = View.VISIBLE
        mBinding.fcTvR3.visibility = View.VISIBLE
        mBinding.fcTvR4.visibility = View.VISIBLE
        mBinding.fcTvR5.visibility = View.VISIBLE
        mBinding.fcTvR6.visibility = View.VISIBLE
        mBinding.fcTvR7.visibility = View.VISIBLE
        mBinding.fcTvR8.visibility = View.VISIBLE

        mBinding.fcTvR1.setLabelAndValue(getString(R.string.produt_lbl), dataItem.product)
        mBinding.fcTvR2.setLabelAndValue(getString(R.string.quality_lbl), dataItem.quality)
        mBinding.fcTvR3.setLabelAndValue(getString(R.string.location), dataItem.location)
        mBinding.fcTvR4.setLabelAndValue(getString(R.string.inco_term_lbl), dataItem.incoTerm)
        mBinding.fcTvR5.setLabelAndValue(getString(R.string.crop_year_lbl), dataItem.cropYear)

        mBinding.fcTvR6.setLabelAndValue(getString(R.string.bid_id), dataItem.bidId)
        mBinding.fcTvR7.setLabelAndValue(getString(R.string.offer_type), dataItem.offerType)
        mBinding.fcTvR8.setLabelAndValue(getString(R.string.publshed_prc), dataItem.publishedPrice.toString()+" "+dataItem.priceUnit )


        mBinding.bidInProgrsDtl.visibility = View.GONE
        mBinding.remarksEdtTxt.visibility = View.VISIBLE
        mBinding.remarksLbl.visibility = View.VISIBLE
        mBinding.bidStatusDtlLay.visibility = View.GONE

        mBinding.bidDtlFooter.visibility = View.VISIBLE
        mBinding.counterLay.visibility = View.VISIBLE
        mBinding.counterCb.visibility = View.VISIBLE

        mBinding.quantityEdtTxt.visibility = View.VISIBLE
        mBinding.quantiUnitLbl.visibility = View.VISIBLE
        mBinding.delPerLay.visibility = View.VISIBLE
        mBinding.delPerLbl.visibility = View.VISIBLE

        mBinding.countrPrcUntLbl.setText(dataItem.priceUnit)
        mBinding.delToEdtTxt.setText(AppUtil.getDateForFc(dataItem.deliveryToDate))
        mBinding.delFromEdtTxt.setText(AppUtil.getDateForFc(dataItem.deliveryFromDate))

        mBinding.quantityEdtTxt.setText(dataItem.quantity.toString())
        mBinding.quantiUnitLbl.setText(dataItem.quantityUnit)


    }
    private fun acceptedRejectedCancelledView(viewType :String){

        mBinding.fcTvR1.visibility = View.VISIBLE
        mBinding.fcTvR2.visibility = View.VISIBLE
        mBinding.fcTvR3.visibility = View.VISIBLE
        mBinding.fcTvR4.visibility = View.VISIBLE
        mBinding.fcTvR5.visibility = View.VISIBLE
        mBinding.fcTvR6.visibility = View.VISIBLE
        mBinding.fcTvR7.visibility = View.VISIBLE
        mBinding.fcTvR8.visibility = View.VISIBLE
        mBinding.fcTvR9.visibility = View.VISIBLE
        mBinding.fcTvR10.visibility = View.VISIBLE
        mBinding.fcTvR11.visibility = View.VISIBLE
        mBinding.fcTvR12.visibility = View.VISIBLE
        mBinding.fcTvR13.visibility = View.VISIBLE



        mBinding.fcTvR1.setLabelAndValue(getString(R.string.offer_type), dataItem.offerType)
        mBinding.fcTvR2.setLabelAndValue(getString(R.string.offer_id), dataItem.bidId)
        mBinding.fcTvR3.setLabelAndValue(getString(R.string.inco_term_lbl), dataItem.incoTerm)
        mBinding.fcTvR4.setLabelAndValue(getString(R.string.produt_lbl), dataItem.product)
        mBinding.fcTvR5.setLabelAndValue(getString(R.string.quality_lbl), dataItem.quality)
        mBinding.fcTvR6.setLabelAndValue(getString(R.string.crop_year_lbl), dataItem.cropYear)
        mBinding.fcTvR7.setLabelAndValue(getString(R.string.location), dataItem.location)
        mBinding.fcTvR8.setLabelAndValue(getString(R.string.quantity_label), dataItem.quantity.toString() + " " + dataItem.quantityUnit)
        mBinding.fcTvR9.setLabelAndValue(getString(R.string.packing_type), dataItem.packingType?:"")
        mBinding.fcTvR10.setLabelAndValue(getString(R.string.packing_size), dataItem.packingSize?:"")
        mBinding.fcTvR11.setLabelAndValue(getString(R.string.payment_term_lbl), dataItem.paymentTerms?:"")
        mBinding.fcTvR12.setLabelAndValue(getString(R.string.delv_period), milliSecToDate(dataItem.deliveryFromDateInMillis)
                + " to " + milliSecToDate(dataItem.deliveryToDateInMillis))
        mBinding.fcTvR13.setLabelAndValue(getString(R.string.offeror_ratings), dataItem.rating)


        mBinding.bidInProgrsDtl.visibility = View.VISIBLE

        mBinding.pubPrcView.setLabelAndValue(getString(R.string.publshed_prc),pendingIfEmpty(dataItem.publishedPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.setLabelAndValue(getString(R.string.ltst_biddr_prc),pendingIfEmpty(dataItem.latestBidderPrice.toString(),dataItem.priceUnit))
        mBinding.latOfrPrcView.setLabelAndValue(getString(R.string.latst_offeror_prc),pendingIfEmpty(dataItem.latestOfferorPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.makeValueBold()
        mBinding.latOfrPrcView.makeValueBold()
        mBinding.pubPrcView.makeValueBold()

        hideCounterView()

        setBidDetailStatusValue(viewType)
    }

    private fun setBidDetailStatusValue(type : String){

        var updatedByVal = dataItem.updatedBy
        if(dataItem.updatedBy.equals("Offeror") && fc_userAccess.equals(OFFERER)){
            updatedByVal = "you"
        }else if(dataItem.updatedBy.equals("Bidder") && fc_userAccess.equals(BIDER)){
            updatedByVal = "you"
        }

        var agreedPrice = getLastAgreedPrice()

        if(type.equals("accepted",true)){

            if(fc_userAccess.equals(BIDER)){

                mBinding.fcTvR14.visibility = View.VISIBLE

                if(dataItem.currentBidRating.isEmpty()){
                    mBinding.fcTvR14.makeValueClickable()
                    mBinding.fcTvR14.setLabelAndValue(getString(R.string.your_rating), getString(R.string.rate_now))
                    mBinding.fcTvR14.onValueClickListener {
                        rateNowView()
                    }
                }else {
                    mBinding.fcTvR14.setLabelAndValue(getString(R.string.your_rating), dataItem.currentBidRating)
                }


            }

            mBinding.bidStatusDtlTv.setText(getString(R.string.deal_accpted_by)+updatedByVal+" on "+"" +
                    ""+milliSecToFcUpdtDate(dataItem.updatedDate)+
                    " at "+agreedPrice+" "+dataItem.priceUnit)
        }else if (type.equals("rejected",true)){
            mBinding.bidStatusDtlTv.setText(getString(R.string.deal_rejected_by)+updatedByVal+" on "+"" +
                    ""+milliSecToFcUpdtDate(dataItem.updatedDate))
        }else if (type.equals("cancelled",true)){
            mBinding.bidStatusDtlTv.setText(getString(R.string.deal_cancelled_by)+updatedByVal+" on "+"" +
                    ""+milliSecToFcUpdtDate(dataItem.updatedDate))
        }


    }

    private fun getLastAgreedPrice() : String{
        var agreedPrice = dataItem.publishedPrice.toString()
        if(dataItem.updatedBy.equals("Offeror") && dataItem.latestBidderPrice.toString().isNotEmpty()){
            agreedPrice = dataItem.latestBidderPrice.toString()
        }else if(dataItem.updatedBy.equals("Bidder") && dataItem.latestOfferorPrice.toString().isNotEmpty()){
            agreedPrice = dataItem.latestOfferorPrice.toString()
        }
        return agreedPrice
    }


    private fun hideCounterView(){
        mBinding.counterLay.visibility = View.GONE
        mBinding.counterCb.visibility = View.GONE

        mBinding.quantityLbl.visibility = View.GONE
        mBinding.quantityEdtTxt.visibility = View.GONE
        mBinding.quantiUnitLbl.visibility = View.GONE
        mBinding.delPerLay.visibility = View.GONE
        mBinding.delPerLbl.visibility = View.GONE
        mBinding.remarksEdtTxt.visibility = View.GONE
        mBinding.remarksLbl.visibility = View.GONE
        mBinding.bidDtlFooter.visibility = View.GONE
    }

    private fun cancelBidObserver(){
        mViewModel.fcCancelOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

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
                            cancelRejctDlg.dismiss()
                            requireActivity().onBackPressed()
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

    private fun rejectBidObserver(){
        mViewModel.fcRejectOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

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
                            cancelRejctDlg.dismiss()
                            Toast.makeText(requireContext(),"Your message has been sent",Toast.LENGTH_SHORT).show()
                            requireActivity().onBackPressed()
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


    private fun checkBidStatusAndUpdateUI(){

        if(dataItem.status != null){
            if(dataItem.status.equals("In-Progress")){

                if(fc_userAccess.equals(OFFERER)){
                    offerorInProgressView()
                }else{
                    bidderInProgressView()
                }

            }else {

                if(dataItem.status.equals("Accepted",true)){
                    acceptedRejectedCancelledView("accepted")
                }
                else if (dataItem.status.equals("Rejected",true)){
                    acceptedRejectedCancelledView("rejected")
                }else if(dataItem.status.equals("Cancelled",true)){
                    acceptedRejectedCancelledView("cancelled")
                }
            }
        }else {
            if(bidderViaPubPrc){
                bidderPubPriceView()
            }
        }



    }

    private fun offerorInProgressView() {


        mBinding.fcTvR1.visibility = View.VISIBLE
        mBinding.fcTvR2.visibility = View.VISIBLE
        mBinding.fcTvR3.visibility = View.VISIBLE
        mBinding.fcTvR4.visibility = View.VISIBLE
        mBinding.fcTvR5.visibility = View.VISIBLE
        mBinding.fcTvR6.visibility = View.VISIBLE
        mBinding.fcTvR7.visibility = View.VISIBLE
        mBinding.fcTvR8.visibility = View.VISIBLE
        mBinding.fcTvR9.visibility = View.VISIBLE
        mBinding.fcTvR10.visibility = View.VISIBLE
        mBinding.fcTvR11.visibility = View.VISIBLE
        mBinding.fcTvR12.visibility = View.VISIBLE
        mBinding.fcTvR13.visibility = View.VISIBLE



        mBinding.fcTvR1.setLabelAndValue(getString(R.string.offer_type), dataItem.offerType)
        mBinding.fcTvR2.setLabelAndValue(getString(R.string.offer_id), dataItem.bidId)
        mBinding.fcTvR3.setLabelAndValue(getString(R.string.inco_term_lbl), dataItem.incoTerm)
        mBinding.fcTvR4.setLabelAndValue(getString(R.string.produt_lbl), dataItem.product)
        mBinding.fcTvR5.setLabelAndValue(getString(R.string.quality_lbl), dataItem.quality)
        mBinding.fcTvR6.setLabelAndValue(getString(R.string.crop_year_lbl), dataItem.cropYear)
        mBinding.fcTvR7.setLabelAndValue(getString(R.string.location), dataItem.location)
        mBinding.fcTvR8.setLabelAndValue(getString(R.string.quantity_label), dataItem.quantity.toString() + " " + dataItem.quantityUnit)
        mBinding.fcTvR9.setLabelAndValue(getString(R.string.packing_type), dataItem.packingType?:"")
        mBinding.fcTvR10.setLabelAndValue(getString(R.string.packing_size), dataItem.packingSize?:"")
        mBinding.fcTvR11.setLabelAndValue(getString(R.string.payment_term_lbl), dataItem.paymentTerms?:"")
        mBinding.fcTvR12.setLabelAndValue(getString(R.string.delv_period), milliSecToDate(dataItem.deliveryFromDateInMillis)
                + " to " + milliSecToDate(dataItem.deliveryToDateInMillis))
        mBinding.fcTvR13.setLabelAndValue(getString(R.string.offeror_ratings), dataItem.rating)

        mBinding.bidInProgrsDtl.visibility = View.VISIBLE

        mBinding.pubPrcView.setLabelAndValue(getString(R.string.publshed_prc),pendingIfEmpty(dataItem.publishedPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.setLabelAndValue(getString(R.string.ltst_biddr_prc),pendingIfEmpty(dataItem.latestBidderPrice.toString(),dataItem.priceUnit))
        mBinding.latOfrPrcView.setLabelAndValue(getString(R.string.latst_offeror_prc),pendingIfEmpty(dataItem.latestOfferorPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.makeValueBold()
        mBinding.latOfrPrcView.makeValueBold()
        mBinding.pubPrcView.makeValueBold()



        mBinding.remarksEdtTxt.visibility = View.VISIBLE
        mBinding.remarksLbl.visibility = View.VISIBLE
        mBinding.bidStatusDtlLay.visibility = View.GONE

        mBinding.bidDtlFooter.visibility = View.VISIBLE
        mBinding.counterLay.visibility = View.VISIBLE
        mBinding.counterCb.visibility = View.VISIBLE
        mBinding.countrPrcUntLbl.visibility = View.VISIBLE

        mBinding.quantityEdtTxt.visibility = View.GONE
        mBinding.quantiUnitLbl.visibility = View.GONE
        mBinding.quantityLbl.visibility = View.GONE
        mBinding.delPerLay.visibility = View.GONE
        mBinding.delPerLbl.visibility = View.GONE
        mBinding.countrPrcUntLbl.setText(dataItem.priceUnit)
        if(dataItem.pendingOn.equals("Bidder")){
            hideCounterView()
        }

    }

    private fun bidderInProgressView() {


        mBinding.fcTvR1.visibility = View.VISIBLE
        mBinding.fcTvR2.visibility = View.VISIBLE
        mBinding.fcTvR3.visibility = View.VISIBLE
        mBinding.fcTvR4.visibility = View.VISIBLE
        mBinding.fcTvR5.visibility = View.VISIBLE
        mBinding.fcTvR6.visibility = View.VISIBLE
        mBinding.fcTvR7.visibility = View.VISIBLE
        mBinding.fcTvR8.visibility = View.VISIBLE
        mBinding.fcTvR9.visibility = View.VISIBLE
        mBinding.fcTvR10.visibility = View.VISIBLE
        mBinding.fcTvR11.visibility = View.VISIBLE
        mBinding.fcTvR12.visibility = View.VISIBLE
        mBinding.fcTvR13.visibility = View.VISIBLE



        mBinding.fcTvR1.setLabelAndValue(getString(R.string.offer_type), dataItem.offerType)
        mBinding.fcTvR2.setLabelAndValue(getString(R.string.offer_id), dataItem.bidId)
        mBinding.fcTvR3.setLabelAndValue(getString(R.string.inco_term_lbl), dataItem.incoTerm)
        mBinding.fcTvR4.setLabelAndValue(getString(R.string.produt_lbl), dataItem.product)
        mBinding.fcTvR5.setLabelAndValue(getString(R.string.quality_lbl), dataItem.quality)
        mBinding.fcTvR6.setLabelAndValue(getString(R.string.crop_year_lbl), dataItem.cropYear)
        mBinding.fcTvR7.setLabelAndValue(getString(R.string.location), dataItem.location)
        mBinding.fcTvR8.setLabelAndValue(getString(R.string.quantity_label), dataItem.quantity.toString() + " " + dataItem.quantityUnit)
        mBinding.fcTvR9.setLabelAndValue(getString(R.string.packing_type), dataItem.packingType?:"")
        mBinding.fcTvR10.setLabelAndValue(getString(R.string.packing_size), dataItem.packingSize?:"")
        mBinding.fcTvR11.setLabelAndValue(getString(R.string.payment_term_lbl), dataItem.paymentTerms?:"")
        mBinding.fcTvR12.setLabelAndValue(getString(R.string.delv_period), milliSecToDate(dataItem.deliveryFromDateInMillis)
                + " to " + milliSecToDate(dataItem.deliveryToDateInMillis))
        mBinding.fcTvR13.setLabelAndValue(getString(R.string.offeror_ratings), dataItem.rating)

        mBinding.bidInProgrsDtl.visibility = View.VISIBLE

        mBinding.pubPrcView.setLabelAndValue(getString(R.string.publshed_prc),pendingIfEmpty(dataItem.publishedPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.setLabelAndValue(getString(R.string.ltst_biddr_prc),pendingIfEmpty(dataItem.latestBidderPrice.toString(),dataItem.priceUnit))
        mBinding.latOfrPrcView.setLabelAndValue(getString(R.string.latst_offeror_prc),pendingIfEmpty(dataItem.latestOfferorPrice.toString(),dataItem.priceUnit))
        mBinding.latBdrPrcView.makeValueBold()
        mBinding.latOfrPrcView.makeValueBold()
        mBinding.pubPrcView.makeValueBold()



        mBinding.remarksEdtTxt.visibility = View.VISIBLE
        mBinding.remarksLbl.visibility = View.VISIBLE
        mBinding.bidStatusDtlLay.visibility = View.GONE

        mBinding.bidDtlFooter.visibility = View.VISIBLE
        mBinding.counterLay.visibility = View.VISIBLE
        mBinding.counterCb.visibility = View.VISIBLE


        mBinding.quantityEdtTxt.visibility = View.GONE
        mBinding.quantiUnitLbl.visibility = View.GONE
        mBinding.quantityLbl.visibility = View.GONE
        mBinding.delPerLay.visibility = View.GONE
        mBinding.delPerLbl.visibility = View.GONE
        mBinding.countrPrcUntLbl.setText(dataItem.priceUnit)

        if(dataItem.pendingOn.equals("Offeror")){
            hideCounterView()
        }

    }

    private fun counterCheckBoxHandler(){
        mBinding.counterCb.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                mBinding.applyTv.setText(getString(R.string.send))
                mBinding.counterEdtTxt.isEnabled = true
            }else{
                mBinding.applyTv.setText(getString(R.string.accept))
                mBinding.counterEdtTxt.isEnabled = false
            }
        }
    }



    private fun hideViewIfEmpty(value : String?,tv : FcDetailTextView,label :String){
        if (value != null) {
            if(value.isEmpty()){
                tv.hideRow()
            }else{
                tv.setLabelAndValue(label,value)
            }
        }else{
            tv.hideRow()
        }

    }

    private fun pendingIfEmpty(str : String,suffix: String):String{
        return if(str.trim().isEmpty())
            "Not Initiated"
        else
            str+" "+suffix

    }

    private fun getSelectedData() {
        val gson = Gson()
        val json: String = AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_OFFER, "").toString()
        dataItem = gson.fromJson(json, ListOfOfferDataItem::class.java)


    }


    fun showPopup(v: View?) {

        val popup = PopupMenu(requireContext(), v)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.dynam_menu, popup.menu)
        val menu = popup.menu

        for(i in moreMenuList.indices){
            menu.add(0, i, 0, moreMenuList[i])
        }


        popup.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        popup.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.title.toString().equals("Cancel this deal",true)){
            showCancelRejectDealPopUp(requireContext(),"Cancel")
        }else if(item.title.toString().equals("Reject this deal",true)){
            AppUtil.sendGoogleEvent(requireContext(),
                    "Apps","Reject Deal","FarmerConnect")
            showCancelRejectDealPopUp(requireContext(),"Reject")
        }else if (item.title.toString().equals("Bid log",true)){
            AppUtil.sendGoogleEvent(requireContext(),
                    "Apps","View Bid Log","FarmerConnect")
            bidLogTtl = "Bid Log ("+dataItem.bidId +" - "+dataItem.refId+")"
            sendBidLogsReq(dataItem.refId)
        }

        return false
    }

    fun checkPerCodesDtl(){
        val result = AppPreferences.getKeyValue(Constants.PrefCode.FC_PERM_CODES,"")
        val resultJson = JSONObject(result)
        val perCodesArr = resultJson.optJSONArray("permCodes")
        mBinding.bidDtlFooter.visibility = View.GONE
        mBinding.counterLay.visibility = View.GONE
        mBinding.counterCb.visibility = View.GONE
        moreMenuList.clear()

        moreMenuList.add("Bid log")

        var hasCancelPermission = false
        var hasRejectPermission = false
        var hasCounterPermission = false
        var hasAcceptPermission = false

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


            if (permCode.equals("STD_APP_BIDS_CANCEL")) {
                hasCancelPermission = true
            }

            if (permCode.equals("STD_APP_BIDS_COUNTER") ) {
                  hasCounterPermission = true
            }

            if (permCode.equals("STD_APP_BIDS_ACCEPT") ) {
                hasAcceptPermission = true
            }


            if (permCode.equals("STD_APP_BIDS_REJECT")) {
                hasRejectPermission = true
            }

        }
        if (count >= 2) {
            fc_userAccess = BOTH
        }


        if(dataItem.status.equals("In-Progress",true)
                && dataItem.pendingOn.equals("Bidder",true)
                && !fc_userAccess.equals(OFFERER)){
            moreMenuList.add("Reject this deal")
        }

        if(dataItem.status.equals("Accepted",true) && hasCancelPermission
                && fc_userAccess.equals(OFFERER)){
            moreMenuList.add("Cancel this deal")
        }



        if(dataItem.status.equals("In-Progress",true)
                && dataItem.pendingOn.equals("Offeror",true)
                && fc_userAccess.equals(OFFERER) && hasRejectPermission ){
            moreMenuList.add("Reject this deal")
        }


             var hasPermisson = hasAcceptPermission || hasCounterPermission
             if( hasPermisson){
                 mBinding.bidDtlFooter.visibility = View.VISIBLE
                 mBinding.counterLay.visibility = View.VISIBLE
                 mBinding.counterCb.visibility = View.VISIBLE
                 mBinding.remarksEdtTxt.visibility = View.VISIBLE
                 mBinding.remarksLbl.visibility = View.VISIBLE
             }

    }



    fun showCancelRejectDealPopUp(context : Context,type :String) {
        if(type.equals("Reject",true)){
            cancelRejctDlg = Dialog(context,android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        }else{
            cancelRejctDlg = Dialog(context)
        }
        cancelRejctDlg.requestWindowFeature(Window.FEATURE_NO_TITLE)
        cancelRejctDlg.setCancelable(false)
        cancelRejctDlg.setContentView(R.layout.cancel_deal_dlg)

        val remarksEdtTxt = cancelRejctDlg.findViewById(R.id.remarks_edt_txt) as EditText
        val backBtn = cancelRejctDlg.findViewById(R.id.back_tv) as TextView
        val submitBtn = cancelRejctDlg.findViewById(R.id.cancel_deal_tv) as TextView
        val header = cancelRejctDlg.findViewById(R.id.hdr) as TextView
        val msg = cancelRejctDlg.findViewById(R.id.msg) as TextView

        val rejectRemarksEdtTxt = cancelRejctDlg.findViewById(R.id.rejct_remarks_edttxt) as EditText
        val rejectBackBtn = cancelRejctDlg.findViewById(R.id.cancle_rjct_tv) as TextView
        val rejectsubmitBtn = cancelRejctDlg.findViewById(R.id.reject_tv) as TextView
        val rejectLatestBdrPrc = cancelRejctDlg.findViewById(R.id.lat_bdr_prc_val) as TextView

        val cancelView = cancelRejctDlg.findViewById(R.id.cancel_view) as RelativeLayout
        val rejectView = cancelRejctDlg.findViewById(R.id.reject_view) as RelativeLayout


        if(type.equals("Reject",true)){
            cancelView.visibility = View.GONE
            rejectView.visibility = View.VISIBLE
            rejectLatestBdrPrc.setText(dataItem.latestBidderPrice.toString()+" "+dataItem.priceUnit)

        }else{
            cancelView.visibility = View.VISIBLE
            rejectView.visibility = View.GONE

            submitBtn.setText(getString(R.string.cancel_deal))
            msg.setText(getString(R.string.you_wont_be_revert))
            header.setText(getString(R.string.cancel_deal))
        }


        backBtn.setOnClickListener {
            cancelRejctDlg.dismiss()
        }
        rejectBackBtn.setOnClickListener {
            cancelRejctDlg.dismiss()
        }

        submitBtn.setOnClickListener {
             val  remarksValue = remarksEdtTxt.text.toString().trim()

            if(remarksValue.isEmpty()){
                DialogUtil.showFcValidationError(requireContext(), "", getString(R.string.pls_entr_remarks))
             //   Toast.makeText(context,getString(R.string.pls_entr_remarks),Toast.LENGTH_SHORT).show()
            }else{
                sendCancelReq(remarksValue)
            }

        }

        rejectsubmitBtn.setOnClickListener {

            val remarksValue = rejectRemarksEdtTxt.text.toString().trim()
            if(remarksValue.isEmpty()){
                DialogUtil.showFcValidationError(requireContext(), "", getString(R.string.pls_entr_remarks))
              //  Toast.makeText(context,getString(R.string.pls_entr_remarks),Toast.LENGTH_SHORT).show()
            }else{
                    sendRejectReq(remarksValue)
            }

        }


        cancelRejctDlg.show()
    }



    private fun sendCancelReq(remarks: String){
        val reqParam = JsonObject()
        val isOfferor = fc_userAccess.equals(OFFERER)
        reqParam.addProperty("remarks",remarks)
        reqParam.addProperty("status","Cancelled")
        mViewModel.sendCancelOfferReq(reqParam,dataItem.refId,isOfferor)
    }

    private fun sendRejectReq(remarks: String){
        val reqParam = JsonObject()
        reqParam.addProperty("remarks",remarks)
        reqParam.addProperty("status","Rejected")
        val isOfferor = fc_userAccess.equals(OFFERER)
        mViewModel.sendRejectOfferReq(reqParam,dataItem.refId,isOfferor)
    }

    private fun datePickerClickListener(){


        mBinding.delFromEdtTxt.setOnClickListener {
            openDatePickerDialog(it)
        }

        mBinding.delToEdtTxt.setOnClickListener {
            openDatePickerDialog(it)
        }
    }

    fun openDatePickerDialog(v: View) {
        val cal = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(requireContext(),
                DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    val selectedDate =
                            year.toString() + "-" + (monthOfYear + 1) + "-" + dayOfMonth.toString()


                    (v as EditText).setText(selectedDate)

                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000;
        datePickerDialog.show()
    }



    private fun convertDateToMilliSec(inputdate : String) : Long{
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val date: Date = sdf.parse(inputdate)
        val millis = date.time
        return millis
    }

    private fun sendBidLogsReq(bidId : String){
        mViewModel.getBidLogs(bidId)
    }


    private fun showBidLogsView(dataList : ArrayList<BidLogData>,priceUnit: String) {


        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.bid_logs_lay)

        val bidLogsListVw = dialog.findViewById<ListView>(R.id.bid_logs_lst);
        val listApapter = BidLogsAdapter(requireContext(), dataList,fc_userAccess,priceUnit)

        val titl = dialog.findViewById<TextView>(R.id.ttl)
        titl.setText(bidLogTtl)

        val closeIcon = dialog.findViewById<ImageView>(R.id.close_icon)
        closeIcon.setOnClickListener {
            dialog.dismiss()
        }


        bidLogsListVw.adapter = listApapter

        dialog.show()

    }



    private fun rateNowView() {


        ratingsDlg = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        ratingsDlg.setContentView(R.layout.rate_now_lay)

        val pricingBtn = ratingsDlg.findViewById<TextView>(R.id.pricing_txt)
        val quantityBtn = ratingsDlg.findViewById<TextView>(R.id.quantity_txt)
        val qualityBtn = ratingsDlg.findViewById<TextView>(R.id.quality_txt)
        val shipmentBtn = ratingsDlg.findViewById<TextView>(R.id.shipment_txt)
        val allBtn = ratingsDlg.findViewById<TextView>(R.id.all_txt)



        val doneBtn = ratingsDlg.findViewById<TextView>(R.id.done_tv)
        val ratingBar = ratingsDlg.findViewById<RatingBar>(R.id.rating_bar)

        val remarksEdt = ratingsDlg.findViewById<EditText>(R.id.remarks_edt_txt)

        val ratingLblOne = ratingsDlg.findViewById<TextView>(R.id.pls_rate_lbl)
        val ratingDesc = ratingsDlg.findViewById<TextView>(R.id.rate_dtl)

        setTagDeSelected(pricingBtn,quantityBtn,qualityBtn,shipmentBtn,allBtn)

        ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->

            when (rating) {
                1f -> {
                    setRatingTitleAndDesc(ratingLblOne,ratingDesc,
                            "BAD !","Please let us know what did not go well")
                }
                2f -> {
                    setRatingTitleAndDesc(ratingLblOne,ratingDesc,
                            "NOT GOOD !","Please let us know what did not go well")
                }
                3f -> {
                    setRatingTitleAndDesc(ratingLblOne,ratingDesc,
                            "NEUTRAL !","Please let us know what did not go well")
                }
                4f -> {
                    setRatingTitleAndDesc(ratingLblOne,ratingDesc,
                            "GOOD !","Please let us know what can be improved")
                }
                5f -> {
                    setRatingTitleAndDesc(ratingLblOne,ratingDesc,
                            "EXCELLENT !","Please let us know what you liked most")
                }
            }

        }

        pricingBtn.setOnClickListener {
            if(it.tag==0){
                it.tag =1
                makeTxtViewSelected(pricingBtn)
                ratingTypeArrList.add("Pricing")
            }else{
                it.tag =0
                ratingTypeArrList.remove("Pricing")
                makeTxtViewUnselected(pricingBtn)
            }
        }
        qualityBtn.setOnClickListener {
            if(it.tag==0){
                it.tag =1
                makeTxtViewSelected(qualityBtn)
                ratingTypeArrList.add("Quality")
            }else{
                it.tag =0
                ratingTypeArrList.remove("Quality")
                makeTxtViewUnselected(qualityBtn)
            }
        }
        quantityBtn.setOnClickListener {
            if(it.tag==0){
                it.tag =1
                makeTxtViewSelected(quantityBtn)
                ratingTypeArrList.add("Quantity")
            }else{
                it.tag =0
                ratingTypeArrList.remove("Quantity")
                makeTxtViewUnselected(quantityBtn)
            }
        }
        shipmentBtn.setOnClickListener {
            if(it.tag==0){
                it.tag =1
                makeTxtViewSelected(shipmentBtn)
                ratingTypeArrList.add("Shipment")
            }else{
                it.tag =0
                ratingTypeArrList.remove("Shipment")
                makeTxtViewUnselected(shipmentBtn)
            }
        }
        allBtn.setOnClickListener {
            if(it.tag==0){
                it.tag =1
                makeTxtViewSelected(pricingBtn,shipmentBtn,quantityBtn,qualityBtn,allBtn)
                setTagSelected(pricingBtn,quantityBtn,qualityBtn,shipmentBtn,allBtn)
                ratingTypeArrList.clear()
                ratingTypeArrList.add("Shipment")
                ratingTypeArrList.add("Quantity")
                ratingTypeArrList.add("Quality")
                ratingTypeArrList.add("Pricing")
            }else{
                it.tag =0
                makeTxtViewUnselected(pricingBtn,shipmentBtn,quantityBtn,qualityBtn,allBtn)
                setTagDeSelected(pricingBtn,quantityBtn,qualityBtn,shipmentBtn,allBtn)
                ratingTypeArrList.clear()
            }
        }



        val titl = ratingsDlg.findViewById<TextView>(R.id.ttl)
        titl.setText("Rating ("+dataItem.bidId +" - "+dataItem.refId+")")

        val closeIcon = ratingsDlg.findViewById<ImageView>(R.id.close_icon)
        closeIcon.setOnClickListener {
            ratingsDlg.dismiss()
        }


        doneBtn.setOnClickListener {

            val remarksValue = remarksEdt.text.toString().trim()
            val bodyParams = JsonObject()
            val ratedOn = JsonArray()
            for(i in ratingTypeArrList.indices){
                ratedOn.add(ratingTypeArrList[i])
            }
            if(remarksValue.isNotEmpty()){
                bodyParams.addProperty("remarks",remarksValue)
                bodyParams.add("ratedOn",ratedOn)
                val rating = ratingBar.rating.toInt().toString()
                mViewModel.sendOfferRating(bodyParams,dataItem.refId,rating)
            }else {
                Toast.makeText(requireContext(),"Please enter remarks",Toast.LENGTH_SHORT).show()
            }

        }

        ratingsDlg.show()

    }

    private fun setTagSelected(vararg txtView: TextView){
        for(tv in txtView){
            tv.setTag(1)
        }
    }
    private fun setTagDeSelected(vararg txtView: TextView){
        for(tv in txtView){
            tv.setTag(0)
        }
    }

    private fun setRatingTitleAndDesc(tvTitle: TextView,tvDesc : TextView,
                 titleVal :String,descVal :String) {
        tvTitle.setText(titleVal)
        tvDesc.setText(descVal)
    }

    private fun makeTxtViewSelected(vararg tv : TextView){
        for(txtview in tv){
            txtview.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ratings_btn_sel_bg))
        }
    }

    private fun makeTxtViewUnselected(vararg tv : TextView){
        for(txtview in tv){
            txtview.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.ratings_btn_bg))
        }

    }


}