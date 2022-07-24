package com.eka.cacapp.ui.workflow

import android.app.Dialog
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.adapter.QtcListLayoutAdapter
import com.eka.cacapp.adapter.QtcSortAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.qtcLayout.*
import com.eka.cacapp.databinding.QtcCompositeFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.AppUtil.Companion.evaluateExpressionResult
import com.eka.cacapp.utils.AppUtil.Companion.hasValue
import com.eka.cacapp.utils.AppUtil.Companion.removeValue
import com.eka.cacapp.utils.Constants.WorkFlow.FROM
import com.eka.cacapp.utils.Constants.WorkFlow.PAGINATION_DEFAULT_SIZE
import com.eka.cacapp.utils.Constants.WorkFlow.SIZE
import com.eka.cacapp.utils.WorkFlowViews.MATCH_PARENT
import com.eka.cacapp.utils.WorkFlowViews.WRAP_CONTENT
import com.eka.cacapp.utils.WorkFlowViews.createLinearLay
import com.eka.cacapp.utils.WorkFlowViews.createTextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


class QtcCompositeFrag : Fragment() {

    private lateinit var mBinding: QtcCompositeFragBinding
    private lateinit var mViewModel: DashboardViewModel

    private var rowOneList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowOneClmOneList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowTwoList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowThreeList: ArrayList<ListLayoutRow> = ArrayList()

    private var detailsViewList: ArrayList<ListLayoutRow> = ArrayList()
    private var detailsListOfItems: ArrayList<ListItem> = ArrayList()

    private var mainRowList: ArrayList<ListingResponse> = ArrayList()
    private val filterKeys = JsonArray()
    private val sortKeys: ArrayList<String> = ArrayList()
    private val sortDisplayName: ArrayList<String> = ArrayList()
    private lateinit var sortItem: SortData
    private lateinit var recyclerViewAdapter: QtcListLayoutAdapter
    private var isClearList = false
    private var hasSearchOption = false
    private var hasSortOption = false
    private var hasFilterOption = false
    private var hashMapDropDown: HashMap<String, JSONArray> = HashMap()
    private var hashMapSpinner: HashMap<String, Spinner> = HashMap()
    private var hashMapSpinnerLastSelected: HashMap<String, Int> = HashMap()
    private var hashMapDisplayName: HashMap<String, String> = HashMap()
    private var operationArray: JsonArray = JsonArray()
    private var qpObject: JsonObject = JsonObject()

    private lateinit var myAdapter: QtcSortAdapter

    private lateinit var bottomSheetDialog: BottomSheetDialog

    private var totalCount = 0
    private var decisionName = ""
    private var decisionData = ""

    private var hasTabs = false
    private var hasMapTabsDisplayName: HashMap<String, String> = HashMap()
    private var hasMapDataRes: HashMap<String, String> = HashMap()
    private var hasMapLayoutRes: HashMap<String, String> = HashMap()
    private var hasMapDecisions: HashMap<Int, String> = HashMap()
    private var currentWorkFLowName = ""

    private var payLoadData = JsonObject()

    private var handlerListItem = ""
    private var qtcLayoutRes = ""
    private var workFlowArrayList: ArrayList<String> = ArrayList()
    private var decisionsArrayList: ArrayList<Decisions> = ArrayList()
    private var workFlowTabs = JSONArray()
    private var workFlowResMap = JSONArray()
    private var workFlowLayoutResMap = JSONArray()

    private var dataReqName = ""
    private var resCount = 0
    private var resFailureCount = 1

    private var loadMoreRecords = false

    private var getInitialDataForView = false
    private var showCountWithTitle = true
    private var layoutType = ""
    private var overFlowPopup : PopupMenu? = null

