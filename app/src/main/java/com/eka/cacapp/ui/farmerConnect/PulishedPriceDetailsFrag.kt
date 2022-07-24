package com.eka.cacapp.ui.farmerConnect

import android.os.Bundle

import android.view.*
import android.widget.PopupMenu
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.eka.cacapp.R
import com.eka.cacapp.adapter.FcPubPriceDtlListAdptr
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.farmconnect.ListOfOfferDataItem
import com.eka.cacapp.data.farmconnect.PubPriceDtlData
import com.eka.cacapp.databinding.PubPricDtlFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.FarmerConnectRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.AppUtil.Companion.getDateForFc
import com.google.gson.Gson
import org.json.JSONObject

class PulishedPriceDetailsFrag : Fragment() {

    private lateinit var mBinding: PubPricDtlFragBinding
    private lateinit var dataItem: ListOfOfferDataItem
    private lateinit var mViewModel: FarmerConnectViewModel

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


            mBinding = DataBindingUtil.inflate(inflater, R.layout.pub_pric_dtl_frag, container, false)

            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()


            (activity as DashboardActivity).clearListView()

            val factory = ViewModelFactory(FarmerConnectRepository(), requireContext())
            mViewModel = ViewModelProvider(this, factory).get(FarmerConnectViewModel::class.java)

            getSelectedData()

            mBinding.ttl.setText(dataItem.bidId)

            mBinding.backIcon.setOnClickListener {
                requireActivity().onBackPressed()
            }

            mBinding.moreImg.setOnClickListener {
                showPopup(it)
            }

            addValuesToDtlAdaptr()

            updateDeleteObserver()


        } catch (e: Exception) {

        }

        return mBinding.root

    }

    private fun updateDeleteObserver(){
        mViewModel.fcDeleteOfferResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

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


    private fun addValuesToDtlAdaptr() {
        var pubPriceListData = ArrayList<PubPriceDtlData>()
        pubPriceListData.add(PubPriceDtlData(getString(R.string.offer_type), dataItem.offerType))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.off_ref_no), dataItem.bidId))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.produt_lbl), dataItem.product))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.quality_lbl), dataItem.quality))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.crop_year_lbl), dataItem.cropYear))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.location), dataItem.location))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.publshed_prc), dataItem.publishedPrice.toString() + " " + dataItem.priceUnit))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.expiry_date), getDateForFc(dataItem.expiryDate)))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.inco_term_lbl), dataItem.incoTerm))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.quantity_label), dataItem.quantity.toString() + " " + dataItem.quantityUnit))
        pubPriceListData.add(PubPriceDtlData(getString(R.string.delv_period), getDateForFc(dataItem.deliveryFromDate)
                + " to " + getDateForFc(dataItem.deliveryToDate)))
        val listApapter = FcPubPriceDtlListAdptr(requireContext(), pubPriceListData)

        mBinding.pubOffrLv.adapter = listApapter
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

        menu.add(0, 0, 0, "Modify")
        menu.add(0, 1, 0, "Delete")

        popup.setOnMenuItemClickListener {
           onOptionsItemSelected(it)
        }

        popup.show()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        if(itemId==0){
            val bundle = bundleOf("modifyReq" to true)
            findNavController().navigate(R.id.action_pulishedPriceDetailsFrag_to_createOfferFrag,bundle)

        }
        else if(itemId==1){
            DialogUtil.infoPopUp(requireContext(),
                    "", "Do you want to delete the offer?",
                    { sendDeleteReq()} )

        }
        return false
    }
    private fun sendDeleteReq(){
        mViewModel.sendDeleteOfferReq(dataItem.bidId)
    }




}