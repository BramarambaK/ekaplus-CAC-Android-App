package com.eka.cacapp.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.widget.ProgressBar
import java.lang.Exception

object ProgressDialogUtil {

    private lateinit var dialogBuilder : AlertDialog.Builder
    private lateinit var alertDialog: AlertDialog
    private lateinit var progressDialog : ProgressBar

    fun showProgressDialog(ctx : Context){
        try {

               if(::alertDialog.isInitialized){
                   if(alertDialog.isShowing){
                       alertDialog.dismiss()
                   }

              }

                dialogBuilder = AlertDialog.Builder(ctx)
                progressDialog = ProgressBar(ctx)

                dialogBuilder.setCancelable(false)
                dialogBuilder.setView(progressDialog)
                alertDialog = dialogBuilder.create()

                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#00141414")))
                alertDialog.show()

           // }

        }catch (e :Exception){

        }

    }

    fun hideProgressDialog(){
        try {
            if(alertDialog.isShowing ){
                alertDialog.dismiss()
            }
        }catch (e : Exception){

        }


    }

    fun isShowing(): Boolean {
        try {
            return alertDialog.isShowing
        }catch (e :Exception){
            return false
        }
    }
}