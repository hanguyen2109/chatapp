package com.example.mvvmfirst.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.AllUserModel
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import kotlinx.android.synthetic.main.item_friend.view.*

class AllFriendAdapter(private var context: Context) :
    ListAdapter<AllUserModel, AllFriendAdapter.ViewHolder>(AllUserModel.diffUtil) {

    private var listener: OnItemClickListener? = null
    lateinit var friendViewModel: FriendViewModel

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false)
        friendViewModel =
            ViewModelProviders.of((parent.context as FragmentActivity)).get<FriendViewModel>(
                FriendViewModel::class.java
            )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener?.onItemClick(getItem(position))
                }
            }
        }

        fun onBind(user: AllUserModel) {
            itemView.apply {
                if (user.userImage == "default") {
                    Glide.with(context).load(R.mipmap.ic_launcher).circleCrop()
                        .into(imageCricleItemFriend)
                } else {
//                imageCricleItemFriend.loadImageCircle(user.userImage)
                    Glide.with(context).load(user.userImage).circleCrop()
                        .into(imageCricleItemFriend)
                }
                textViewNameItemUser.text = user.userName.toString()
                when (user.userType) {
                    "NoFriend" -> {
                        buttonRequestItemFriend.setText(R.string.txt_request_friend)
                        buttonRequestItemFriend.setBackgroundResource(R.drawable.button_enable)
                        buttonRequestItemFriend.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                    "friend" -> {
                        buttonRequestItemFriend.visibility = View.GONE
//                        buttonRequestItemFriend.setText(R.string.un_friend)
//                        buttonRequestItemFriend.setBackgroundResource(R.drawable.button_enable)
//                        buttonRequestItemFriend.setTextColor(
//                            ContextCompat.getColor(
//                                context,
//                                R.color.white
//                            )
//                        )
                    }
                    "sendRequest" -> {
                        buttonRequestItemFriend.setText(R.string.txt_cancel)
                        buttonRequestItemFriend.setBackgroundResource(R.drawable.button_unfriend)
                        buttonRequestItemFriend.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
//                    "friendRequest" -> {
//                        buttonRequestItemFriend.visibility = View.GONE
////                        buttonRequestItemFriend.setText(R.string.accept)
////                        buttonRequestItemFriend.setBackgroundResource(R.drawable.button_enable)
////                        buttonRequestItemFriend.setTextColor(
////                            ContextCompat.getColor(
////                                context,
////                                R.color.white
////                            )
////                        )
//                    }
                }
                buttonRequestItemFriend.setOnClickListener {
                    when (buttonRequestItemFriend.text.toString()) {
                        "Kết bạn", "Add Friend" -> {
                            friendViewModel.createFriend(user)
                            buttonRequestItemFriend.setText(R.string.txt_cancel)
                        }
                        "Hủy", "Hủy bạn", "UnFriend", "Cancel" -> {
                            friendViewModel.deleteFriend(user)
                            buttonRequestItemFriend.setText(R.string.txt_request_friend)
                        }
//                        "Đồng ý", "Accept" -> {
//                            friendViewModel.updateFriend(user)
//                            buttonRequestItemFriend.visibility = View.GONE
//                        }
//                        "Hủy kết bạn" -> {
//                            buttonRequestItemFriend.visibility = View.GONE
////                            friendViewModel.deleteFriend(user)
////                            buttonRequestItemFriend.setText(R.string.add_friend)
//                        }
                    }
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(userModel: AllUserModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

//    override fun getItemCount(): Int {
//        return allUserArray.size
//
//    }

}
