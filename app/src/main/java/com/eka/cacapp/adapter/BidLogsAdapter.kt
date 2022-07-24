package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.eka.cacapp.R
import com.eka.cacapp.data.farmconnect.BidLogData
import com.eka.cacapp.utils.FarmerConnectUtils.milliSecToBidLogDate

class BidLogsAdapter(private val context: Context,
                     private val arrayList: ArrayList<BidLogData>,
                      private val fcUsrAccess : String,
                      private val prcUnt : String) : BaseAdapter() {

    private lateinit var rowOneTv: TextView
    private lateinit var rowTwoTv: TextView
    private lateinit var rowThreeTv: TextView
    private lateinit var statusImage: ImageView
    private lateinit var dashView : View
    private var fc_userAccess = fcUsrAccess
    private  val BIDER = "bider"
    private val priceUnit = prcUnt

    override fun getCount(): Int {
        return arrayList.size
    }
    override fun getItem(position: Int): Any {
        return position
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.big_log_row, parent, false)
        rowOneTv = convertView.findViewById(R.id.row_one_tv)
        rowTwoTv = convertView.findViewById(R.id.row_two_tv)
        rowThreeTv = convertView.findViewById(R.id.row_three_tv)
        dashView = convertView.findViewById(R.id.dash_view)

        statusImage = convertView.findViewById(R.id.bid_status_icon)

        val bidItem =  arrayList[position]



        if (bidItem.by.equals("Bidder"))  {
            if (fc_userAccess.equals(BIDER)){
                statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_log_uparrow))
                rowTwoTv.setText(bidItem.price.toString()+ " "+priceUnit)

            }else{
                statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_log_downarrow))
                rowTwoTv.setText(bidItem.price.toString()+ " "+priceUnit)
            }
        } else if (bidItem.by.equals("Offeror")) {
            if (fc_userAccess.equals(BIDER)){
                statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_log_downarrow))
                rowTwoTv.setText(bidItem.price.toString()+ " "+priceUnit)
            }else{
                statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                        R.drawable.ic_log_uparrow))
                rowTwoTv.setText(bidItem.price.toString()+ " "+priceUnit)
            }
        }


        if(bidItem.logType==0){
            statusImage.setImageDrawable(ContextCompat.getDrawable(context,
            R.drawable.ic_log_start))
            rowOneTv.setText(context.getString(R.string.published_price))
            rowTwoTv.setText(bidItem.price.toString()+ " "+priceUnit)
            rowThreeTv.visibility = View.GONE
        }else if(bidItem.logType==1){
            statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_log_greentick))
            rowTwoTv.setText(context.getString(R.string.accepted))

        }
        else if(bidItem.logType== -1){
            statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_card_rejected))
            rowTwoTv.setText(context.getString(R.string.rejected))
        }
        else if(bidItem.logType== -2){
            statusImage.setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.ic_log_cancel))
            rowTwoTv.setText(context.getString(R.string.cancelled))
        }


        if(bidItem.date >0){
            rowOneTv.setText(milliSecToBidLogDate(bidItem.date)+ " IST")
        }

        var remarks = bidItem.remarks
        if(remarks.isEmpty()){
            remarks = context.getString(R.string.no_remarks)
        }
        rowThreeTv.setText(bidItem.name+", "+bidItem.by+" : "+remarks)

        if(position==arrayList.size-1){
            dashView.visibility = View.INVISIBLE
        }

        return convertView
    }
}