package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.appSubList.AppSubListItem
import com.eka.cacapp.databinding.AppSubListItemBinding

class AppSubListAdapter (cntx : Context,listItems: List<AppSubListItem>) : RecyclerView.Adapter<AppSubListAdapter.ViewHolder>() {
    private val categoryList: List<AppSubListItem> = listItems
    private val context = cntx
    var onItemClick: ((AppSubListItem) -> Unit)? = null
    var onFavImgClick: ((AppSubListItem,Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: AppSubListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.app_sub_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel: AppSubListItem = categoryList[position]
        holder.dashCatItemBinding.categoryTitleTv.text = dataModel.name

        if(dataModel.isFavourite.equals("true",true)){
            holder.dashCatItemBinding.favImg.setImageDrawable(ContextCompat.getDrawable(context,
                R.drawable.ic_star_filled))
        }else{
            holder.dashCatItemBinding.favImg.setImageDrawable(ContextCompat.getDrawable(context,
                R.drawable.ic_star_border))
        }


        holder.dashCatItemBinding.favImg.setOnClickListener {
            onFavImgClick?.invoke(categoryList[position],position)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(var dashCatItemBinding: AppSubListItemBinding) : RecyclerView.ViewHolder(dashCatItemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }
        }
    }




}