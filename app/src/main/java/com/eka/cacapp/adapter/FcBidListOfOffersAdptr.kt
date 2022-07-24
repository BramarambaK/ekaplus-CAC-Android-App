package com.eka.cacapp.adapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eka.cacapp.R
import com.eka.cacapp.data.farmconnect.ListOfOfferData
import com.eka.cacapp.data.farmconnect.ListOfOfferDataItem
import com.eka.cacapp.databinding.BidListOffrItemBinding
import com.eka.cacapp.utils.AppUtil.Companion.convertDateToMonthYear


class FcBidListOfOffersAdptr  (listItems: ListOfOfferData, context: Context) : RecyclerView.Adapter<FcBidListOfOffersAdptr.ViewHolder>() {
    private val offerList: ListOfOfferData = listItems
    var onItemClick: ((ListOfOfferDataItem) -> Unit)? = null
    private val mContext = context


    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        val binding: BidListOffrItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.bid_list_offr_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)


        val dataModelMain: ListOfOfferDataItem = offerList[position]

        holder.bidListItemBinding.r1c1.setText(dataModelMain.offerType
        +" | "+dataModelMain.bidId+" | "+dataModelMain.incoTerm)
        holder.bidListItemBinding.r1c2.setText(dataModelMain.paymentTerms)

        holder.bidListItemBinding.labelr2c1.setText(mContext.getString(R.string.location_lbl))
        holder.bidListItemBinding.labelr2c2.setText(mContext.getString(R.string.quality))
        holder.bidListItemBinding.labelr2c3.setText(mContext.getString(R.string.crop_year))

        holder.bidListItemBinding.r2c1.setText(dataModelMain.location)
        holder.bidListItemBinding.r2c2.setText(dataModelMain.quality)
        holder.bidListItemBinding.r2c3.setText(dataModelMain.cropYear)

        holder.bidListItemBinding.labelr3c1.setText(mContext.getString(R.string.quantity))
        holder.bidListItemBinding.labelr3c2.setText(mContext.getString(R.string.delivery_period))

        holder.bidListItemBinding.r3c1.setText(dataModelMain.quantity.toString())
        holder.bidListItemBinding.r3c2.setText(
                convertDateToMonthYear(dataModelMain.deliveryToDate))

        holder.bidListItemBinding.r4c1.setText(dataModelMain.offerorName)
        holder.bidListItemBinding.r4c2.setText(mContext.getString(R.string.ratings)+dataModelMain.rating)

      val text = "Published Price : <font color=#9A33FF><b>"+dataModelMain.publishedPrice+" "+dataModelMain.priceUnit+ "</b></font> " +
               " ( Expired in "+dataModelMain.expiresIn+" )"
        holder.bidListItemBinding.purPriceTxt.setText(Html.fromHtml(text))


    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    inner class ViewHolder(var bidListItemBinding: BidListOffrItemBinding) : RecyclerView.ViewHolder(bidListItemBinding.root) {


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


}