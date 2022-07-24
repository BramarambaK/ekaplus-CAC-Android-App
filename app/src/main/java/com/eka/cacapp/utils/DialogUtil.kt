package com.eka.cacapp.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.eka.cacapp.R

object  DialogUtil {
    fun showErrorDialog( context : Context,msg: String?) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.error_dialog)
        val text = dialog.findViewById(R.id.text_dialog) as TextView
        text.text = msg
        val dialogButton: Button = dialog.findViewById(R.id.btn_dialog) as Button
        dialogButton.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

     fun infoPopUp(context: Context,title:String,msg: String,action: (() -> Unit)? = null){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setTitle(title)
        builder1.setMessage(msg)
        builder1.setCancelable(true)
        builder1.setPositiveButton(
                "Ok"
        ) { dialog, _ -> dialog.cancel()
            action?.invoke()

        }
         val alert11: AlertDialog = builder1.create()
        alert11.show()
    }

    fun confirmationPopUp(context: Context,
                          title:String,msg: String,
                          positiveBtnText : String,
                          negBtnText :String,
                          action: (() -> Unit)? = null){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setTitle(title)
        builder1.setMessage(msg)
        builder1.setCancelable(true)
        builder1.setNegativeButton(
                negBtnText
        ) { dialog, _ -> dialog.cancel()
        }
        builder1.setPositiveButton(
                positiveBtnText
        ) { dialog, _ -> dialog.cancel()
            action?.invoke()

        }
        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }


    fun showFcValidationError(context: Context,title:String,msg: String){
        val builder1: AlertDialog.Builder = AlertDialog.Builder(context)
        builder1.setTitle(title)
        builder1.setMessage(msg)
        builder1.setCancelable(true)
        builder1.setPositiveButton(
                "Ok"
        ) { dialog, _ -> dialog.cancel()
        }
        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }
}