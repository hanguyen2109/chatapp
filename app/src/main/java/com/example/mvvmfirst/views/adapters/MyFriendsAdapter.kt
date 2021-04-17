package com.example.mvvmfirst.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import kotlinx.android.synthetic.main.item_friend.view.*

class MyfriendsAdapter(
    private var context: Context
) : ListAdapter<AllUserModel, MyfriendsAdapter.ViewHolder>(AllUserModel.diffUtil) {
    var listener: OnItemClickListener? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener{
                val position = adapterPosition
                if(position!=null && position!= RecyclerView.NO_POSITION){
                    listener?.onItemClick(getItem(position))
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyfriendsAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyfriendsAdapter.ViewHolder, position: Int) {
        val user = getItem(position)
        holder.itemView.relativeItemFriend.visibility = View.VISIBLE
        holder.itemView.buttonRequestItemFriend.visibility = View.INVISIBLE
        if (user.userImage == "default") {
            Glide.with(context).load(R.mipmap.ic_launcher).circleCrop()
                .into(holder.itemView.imageCricleItemFriend)
        } else {
            Glide.with(context).load(user.userImage).circleCrop()
                .into(holder.itemView.imageCricleItemFriend)
        }
//        holder.itemView.textViewHeaderFriend.setText(user.userName.substring(0,1).toUpperCase())
        holder.itemView.textViewNameItemUser.text = user.userName
//        if(position>0){
//            var i = position-1
//            if (i <= this.itemCount && user.getUserName().substring(0, 1).toUpperCase()
//                    .equals(getUserAt(i).getUserName().substring(0, 1).toUpperCase())
//            ) {
//                holder.itemView.textViewHeaderFriend.visibility == View.INVISIBLE
//            }
//        }
    }
    //get user at position #
    fun getUserAt(position: Int): AllUserModel{
        return getItem(position)
    }

    interface OnItemClickListener : View.OnClickListener {
        fun onItemClick(allUserModel: AllUserModel?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }
}
