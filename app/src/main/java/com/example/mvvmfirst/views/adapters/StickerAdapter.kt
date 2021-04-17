package com.example.mvvmfirst.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmfirst.R
import kotlinx.android.synthetic.main.fragment_chat.view.*
import kotlinx.android.synthetic.main.sticker_item.view.*

class StickerAdapter(private val itemArray: List<Int>) :
    RecyclerView.Adapter<StickerAdapter.ViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sticker_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemArray.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = itemArray[position]
        holder.onBind(currentItem)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(sticker: Int) {
            itemView.apply {
                sticker_preview.setImageResource(sticker)
                sticker_preview.setOnClickListener {
                    val name = it.resources.getResourceEntryName(sticker)
                    listener?.onItemClick(name)
                }
            }
        }

    }

    interface OnItemClickListener {
        fun onItemClick(stick: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}
