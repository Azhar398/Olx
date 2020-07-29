package com.app.olxapplication.ui.uploadPhoto.adapter

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.olxapplication.MainActivity
import com.app.olxapplication.R
import com.app.olxapplication.model.CategoriesModel
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UploadImagesAdapter(internal var activity: Activity,
                          internal var imageArrayList: ArrayList<String>,
                          internal var itemClick:ItemClickListener

                            ): RecyclerView.Adapter<UploadImagesAdapter.ViewHolder>()
{
    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context

        val viewHolder= LayoutInflater.from(parent.context).inflate(
            R.layout.adapter_upload_image,
            parent,false)

        return ViewHolder(viewHolder)
    }

    override fun getItemCount(): Int {
        return imageArrayList.size+1
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position<imageArrayList.size){
            val bitmap = BitmapFactory.decodeFile(imageArrayList[position])
            holder.imageView.setImageBitmap(bitmap)
        }
        holder.itemView.setOnClickListener(View.OnClickListener {
            if (position==imageArrayList.size){
                itemClick.onItemClick()
            }
        })

    }

    fun updateList(temp: ArrayList<String>) {
        imageArrayList = temp
        notifyDataSetChanged()
    }

    fun customNotify(selectedImageArrayList: java.util.ArrayList<String>) {
        this.imageArrayList=selectedImageArrayList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
    }

    interface ItemClickListener {
        fun onItemClick()
    }
}