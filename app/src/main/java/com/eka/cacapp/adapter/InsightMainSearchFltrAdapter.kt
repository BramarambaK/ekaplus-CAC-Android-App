package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import com.eka.cacapp.R
import com.eka.cacapp.data.insight.InsightChartSelFltr
import com.eka.cacapp.databinding.InsightsListRowBinding
import kotlin.collections.ArrayList


class InsightMainSearchFltrAdapter (var context : Context,
                                 private var mainList:  ArrayList<InsightChartSelFltr> ) :
        RecyclerView.Adapter<InsightMainSearchFltrAdapter.ViewHolder>(), Filterable {

    var insightSelFltrList : ArrayList<InsightChartSelFltr> = ArrayList()
    var onItemClick: ((InsightChartSelFltr) -> Unit)? = null

            init {
                insightSelFltrList = mainList
    }


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: InsightsListRowBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.insights_list_row, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: InsightChartSelFltr = insightSelFltrList [position]
        holder.dashCatItemBinding.categoryTitleTv.text = item.insightName

        var imageName =  item.insightMainChartType.toLowerCase()

        val resId: Int = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName())

        if(resId ==0){
             holder.dashCatItemBinding.imageView.setImageResource(R.drawable.insight_list)
         }else{
             holder.dashCatItemBinding.imageView.setImageResource(resId)
         }




    }

    override fun getItemCount(): Int {
        return insightSelFltrList .size
    }

    inner class ViewHolder(var dashCatItemBinding: InsightsListRowBinding) : RecyclerView.ViewHolder(dashCatItemBinding.root) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(insightSelFltrList [adapterPosition])
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    insightSelFltrList = mainList
                } else {
                    val resultList = ArrayList<InsightChartSelFltr>()
                    for (row in insightSelFltrList) {
                        if (row.insightName.toLowerCase().contains(charSearch.toLowerCase())
                        ) {
                            resultList.add(row)
                        }
                    }
                    insightSelFltrList = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = insightSelFltrList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                insightSelFltrList = results?.values as ArrayList<InsightChartSelFltr>
                notifyDataSetChanged()
            }

        }
    }


}


