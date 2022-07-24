package com.eka.cacapp.ui.notidtl

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.NotificationScrnAdptr
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.noti.NotiListData
import com.eka.cacapp.databinding.NotiListScrnFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.*
import com.google.gson.Gson
import org.json.JSONObject


class NotificationListScreenFrag : Fragment()  {

    private lateinit var mBinding : NotiListScrnFragBinding
    private lateinit var mViewModel: DashboardViewModel
    private lateinit var notiListAdapter : NotificationScrnAdptr


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.noti_list_scrn_frag, container, false)

        (activity as DashboardActivity).clearSelectedViews()
        (activity as DashboardActivity).showBackButton()


        (activity as DashboardActivity).clearListView()

        mBinding.serviceTtl.setText(getString(R.string.notifications))


        handleBack()

        val factory  = ViewModelFactory(DashboardRepository(),requireContext())

        mViewModel = ViewModelProvider(this,factory).get(DashboardViewModel::class.java)

        mViewModel.getNotificationList()



        mViewModel.notificationListResponse.observe(viewLifecycleOwner, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            if(viewLifecycleOwner.lifecycle.currentState== Lifecycle.State.RESUMED){
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        val dataArr = jsonObj.optJSONArray("data")
                        var resArray = ArrayList<NotiListData>()
                        for(i in 0 until dataArr.length()){
                            val jobj = dataArr[i]
                            val notiListResponse: NotiListData = Gson().fromJson(
                                    jobj.toString(),
                                    NotiListData::class.java
                            )
                            resArray.add(notiListResponse)
                        }
                        addDataToAdapter(resArray)

                        if(dataArr.length()==0){
                            mBinding.noNotiTv.visibility = View.VISIBLE
                        }



                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()

                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }
            }
        })





        return mBinding.root

    }

    private fun handleBack() {
        mBinding.backImg.setOnClickListener {
            requireActivity().onBackPressed()
        }
    }





    private fun addDataToAdapter(listOfData : ArrayList<NotiListData>){

        try {

            notiListAdapter = NotificationScrnAdptr(listOfData,requireContext())
            val llm = LinearLayoutManager(requireActivity())
            llm.orientation = LinearLayoutManager.VERTICAL
            mBinding.bidsRecyclerView.layoutManager = llm
            mBinding.bidsRecyclerView.adapter = notiListAdapter

            notiListAdapter.onItemClick = { selectedItem ->
                val gson = Gson()
                val json = gson.toJson(selectedItem)
                AppPreferences.saveValue(Constants.PrefCode.NOTI_SEL_ITEM,json)
                findNavController().navigate(R.id.action_notificationListScreenFrag_to_notifiListDetailsFrag)


            }


        }catch (e : Exception){

        }

    }



    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }



}