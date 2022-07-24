package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.eka.cacapp.R
import com.eka.cacapp.data.qtcLayout.FilterItem
import com.eka.cacapp.data.qtcLayout.SortData
import com.eka.cacapp.utils.Constants.WorkFlow.ASC
import com.eka.cacapp.utils.Constants.WorkFlow.DESC
import com.google.android.material.bottomsheet.BottomSheetDialog

class QtcSortAdapter(private val context: Context, private val sortItem: SortData,
                     bottomSheetDialog: BottomSheetDialog) : BaseAdapter() {
    private lateinit var ascImageView: ImageView
    private lateinit var name: TextView
    private lateinit var dscImageView: ImageView
    private var bottomSheet: BottomSheetDialog = bottomSheetDialog
    var onItemClick: ((FilterItem) -> Unit)? = null

    override fun getCount(): Int {
        return sortItem.sortKeys.size
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.filter_list_row, parent, false)
        ascImageView = convertView.findViewById(R.id.asc_img)
        name = convertView.findViewById(R.id.tv)
        dscImageView = convertView.findViewById(R.id.dsc_img)

        val arrayList = sortItem.sortKeys
        val arrayListName = sortItem.sortDisplayName

        if (sortItem.isSelected) {


            if (sortItem.selItemPos == position) {
                if (sortItem.selItemType.equals(ASC, true)) {
                    ascImageView.setImageResource(R.drawable.ic_sort_selected_ascending)
                } else {
                    dscImageView.setImageResource(R.drawable.ic_sort_selected_descending)
                }
            }


        }


        ascImageView.setOnClickListener {
            bottomSheet.dismiss()
            onItemClick?.invoke(FilterItem(arrayList[position], ASC, position))


        }
        dscImageView.setOnClickListener {
            bottomSheet.dismiss()
            onItemClick?.invoke(FilterItem(arrayList[position], DESC, position))
        }

        name.text = arrayListName[position]

        return convertView
    }


}
