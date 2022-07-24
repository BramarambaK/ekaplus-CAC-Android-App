package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.search.SearchData
import com.eka.cacapp.databinding.DashSearchRowItemBinding
import java.util.*

class DashSearchAdapter (listItems: ArrayList<SearchData>, context: Context) : RecyclerView.Adapter<DashSearchAdapter.ViewHolder>() {
    private val searchList: ArrayList<SearchData> = listItems
    var onItemClick: ((SearchData) -> Unit)? = null
    private val mContext = context


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: DashSearchRowItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.dash_search_row_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)


        val dataModelMain: SearchData = searchList[position]

        try {
            if(position==0){
                holder.listItemBinding.firstRowDiv.visibility = View.VISIBLE
            }else{
                holder.listItemBinding.firstRowDiv.visibility = View.GONE
            }


            holder.listItemBinding.ttl.setText(dataModelMain.name)

            holder.listItemBinding.entityType.text = dataModelMain.entityType.capitalize()


            var imageName =  ""
            if(dataModelMain.entityType.equals("app",true)){
                if(dataModelMain.category == null){
                    imageName = ""
                }else{
                    imageName= dataModelMain.category.toString().toLowerCase()
                }

                imageName.replace("&","")
                if(imageName.startsWith("risk")){
                    imageName = "riskcompliance"
                }else if (imageName.startsWith("operations")){
                    imageName = "supplychainandoperation"
                }

                val resId: Int = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName())

                if(resId ==0){
                    holder.listItemBinding.searchResImg.setImageResource(R.drawable.trade)
                }else{
                    holder.listItemBinding.searchResImg.setImageResource(resId)
                }
            }else{
                imageName= dataModelMain.chartType.toString().toLowerCase()

                val resId: Int = mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName())

                if(resId ==0){
                    holder.listItemBinding.searchResImg.setImageResource(R.drawable.insight_list)
                }else{
                    holder.listItemBinding.searchResImg.setImageResource(resId)
                }
            }



        }catch (e : Exception){

        }


    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    inner class ViewHolder(var listItemBinding: DashSearchRowItemBinding) : RecyclerView.ViewHolder(listItemBinding.root) {


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(searchList[adapterPosition])
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