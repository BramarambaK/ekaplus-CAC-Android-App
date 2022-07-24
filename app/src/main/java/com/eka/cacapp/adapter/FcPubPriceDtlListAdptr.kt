package com.eka.cacapp.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

import android.widget.TextView
import com.eka.cacapp.R
import com.eka.cacapp.data.farmconnect.PubPriceDtlData


class FcPubPriceDtlListAdptr (private val context: Context,
                              private val arrayList: ArrayList<PubPriceDtlData>) : BaseAdapter() {

    private lateinit var row_label: TextView
    private lateinit var row_Value: TextView

    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.pub_price_lst_row, parent, false)
        row_label = convertView.findViewById(R.id.row_label)
        row_Value = convertView.findViewById(R.id.row_value)

        row_label.text = arrayList[position].label ?: ""
        row_Value.text = arrayList[position].value ?: ""

        if(arrayList[position].isLabelBold){

            row_label.setTypeface(row_label.getTypeface(), Typeface.BOLD)
        }
        if(arrayList[position].isValueBold){
            row_Value.setTypeface(row_Value.getTypeface(), Typeface.BOLD)
        }

        return convertView
    }
}