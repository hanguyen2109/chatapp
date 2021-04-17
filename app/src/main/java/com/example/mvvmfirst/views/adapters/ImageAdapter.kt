package com.example.mvvmfirst.views.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File

class ImageAdapter(val imageArray: ArrayList<String>) :
    RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return imageArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentImage =imageArray[position]
        holder.onBind(currentImage)

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(image: String){
            val position = adapterPosition
            itemView.apply {
                Glide.with(context).load(image).into(image_preview)
                image_preview.setOnClickListener {
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener!!.onItemClick(Uri.fromFile(File(image)))
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(uri: Uri)
    }

    fun setOnTemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
