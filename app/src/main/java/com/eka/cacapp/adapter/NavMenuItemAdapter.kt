package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.eka.cacapp.R

/**
 * adapter for connect menu list in navigation drawer
 * */
class NavMenuItemAdapter(private val context: Context,private  val items: ArrayList<String>) : BaseAdapter() {
    override fun getCount(): Int {
        return items.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        var convertView = convertView
        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                .inflate(R.layout.nav_menu_row, parent, false)
        }



        val navItemTv =
            convertView!!.findViewById<View>(R.id.nav_item_tv) as TextView
        navItemTv.setText(items[position])
        return convertView
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


}

