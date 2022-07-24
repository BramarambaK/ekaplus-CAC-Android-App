package com.eka.cacapp.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.qtcLayout.ListingResponse
import com.eka.cacapp.databinding.QtcListItemBinding

class QtcListLayoutAdapter (listItems: List<ListingResponse>,context: Context) : RecyclerView.Adapter<QtcListLayoutAdapter.ViewHolder>() {
    private val categoryList: List<ListingResponse> = listItems
    var onItemClick: ((ListingResponse) -> Unit)? = null
    private val mContext = context
    private var hasRowOne = false
    private var hasRowTwo = false
    private var hasRowThree = false

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: QtcListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.qtc_list_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)


        val dataModelMain: ListingResponse = categoryList[position]

        for (k in dataModelMain.listRows!!.indices){

            val dataModel = dataModelMain.listRows!![k]



            if (dataModel.name.equals("Row1", true)) {
                if(dataModel.weightSum !=0f){
                    hasRowOne = true
                }
                holder.dashCatItemBinding.row1.weightSum = dataModel.weightSum

                for (i in dataModel.listItems!!.indices) {

                    if (dataModel.listItems!![i].itemPlacement.equals("ROW1IMG1",true)) {
                        holder.dashCatItemBinding.r1c1image.visibility = View.VISIBLE
                        val imageName =  dataModel.listItems!![i].itemValue!!.toLowerCase()
                        val resId: Int = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName())
                        holder.dashCatItemBinding.r1c1image.setImageResource(resId)


                    }

                    else{
                        if (dataModel.listItems!![i].itemPlacement.equals("Row1",true)
                                ||dataModel.listItems!![i].itemPlacement.equals("ROW1COL1",true)) {

                            when {

                                holder.dashCatItemBinding.r1col1.text.toString() ==""
                                        &&dataModel.listItems!![i].itemPlacement.equals("ROW1COL1",true) -> {
                                    holder.dashCatItemBinding.r1col1.visibility = View.VISIBLE
                                    holder.dashCatItemBinding.r1col1.text = dataModel.listItems!![i].itemValue
                                }

                                holder.dashCatItemBinding.r1c1.text.toString() =="" -> {
                                    holder.dashCatItemBinding.r1c1.visibility = View.VISIBLE
                                    holder.dashCatItemBinding.r1c1.text = dataModel.listItems!![i].itemValue

                                }
                                holder.dashCatItemBinding.r1c2.text.toString() =="" -> {
                                    holder.dashCatItemBinding.r1c2.text = dataModel.listItems!![i].itemValue!!.trim()
                                    holder.dashCatItemBinding.r1c2.visibility = View.VISIBLE
                                }
                                holder.dashCatItemBinding.r1c3.text.toString() =="" -> {
                                    holder.dashCatItemBinding.r1c3.text = dataModel.listItems!![i].itemValue
                                    holder.dashCatItemBinding.r1c3.visibility = View.VISIBLE
                                    holder.dashCatItemBinding.r1c2.gravity = Gravity.CENTER
                                }
                                holder.dashCatItemBinding.r1c4.text.toString() =="" -> {
                                    holder.dashCatItemBinding.r1c4.text = dataModel.listItems!![i].itemValue
                                    holder.dashCatItemBinding.r1c4.visibility = View.VISIBLE
                                }
                                holder.dashCatItemBinding.r1c5.text.toString() =="" -> {
                                    holder.dashCatItemBinding.r1c5.text = dataModel.listItems!![i].itemValue
                                    holder.dashCatItemBinding.r1c5.visibility = View.VISIBLE
                                }
                            }



                        }

                    }


                }
            }

            else if (dataModel.name.equals("Row2", true)) {
                if(dataModel.weightSum !=0f){
                    hasRowTwo = true
                }
                holder.dashCatItemBinding.row2.weightSum = dataModel.weightSum

                for (i in dataModel.listItems!!.indices) {

                    when (i) {
                        0 -> {
                            holder.dashCatItemBinding.r2c1.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr2c1.text = dataModel.listItems!![i].itemLabel
                        }
                        1 -> {
                            holder.dashCatItemBinding.r2c2.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr2c2.text = dataModel.listItems!![i].itemLabel
                        }
                        2 -> {
                            holder.dashCatItemBinding.r2c3.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr2c3.text = dataModel.listItems!![i].itemLabel
                        }
                    }


                }

            }

            else if (dataModel.name.equals("Row3", true)) {

                if(dataModel.weightSum !=0f){
                    hasRowThree= true
                }

                holder.dashCatItemBinding.row3.weightSum = dataModel.weightSum

                for (i in dataModel.listItems!!.indices) {

                    when (i) {
                        0 -> {
                            holder.dashCatItemBinding.r3c1.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr3c1.text = dataModel.listItems!![i].itemLabel
                        }
                        1 -> {
                            holder.dashCatItemBinding.r3c2.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr3c2.text = dataModel.listItems!![i].itemLabel
                        }
                        2 -> {
                            holder.dashCatItemBinding.r3c3.text = dataModel.listItems!![i].itemValue
                            holder.dashCatItemBinding.labelr3c3.text = dataModel.listItems!![i].itemLabel
                        }
                    }


                }

            }
        }

        if(!hasRowThree){
            holder.dashCatItemBinding.row3.visibility = View.GONE
            holder.dashCatItemBinding.row3LineView.visibility = View.GONE
        }
        if(!hasRowTwo){
            holder.dashCatItemBinding.row2.visibility = View.GONE
        }
        if(!hasRowOne){
            holder.dashCatItemBinding.row1.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class ViewHolder(var dashCatItemBinding: QtcListItemBinding) : RecyclerView.ViewHolder(dashCatItemBinding.root) {


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(categoryList[adapterPosition])
            }


        }


    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}