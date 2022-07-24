package com.eka.cacapp.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import com.eka.cacapp.BuildConfig
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.JsonArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class AppUtil {

    companion object {
        fun bitmapToString(image: Bitmap): String? {
            val baos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.PNG, 100, baos)
            val b: ByteArray = baos.toByteArray()
            return Base64.encodeToString(b, Base64.DEFAULT)
        }

        fun stringToBitmap(input: String?): Bitmap? {
            val decodedByte = Base64.decode(input, 0)
            return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.size)
        }

        fun printLongLog(tag: String, string: String) {
            val maxLogSize = 4000
            string.chunked(maxLogSize).forEach { Log.i(tag, it) }
        }

         fun convertDateToMonthYear( inputDate: String): String{
            val monthDate = SimpleDateFormat("MMM-yyyy", Locale.ENGLISH)
            val sdf = SimpleDateFormat("yyyy-MM-dd")

            val actualDate = inputDate.substringBefore("T")

            val date = sdf.parse(actualDate)

            return monthDate.format(date)

        }


        // for getting XTenant Id value
        fun getXTenanatId() : String{
            var xTenantId =""
            val baseUrl = AppPreferences.getKeyValue(Constants.PrefCode.BASE_URL,"").toString()
            if(baseUrl.contains(".")){
                val splittedArr = baseUrl.substringAfter("://").split(".").toTypedArray()

                if(splittedArr.isNotEmpty()){
                    xTenantId = splittedArr[0]
                }

            }
            return xTenantId
        }

        //For getting language code
        fun getLangCode() : String{
            return "en-US"
        }

        fun getJsonDataFromAsset(context: Context, fileName: String): String? {
            val jsonString: String
            try {
                jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            } catch (ioException: IOException) {

                return null
            }
            return jsonString
        }


        fun hasValue(arr: JsonArray, targetValue: String): Boolean {
            for (i in 0 until arr.size()) {

                if (arr[i].asString.equals(targetValue,true) ) return true
            }
            return false
        }

        fun removeValue(arr: JsonArray, targetValue: String) {
            for (i in 0 until arr.size()) {

                if (arr[i].asString.equals(targetValue,true)) {
                    arr.remove(i)
                    break
                }

            }
        }



       private fun evaluateExpression(symbol : String, expression : String,dataObj : JSONObject) : Boolean{
            return if(expression.contains(symbol)){
                val split = expression.split(symbol)
                if(split.size>1){
                    val splitOne = split[0].trim()
                    val splitTwo = split[1].trim()
                    val key = dataObj.optString(splitOne.trim()).trim()



                    if(key.equals(splitTwo,true) && symbol.equals("==",true)){
                        true
                    }else !key.equals(splitTwo,true) && symbol.equals("!=",true)


                }else{
                    false
                }
            }else{
                false
            }
        }
        fun evaluateExpressionResult(expression : String,dataObj : JSONObject) : Boolean{
            return when {
                expression.contains("!=") -> {
                    evaluateExpression("!=",expression,dataObj)
                }
                expression.contains("==") -> {
                    evaluateExpression("==",expression,dataObj)
                }
                else -> {
                    false
                }
            }
        }

        fun dpToPx(dp: Int): Int {
            return (dp * Resources.getSystem().getDisplayMetrics().density) as Int
        }

        fun pxToDp(px: Int): Int {
            return (px / Resources.getSystem().getDisplayMetrics().density) as Int
        }

        fun spToPx(sp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, Resources.getSystem().getDisplayMetrics())
        }

        fun getCurrentYear() : Int{
            return Calendar.getInstance().get(Calendar.YEAR)
        }
        fun getCurrentVersionName():String {
            return BuildConfig.VERSION_NAME
        }
        fun getDateForFc( inputDate: String): String{
            return inputDate.substringBefore("T")
        }

        fun getVersionNameTxt() : String{
            return "Version "+ getCurrentVersionName()
        }

        fun saveAppSelectedFavDtl(appId :String?,appType :String?,isFav:String?,entityType :String){
            AppPreferences.saveValue(Constants.PrefCode.FAV_APP_ID,appId?:"")
            AppPreferences.saveValue(Constants.PrefCode.FAV_APP_TYPE,appType?:"")
            AppPreferences.saveValue(Constants.PrefCode.IS_FAV_APP,isFav?:"false")
            AppPreferences.saveValue(Constants.PrefCode.ENTITY_TYPE,entityType)

        }

        fun getAppFooterTxt():String{
           return "\u00a9 2017-"+getCurrentYear().toString()+ " Eka Software Solutions Pvt. Ltd.All rights reserved"
        }

        fun sendGoogleEvent(context: Context,category : String,action :String,label :String){
            val bundle = Bundle()
            bundle.putString("category", category)
            bundle.putString("action", action)
            bundle.putString("label", label)
            FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        }

        fun compareDates(dateOneStr: String,dateTwoStr:String,operator:String) : Boolean{
            try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateOne = dateFormat.parse(dateOneStr)
            val dateTwo = dateFormat.parse(dateTwoStr)
            var result = false
            if(operator.equals("=>")){
                if (dateOne.compareTo(dateTwo) > 0
                        ||dateOne.compareTo(dateTwo) == 0)
                {
                    return true
                }
            }
            else  if(operator.equals("<=")){
                if (dateOne.compareTo(dateTwo) < 0
                        ||dateOne.compareTo(dateTwo) == 0)
                {
                    return true
                }
            }else if (operator.equals(">")){
                if (dateOne.compareTo(dateTwo) > 0)
                {
                    return true
                }
            }
            else if (operator.equals("<")){
                if (dateOne.compareTo(dateTwo) < 0)
                {
                    return true
                }
            }
            else if (operator.equals("==")){
                if (dateOne.compareTo(dateTwo) == 0)
                {
                    return true
                }
            }
            return false
            }catch (e :Exception){

                return false
            }
        }
    }
}