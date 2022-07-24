package com.eka.cacapp.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.eka.cacapp.R
import com.eka.cacapp.adapter.NavMenuItemAdapter
import com.eka.cacapp.base.ViewModelFactory
import com.eka.cacapp.data.appMainList.CategoryInfoModel
import com.eka.cacapp.data.qtcLayout.QtcMapData
import com.eka.cacapp.databinding.ActivityDashboardBinding
import com.eka.cacapp.network.Resource
import com.eka.cacapp.repositories.DashboardRepository
import com.eka.cacapp.ui.dashboard.*
import com.eka.cacapp.ui.settings.SettingsActivity
import com.eka.cacapp.utils.*
import com.eka.cacapp.utils.Constants.WorkFlow.PAGINATION_DEFAULT_SIZE
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.intercom.android.sdk.Intercom
import org.json.JSONArray
import org.json.JSONObject
import java.util.*


class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var mBinding: ActivityDashboardBinding
    private lateinit var mToolbar: Toolbar
    private lateinit var mListView: ListView
    private lateinit var mViewModel: DashboardViewModel
    private var mNavItemArrayList: ArrayList<String> = ArrayList()
    private lateinit var header: ViewGroup
    private var navToAppsIfNoFav = true
    private lateinit var favoriteTv: TextView
    private lateinit var appsTv: TextView
    private lateinit var toggle: ActionBarDrawerToggle

    private var handlerListItem = ""
    private var qtcLayoutRes = ""
    private var workFlowArrayList: ArrayList<String> = ArrayList()
    private var workFlowTabs = JSONArray()
    private var workFlowResMap = JSONArray()
    private var workFlowLayoutResMap = JSONArray()

    private var dataReqName = ""
    private var resCount = 0
    private var resFailureCount = 1
    private var layoutType = ""
    private var payLoadData = JsonObject()

    private var qtckBackStack : Stack<QtcMapData> = Stack()
    private var defaultTraderKey = ""
    private lateinit var aboutUsTv: TextView
    private lateinit var needHelpTv: TextView
    private lateinit var notifMenuItem : MenuItem

    private var lastFormTag = ""

    private lateinit var switchCrpTv: TextView

    lateinit var  switchCorporate : SwitchCorporate
    private lateinit var currentCorporateDtl : JSONObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)

        favoriteTv = mBinding.navContent.findViewById(R.id.nav_fav_tv)
        aboutUsTv = mBinding.navContent.findViewById(R.id.nav_about_us_tv)
        needHelpTv = mBinding.navContent.findViewById(R.id.nav_help_tv)

        appsTv = mBinding.navContent.findViewById(R.id.nav_apps_tv)

        switchCrpTv = mBinding.navContent.findViewById(R.id.nav_swtch_crp_tv)

        setFooterAndVersion()


        val factory = ViewModelFactory(DashboardRepository(),this)

        mViewModel = ViewModelProvider(this, factory).get(DashboardViewModel::class.java)

        mListView = mBinding.navContent.findViewById(R.id.list_view)

        val inflater = layoutInflater
        header = inflater.inflate(R.layout.nav_menu_hdr_view, mListView, false) as ViewGroup

        toggle = ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        mBinding.navView.setNavigationItemSelectedListener(this)

        setClientBannerImage()
        updateDrawerMenuTitle()
