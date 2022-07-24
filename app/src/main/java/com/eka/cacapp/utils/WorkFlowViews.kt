package com.eka.cacapp.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.eka.cacapp.R
import com.eka.cacapp.data.qtcLayout.WorkFlowMargin


object WorkFlowViews {

    const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT
    const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT

    fun createView(context: Context, width : Int, height : Int, margins : WorkFlowMargin,
                   colorCode : String): View {
        val v = View(context)
        val params = LinearLayout.LayoutParams(width, height)
        params.setMargins(margins.left, margins.top, margins.right, margins.bottom)
        v.layoutParams = params
        v.setBackgroundColor(Color.parseColor(colorCode))
        return v
    }


    fun createLinearLay(context: Context, orientation: Int,width: Int,height: Int, weight: Float
                        ,margins : WorkFlowMargin,gravity: Int=0): LinearLayout {
        val parent = LinearLayout(context)
        val params = LinearLayout.LayoutParams(width,height, weight)
        params.setMargins(margins.left, margins.top, margins.right, margins.bottom)
        parent.layoutParams = params
        parent.orientation = orientation
        parent.gravity = gravity
        return parent
    }

    fun createTextView(context: Context, text: String, width: Int, height: Int, weight: Float
                       , margins : WorkFlowMargin,
                       gravity : Int= Gravity.CENTER, textColor : Int = R.color.black,
                       textSize : Int = R.dimen.dtl_view_text_sz): TextView? {
        val tv = TextView(context)
        tv.text = showEmptyIfNull(text)
        tv.gravity = gravity
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.resources.getDimension(textSize))
        val params = TableLayout.LayoutParams(width,height, weight)
        params.setMargins(margins.left, margins.top, margins.right, margins.bottom)
        tv.layoutParams = params
        tv.setTextColor(ContextCompat.getColor(context, textColor))

        return tv
    }

    fun showEmptyIfNull(txt : String) : String{
        if(txt == null ){
            return ""
        }else if (txt.trim().equals("null",true)){
            return ""
        }
        else{
            return txt
        }
    }

    fun createEditText(context: Context, hint: String, width: Int, height: Int, weight: Float
                       , margins : WorkFlowMargin,
                       gravity : Int= Gravity.CENTER, textColor : Int = R.color.black,
                       textSize : Int = R.dimen.dtl_view_text_sz): EditText {
        val tv = EditText(context)
        tv.hint = hint
        tv.gravity = gravity
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(textSize))
        val params = TableLayout.LayoutParams(width,height, weight)
        params.setMargins(margins.left, margins.top, margins.right, margins.bottom)
        tv.layoutParams = params
        tv.setTextColor(ContextCompat.getColor(context, textColor))

        return tv
    }


}