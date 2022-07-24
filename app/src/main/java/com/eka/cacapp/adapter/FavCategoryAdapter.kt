package com.eka.cacapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.appSubList.AppSubListItem
import com.eka.cacapp.databinding.FavCatItemBinding

class FavCategoryAdapter(listItems: List<AppSubListItem>) : RecyclerView.Adapter<FavCategoryAdapter.ViewHolder>() {
    private val categoryList: List<AppSubListItem> = listItems

    var onItemClick: ((AppSubListItem) -> Unit)? = null


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: FavCatItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fav_cat_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel: AppSubListItem = categoryList[position]
        holder.favCatItemBinding.categoryTitleTv.text = dataModel.name

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(var favCatItemBinding: FavCatItemBinding) : RecyclerView.ViewHolder(favCatItemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }
        }
    }


}