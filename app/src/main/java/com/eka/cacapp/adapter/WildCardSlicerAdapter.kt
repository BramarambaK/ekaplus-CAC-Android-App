package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.eka.cacapp.R
import com.eka.cacapp.data.insight.WildCardSlicerData
import com.google.android.material.bottomsheet.BottomSheetDialog

class WildCardSlicerAdapter(private val context: Context, private val itemList: ArrayList<String>,
                            bottomSheetDialog: BottomSheetDialog, selectedDataList : java.util.ArrayList<String>) : BaseAdapter() {

    private lateinit var name: CheckBox
    private var selectedArrList : ArrayList<String> = selectedDataList
    var onItemClick: ((WildCardSlicerData) -> Unit)? = null

    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.wildcard_slicer_row, parent, false)

        name = convertView.findViewById(R.id.tv)

        val item = itemList[position]

        name.setOnCheckedChangeListener { buttonView, isChecked ->
            onItemClick?.invoke(WildCardSlicerData(itemList[position],isChecked))
        }

        name.text = item
        if(selectedArrList.contains(item)){
            name.isChecked = true
        }

        return convertView
    }


}
