package com.example.mvvmfirst.views.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.ChatModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.item_message.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(
    private var context: Context,
    private var chatModelArrayList: ArrayList<ChatModel>
) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    var listener: OnItemClickListener? = null
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageAdapter.ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return chatModelArrayList.size

    }

    override fun onBindViewHolder(holder: MessageAdapter.ViewHolder, position: Int) {
        val userChat = chatModelArrayList[position]
        holder.itemView.textViewUserName.setText(userChat.getUserModel()?.getUserName())
        if (userChat.getUserModel()?.getUserImgUrl().equals("default")) {
            Glide.with(context).load(R.mipmap.ic_launcher).circleCrop()
                .into(holder.itemView.imageFriendChat)
        } else {
            Glide.with(context).load(userChat.getUserModel()?.getUserImgUrl()).circleCrop()
                .into(holder.itemView.imageFriendChat)
        }
        var message: String = ""
        var date: String = ""
        var time: String = ""
        var type: String = ""
        var idSender: String = ""
        var count = 0
        for (i in userChat.getMessageModelArrayList()!!.indices) {
            if (userChat.getMessageModelArrayList()!!.get(i)?.getIdReceiver()
                    .equals(firebaseUser?.uid) && userChat.getMessageModelArrayList()!!.get(i)
                    ?.getIdSender().equals(
                        userChat.getUserModel()?.getUserId()
                    ) && !userChat.getMessageModelArrayList()!!
                    .get(i)
                    ?.getCheckSeen()!!
            ) {
                count++
            }
            idSender = userChat.getMessageModelArrayList()!!.get(i)?.getIdSender()!!
            message = userChat.getMessageModelArrayList()!!.get(i)?.getMessage()!!
            date = userChat.getMessageModelArrayList()!!.get(i)?.getDate()!!
            time = userChat.getMessageModelArrayList()!!.get(i)?.getTime()!!
            type = userChat.getMessageModelArrayList()!!.get(i)?.getType()!!
        }
        assert(idSender != null)
        if (idSender.equals(firebaseUser?.uid)) {
            message = "Bạn" + message
        }
        assert(type != null)
        if (type.equals("Text")) {
            holder.itemView.textViewLastMess.setText(message)
        } else if (type.equals("sticker")) {
            if (idSender.equals(firebaseUser?.uid)) {
                holder.itemView.textViewLastMess.setText("Bạn: Sticker")
            } else {
                holder.itemView.textViewLastMess.setText("Sticker")
            }
        } else {
            if (idSender.equals(firebaseUser?.uid)) {
                holder.itemView.textViewLastMess.setText("Bạn: Image")
            } else {
                holder.itemView.textViewLastMess.setText("Image")
            }
        }
        //number message not read
        if (count > 0) {
            holder.itemView.textViewSumNotRead.visibility = View.VISIBLE
            if (count > 9) {
                holder.itemView.textViewSumNotRead.setText("" + 9 + "+")
            } else {
                holder.itemView.textViewSumNotRead.setText("" + count + "")
            }
            holder.itemView.textViewLastMess.setTextColor(Color.BLACK)
            holder.itemView.textViewLastMess.setTypeface(Typeface.DEFAULT_BOLD)
            holder.itemView.textViewTimeMessage.setTextColor(Color.BLACK)
            holder.itemView.textViewTimeMessage.setTypeface(Typeface.DEFAULT_BOLD)
        } else {
            holder.itemView.textViewSumNotRead.visibility = View.GONE
            holder.itemView.textViewLastMess.setTypeface(Typeface.DEFAULT)
            holder.itemView.textViewTimeMessage.setTypeface(Typeface.DEFAULT)
            holder.itemView.textViewLastMess.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.grey
                )
            )
            holder.itemView.textViewTimeMessage.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.grey
                )
            )
        }
        //set time and date
        var calendar: Calendar = Calendar.getInstance()
        var simpleDateFormat = SimpleDateFormat("dd/MM/yyy", Locale.getDefault())
        var a: String = simpleDateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, -1)
        if (a.equals(date)) {
            holder.itemView.textViewTimeMessage.setText(time)
        } else {
            holder.itemView.textViewTimeMessage.setText(date)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(chatModelArrayList.get(position))
                }
            }
        }
    }

    interface OnItemClickListener : View.OnClickListener {
        fun onItemClick(userModel: ChatModel?)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        if (listener != null) {
            this.listener = listener
        }
    }
}
