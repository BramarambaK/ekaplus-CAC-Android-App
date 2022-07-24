package com.eka.cacapp.ui.workflow

import android.app.Dialog

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.adapter.QtcListLayoutAdapter
import com.eka.cacapp.adapter.QtcSortAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.qtcLayout.*
import com.eka.cacapp.databinding.QtcListFragBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.dashboard.DashboardViewModel
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.AppUtil.Companion.getJsonDataFromAsset
import com.eka.cacapp.utils.AppUtil.Companion.hasValue
import com.eka.cacapp.utils.AppUtil.Companion.removeValue
import com.eka.cacapp.utils.Constants
import com.eka.cacapp.utils.Constants.WorkFlow.FROM
import com.eka.cacapp.utils.Constants.WorkFlow.PAGINATION_DEFAULT_SIZE
import com.eka.cacapp.utils.Constants.WorkFlow.SIZE
import com.eka.cacapp.utils.ProgressDialogUtil
import com.eka.cacapp.utils.WorkFlowViews.MATCH_PARENT
import com.eka.cacapp.utils.WorkFlowViews.WRAP_CONTENT
import com.eka.cacapp.utils.WorkFlowViews.createTextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject


class QtcListFrag : Fragment() {

    private var payLoadData = JsonObject()
    private lateinit var mBinding: QtcListFragBinding
    private lateinit var mViewModel: DashboardViewModel

    private var rowOneList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowOneClmOneList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowTwoList: ArrayList<ListLayoutRow> = ArrayList()
    private var rowThreeList: ArrayList<ListLayoutRow> = ArrayList()

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


            mBinding = DataBindingUtil.inflate(inflater, R.layout.qtc_list_frag, container, false)


            (activity as DashboardActivity).clearSelectedViews()
            (activity as DashboardActivity).showBackButton()


            operationArray.add("pagination")


            addDefaultPaginationSize(qpObject)


            sortItem = SortData(sortKeys, sortDisplayName, 0, "NA", false)
            addValuesToAdapter()


            (activity as DashboardActivity).clearListView()

            (activity as DashboardActivity).makeAppTabSelected()

            val factory = ViewModelFactory(DashboardRepository(),requireContext())

            mViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

            mBinding.bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


