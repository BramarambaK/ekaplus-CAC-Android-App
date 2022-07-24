package com.eka.cacapp.ui.onBoarding

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.eka.cacapp.R
import com.eka.cacapp.adapter.OnBoardingAdapter
import com.eka.cacapp.databinding.ActivityOnboardingBinding
import com.eka.cacapp.network.RestClient
import com.eka.cacapp.ui.DashboardActivity
import com.eka.cacapp.ui.MainActivity
import com.eka.cacapp.utils.AppPreferences
import com.eka.cacapp.utils.Constants
import com.eka.cacapp.utils.ProgressDialogUtil
import com.eka.cacapp.utils.rootedCheck.CheckEmulator
import com.eka.cacapp.utils.rootedCheck.RootCheck
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import org.jsoup.Jsoup


/**
 * On boarding activity for displaying on boarding screens
 * */
class OnBoardingActivity : AppCompatActivity() , AppUpdateResponse{

    private lateinit var mBinding: ActivityOnboardingBinding
    private val IMMEDIATE_APP_UPDATE_REQ_CODE = 154

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding)


        val rootCheck = RootCheck(this);
        val isEmu = CheckEmulator.isEmulator(this);
        val isRooted = rootCheck.isRooted;

        if(isRooted || isEmu){
            Toast.makeText(this,"Security Error (000). Contact System Administrator",
            Toast.LENGTH_LONG).show()
            finish()
            return
        }

        checkUpdate()

     //  checkPlayStoreVerions()
       // navToNextScreen()


    }

    /**
     * function for view pager adapter setup
     * */

    private fun setupAdapter() {

        try {

            mBinding.skipBtn.visibility = View.VISIBLE

            val adapter = OnBoardingAdapter(supportFragmentManager)
            adapter.addFragment(OnBoardingFirstSlideFrag())
            adapter.addFragment(OnBoardingSecondFrag())
            adapter.addFragment(OnBoardingThirdFrag())
            adapter.addFragment(OnBoardingFourFrag())
            mBinding.onBoardingViewPager.adapter = adapter
            mBinding.dots.attachViewPager(mBinding.onBoardingViewPager)
            mBinding.dots.setDotDrawable(R.drawable.onboarding_selected_circle, R.drawable.onboarding_circle)

        } catch (e: Exception) {

        }

    }

    private fun initListener() {
        mBinding.skipBtn.setOnClickListener {
            mBinding.onBoardingViewPager.setCurrentItem(3,false)
        }


        // view pager on page change listener
        mBinding.onBoardingViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (position == 3) {
                    mBinding.skipBtn.visibility = View.GONE
                } else {
                    mBinding.skipBtn.visibility = View.VISIBLE
                }

            }

            override fun onPageScrollStateChanged(state: Int) {}
        })



    }

    private fun checkPlayStoreVerions(){
        GetVersionCode(this,this).execute()
    }

    private fun checkUpdate() {
        ProgressDialogUtil.showProgressDialog(this)
        val appUpdateManager = AppUpdateManagerFactory.create(this)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        IMMEDIATE_APP_UPDATE_REQ_CODE)
                }catch (e : SendIntentException){
                    navToNextScreen()
                }

            }else {
                navToNextScreen()
            }
        }.addOnCompleteListener {
            ProgressDialogUtil.hideProgressDialog()
        }.addOnFailureListener {
            ProgressDialogUtil.hideProgressDialog()
            navToNextScreen()
        }



    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                finish()
            } else if (resultCode == RESULT_OK) {
                Toast.makeText(
                    applicationContext,
                    "Update success!! ", Toast.LENGTH_LONG
                ).show()
            } else {
                finish()
                navigateToPlayStore(this)
            }
        }
    }

    override fun onUpdateCheckResponse(onlineVersion: String?) {

        try {
            var currentVersion= getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            var onlnV = onlineVersion!!

            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                currentVersion = currentVersion.replace(".","")
                onlnV = onlnV!!.replace(".","")
                var currentVInt = currentVersion.toInt()
                var onlineVInt = onlnV.toInt()
                if (onlineVInt>currentVInt) {
                    showUpdatePopup(this,"New Version Available",
                            "There is a newer version available for download! Please update the app by visiting the Play Store.")
                }else{
                    navToNextScreen()
                }
            }else{
                navToNextScreen()
            }
        }catch (e : java.lang.Exception){

            navToNextScreen()
        }

    }

    private fun navToNextScreen(){

        AppPreferences.saveValue(Constants.PrefCode.INSIGHT_LISTING_CACHE_DATA,
                "")
        //check for showing on boarding screens
        if(AppPreferences.getKeyValue(Constants.PrefCode.SHOW_ON_BOARDING,"Y") == "N"){

            if(AppPreferences.getKeyValue(Constants.PrefCode.IS_LOGGED_IN,"N") == "Y"){
                val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString();
                RestClient.getInstance(this,baseUrl)
                RestClient.addRefreshToken(baseUrl)
                val intent = Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }


        }else{
            setupAdapter()
            initListener()
        }

    }


    class GetVersionCode(context: Context,appUpdateResponse: AppUpdateResponse) : AsyncTask<Void?, String?, String?>() {

        var delegate = appUpdateResponse
        var applicContext = context
        override fun onPreExecute() {
            super.onPreExecute()
            ProgressDialogUtil.showProgressDialog(applicContext)
        }

        override fun doInBackground(vararg params: Void?): String? {
            var newVersion: String? = null
            return try {
                newVersion =
                    Jsoup.connect("https://play.google.com/store/apps/details?id=" +  applicContext.getPackageName() + "&hl=it")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")[7]
                        .ownText()
                newVersion
            } catch (e: Exception) {
                newVersion
            }
        }

        override fun onPostExecute(onlineVersion: String?) {
            super.onPostExecute(onlineVersion)
            ProgressDialogUtil.hideProgressDialog()
            delegate!!.onUpdateCheckResponse(onlineVersion);

        }


    }
    fun navigateToPlayStore(context: Context) {
        val appId: String =  context.getPackageName()
        val rateIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=$appId")
        )
        var marketFound = false

        val otherApps: List<ResolveInfo> = context.getPackageManager()
                .queryIntentActivities(rateIntent, 0)
        for (otherApp in otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    == "com.android.vending"
            ) {
                val otherAppActivity = otherApp.activityInfo
                val componentName = ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                )
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                rateIntent.component = componentName
                context.startActivity(rateIntent)
                marketFound = true
                break
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            val webIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appId")
            )
            context.startActivity(webIntent)
        }
    }


    fun showUpdatePopup(context: Context,title:String,msg: String){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setTitle(title)
        builder1.setMessage(msg)
        builder1.setCancelable(false)
        builder1.setPositiveButton(
                "Ok"
        ) { dialog, _ -> dialog.cancel()
            navigateToPlayStore(context)
        }
        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }


}