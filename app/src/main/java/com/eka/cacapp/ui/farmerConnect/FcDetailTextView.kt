package com.eka.cacapp.ui.farmerConnect

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.eka.cacapp.R

class FcDetailTextView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {


    private  var  valueTxtView: TextView
    private  var  labelTxtView: TextView
    private var dtlRow : LinearLayout

    init {
        inflate(context, R.layout.fc_dtl_txt_view, this)

        labelTxtView      = findViewById(R.id.row_label)
        valueTxtView = findViewById(R.id.row_value)
        dtlRow = findViewById(R.id.fc_dtl_rw)


        val attributes = context.obtainStyledAttributes(attrs, R.styleable.FC_DTL_TXT_V)

        labelTxtView.text = attributes.getString(R.styleable.FC_DTL_TXT_V_fc_label)
        valueTxtView.text = attributes.getString(R.styleable.FC_DTL_TXT_V_fc_value)

        if(attributes.getString(R.styleable.FC_DTL_TXT_V_fc_label_bold).toString().equals("Y")){
            labelTxtView.setTypeface(labelTxtView.getTypeface(), Typeface.BOLD)
        }
        if(attributes.getString(R.styleable.FC_DTL_TXT_V_fc_value_bold).toString().equals("Y")){
            valueTxtView.setTypeface(valueTxtView.getTypeface(), Typeface.BOLD)
        }

        attributes.recycle()

    }

    fun setLabelAndValue(labelText : String,valueText:String){
        labelTxtView.text = labelText
        valueTxtView.text = valueText
    }
    fun makeLabelBold(){
        labelTxtView.setTypeface(labelTxtView.getTypeface(), Typeface.BOLD)
    }
    fun makeValueBold(){
        valueTxtView.setTypeface(valueTxtView.getTypeface(), Typeface.BOLD)
    }
    fun hideRow(){
        dtlRow.visibility = View.GONE
    }
    fun onValueClickListener(onValueClicked: () -> Unit){
        valueTxtView.setOnClickListener {
            onValueClicked()
        }
    }
    fun makeValueClickable(){
        valueTxtView.setTypeface(valueTxtView.typeface,Typeface.BOLD)
        valueTxtView.setTextColor(ContextCompat.getColor(context,R.color.rate_now_clr))
    }

    fun changeValueColor(colorCode : Int){
        valueTxtView.setTextColor(ContextCompat.getColor(context,colorCode))
    }
}