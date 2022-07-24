package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.eka.cacapp.R
import com.eka.cacapp.data.insight.BasicFltrData


class InsightBasicFltrAdapter(private val context: Context,
                              private val arrayList: ArrayList<BasicFltrData>) : BaseAdapter() {

    private lateinit var row_title: TextView
    private lateinit var isChecked: ImageView
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
        convertView = LayoutInflater.from(context).inflate(R.layout.insight_fltr_lst_row, parent, false)
        row_title = convertView.findViewById(R.id.row_title)
        isChecked = convertView.findViewById(R.id.is_checked)

        row_title.text = arrayList[position].itemName
        if(arrayList[position].isChecked){
            isChecked.visibility = View.VISIBLE
        }else{
            isChecked.visibility = View.GONE
        }

        return convertView
    }
}