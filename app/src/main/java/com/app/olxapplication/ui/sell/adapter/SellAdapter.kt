package com.app.olxapplication.ui.sell.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olxapplication.R
import com.app.olxapplication.model.CategoriesModel
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class SellAdapter(var categoriesList:MutableList<CategoriesModel>,
                  var  itemClickListener: ItemClickListener)
    : RecyclerView.Adapter<SellAdapter.ViewHolder>()
{
    private lateinit var context:Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder= LayoutInflater.from(parent.context).inflate(R.layout.adapter_sell,
            parent,false)

        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
       return categoriesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewTitle.setText(categoriesList.get(position).key)
        Glide.with(context)
            .load(categoriesList.get(position).image_bw)
            .placeholder(R.drawable.ic_placeholder)
            .into(holder.imageView)

        holder.itemView.setOnClickListener(View.OnClickListener {
            itemClickListener.onItemClick(position)
        })

    }

    fun updateList(temp: MutableList<CategoriesModel>) {
        categoriesList = temp
        notifyDataSetChanged()
    }

    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView) {
        val textViewTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val imageView = itemView.findViewById<ImageView>(R.id.ivIcon)
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}