    private var backStackWorkFlowTabs = JSONArray()
    private var backStackWorkFlowResMap = JSONArray()
    private var backStackWorkFlowLayoutResMap = JSONArray()
    private var parentWorkFlowName = ""
    private var parentPayLoadObj = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        try {

            mBinding = DataBindingUtil.inflate(inflater, R.layout.qtc_composite_frag, container, false)

            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()

            mBinding.backImg.setOnClickListener {
                handleBackPressed()
            }

            mBinding.root.rootView.isFocusableInTouchMode = true
            mBinding.root.rootView.requestFocus()

            mBinding.root.setOnKeyListener(object : View.OnKeyListener {
                override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                    if (event.action === KeyEvent.ACTION_DOWN) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            handleBackPressed()
                            return true
                        }
                    }
                    return false
                }
            })

            parentPayLoadObj = AppPreferences.getKeyValue(Constants.PrefCode.QTC_PAYLOAD_OBJ,"").toString()

            operationArray.add("pagination")

            addDefaultPaginationSize(qpObject)

            sortItem = SortData(sortKeys, sortDisplayName, 0, "NA", false)

            parseMapDataRes()
            parseMapLayoutRes()
            checkForTabsView()

            if (!hasTabs) {
                hasMapDataRes.forEach { (key, value) ->
                    addValuesToAdapter(key)
                }
                if(hasMapDataRes.isEmpty()){
                    addValuesToAdapter(currentWorkFLowName)
                }
            } else {
                addValuesToAdapter(currentWorkFLowName)
            }

            displayDecisions()


            (activity as DashboardActivity).clearListView()

            (activity as DashboardActivity).makeAppTabSelected()

            val factory = ViewModelFactory(DashboardRepository(),requireContext())

            mViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

            mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


            mBinding.qtcRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                        if (mainRowList.size < totalCount
                                && !loadMoreRecords) {
                            isClearList = false
                            qpObject.addProperty(SIZE, PAGINATION_DEFAULT_SIZE)
                            qpObject.addProperty(FROM, mainRowList.size)
                            loadMoreRecords = true
                            mViewModel.connectAppData(
                                    currentWorkFLowName,
                                    qpObject, operationArray, payLoadData)
                        }


                    }
                }
            })


            mViewModel.connectColumnDataResponse.observe(viewLifecycleOwner, Observer {
                if (it is Resource.Loading) {
                    activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                }
                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()

                        val jsonObject = JSONObject(result)
                        val dataArray = jsonObject.optJSONArray("data")?: JSONArray()
                        if(dataArray.length()>0){
                            val dataObj = dataArray.getJSONObject(0)

                            for (i in 0 until filterKeys.size()) {
                                val key = filterKeys[i].asString.trim()
                                hashMapDropDown[key] = dataObj.get(key) as JSONArray
                            }

                            createFilterScreen()
                        }


                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }

            })


            mViewModel.connectAppDecisionResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {
                    activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                }

                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()


                        val result = it.value.string()

                        val jsonObject = JSONObject(result)

                        val dataObj = jsonObject.optJSONObject("data")?: JSONObject()
                        var message = dataObj.optString("message") ?: ""

                        if(message.trim().equals("",true)){
                            message = jsonObject.optString("message") ?: ""
                        }

                        if (jsonObject.optBoolean("showPopUp")) {
                            DialogUtil.infoPopUp(requireContext(),
                                    "", message,
                                    { navToListAfterDecision() }
                            )
                        }


                    }


                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }


            })



            mViewModel.connectAppLayoutResponse.observe(viewLifecycleOwner, Observer {


                if (it is Resource.Loading) {
                    this?.let { it1 ->

                        activity?.let { it1 ->

                            if (layoutType.equals("")) {
                                ProgressDialogUtil.showProgressDialog(it1)
                            }

                        }

                    }
                }
                when (it) {
                    is Resource.Success -> {
                        //    ProgressDialogUtil.hideProgressDialog()


                        val result = it.value.string()
                        qtcLayoutRes = result


                        if (layoutType.equals("")) {

                            AppPreferences.saveValue(Constants.PrefCode.QTC_LIST_RES, result)


                            val res = JSONObject(qtcLayoutRes)
                            val flowObject = res.getJSONObject("flow")

                            val workFowObj = flowObject.getJSONObject(decisionName)
                            val layoutObj = workFowObj.getJSONObject("layout")
                            layoutType = layoutObj.getString("name")
                            getInitialDataForView = layoutObj.optBoolean("getInitialData")

                            if (layoutType.equals("list", true)) {
                                dataReqName = decisionName
                                workFlowArrayList.add(dataReqName)

                                val jsonObject = JSONObject()
                                jsonObject.put("key", dataReqName)
                                jsonObject.put("value", qtcLayoutRes)
                                workFlowLayoutResMap.put(jsonObject)

                                mViewModel.connectAppData(handlerListItem, qpObject, operationArray, payLoadData)
                            } else if (layoutType.equals("view", true)) {
                                val paginationArray = JsonArray()
                                dataReqName = decisionName

                                if (getInitialDataForView) {
                                    getInitialDataForView = false

                                    mViewModel.connectAppData(dataReqName, qpObject, paginationArray, payLoadData)

                                } else {
                                    val jsonObject = JSONObject()
                                    val dataObject = JSONObject();
                                    val dataArr = JSONArray();
                                    val payObj = JSONObject(payLoadData.toString())
                                    dataArr.put(payObj)
                                    dataObject.put("data", dataArr)

                                    jsonObject.put("key", dataReqName)
                                    jsonObject.put("value", dataObject)
                                    workFlowResMap.put(jsonObject)

                                    val jsonObject2 = JSONObject()


                                    jsonObject2.put("key", dataReqName)
                                    jsonObject2.put("value", qtcLayoutRes)
                                    workFlowLayoutResMap.put(jsonObject2)


                                    ProgressDialogUtil.hideProgressDialog()

                                    AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, workFlowResMap.toString())
                                    AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())
                                    AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())


                                    (activity as DashboardActivity).addValueToBackStack(QtcMapData(backStackWorkFlowResMap,
                                            backStackWorkFlowLayoutResMap,backStackWorkFlowTabs,parentWorkFlowName,parentPayLoadObj))

                                    findNavController().navigate(R.id.action_qtcCompositeFrag_self)
                                }


                            }


                            //end
                            else if (layoutType.equals("customv2", true)) {

                                val workFlowArr = workFowObj.getJSONArray("workflows")
                                for (i in 0 until workFlowArr.length()) {
                                    val workObj = workFlowArr.getJSONObject(i)
                                    if (!workObj.optString("display").equals("")) {
                                        val displayName = workObj.getString("display")

                                        if (displayName.equals("tabs", true)) {
                                            val disworkFlowArr = workObj.getJSONArray("workflows")
                                            for (j in 0 until disworkFlowArr.length()) {
                                                val obj = disworkFlowArr.getJSONObject(j)
                                                var name = obj.getString("name")

                                                workFlowArrayList.add(name)
                                                val jsonObject = JSONObject()
                                                jsonObject.put("key", name)
                                                jsonObject.put("value", obj.getString("tabHeader"))

                                                workFlowTabs.put(jsonObject)

                                            }
                                        }


                                    } else {
                                        workFlowTabs = JSONArray()
                                        workFlowArrayList.add(workObj.getString("name"))
                                    }

                                }


                                for (k in workFlowArrayList.indices) {
                                    val workFlowName = workFlowArrayList[k]

                                    val res = JSONObject(qtcLayoutRes)


                                    val flowObject = res.getJSONObject("flow")


                                    var workFowObj = flowObject.optJSONObject(workFlowName)
                                    if (workFowObj == null) {
                                        workFowObj = flowObject.getJSONObject("composite").getJSONObject(workFlowName)
                                                .getJSONObject("flow").getJSONObject(workFlowName)
                                    }

                                    if (workFowObj != null) {
                                        val jsonObject = JSONObject()
                                        jsonObject.put("key", workFlowName)
                                        jsonObject.put("value", qtcLayoutRes)
                                        workFlowLayoutResMap.put(jsonObject)
                                    }

                                }

                                if (workFlowLayoutResMap.length() == workFlowArrayList.size) {
                                    dataReqName = workFlowArrayList[0]
                                    mViewModel.connectAppData(dataReqName, qpObject, operationArray, payLoadData)
                                } else {

                                    dataReqName = workFlowArrayList[workFlowLayoutResMap.length() - 1]
                                    mViewModel.connectAppLayout(dataReqName)

                                }

                            } else if (layoutType.equals("cancelpopup", true)) {
                                val optionObj = layoutObj.optJSONObject("option")
                                var bodyMsg = optionObj.optString("bodyMessage")
                                val decisonArr = workFowObj.optJSONArray("decisions")

                                val key = bodyMsg.substringAfter("\${").substringBefore("}")

                                val payLoadString = AppPreferences.getKeyValue(Constants.PrefCode.QTC_PAYLOAD_OBJ, "").toString()
                                val payLoadObj = JSONObject(payLoadString)
                                val keyValue = payLoadObj.optString(key)

                                bodyMsg = bodyMsg.replace("\${", "")
                                bodyMsg = bodyMsg.replace("}", "")
                                bodyMsg = bodyMsg.replace(key, keyValue)

                                var task = ""
                                var label = ""
                                var type = ""
                                for (d in 0 until decisonArr.length()) {
                                    val obj = decisonArr.optJSONObject(d)
                                    task = obj.optString("task")
                                    label = obj.optString("label")
                                    type = obj.optString("type")

                                }
                                ProgressDialogUtil.hideProgressDialog()
                                popUpLayout("Confirmation", bodyMsg, label, task)

                            }

                        } else {
                            if (workFlowLayoutResMap.length() == workFlowArrayList.size - 1) {
                                val jsonObject = JSONObject()
                                jsonObject.put("key", dataReqName)
                                jsonObject.put("value", qtcLayoutRes)
                                workFlowLayoutResMap.put(jsonObject)


                                dataReqName = workFlowArrayList[0]

                                mViewModel.connectAppData(dataReqName, qpObject, operationArray, payLoadData)

                            } else {
                                val jsonObject = JSONObject()
                                jsonObject.put("key", dataReqName)
                                jsonObject.put("value", qtcLayoutRes)
                                workFlowLayoutResMap.put(jsonObject)
                            }
                        }

                    }


                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        handleApiError(it){null}
                    }
                }

            })



            mViewModel.connectAppDataResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {

                    if(isClearList||loadMoreRecords){
                        activity?.let { it1 ->
                                ProgressDialogUtil.showProgressDialog(it1)
                        }
                    }

                    //   activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                }
                when (it) {
                    is Resource.Success -> {
                        // ProgressDialogUtil.hideProgressDialog()

                        if (loadMoreRecords || isClearList) {
                            ProgressDialogUtil.hideProgressDialog()
                            loadMoreRecords = false

                            val result = it.value.string()
                            updateDataValues(result, currentWorkFLowName)
                            isClearList = false
                        } else {

                            //  ProgressDialogUtil.hideProgressDialog()
                            resCount++
                            val result = it.value.string()

                            val jsonObject = JSONObject()
                            jsonObject.put("key", dataReqName)
                            jsonObject.put("value", result)
                            workFlowResMap.put(jsonObject)

                            AppPreferences.saveValue(Constants.PrefCode.QTC_DATA_RES, result)

                            try {

                                if (workFlowResMap.length() == workFlowArrayList.size) {
                                    ProgressDialogUtil.hideProgressDialog()
                                    AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())
                                    AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, workFlowResMap.toString())
                                    AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())

                                    (activity as DashboardActivity).addValueToBackStack(QtcMapData(backStackWorkFlowResMap,
                                            backStackWorkFlowLayoutResMap,backStackWorkFlowTabs,parentWorkFlowName,parentPayLoadObj))

                                    findNavController().navigate(R.id.action_qtcCompositeFrag_self)
                                } else {
                                    val paginationArray = JsonArray()

                                    dataReqName = workFlowArrayList[workFlowResMap.length()]

                                    mViewModel.connectAppData(dataReqName, qpObject, paginationArray, payLoadData)

                                }

                            } catch (e: Exception) {

                            }


                        }
                    }

                    is Resource.Failure -> {
                        if (workFlowResMap.length() > 0 && workFlowResMap.length() == workFlowArrayList.size - 1) {
                            ProgressDialogUtil.hideProgressDialog()
                            AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())
                            AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, workFlowResMap.toString())
                            AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())

                            (activity as DashboardActivity).addValueToBackStack(QtcMapData(backStackWorkFlowResMap,
                            backStackWorkFlowLayoutResMap,backStackWorkFlowTabs,parentWorkFlowName,parentPayLoadObj))


                            findNavController().navigate(R.id.action_qtcCompositeFrag_self)
                        }
                        if (resFailureCount < workFlowArrayList.size) {


                            dataReqName = workFlowArrayList[resFailureCount]
                            resFailureCount++

                            mViewModel.connectAppData(dataReqName, qpObject, operationArray, payLoadData)

                        } else {
                            ProgressDialogUtil.hideProgressDialog()
                            handleApiError(it){null}
                        }
                        // handleError(it) {null}
                        //handleApiError(it){null}
                    }
                }

            })

            mBinding.close.setOnClickListener {
                hideSearchView()
            }

            mBinding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {

                    hideSearchView()
                    isClearList = true

                    if (!hasValue(operationArray, "search")) {
                        operationArray.add("search")
                    }
                    addDefaultPaginationSize(qpObject)


                    val fields = JsonArray()
                    fields.add("*")
                    val searchObj = JsonObject()
                    val multiMatchObj = JsonObject()
                    multiMatchObj.addProperty("query", p0!!)
                    multiMatchObj.addProperty("type", "phrase_prefix")
                    multiMatchObj.add("fields", fields)

                    searchObj.add("multi_match", multiMatchObj)

                    qpObject.add("query", searchObj)


                    mViewModel.connectSearchAppData(
                            currentWorkFLowName, qpObject,
                            operationArray)
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {

                    return false
                }
            })




        } catch (e: Exception) {

        }
        return mBinding.root
    }

    private fun clearPrevData() {
        workFlowArrayList.clear()
        workFlowTabs = JSONArray()
        workFlowResMap = JSONArray()
        workFlowLayoutResMap = JSONArray()
        layoutType = ""
        dataReqName = ""
        resCount = 0
        resFailureCount = 1
    }

    private fun checkForTabsView() {
        val tabsRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_Tabs_MAP, "")

        if (tabsRes!!.trim().isEmpty()) {
            hasTabs = false
            mBinding.tabLayout.visibility = View.GONE
            return
        }

        val jsonArray = JSONArray(tabsRes)
        backStackWorkFlowTabs = jsonArray
        if (jsonArray.length() != 0) {
            hasTabs = true
            mBinding.tabLayout.visibility = View.VISIBLE

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)
                val key = jsonObject.getString("key")
                var tabHeader = jsonObject.getString("value")
                hasMapTabsDisplayName[key] = tabHeader
                if (i == 0) {
                    currentWorkFLowName = key
                }
                var dataRes = hasMapDataRes[key] ?: ""

                if (!dataRes.equals("")) {
                    val dataJson = JSONObject(dataRes)
                    val dataArry = dataJson.getJSONArray("data")
                    if (dataArry.length() > 0) {
                        tabHeader += " (${dataArry.length()})"
                    }
                }



                mBinding.tabLayout.addTab(mBinding.tabLayout.newTab().setText(tabHeader)
                        .setTag(key)
                )

            }



            mBinding.tabLayout.tabGravity = TabLayout.GRAVITY_CENTER

            mBinding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {

                    val key = tab.tag.toString()
                    var data = ""
                    if (hasMapDataRes.containsKey(key)) {
                        data = hasMapDataRes[key].toString()
                    }


                    currentWorkFLowName = key
                    // updateDataValues(data,key)
                    isClearList = true
                    updateDataValues(data, currentWorkFLowName)
                    isClearList = false

                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })


        }


    }

    private fun parseMapLayoutRes() {
        val dataRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_LAY_MAP, "")

        val jsonArrayData = JSONArray(dataRes)
        backStackWorkFlowLayoutResMap = jsonArrayData
        if (jsonArrayData.length() != 0) {

            for (i in 0 until jsonArrayData.length()) {
                val jsonObject = jsonArrayData.getJSONObject(i)
                val key = jsonObject.getString("key")
                val value = jsonObject.getString("value")
                hasMapLayoutRes[key] = value
            }

        }
        if(currentWorkFLowName.equals("")){
            currentWorkFLowName =   AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString()
        }
    }

    private fun parseMapDataRes() {
        val dataRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_RES_MAP, "")
        if(dataRes.toString().trim().equals("")){
            return
        }
        val jsonArrayData = JSONArray(dataRes)
        backStackWorkFlowResMap = jsonArrayData
        if (jsonArrayData.length() != 0) {

            for (i in 0 until jsonArrayData.length()) {
                val jsonObject = jsonArrayData.getJSONObject(i)
                val key = jsonObject.getString("key")
                val value = jsonObject.getString("value")
                if (i == 0) {
                    currentWorkFLowName = key
                }

                hasMapDataRes[key] = value
            }

        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_search -> {
                mBinding.searchView.isIconified = false
                mBinding.searchView.visibility = View.VISIBLE
                mBinding.searchLay.visibility = View.VISIBLE
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_sort -> {
                hideSearchView()
                showFilterBottomSheet()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_filter -> {
                hideSearchView()
                mViewModel.connectDistinctColumnData(
                        currentWorkFLowName
                        , filterKeys)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    private fun hideSearchView() {
        mBinding.searchView.visibility = View.GONE
        mBinding.searchLay.visibility = View.GONE
        mBinding.searchView.setQuery("", false)
    }


    /**
     * Function to add categories to recycler view adapter
     * */
    private fun addValuesToAdapter(workFlowName: String) {

        val layOutRes = hasMapLayoutRes[workFlowName] ?: ""
       //   val layOutRes = getJsonDataFromAsset(requireActivity(), "lay_res.json").toString()
        val dataRes = hasMapDataRes[workFlowName] ?: ""
        parseData(layOutRes, dataRes, workFlowName)
        setValuesToAdapter()

    }


    private fun updateDataValues(dataRes: String, workFlowName: String) {
        val layoutRes = hasMapLayoutRes[workFlowName] ?: ""
        parseData(layoutRes, dataRes, workFlowName)
        notifyAdapterChanges()

    }

    private fun parseData(layOutRes: String, dataRes: String, workFlowName: String) {

        try {
            if (isClearList) {
                mainRowList.clear()
            }

            rowOneClmOneList.clear()
            rowOneList.clear()
            rowTwoList.clear()
            rowThreeList.clear()


            val res = JSONObject(layOutRes)


            val flowObject = res.getJSONObject("flow")


            var workFowObj = flowObject.optJSONObject(workFlowName)
            if (workFowObj == null) {
                workFowObj = flowObject.getJSONObject("composite").getJSONObject(workFlowName)
                        .getJSONObject("flow").getJSONObject(workFlowName)
            }


            val decisionArr = workFowObj.getJSONArray("decisions")
            if (decisionArr.length() > 0) {

                for (d in 0 until decisionArr.length()) {
                    val decisionObj = decisionArr.getJSONObject(d)
                    val outComeObj = decisionObj.getJSONArray("outcomes")[0] as JSONObject
                    val selection = decisionObj.optString("selection")
                    val position = decisionObj.optString("position")


                    if (selection.equals("row-selection", true) ||
                            position.equals("row-selection", true)) {
                        decisionName = outComeObj.getString("name")
                        decisionData = outComeObj.getString("data")
                    }


                }

            }


            val title = workFowObj.getString("label");
            val layoutObj = workFowObj.getJSONObject("layout")
            val optionsObj = layoutObj.optJSONObject("options")

            val layOutName = layoutObj.optString("name")

            getInitialDataForView = layoutObj.optBoolean("getInitialData")


            if (optionsObj != null) {
                hasSearchOption = optionsObj.optBoolean("serverSearch")
            }

            var fieldsArr = JSONArray()
            if (layOutName.equals("list", true)) {
                fieldsArr = workFowObj.getJSONArray("fields")
            } else if (layOutName.equals("view", true)) {
                fieldsArr = workFowObj.getJSONArray("fields").getJSONArray(0)
            }


            val objectMeta = res.getJSONObject("objectMeta")
            val fields = objectMeta.getJSONObject("fields")


            for (i in 0 until fieldsArr.length()) {

                var item = fieldsArr.getJSONObject(i)

                val key = item.getString("key")
                val placement = item.optString("placement")
                val label = item.optString("label")
                val hasSort = item.optBoolean("sort")
                val hasFilter = item.optBoolean("filter")
                val itemType = item.optString("type")
                val valueExpression = item.optString("valueExpression")



                val keyObject = fields.getJSONObject(key)

                var displayName = ""
                if (label.equals("", true)) {
                    displayName = keyObject.getString(key)
                } else {
                    displayName = label
                }

                val labelKey = keyObject.getString("labelKey")
                val type = keyObject.optString("type")
                val dropDownValue = keyObject.optString("dropdownValue")


                val isRequired = keyObject.optBoolean("isRequired")
                val dataType = keyObject.optString("dataType")
                hashMapDisplayName[key] = displayName


                if (hasSort) {
                    hasSortOption = true
                    sortKeys.add(key)
                    sortDisplayName.add(displayName)
                }
                if (hasFilter) {
                    hasFilterOption = true
                    filterKeys.add(key)
                }

                if (layOutName.equals("list", true)) {

                    currentWorkFLowName = workFlowName

                    if (placement.equals("Row1", true)) {
                        val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter, dropDownValue)

                        if (rowOneList.size < 2) {
                            rowOneList.add(llR)
                        }

                    } else if (placement.equals("ROW1IMG1", true)
                            || placement.equals("ROW1COL1", true)
                    ) {
                        val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter, dropDownValue)
                        rowOneClmOneList.add(llR)
                    } else if (placement.equals("Row2", true)) {
                        val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter, dropDownValue)
                        rowTwoList.add(llR)
                    } else if (placement.equals("Row3", true)) {
                        val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter, dropDownValue)
                        rowThreeList.add(llR)
                    } else {

                        if (rowTwoList.size < 3) {
                            val llR = ListLayoutRow(key, label, displayName, type, "Row2", hasSort, hasFilter, dropDownValue)
                            rowTwoList.add(llR)
                        } else if (rowThreeList.size < 3) {
                            val llR = ListLayoutRow(key, label, displayName, type, "Row3", hasSort, hasFilter, dropDownValue)
                            rowThreeList.add(llR)
                        }

                    }
                } else if (layOutName.equals("view", true)) {

                    showCountWithTitle = false

                    if (!itemType.equals("hidden", true)) {


                        val llR = ListLayoutRow(key, label, displayName, type, "NA", hasSort,
                                hasFilter, dropDownValue,valueExpression)
                        detailsViewList.add(llR)


                    }

                }


            }


            var rowOneWeightSum = 0f
            if (rowOneClmOneList.size == 0) {
                rowOneWeightSum = rowOneList.size.toFloat()
            } else {
                rowOneWeightSum = (rowOneClmOneList.size + rowOneList.size).toFloat()
            }

            val rowTwoWeightSum = rowTwoList.size.toFloat()
            val rowThreeWeightSum = rowThreeList.size.toFloat()

            mBinding.serviceTtl.setText(title)



            if (dataRes.equals("") && layOutName.equals("list", true)) {
                showFooterMenu(false, false, false)
                mBinding.listEmptyView.visibility = View.VISIBLE
                return
            } else {
                mBinding.listEmptyView.visibility = View.GONE
            }
            val dataJson = JSONObject(dataRes)
            val dataArry = dataJson.optJSONArray("data")?:JSONArray()
            totalCount = dataJson.optInt("totalCount")

            if (dataArry.length() == 0 && layOutName.equals("list", true)) {
                showFooterMenu(false, false, false)
                mBinding.listEmptyView.visibility = View.VISIBLE
                return
            } else {
                mBinding.listEmptyView.visibility = View.GONE
            }


            var titleValue = ""
            if (showCountWithTitle && !hasTabs) {
                titleValue = title + " (${totalCount})"
            } else {
                titleValue = title
            }


            mBinding.serviceTtl.setText(titleValue)

            for (i in 0 until dataArry.length()) {
                if (layOutName.equals("list", true)) {
                    val item = dataArry.getJSONObject(i)
                    var rowLists: ArrayList<ListRow> = ArrayList()
                    val listOfItems: MutableList<ListItem> = mutableListOf()
                    val listOfItems1: MutableList<ListItem> = mutableListOf()
                    val listOfItems2: MutableList<ListItem> = mutableListOf()
                    for (j in rowOneClmOneList.indices) {

                        var valueKey = rowOneClmOneList[j].key

                        val value = item.getString(valueKey)

                        val row = ListItem(1f, rowOneClmOneList[j].key, rowOneClmOneList[j].displayName, value,
                                rowOneClmOneList[j].placementName, j, rowOneClmOneList[j].type,
                                rowOneClmOneList[j].hasSort, rowOneClmOneList[j].hasFilter)
                        listOfItems.add(row)
                    }

                    for (j in rowOneList.indices) {
                        var valueKey = rowOneList[j].key

                        val value = item.getString(valueKey)
                        val row = ListItem(1f, rowOneList[j].key, rowOneList[j].displayName, value,
                                rowOneList[j].placementName, rowOneClmOneList.size + j, "text",
                                rowOneList[j].hasSort, rowOneList[j].hasFilter)


                        listOfItems.add(row)
                    }


                    val rowList = ListRow("Row1", rowOneWeightSum, listOfItems)

                    for (j in rowTwoList.indices) {
                        var valueKey = rowTwoList[j].key

                        val value = item.getString(valueKey)
                        val row = ListItem(1f, rowTwoList[j].key, rowTwoList[j].displayName, value,
                                rowTwoList[j].placementName, j, rowTwoList[j].type,
                                rowTwoList[j].hasSort, rowTwoList[j].hasFilter)

                        listOfItems1.add(row)
                    }

                    val row2List = ListRow("Row2", rowTwoWeightSum, listOfItems1)

                    for (j in rowThreeList.indices) {
                        var valueKey = rowThreeList[j].key

                        val value = item.getString(valueKey)
                        val row = ListItem(1f, rowThreeList[j].key, rowThreeList[j].displayName, value,
                                rowThreeList[j].placementName, j, rowThreeList[j].type,
                                rowTwoList[j].hasSort, rowTwoList[j].hasFilter)

                        listOfItems2.add(row)
                    }

                    val row3List = ListRow("Row3", rowThreeWeightSum, listOfItems2)

                    rowLists.add(rowList)
                    rowLists.add(row2List)
                    rowLists.add(row3List)

                    val lsResponse = ListingResponse(rowLists, item.toString(), decisionName)
                    mainRowList.add(lsResponse)

                } else if (layOutName.equals("view", true)) {
                    val item = dataArry.getJSONObject(i)

                    for (j in detailsViewList.indices) {
                        var valueKey = ""
                        if (detailsViewList[j].type.equals("dropdown", true)
                                && !detailsViewList[j].dropDownValue.equals("")) {
                            valueKey = detailsViewList[j].dropDownValue
                        } else {
                            valueKey = detailsViewList[j].key
                        }

                        var value = item.optString(valueKey)

                        if(detailsViewList[j].valueExpression.isNotEmpty()){
                            value = detailsViewList[j].valueExpression
                            var keysArrayList = findKeysToReplace(detailsViewList[j].valueExpression)
                            for(k in keysArrayList.indices){
                                val key = keysArrayList[k]
                                value = value.replace(key,item.optString(key))
                            }
                            value = value.replace("\${", "")
                            value = value.replace("}", "")
                            value = value.replace("'", "");

                            value = value.replace("return", "")

                        }



                        val row = ListItem(1f, detailsViewList[j].key, detailsViewList[j].displayName, value,
                                detailsViewList[j].placementName, j, detailsViewList[j].type,
                                detailsViewList[j].hasSort, detailsViewList[j].hasFilter)

                        detailsListOfItems.add(row)
                    }


                }


            }


            showFooterMenu(hasSearchOption, hasSortOption, hasFilterOption)

            if (layOutName.equals("view", true)) {
                mBinding.detailsView.visibility = View.VISIBLE

                val mainLayout = createLinearLay(requireActivity(), LinearLayout.VERTICAL,
                        MATCH_PARENT, WRAP_CONTENT, 1f,
                        WorkFlowMargin(10, 10, 20, 10))
                for (i in 0 until detailsListOfItems.size) {


                    val iteamOne = detailsListOfItems[i]

                    val row = createLinearLay(requireActivity(), LinearLayout.HORIZONTAL,
                            MATCH_PARENT, WRAP_CONTENT, 2f,
                            WorkFlowMargin(0, 0, 0, 0))

                    val tvLabel = createTextView(requireActivity(), iteamOne.itemLabel.toString() + " :", MATCH_PARENT, WRAP_CONTENT,
                            1f, WorkFlowMargin(20, 10, 10, 10), Gravity.LEFT)



                    val tvValue = createTextView(requireActivity(), iteamOne.itemValue.toString(), MATCH_PARENT, WRAP_CONTENT,
                            1f, WorkFlowMargin(20, 10, 10, 10), Gravity.LEFT)

                    row.addView(tvLabel)
                    row.addView(tvValue)
                    mainLayout.addView(row)


                }

                mBinding.detailCard.addView(mainLayout)

            }

        } catch (e: Exception) {

        }
    }

    /**
     * used to find the keys to be replaced from value expression*/
    fun findKeysToReplace(str: String?) : ArrayList<String> {
        val arraList = ArrayList<String>()
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

    private fun showFooterMenu(hasSearch: Boolean,
                               hasSort: Boolean, hasFilter: Boolean) {

        if (hasFilter || hasSearch || hasSort) {

            mBinding.bottomNavigationView.visibility = View.VISIBLE
            mBinding.bottomNavigationView.menu.findItem(R.id.navigation_search).isVisible = hasSearch
            mBinding.bottomNavigationView.menu.findItem(R.id.navigation_sort).isVisible = hasSort
            mBinding.bottomNavigationView.menu.findItem(R.id.navigation_filter).isVisible = hasFilter


        } else {
            mBinding.bottomNavigationView.visibility = View.GONE
        }


    }

    private fun setValuesToAdapter() {
        recyclerViewAdapter = QtcListLayoutAdapter(mainRowList, requireActivity())
        val llm = LinearLayoutManager(requireActivity())
        llm.orientation = LinearLayoutManager.VERTICAL
        mBinding.qtcRecyclerView.layoutManager = llm
        mBinding.qtcRecyclerView.adapter = recyclerViewAdapter


        recyclerViewAdapter.onItemClick = { selectedItem ->

            clearPrevData()

            val payLoadObj: JsonObject = Gson().fromJson(selectedItem.payLoadData, JsonObject::class.java)
            payLoadData = payLoadObj
            decisionName = selectedItem.decisionName
            dataReqName = decisionName


            AppPreferences.saveValue(Constants.PrefCode.QTC_PAYLOAD_OBJ, selectedItem.payLoadData).toString()
            AppPreferences.saveValue(Constants.PrefCode.QTC_HANDLER, dataReqName).toString()
            mViewModel.connectAppLayout(decisionName)

        }


    }

    private fun notifyAdapterChanges() {
        recyclerViewAdapter.notifyDataSetChanged()
    }


    private fun createFilterScreen() {

        val dialog = Dialog(requireActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.filter_screen_lay)
        val applyTv = dialog.findViewById<TextView>(R.id.apply_tv)
        val resetTv = dialog.findViewById<TextView>(R.id.reset_tv)
        val mainLay = dialog.findViewById<LinearLayout>(R.id.main_lay);

        dialog.findViewById<ImageView>(R.id.close_icon).setOnClickListener {
            dialog.dismiss()
        }


        hashMapDropDown.forEach { (key, value) ->
            println("$key = $value")
            val spinnerArray = ArrayList<String>()
            for (i in 0 until value.length()) {
                if (i == 0) {
                    spinnerArray.add(" ")
                }
                spinnerArray.add(value[i].toString())
            }
            val params = TableLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0f)
            params.setMargins(10, 10, 10, 10)


            val spinner = Spinner(requireContext())
            spinner.layoutParams = params
            spinner.tag = key
            spinner.setBackgroundResource(R.drawable.filer_spnr_bg);
            val spinnerArrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerArray)
            spinner.adapter = spinnerArrayAdapter

            if (hashMapSpinnerLastSelected.containsKey(key)) {
                spinner.setSelection(hashMapSpinnerLastSelected[key] ?: 0)
            }

            mainLay.addView(createTextView(requireActivity(), hashMapDisplayName.get(key).toString(), MATCH_PARENT, WRAP_CONTENT,
                    0f, WorkFlowMargin(20, 10, 20, 10), Gravity.LEFT))
            mainLay.addView(spinner)
            hashMapSpinner[key] = spinner


        }

        resetTv.setOnClickListener {
            hashMapDropDown.forEach { (key, value) ->
                hashMapSpinner.get(key)?.setSelection(0)
            }
            removeValue(operationArray, "filter")
        }


        applyTv.setOnClickListener {
            val mustArr = JsonArray()
            var hasSelectedValue = false
            hashMapDropDown.forEach { (key, value) ->

                val obj = JsonObject()
                val termsObj = JsonObject()
                val itemArr = JsonArray()
                if (hashMapSpinner.get(key)?.selectedItemPosition != 0) {
                    itemArr.add(hashMapSpinner.get(key)?.selectedItem.toString())
                    hashMapSpinnerLastSelected[key] = hashMapSpinner.get(key)?.selectedItemPosition
                            ?: 0
                    obj.add(key + ".raw", itemArr)
                    termsObj.add("terms", obj)

                    mustArr.add(termsObj)
                    hasSelectedValue = true
                }


            }
            dialog.dismiss()

            isClearList = true

            if (!hasValue(operationArray, "filter")) {
                operationArray.add("filter")
            }


            val mustObj = JsonObject()
            val boolObj = JsonObject()
            mustObj.add("must", mustArr)
            boolObj.add("bool", mustObj)

            addDefaultPaginationSize(qpObject)
            qpObject.add("query", boolObj)



            if (hasSelectedValue) {
                mViewModel.connectFilterAppData(
                        currentWorkFLowName,
                        qpObject, operationArray)
            } else {
                hashMapSpinnerLastSelected.clear()
            }

        }

        dialog.show()
    }


    private fun showFilterBottomSheet() {
        val view: View = layoutInflater.inflate(R.layout.qtc_filter_lay, null)
        bottomSheetDialog = BottomSheetDialog(requireActivity())
        val listView = view.findViewById<ListView>(R.id.lst_view)
        val radioButton = view.findViewById<RadioButton>(R.id.radio_button)

        radioButton.setOnClickListener {
            removeValue(operationArray, "sort")
            bottomSheetDialog.dismiss()
            sortItem = SortData(sortKeys, sortDisplayName, 0, "NA", false)
        }


        if (sortItem.isSelected) {
            radioButton.isChecked = false
        }

        myAdapter = QtcSortAdapter(requireActivity(), sortItem, bottomSheetDialog)
        listView?.adapter = myAdapter
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()


        myAdapter.onItemClick = { selectedItem ->
            radioButton.isChecked = false

            val sortArr = JsonArray()
            val jsonObject = JsonObject()
            val valueObj = JsonObject()
            valueObj.addProperty("order", selectedItem.orderType)
            jsonObject.add(selectedItem.item + ".raw", valueObj)
            sortArr.add(jsonObject)
            isClearList = true

            if (!hasValue(operationArray, "sort")) {
                operationArray.add("sort")
            }

            addDefaultPaginationSize(qpObject)

            qpObject.add("sort", sortArr)

            sortItem = SortData(sortKeys, sortDisplayName, selectedItem.pos, selectedItem.orderType, true)

            mViewModel.connectSortAppData(
                    currentWorkFLowName,
                    qpObject, operationArray)
        }

    }

    fun addDefaultPaginationSize(jsonObject: JsonObject) {
        jsonObject.addProperty(SIZE, PAGINATION_DEFAULT_SIZE)
        jsonObject.addProperty(FROM, 0)
    }

    fun displayDecisions(){

        val layOutRes = hasMapLayoutRes[currentWorkFLowName] ?: "{}"
        val workFlowName = AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString()
        parentWorkFlowName =workFlowName


        val res = JSONObject(layOutRes)
        val flowObject = res.optJSONObject("flow") ?: JSONObject()

        var workFowObj = flowObject.optJSONObject(workFlowName) ?: JSONObject()

        val decisionArr = workFowObj.optJSONArray("decisions") ?: JSONArray()

        if (decisionArr.length() > 0) {

            for (d in 0 until decisionArr.length()) {
                val decisionObj = decisionArr.getJSONObject(d)
                val outComeObj = decisionObj.getJSONArray("outcomes")[0] as JSONObject
                val selection = decisionObj.optString("selection")
                val position = decisionObj.optString("position")
                val displayed = decisionObj.optString("displayed")
                val label = decisionObj.optString("label")
                val labelkey = decisionObj.optString("labelkey")
                //labelkey
                val outComeName = outComeObj.optString("name")
                val outcomeData = outComeObj.optString("data")
                val outcomeDisplayType = outComeObj.optString("displayType")


                val outcomeObj = Outcome(outcomeDisplayType, outComeName, outcomeData)
                val outComeObjList: ArrayList<Outcome> = ArrayList()
                outComeObjList.add(outcomeObj)
                val decisions = Decisions(displayed, label, labelkey, selection, position, outComeObjList)
                decisionsArrayList.add(decisions)

                if (selection.equals("row-selection", true) ||
                        position.equals("row-selection", true)) {
                    decisionName = outComeObj.getString("name")
                    decisionData = outComeObj.getString("data")
                }


            }
        }

        for (i in decisionsArrayList.indices) {
            val decisions = decisionsArrayList[i]

            if (decisions.selection.equals("row-selection", true) ||
                    decisions.position.equals("row-selection", true)) {
                val outcomeObj = decisions.outcomes[0]
                val name = outcomeObj.name
                decisionName = name
            } else {
                val outcomeObj = decisions.outcomes[0]

                val id = hasMapDecisions.size
                val payLoadString = AppPreferences.getKeyValue(Constants.PrefCode.QTC_PAYLOAD_OBJ, "").toString()

                val paObj = JSONObject(payLoadString)
                var show = false
                val display = decisions.displayed

                if (display.contains("&&")) {
                    val mainSplit = display.split("&&")

                    show = evaluateExpressionResult(mainSplit[0], paObj) && evaluateExpressionResult(mainSplit[1], paObj)

                } else if (display.contains("||")) {
                    val mainSplit = display.split("||")

                    show = evaluateExpressionResult(mainSplit[0], paObj) || evaluateExpressionResult(mainSplit[1], paObj)

                } else {
                    show = evaluateExpressionResult(display, paObj)
                }


                if (show) {
                    mBinding.moreImg.visibility = View.VISIBLE
                    mBinding.moreImg.setOnClickListener {
                       overFlowPopup?.show()
                    }

                    if(overFlowPopup==null){
                        overFlowPopup = createPopUpMenu()
                    }
                    overFlowPopup?.menu?.add(0, id, 0, decisions.label)
                    hasMapDecisions[id] = outcomeObj.name
                }

            }

        }

    }
    fun createPopUpMenu(): PopupMenu{
        val popup = PopupMenu(requireContext(), mBinding.moreImg)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.dynam_menu, popup.menu)

        popup.setOnMenuItemClickListener {
            onOptionsItemSelected(it)
        }

        return popup

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (hasMapDecisions.containsKey(item.itemId)) {
            layoutType = ""
            decisionName = hasMapDecisions[item.itemId].toString()
            mViewModel.connectAppLayout(decisionName)

        }

        return false
    }


    private fun popUpLayout(title: String,
                            msg: String, positiveBtnTtl: String, decisionName: String) {

        val payLoadString = AppPreferences.getKeyValue(Constants.PrefCode.QTC_PAYLOAD_OBJ, "").toString()
        val payLoadObj: JsonObject = Gson().fromJson(payLoadString, JsonObject::class.java)
        val jsonObj = JsonObject()
        jsonObj.add(decisionName, payLoadObj)

        val builder1: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder1.setTitle(title)
        builder1.setMessage(msg)
        builder1.setCancelable(true)

        builder1.setPositiveButton(
                positiveBtnTtl
        ) { dialog, _ ->
            dialog.cancel()
            mViewModel.connectAppDecision(decisionName,decisionName, jsonObj)
        }

        builder1.setNegativeButton(
                "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }


    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }


    private fun handleBackPressed(){

        val backStackValue =(activity as DashboardActivity).getValueToBackStack()
        if(backStackValue==null){
            findNavController().navigate(R.id.action_qtcCompositeFrag_to_appDetailFrag)
            (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        }else {
            AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, backStackValue?.workFlowTabs.toString())
            AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, backStackValue?.workFlowResMap.toString())
            AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, backStackValue?.workFlowLayoutResMap.toString())
            AppPreferences.saveValue(Constants.PrefCode.QTC_HANDLER, backStackValue?.parentWFName.toString())
            AppPreferences.saveValue(Constants.PrefCode.QTC_PAYLOAD_OBJ, backStackValue?.parentPayLoadObj.toString())

            findNavController().navigate(R.id.action_qtcCompositeFrag_self)

        }


    }

    private fun navToListAfterDecision(){

        val backStackValue =(activity as DashboardActivity).getValueToBackStack()
        currentWorkFLowName =  backStackValue?.parentWFName.toString()
        (activity as DashboardActivity).listClickHandler(currentWorkFLowName)
    }

}



