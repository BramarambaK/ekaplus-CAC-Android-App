package com.eka.cacapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.appMainList.CategoryInfoModelItem
import com.eka.cacapp.databinding.DashCatItemBinding

/**
 * Dashboard Category Adapter for showing list of categories
 * */

class DashCategoryAdapter(listItems: List<CategoryInfoModelItem>) : RecyclerView.Adapter<DashCategoryAdapter.ViewHolder>() {
    private val categoryList: List<CategoryInfoModelItem> = listItems
    var onItemClick: ((CategoryInfoModelItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: DashCatItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.dash_cat_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel: CategoryInfoModelItem = categoryList[position]
        holder.dashCatItemBinding.categoryTitleTv.text = dataModel.name
        holder.dashCatItemBinding.appCountTv.text = dataModel.appsCount.toString()

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(var dashCatItemBinding: DashCatItemBinding) : RecyclerView.ViewHolder(dashCatItemBinding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }
        }
    }


}