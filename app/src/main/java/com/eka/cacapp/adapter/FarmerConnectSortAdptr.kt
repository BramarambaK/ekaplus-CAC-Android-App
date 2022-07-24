package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.eka.cacapp.R
import com.eka.cacapp.data.farmconnect.FCSortData
import com.eka.cacapp.data.qtcLayout.FilterItem
import com.eka.cacapp.utils.Constants
import com.google.android.material.bottomsheet.BottomSheetDialog

class FarmerConnectSortAdptr (private val context: Context, private val itemList: ArrayList<FCSortData>,
                              bottomSheetDialog: BottomSheetDialog, selectedPos : Int, selectedType :String) : BaseAdapter() {
    private lateinit var ascImageView: ImageView
    private lateinit var name: TextView
    private lateinit var dscImageView: ImageView
    private var selPos =selectedPos
    private var selType =selectedType
    private var bottomSheet: BottomSheetDialog = bottomSheetDialog
    var onItemClick: ((FilterItem) -> Unit)? = null

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
        convertView = LayoutInflater.from(context).inflate(R.layout.farm_conn_sort_row, parent, false)
        ascImageView = convertView.findViewById(R.id.asc_img)
        name = convertView.findViewById(R.id.tv)
        dscImageView = convertView.findViewById(R.id.dsc_img)

        val item = itemList[position]

        if(selPos != -1){

            if (selPos == position) {
                if (selType.equals(Constants.WorkFlow.ASC, true)) {
                    ascImageView.setImageResource(R.drawable.ic_sort_selected_ascending)
                } else {
                    dscImageView.setImageResource(R.drawable.ic_sort_selected_descending)
                }
            }
        }
        ascImageView.setOnClickListener {
            bottomSheet.dismiss()
            onItemClick?.invoke(FilterItem(item.columnId, Constants.WorkFlow.ASC, position))


        }
        dscImageView.setOnClickListener {
            bottomSheet.dismiss()
            onItemClick?.invoke(FilterItem(item.columnId, Constants.WorkFlow.DESC, position))
        }

        name.text = item.columnName

        return convertView
    }


}
