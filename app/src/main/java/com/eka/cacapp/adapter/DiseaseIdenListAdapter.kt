package com.eka.cacapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eka.cacapp.R
import com.eka.cacapp.data.diseaseIden.DisIdenListModel
import com.eka.cacapp.data.diseaseIden.Response
import com.eka.cacapp.databinding.DisIdenListItemBinding
import com.squareup.picasso.Picasso

import java.text.SimpleDateFormat
import java.util.*


class DiseaseIdenListAdapter (listItems: DisIdenListModel,context : Context) : RecyclerView.Adapter<DiseaseIdenListAdapter.ViewHolder>() {
    private val diseaseItemList: List<Response> = listItems.response

    var onItemClick: ((Response) -> Unit)? = null

    var onDeleteClick: ((Response) -> Unit)? = null

    var ctx : Context = context

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: DisIdenListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.dis_iden_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel: Response = diseaseItemList[position]

        var title = ""
        var disDtls = ""
        var ttlColor = "#999999"


        if(dataModel.responseInfo.isNotEmpty()){
            if(dataModel.responseInfo[0].status.equals("Failed")){
                title = "INVALID IMAGE"
                disDtls = "Invalid image"
                ttlColor = "#999999"
                holder.diseaseIdenItemBinding.delItem.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.inProgressImgView.visibility = View.GONE
                holder.diseaseIdenItemBinding.title.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.subTitle.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.dateTimeTv.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.imgName.visibility = View.VISIBLE
            }else if (dataModel.responseInfo[0].status.equals("In Progress")){
                holder.diseaseIdenItemBinding.delItem.visibility = View.GONE
                holder.diseaseIdenItemBinding.inProgressImgView.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.title.visibility = View.GONE
                holder.diseaseIdenItemBinding.subTitle.visibility = View.GONE
                holder.diseaseIdenItemBinding.dateTimeTv.visibility = View.GONE
                holder.diseaseIdenItemBinding.imgName.visibility = View.GONE

                Glide.with(ctx).asGif().load(R.raw.image_progress).into(holder.diseaseIdenItemBinding.inProgressImgView);
            }
            else {
                holder.diseaseIdenItemBinding.delItem.visibility = View.GONE
                holder.diseaseIdenItemBinding.inProgressImgView.visibility = View.GONE
                holder.diseaseIdenItemBinding.title.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.subTitle.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.dateTimeTv.visibility = View.VISIBLE
                holder.diseaseIdenItemBinding.imgName.visibility = View.VISIBLE

                if (dataModel.responseInfo[0].category.equals("Infected")){
                    title = "THREAT FOUND"
                    disDtls = "The plant is infected with "+dataModel.responseInfo[0].processName
                    ttlColor = "#D0021B"
                }else {
                    title = "NO THREAT FOUND"
                    disDtls = "The plant is healthy"
                    ttlColor = "#009688"
                }

            }
        }

        holder.diseaseIdenItemBinding.title.setTextColor(Color.parseColor(ttlColor));
        holder.diseaseIdenItemBinding.title.text = title
        holder.diseaseIdenItemBinding.subTitle.text = disDtls


        holder.diseaseIdenItemBinding.imgName.text = dataModel.imageName

        holder.diseaseIdenItemBinding.dateTimeTv.text = disIdenDateFormat(dataModel.createdDate)

        Picasso.get().load(dataModel.tn_image_url).into(holder.diseaseIdenItemBinding.imageView)

        holder.diseaseIdenItemBinding.delItem.setOnClickListener {

            onDeleteClick?.invoke(diseaseItemList[position])
        }


    }

    override fun getItemCount(): Int {
        return diseaseItemList.size
    }

    inner class ViewHolder(var diseaseIdenItemBinding: DisIdenListItemBinding) : RecyclerView.ViewHolder(diseaseIdenItemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(diseaseItemList[adapterPosition])
            }
        }
    }

    fun disIdenDateFormat(inputMilliSec: Long): String? {
        try {
            val date = Date(inputMilliSec)
            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH)
            return dateFormat.format(date)
        }catch (e : Exception){
            return ""
        }

    }

}