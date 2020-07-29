package com.app.olxapplication.ui.myAds.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olxapplication.R
import com.app.olxapplication.model.CategoriesModel
import com.app.olxapplication.model.DataItemModel
import com.app.olxapplication.ui.home.adapter.CategoriesAdapter
import com.app.olxapplication.ui.uploadPhoto.adapter.UploadImagesAdapter
import com.app.olxapplication.utilities.Constants
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat

class MyAdsAdapter(
    var dataItemModel: MutableList<DataItemModel>,
    var mClickListener: ItemClickListener)
    : RecyclerView.Adapter<MyAdsAdapter.ViewHolder>()
{
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder= LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_my_ads,
            parent,false)

        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return dataItemModel.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.textViewPrice.setText(Constants.CURRENCY_SYMBOL+dataItemModel.get(position).Price)
        holder.textViewBrand.setText(dataItemModel.get(position).Brand)
        holder.textViewAddress.setText(dataItemModel.get(position).address)

        Glide.with(context)
            .load(dataItemModel.get(position).images.get(0))
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)


        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate= sdf.format(dataItemModel[position].createdDate?.time)
        holder.textViewDate.setText(formattedDate)
        holder.itemView.setOnClickListener(View.OnClickListener {
            mClickListener.onItemClick(position)
        })

    }

    fun updateList(temp: MutableList<DataItemModel>) {
        dataItemModel = temp
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val textViewPrice = itemView.findViewById<TextView>(R.id.tvPrice)
        val textViewBrand = itemView.findViewById<TextView>(R.id.tvBrand)
        val textViewAddress = itemView.findViewById<TextView>(R.id.tvAddress)
        val textViewDate = itemView.findViewById<TextView>(R.id.tvDate)
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}
