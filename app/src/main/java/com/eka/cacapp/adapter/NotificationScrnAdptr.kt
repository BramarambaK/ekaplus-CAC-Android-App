package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.noti.NotiListData
import com.eka.cacapp.databinding.NotiListScrnItemBinding
import java.text.SimpleDateFormat
import java.util.*

class NotificationScrnAdptr (listItems: ArrayList<NotiListData>, context: Context) : RecyclerView.Adapter<NotificationScrnAdptr.ViewHolder>() {
    private val notifList:  ArrayList<NotiListData> = listItems
    var onItemClick: ((NotiListData) -> Unit)? = null
    private val mContext = context


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: NotiListScrnItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.noti_list_scrn_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)


        val dataModelMain:  NotiListData = notifList[position]

        try {


            holder.listItemBinding.ttl.setText(dataModelMain.Name)
            holder.listItemBinding.limittype.setText(mContext.getString(R.string.limit_type)+dataModelMain.Limit_Type)
            holder.listItemBinding.measName.setText(mContext.getString(R.string.measure_name)+dataModelMain.Measure_Name)


            val c: Calendar = Calendar.getInstance()

            val sdf = SimpleDateFormat("dd-MMM-yyyy")
            val getCurrentDateTime: String = sdf.format(c.getTime())
            c.add(Calendar.DATE, -1);
            val getYesterdayDateTime: String = sdf.format(c.getTime())



            val currentDate = sdf.parse(getCurrentDateTime)
            if(dataModelMain.Run_Date != null){
                val runDate = sdf.parse(dataModelMain.Run_Date)
                val yesterdayDate = sdf.parse(getYesterdayDateTime)

                if (currentDate.compareTo(runDate) == 0) {
                    holder.listItemBinding.dayTv.setText(mContext.getString(R.string.today))
                }
                else if (yesterdayDate.compareTo(runDate) == 0) {
                    holder.listItemBinding.dayTv.setText(mContext.getString(R.string.yesterday))
                }else{
                    holder.listItemBinding.dayTv.setText(runDate.toString())
                }
            }



            if(dataModelMain.Status.equals("Limit Breached",true)){
                holder.listItemBinding.notiIcon.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.noti_red_icon))
            }else{
                holder.listItemBinding.notiIcon.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.noti_yellow_icon))
            }



        }catch (e : Exception){

        }


    }

    override fun getItemCount(): Int {
        return notifList.size
    }

    inner class ViewHolder(var listItemBinding: NotiListScrnItemBinding) : RecyclerView.ViewHolder(listItemBinding.root) {


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(notifList[adapterPosition])
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