/*
        if(AppPreferences.getKeyValue(Constants.PrefCode.NAV_TO_FAV,"").equals("Y",true)){
            changeStartDestination(R.id.favoriteCatFrag)
        }else{
            changeStartDestination(R.id.dashboardFrag)
        }
*/
        AppPreferences.saveValue(Constants.PrefCode.FAV_LIST, "")
        AppPreferences.saveValue(Constants.PrefCode.CAT_LIST, "")

        checkForProfileOption()

        mViewModel.getFavCategory()
        mViewModel.getNotificationList()

        switchCrpTv.setOnClickListener {
            mViewModel.getCurrentCorporate()
        }

        appsTv.setOnClickListener {
            AppUtil.sendGoogleEvent(this,"Home","App Menu","General")
            mViewModel.getCategory()
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        favoriteTv.setOnClickListener {
            AppUtil.sendGoogleEvent(this,"Home","App Menu","General")
            navToAppsIfNoFav = false
            mViewModel.getFavCategory()
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)

        }

        aboutUsTv.setOnClickListener {
            AppUtil.sendGoogleEvent(this,"Home",
                    "About us","General")
            val aboutUsScreen = AboutUsScreen(this,
                    android.R.style.Theme_Light_NoTitleBar_Fullscreen)
            aboutUsScreen.show()
        }

        needHelpTv.setOnClickListener {
            AppUtil.sendGoogleEvent(this,"Home",
                    "Need help?","General")
            val needHelpScreen = NeedHelpScreen(this,
                    android.R.style.Theme_Light_NoTitleBar_Fullscreen)
            needHelpScreen.show()
        }

        mBinding.navHeader.findViewById<ImageView>(R.id.settings_icon).setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
           // finish()
        }





        mViewModel.getFavCategoryResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                this.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    val favCategoryResponse: CategoryInfoModel = Gson().fromJson(
                            it.value.toString(),
                            CategoryInfoModel::class.java
                    )


                    if (favCategoryResponse.isEmpty()) {

                        if (navToAppsIfNoFav) {
                            mViewModel.getCategory()
                        } else {
                            ProgressDialogUtil.hideProgressDialog()
                        }


                    } else {
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.FAV_LIST, it.value.toString())

                        findNavController(R.id.nav_host_fragment).navigate(R.id.favoriteCatFrag)


                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}

                    if (navToAppsIfNoFav) {
                        mViewModel.getCategory()
                    } else {
                        handleApiError(it){null}
                    }


                }
            }
        })



        mViewModel.getCategoryResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                this?.let { it1 ->
                    if (!navToAppsIfNoFav) {
                        ProgressDialogUtil.showProgressDialog(it1)
                    }

                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()
                    AppPreferences.saveValue(Constants.PrefCode.CAT_LIST, it.value.toString())
/*                    AppPreferences.saveValue(Constants.PrefCode.NAV_TO_FAV,"N")*/

                    findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFrag)

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    // handleError(it) {null}
                    handleApiError(it){null}
                }
            }

        })



        mViewModel.connectAppDataResponse.observe(this, Observer {
            if (it is Resource.Loading) {
                this?.let { it1 ->

                    //  ProgressDialogUtil.showProgressDialog(it1)

                }
            }
            when (it) {
                is Resource.Success -> {

                    resCount++
                    val result =   it.value.string()

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
                            findNavController(R.id.nav_host_fragment).navigate(R.id.qtcCompositeFrag)
                        }
                        else{
                            val paginationArray = JsonArray()
                            dataReqName = workFlowArrayList[workFlowResMap.length()]
                            mViewModel.connectAppData(dataReqName, defaultQpObject(), paginationArray,payLoadData)
                        }


                    } catch (e: Exception) {
                        ProgressDialogUtil.hideProgressDialog()

                    }


                }
                is Resource.Failure -> {


                    if (workFlowResMap.length() > 0 &&
                            workFlowResMap.length() == workFlowArrayList.size - 1) {
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())
                        AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, workFlowResMap.toString())
                        AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())
                        findNavController(R.id.nav_host_fragment).navigate(R.id.qtcCompositeFrag)
                    }

                    else if (resFailureCount<workFlowArrayList.size){

                        val paginationArray = JsonArray()

                        dataReqName = workFlowArrayList[resFailureCount]
                        resFailureCount ++
                        mViewModel.connectAppData(dataReqName, defaultQpObject(), paginationArray,payLoadData)

                    }

                    else  {
                        ProgressDialogUtil.hideProgressDialog()
                        AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())
                        AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, "")
                        AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())
                        findNavController(R.id.nav_host_fragment).navigate(R.id.qtcCompositeFrag)
                    }


                    // handleError(it) {null}
                 //   handleApiError(it){null}
                }
            }

        })

        mViewModel.connectAppLayoutResponse.observe(this, Observer {
            if (it is Resource.Loading) {
                this?.let { it1 ->

                    if(layoutType.equals("")){
                        ProgressDialogUtil.showProgressDialog(it1)
                    }


                }
            }
            when (it) {
                is Resource.Success -> {
                    //ProgressDialogUtil.hideProgressDialog()

                    val result = it.value.string()
                    qtcLayoutRes = result


                    if (layoutType.equals("")) {

                        AppPreferences.saveValue(Constants.PrefCode.QTC_LIST_RES, result)


                        val res = JSONObject(qtcLayoutRes)
                        val flowObject = res.getJSONObject("flow")

                        var workFowObj = flowObject.optJSONObject(
                                AppPreferences.getKeyValue(Constants.PrefCode.QTC_HANDLER, "").toString())


                        val layoutObj = workFowObj.getJSONObject("layout")
                        layoutType = layoutObj.getString("name")

                        if (layoutType.equals("list", true)) {
                            dataReqName = handlerListItem
                            workFlowArrayList.add(dataReqName)
                            val jsonObject = JSONObject()
                            jsonObject.put("key", dataReqName)
                            jsonObject.put("value", qtcLayoutRes)
                            workFlowLayoutResMap.put(jsonObject)
                            val paginationArray = JsonArray()
                            paginationArray.add("pagination")


                           mViewModel.connectAppData(handlerListItem, defaultQpObject(), paginationArray,payLoadData)
                        }
                        else if (layoutType.equals("create",true)){
                            ProgressDialogUtil.hideProgressDialog()
                            val jsonObject = JSONObject()
                            jsonObject.put("key", handlerListItem)
                            jsonObject.put("value", qtcLayoutRes)
                            workFlowLayoutResMap.put(jsonObject)

                            AppPreferences.saveValue(Constants.PrefCode.QTC_Tabs_MAP, workFlowTabs.toString())
                            AppPreferences.saveValue(Constants.PrefCode.QTC_RES_MAP, workFlowResMap.toString())
                            AppPreferences.saveValue(Constants.PrefCode.QTC_LAY_MAP, workFlowLayoutResMap.toString())


                            if(findNavController(R.id.nav_host_fragment).currentDestination?.id == R.id.qtcFormFrag) {
/*                                val navHostFragment: NavHostFragment? = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                                val qtcFrag = navHostFragment!!.childFragmentManager.fragments[0] as QtcFormFrag
                                qtcFrag.actionSelf()*/
                                AppPreferences.saveValue(Constants.PrefCode.QTC_SELF, "Y")

                                findNavController(R.id.nav_host_fragment).navigate(R.id.qtcFormFrag)
                            }else {
                                AppPreferences.saveValue(Constants.PrefCode.QTC_SELF, "N")
                                findNavController(R.id.nav_host_fragment).navigate(R.id.qtcFormFrag)
                            }


                          //  findNavController(R.id.nav_host_fragment).navigate(R.id.qtcFormFrag)
                        }

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
                                val paginationArray = JsonArray()
                                paginationArray.add("pagination")

                                dataReqName = workFlowArrayList[0]
                                mViewModel.connectAppData(dataReqName,defaultQpObject(),paginationArray,payLoadData)
                            } else {
                                dataReqName = workFlowArrayList[workFlowLayoutResMap.length()-1]
                                mViewModel.connectAppLayout(dataReqName)
                            }

                        }

                    } else {
                         if (workFlowLayoutResMap.length() == workFlowArrayList.size-1){
                            val jsonObject = JSONObject()
                            jsonObject.put("key", dataReqName)
                            jsonObject.put("value", qtcLayoutRes)
                            workFlowLayoutResMap.put(jsonObject)

                            val paginationArray = JsonArray()
                            paginationArray.add("pagination")


                            dataReqName = workFlowArrayList[0]

                            mViewModel.connectAppData(dataReqName, defaultQpObject(), paginationArray,payLoadData)

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

        notificationListResObserver()

        userProfileObserver()


        getCurrentCorporateObserver()

        getListOfCorporatesObserver()

        switchCorporateObserver()



    }

    private fun switchCorporateObserver() {
        mViewModel.switchCorporateResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                this.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()


                    try {
                        val res = it.value.string()
                        val jsonRes = JSONObject(res)
                        if(jsonRes.optString("status").equals("Success",true)){
                            switchCorporate.onSuccess(it.value.string())

                            DialogUtil.infoPopUp(this,"Warning","The corporate has been switched to "+
                                    jsonRes.optString("corporateName"))
                        }else{
                            switchCorporate.onFailure()
                        }

                    }catch (e : java.lang.Exception){

                    }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()
                    switchCorporate.onFailure()
                }
            }
        })

    }

    private fun getListOfCorporatesObserver() {
        mViewModel.listOfCorporateResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                this.let { it1 ->
                }
            }
            when (it) {
                is Resource.Success -> {
                    ProgressDialogUtil.hideProgressDialog()

                    try {
                        val jsonObject = JSONObject(it.value.string())
                        if(jsonObject.optJSONArray("data").length()>0){
                            switchCorporate = SwitchCorporate(this,mViewModel,currentCorporateDtl,
                                    jsonObject.toString())
                            switchCorporate.show()
                        }else {
                            Toast.makeText(this,"Data not available",Toast.LENGTH_SHORT).show()
                        }

                    }catch (e : java.lang.Exception){

                    }


                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }
        })

    }

    private fun getCurrentCorporateObserver() {
        mViewModel.currentCorporateResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                this.let { it1 ->
                    ProgressDialogUtil.showProgressDialog(it1)
                }
            }
            when (it) {
                is Resource.Success -> {

                    try {
                        currentCorporateDtl = JSONObject(it.value.string())
                        mViewModel.getListOfCorporate()
                    }catch (e : java.lang.Exception){
                        ProgressDialogUtil.hideProgressDialog()
                    }

                }
                is Resource.Failure -> {
                    ProgressDialogUtil.hideProgressDialog()

                }
            }
        })

    }

    private fun checkForProfileOption() {
        if(AppPreferences.getKeyValue(Constants.PrefCode.USER_TYPE,"").equals("3",true)){

            mBinding.navHeader.findViewById<TextView>(R.id.first_letter_tv).setOnClickListener {
                mViewModel.getUserProfile()
            }

            mBinding.navHeader.findViewById<TextView>(R.id.client_name_tv).paintFlags =
                    mBinding.navHeader.findViewById<TextView>(R.id.client_name_tv).getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG

            mBinding.navHeader.findViewById<TextView>(R.id.client_name_tv).setOnClickListener {
                mViewModel.getUserProfile()
            }
        }
    }

    private fun notificationListResObserver(){
        mViewModel.notificationListResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {

            }
            if(this.lifecycle.currentState== Lifecycle.State.RESUMED){
                when (it) {
                    is Resource.Success -> {
                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        val unseenCount = jsonObj.optString("unseenCount")
                        val icon: LayerDrawable = notifMenuItem.icon as LayerDrawable
                        setBadgeCount(this, icon, unseenCount.toString())
                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        handleApiError(it){null}
                    }
                }
            }
        })


    }

    private fun userProfileObserver(){
        mViewModel.userProfileResponse.observe(this, androidx.lifecycle.Observer { it ->

            if (it is Resource.Loading) {
                ProgressDialogUtil.showProgressDialog(this)
            }
            if(this.lifecycle.currentState== Lifecycle.State.RESUMED){
                when (it) {
                    is Resource.Success -> {

                        ProgressDialogUtil.hideProgressDialog()
                        val result = it.value.string()
                        val jsonObj = JSONObject(result)
                        val userProfile = UserProfileScreen(this,
                                android.R.style.Theme_Light_NoTitleBar_Fullscreen,jsonObj)
                        userProfile.show()

                    }
                    is Resource.Failure -> {
                        ProgressDialogUtil.hideProgressDialog()
                        handleApiError(it){null}
                    }
                }
            }
        })


    }

    private fun setFooterAndVersion() {
      val footerTxt=  mBinding.navFooter.findViewById<TextView>(R.id.footer_tv)
        footerTxt.text = AppUtil.getAppFooterTxt()
      val versionTv=  mBinding.navFooter.findViewById<TextView>(R.id.version_name_tv)
        versionTv.text = AppUtil.getVersionNameTxt()
    }

    private fun defaultQpObject () : JsonObject{
        val qpObj = JsonObject()
        qpObj.addProperty("size", PAGINATION_DEFAULT_SIZE)
        qpObj.addProperty("from", 0)
        return qpObj
    }

    private fun updateDrawerMenuTitle() {
        try {

            val clientName = AppPreferences.getKeyValue(Constants.PrefCode.FIRST_NAME, "")

            mBinding.navView.findViewById<TextView>(R.id.client_name_tv).text = clientName
            mBinding.navView.findViewById<TextView>(R.id.first_letter_tv).text = clientName?.substring(0, 1)


        } catch (e: Exception) {

        }
    }

    /**
     * function to check
     * if client image exists, will display that or default image
     * */
    private fun setClientBannerImage() {

        try {

            val bannerImageValue = AppPreferences.getKeyValue(Constants.PrefCode.BANNER_LOGO, "")

            if (bannerImageValue.equals("")) {
                mBinding.navView.findViewById<ImageView>(R.id.headerImageView).setImageResource(R.drawable.ic_eka_logo)

            } else {
                mBinding.navView.findViewById<ImageView>(R.id.headerImageView).setImageBitmap(AppUtil.stringToBitmap(bannerImageValue))
            }
        } catch (e: Exception) {

        }
    }


    /**
     * for setting action bar title
     * */
    fun setTitle(title: String) {
        mToolbar.title = title
    }


    fun changeStartDestination(destination: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val graphInflater = navHostFragment.navController.navInflater
        val navGraph = graphInflater.inflate(R.navigation.dash_nav_graph)
        val navController = navHostFragment.navController

        val destination = destination
        navGraph.startDestination = destination
        navController.graph = navGraph
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.dash_action_menu, menu)
        notifMenuItem = menu.findItem(R.id.menu_item_noti)

        return true
    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {

            if(AppPreferences.getKeyValue(Constants.PrefCode.SELECTED_APP_ID,"").toString().equals("Error")){
                AppPreferences.saveValue(Constants.PrefCode.SELECTED_APP_ID,"")
                findNavController(R.id.nav_host_fragment).popBackStack()
                findNavController(R.id.nav_host_fragment).navigate(R.id.dashboardFrag)
            }else{
                super.onBackPressed()
                this!!.supportActionBar!!.show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {


        when (item.itemId) {
            R.id.menu_item_noti ->{
                AppUtil.sendGoogleEvent(this,"Home","Notifications","Notifications")
                findNavController(R.id.nav_host_fragment).navigate(R.id.notificationListScreenFrag)
                return true
            }
            R.id.menu_item_search ->{
                findNavController(R.id.nav_host_fragment).navigate(R.id.searchFragment)
                return true
            }
            R.id.menu_item_chat ->{
                Intercom.client().displayMessenger()
                return true
            }
            // R.id. -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                Toast.makeText(this, "Hello Home", Toast.LENGTH_SHORT).show()

            }
            else -> {
                //Todo
            }
        }


        mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
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


    fun addItemsToConnectList(handlerList: MutableList<String>, arrayList: MutableList<String>, headerText: String) {
        mBinding.navContent.findViewById<View>(R.id.list_view_footer).visibility = View.VISIBLE
        mListView.visibility = View.VISIBLE
        mNavItemArrayList.addAll(arrayList)
        val mNavMenuItemAdapter = NavMenuItemAdapter(this, mNavItemArrayList)
        header.findViewById<TextView>(R.id.hdr_tv).text = headerText
        mListView.addHeaderView(header, null, false)
        mListView.adapter = mNavMenuItemAdapter

        var layWeight = 1.0f
        val params: LinearLayout.LayoutParams = mListView.getLayoutParams() as LinearLayout.LayoutParams
        if(mNavItemArrayList.size <= 5){
            layWeight = (mNavItemArrayList.size+1).toFloat()
            params.height = 100 * (mNavItemArrayList.size+1)
        }else {
           layWeight = 6.0f
            params.height = 600
        }

        params.weight = layWeight
        mListView.setLayoutParams(params)

        mListView.setOnItemClickListener { parent, view, position, id ->
            clearPrevData()
            defaultTraderKey = ""
            handlerListItem = handlerList[position - 1]

            AppPreferences.saveValue(Constants.PrefCode.QTC_HANDLER, handlerListItem)
            lastFormTag = ""
            mViewModel.connectAppLayout(handlerListItem)

        }


    }

    fun listClickHandler(workFlowName: String){
        clearPrevData()
        handlerListItem = workFlowName
        AppPreferences.saveValue(Constants.PrefCode.QTC_HANDLER, workFlowName)
        mViewModel.connectAppLayout(workFlowName)
    }
    fun setDefaultTraderName(traderKey : String){
        defaultTraderKey = traderKey
    }
    fun getDefaultTraderKey():String{
        return defaultTraderKey
    }

    fun setLastFromTag(traderKey : String){
        lastFormTag = traderKey
    }
    fun getLastFormTag():String{
        return lastFormTag
    }

    fun clearListView() {
        mListView.visibility = View.GONE
        mBinding.navContent.findViewById<View>(R.id.list_view_footer).visibility = View.GONE
        mNavItemArrayList.clear()
        mListView.removeHeaderView(header)
        val mNavMenuItemAdapter = NavMenuItemAdapter(this, mNavItemArrayList)
        mNavMenuItemAdapter.notifyDataSetChanged()
        mListView.adapter = mNavMenuItemAdapter
    }


    fun makeAppTabSelected() {
        makeNavTextViewSelected(appsTv)
        makeNavTextViewUnSelected(favoriteTv)
    }

    private fun makeNavTextViewSelected(tv: TextView) {
        tv.setTextColor(ContextCompat.getColor(this, R.color.default_nav_bg))
        tv.setBackgroundColor(ContextCompat.getColor(this, R.color.selected_tab_bg))
    }

    private fun makeNavTextViewUnSelected(tv: TextView) {
        tv.setTextColor(ContextCompat.getColor(this, R.color.selected_tab_bg))
        tv.setBackgroundColor(ContextCompat.getColor(this, R.color.default_nav_bg))
    }

    fun makeFavTabSelected() {
        makeNavTextViewSelected(favoriteTv)
        makeNavTextViewUnSelected(appsTv)
    }


    fun clearSelectedViews() {
        makeNavTextViewUnSelected(appsTv)
        makeNavTextViewUnSelected(favoriteTv)
    }

    fun showBackButton() {

        setDrawerEnabled(false)
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        //getSupportActionBar().setTitle(getString(R.string.app_name))
        mToolbar.setNavigationOnClickListener {

            findNavController(R.id.nav_host_fragment).navigate(R.id.favoriteCatFrag)
            showHamburgerMenu()

        }
    }

    fun handleBackButtonAction() {

        setDrawerEnabled(false)
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        //getSupportActionBar().setTitle(getString(R.string.app_name))
        mToolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }


    fun setDrawerEnabled(enabled: Boolean) {
        val lockMode = if (enabled) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        mBinding.drawerLayout.setDrawerLockMode(lockMode)
        toggle.isDrawerIndicatorEnabled = enabled
    }

    fun showHamburgerMenu() {
        toggle = ActionBarDrawerToggle(
                this, mBinding.drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        mBinding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        setDrawerEnabled(true)
    }

    fun addValueToBackStack(item : QtcMapData){
        qtckBackStack.push(item)
    }

    fun getValueToBackStack() : QtcMapData?{
        if(qtckBackStack.isEmpty()){
            return null
        }else{
           return qtckBackStack.pop()
        }

    }

    fun setBadgeCount(context: Context?, icon: LayerDrawable, count: String?) {
        val badge: BadgeDrawable

        // Reuse drawable if possible
        val reuse: Drawable? = icon.findDrawableByLayerId(R.id.ic_badge)
        badge = if (reuse != null && reuse is BadgeDrawable) {
            reuse
        } else {
            BadgeDrawable(context)
        }
        badge.setCount(count)
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_badge, badge)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


    fun getPixelsEquvToDp(inputNum : Int ) : Int {
        val scale: Float = this.getResources().getDisplayMetrics().density
        val pixels = (inputNum * scale + 0.5f).toInt()
        return pixels
    }

}