            mBinding.qtcRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {

                        if (mainRowList.size < totalCount) {
                            isClearList = false
                            qpObject.addProperty(SIZE, PAGINATION_DEFAULT_SIZE)
                            qpObject.addProperty(FROM, mainRowList.size)
                            mViewModel.connectAppData(
                                    AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString(),
                                    qpObject, operationArray,payLoadData)
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
                        val dataObj = jsonObject.getJSONArray("data")[0] as JSONObject

                        for (i in 0 until filterKeys.size()) {
                            val key = filterKeys[i].asString.trim()
                            hashMapDropDown[key] = dataObj.get(key) as JSONArray
                        }

                        createFilterScreen()

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        // handleError(it) {null}
                        //handleApiError(it){null}
                    }
                }

            })


            mViewModel.connectAppDataResponse.observe(viewLifecycleOwner, Observer {

                if (it is Resource.Loading) {
                    activity?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                }
                when (it) {
                    is Resource.Success -> {
                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        updateDataValues(result)
                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
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
                            AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString(), qpObject,
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
                        AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString()
                        , filterKeys)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun hideSearchView(){
        mBinding.searchView.visibility = View.GONE
        mBinding.searchLay.visibility = View.GONE
        mBinding.searchView.setQuery("", false)
    }


    /**
     * Function to add categories to recycler view adapter
     * */
    private fun addValuesToAdapter() {

        //val layOutRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_LIST_RES, "").toString()
          val layOutRes = getJsonDataFromAsset(requireActivity(), "lay_res.json").toString()
        val dataRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_DATA_RES, "").toString()
        parseData(layOutRes, dataRes)
        setValuesToAdapter()
    }


    private fun updateDataValues(dataRes: String) {

        val layoutRes = AppPreferences.getKeyValue(Constants.PrefCode.QTC_LIST_RES, "").toString()

        parseData(layoutRes, dataRes)
        notifyAdapterChanges()

    }

    private fun parseData(layOutRes: String, dataRes: String) {

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

            val workFowObj = flowObject.getJSONObject(
                    AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString())

            val decisionObj = workFowObj.getJSONArray("decisions")[0] as JSONObject
            val outComeObj = decisionObj.getJSONArray("outcomes")[0] as JSONObject
            decisionName = outComeObj.getString("name")
            decisionData = outComeObj.getString("data")

            val title = workFowObj.getString("label");

            val layoutObj = workFowObj.getJSONObject("layout")
            val optionsObj = layoutObj.getJSONObject("options")

            hasSearchOption = optionsObj.getBoolean("serverSearch")


            val fieldsArr = workFowObj.getJSONArray("fields")

            val objectMeta = res.getJSONObject("objectMeta")
            val fields = objectMeta.getJSONObject("fields")


            for (i in 0 until fieldsArr.length()) {
                val item = fieldsArr.getJSONObject(i)

                val key = item.getString("key")
                val placement = item.getString("placement")
                val label = item.optString("label")
                val hasSort = item.optBoolean("sort")
                val hasFilter = item.optBoolean("filter")


                val keyObject = fields.getJSONObject(key)

                val displayName = keyObject.getString(key)
                val labelKey = keyObject.getString("labelKey")
                val type = keyObject.optString("type")
                val isRequired = keyObject.optBoolean("isRequired")
                val dataType = keyObject.getString("dataType")
                val dropDownValue = keyObject.optString("dropdownValue")

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


                if (placement.equals("Row1", true)) {
                    val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter,dropDownValue)

                    if (rowOneList.size < 2) {
                        rowOneList.add(llR)
                    }

                } else if (placement.equals("ROW1IMG1", true)
                        || placement.equals("ROW1COL1", true)
                ) {
                    val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter,dropDownValue)
                    rowOneClmOneList.add(llR)
                } else if (placement.equals("Row2", true)) {
                    val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter,dropDownValue)
                    rowTwoList.add(llR)
                } else if (placement.equals("Row3", true)) {
                    val llR = ListLayoutRow(key, label, displayName, type, placement, hasSort, hasFilter,dropDownValue)
                    rowThreeList.add(llR)
                } else {

                    if (rowTwoList.size < 3) {
                        val llR = ListLayoutRow(key, label, displayName, type, "Row2", hasSort, hasFilter,dropDownValue)
                        rowTwoList.add(llR)
                    } else if (rowThreeList.size < 3) {
                        val llR = ListLayoutRow(key, label, displayName, type, "Row3", hasSort, hasFilter,dropDownValue)
                        rowThreeList.add(llR)
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

            val dataJson = JSONObject(dataRes)
            val dataArry = dataJson.getJSONArray("data")
            totalCount = dataJson.getInt("totalCount")

            (activity as DashboardActivity).setTitle(title + " (${dataJson.get("totalCount")})")

            for (i in 0 until dataArry.length()) {
                val item = dataArry.getJSONObject(i)
                var rowLists: ArrayList<ListRow> = ArrayList()
                val listOfItems: MutableList<ListItem> = mutableListOf()
                val listOfItems1: MutableList<ListItem> = mutableListOf()
                val listOfItems2: MutableList<ListItem> = mutableListOf()
                for (j in rowOneClmOneList.indices) {
                    val value = item.getString(rowOneClmOneList[j].key)
                    val row = ListItem(1f, rowOneClmOneList[j].key, rowOneClmOneList[j].displayName, value,
                            rowOneClmOneList[j].placementName, j, rowOneClmOneList[j].type,
                            rowOneClmOneList[j].hasSort, rowOneClmOneList[j].hasFilter)
                    listOfItems.add(row)
                }

                for (j in rowOneList.indices) {
                    val value = item.getString(rowOneList[j].key)
                    val row = ListItem(1f, rowOneList[j].key, rowOneList[j].displayName, value,
                            rowOneList[j].placementName, rowOneClmOneList.size + j, "text",
                            rowOneList[j].hasSort, rowOneList[j].hasFilter)


                    listOfItems.add(row)
                }


                val rowList = ListRow("Row1", rowOneWeightSum, listOfItems)

                for (j in rowTwoList.indices) {
                    val value = item.getString(rowTwoList[j].key)
                    val row = ListItem(1f, rowTwoList[j].key, rowTwoList[j].displayName, value,
                            rowTwoList[j].placementName, j, rowTwoList[j].type,
                            rowTwoList[j].hasSort, rowTwoList[j].hasFilter)

                    listOfItems1.add(row)
                }

                val row2List = ListRow("Row2", rowTwoWeightSum, listOfItems1)

                for (j in rowThreeList.indices) {
                    val value = item.getString(rowThreeList[j].key)
                    val row = ListItem(1f, rowThreeList[j].key, rowThreeList[j].displayName, value,
                            rowThreeList[j].placementName, j, rowThreeList[j].type,
                            rowTwoList[j].hasSort, rowTwoList[j].hasFilter)

                    listOfItems2.add(row)
                }

                val row3List = ListRow("Row3", rowThreeWeightSum, listOfItems2)

                rowLists.add(rowList)
                rowLists.add(row2List)
                rowLists.add(row3List)

                val lsResponse = ListingResponse(rowLists,item.toString(),decisionName)
                mainRowList.add(lsResponse)


            }

            showFooterMenu(hasSearchOption, hasSortOption, hasFilterOption)


        } catch (e: Exception) {

        }
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

            if(hashMapSpinnerLastSelected.containsKey(key)){
               spinner.setSelection(hashMapSpinnerLastSelected[key]?:0)
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
                    hashMapSpinnerLastSelected[key] = hashMapSpinner.get(key)?.selectedItemPosition?:0
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
                        AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString(),
                        qpObject, operationArray)
            }else{
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
                    AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString(),
                    qpObject, operationArray)
        }

    }

    fun addDefaultPaginationSize(jsonObject: JsonObject) {
        jsonObject.addProperty(SIZE, PAGINATION_DEFAULT_SIZE)
        jsonObject.addProperty(FROM, 0)
    }
}



