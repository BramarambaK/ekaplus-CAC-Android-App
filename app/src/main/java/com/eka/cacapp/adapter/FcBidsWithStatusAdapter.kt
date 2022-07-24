package com.eka.cacapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.farmconnect.ListOfOfferData
import com.eka.cacapp.data.farmconnect.ListOfOfferDataItem
import com.eka.cacapp.databinding.BidWithStatusItemBinding
import com.eka.cacapp.utils.FarmerConnectUtils.*

class FcBidsWithStatusAdapter  (listItems: ListOfOfferData, context: Context, fcMode : String) : RecyclerView.Adapter<FcBidsWithStatusAdapter.ViewHolder>() {
    private val offerList: ListOfOfferData = listItems
    var onItemClick: ((ListOfOfferDataItem) -> Unit)? = null
    private val mContext = context
    private val farConnectMode = fcMode


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: BidWithStatusItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.bid_with_status_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)


        val dataModelMain: ListOfOfferDataItem = offerList[position]

        holder.bidListItemBinding.r1c1.setText(dataModelMain.offerType
                +" | "+dataModelMain.refId+" | "+dataModelMain.incoTerm)
        holder.bidListItemBinding.r1c2.setText(dataModelMain.paymentTerms)

        holder.bidListItemBinding.labelr2c1.setText(mContext.getString(R.string.location_lbl))
        holder.bidListItemBinding.labelr2c2.setText(mContext.getString(R.string.quality))
        holder.bidListItemBinding.labelr2c3.setText(mContext.getString(R.string.crop_year))

        holder.bidListItemBinding.r2c1.setText(dataModelMain.location)
        holder.bidListItemBinding.r2c2.setText(dataModelMain.quality)
        holder.bidListItemBinding.r2c3.setText(dataModelMain.cropYear)

        holder.bidListItemBinding.labelr3c1.setText(mContext.getString(R.string.quantity))
        holder.bidListItemBinding.labelr3c2.setText(mContext.getString(R.string.delivery_period))

        holder.bidListItemBinding.r3c1.setText(dataModelMain.quantity.toString()+" "+dataModelMain.quantityUnit)
        holder.bidListItemBinding.r3c2.setText(
                miliiSecToMonthYear(dataModelMain.deliveryFromDateInMillis))

        holder.bidListItemBinding.r4c1.setText(dataModelMain.offerorName)
        holder.bidListItemBinding.r4c2.setText(mContext.getString(R.string.ratings)+dataModelMain.rating)

        getStatusImageDrawable(holder.bidListItemBinding.statusImg,dataModelMain.status,
        dataModelMain.pendingOn,holder.bidListItemBinding.cancelledView)
      //  holder.bidListItemBinding.statusImg.setImageDrawable(ContextCompat.getDrawable(mContext,
      //    R.drawable.ic_card_downarrow))


    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    inner class ViewHolder(var bidListItemBinding: BidWithStatusItemBinding) : RecyclerView.ViewHolder(bidListItemBinding.root) {


        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(offerList[adapterPosition])
            }


        }


    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getStatusImageDrawable(imgView : ImageView,status : String,pendingOn :String,
                  cancelView : LinearLayout) {
        cancelView.visibility = View.GONE
        if(status.equals("Accepted",true)){
            imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_card_greentick))
        }
       else if(status.equals("Rejected",true)){
            imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_card_rejected))
        }
        else if(status.equals("Cancelled",true)){
            imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                    R.drawable.ic_card_greentick))

            cancelView.visibility = View.VISIBLE
        }
       else if(farConnectMode.equals(FC_MODE_BID) &&
                status.equals("In-Progress",true)){
            if(pendingOn.equals("Offeror")){
                imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_card_uparrow))
            }else{
                imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_card_downarrow))
            }

        }else if (farConnectMode.equals(FC_MODE_OFFEROR)
                && status.equals("In-Progress",true)){
            if(pendingOn.equals("Offeror")){
                imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_card_downarrow))
            }else{
                imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.ic_card_uparrow))
            }
        }
     //   imgView.setImageDrawable(ContextCompat.getDrawable(mContext,
     //           R.drawable.ic_card_downarrow))
      //  "pendingOn":"Offeror","status":"In-Progress"
     //   "pendingOn":"None","status":"Accepted"
      //  "pendingOn":"None","status":"Rejected"
      //  "pendingOn":"None","status":"Cancelled"
    }

}