package com.example.mvvmfirst.views.adapters

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.MessageModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_chat_left.view.*
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ChatAdapter(private val context: Context) :
    ListAdapter<MessageModel, ChatAdapter.ViewHolder>(MessageModel.diffUtil) {

    private var TITLE_LEFT = 0
    private var TITLE_RIGHT = 1
    var urlImage: String? = null
    private var listener: OnItemClickListener? = null
    var media: MediaPlayer? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TITLE_RIGHT) {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
            return ViewHolder((view))
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
            return ViewHolder((view))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            val position = adapterPosition
            itemView.imageMessChat.setOnClickListener {
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(getItem(position))
                }
            }
        }

        fun onBind(messageModel: MessageModel) {
            itemView.apply {
                val position = adapterPosition
                when (messageModel.getType()) {
                    "Text" -> {
                        textMessChat.visibility = View.VISIBLE
                        textMessChat.text = messageModel.getMessage()
                        imageMessChat.visibility = View.GONE
                        imageSickerMessChat.visibility = View.GONE
                        relativePlay.visibility = View.GONE
                    }
                    "sticker" -> {
                        imageMessChat.visibility = View.GONE
                        textMessChat.visibility = View.GONE
                        relativePlay.visibility = View.GONE
                        imageSickerMessChat.visibility = View.VISIBLE
                        val resID = context.resources.getIdentifier(
                            messageModel.getMessage(),
                            "drawable",
                            context.packageName
                        )
                        imageSickerMessChat.setImageResource(resID)
                    }
                    "Record" -> {
                        var isPlaying = false
                        textMessChat.visibility = View.GONE
                        imageMessChat.visibility = View.GONE
                        imageSickerMessChat.visibility = View.GONE
                        relativePlay.visibility = View.VISIBLE
                        imageButtonPlayRecord.setOnClickListener {
                            if (!isPlaying) {// is false
                                isPlaying = true
                                imageButtonPlayRecord.setImageResource(R.drawable.ic_pause)
                                media = MediaPlayer()
                                try{
                                    media?.setDataSource(messageModel.getMessage().toString())
                                    media?.prepare()
                                }catch (e: Exception){
                                    e.printStackTrace()
                                }
                                media?.start()
                                val simpleDateFormat = SimpleDateFormat("mm:ss")
                                textTimeRecord.text = simpleDateFormat.format(media?.duration)
                                viewPlay.max = media?.duration!!
                                val handler = Handler()
                                handler.postDelayed(object : Runnable {
                                    override fun run() {
                                        val simpleDateFormat1 = SimpleDateFormat("mm:ss")
                                        val time: String =
                                            simpleDateFormat1.format(media?.currentPosition)
                                        Log.d("time start", time.toString())
                                        viewPlay.progress = media?.currentPosition!!
                                        if (time == textTimeRecord.text) {
                                            imageButtonPlayRecord.setImageResource(R.drawable.ic_play_circle)
                                            viewPlay.progress = 0
                                        }
                                        handler.postDelayed(this, 100)
                                    }
                                }, 100)
                            } else {
                                media?.pause()
                                imageButtonPlayRecord.setImageResource(R.drawable.ic_play_circle)
                                isPlaying = false
                            }
                        }

                        viewPlay.setOnSeekBarChangeListener(object :
                            SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(
                                seekBar: SeekBar?,
                                progress: Int,
                                fromUser: Boolean
                            ) {

                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                                media?.seekTo(viewPlay.progress)
                            }

                        })
                    }
                    else -> {
                        imageSickerMessChat.visibility = View.GONE
                        relativePlay.visibility = View.GONE
                        imageMessChat.visibility = View.VISIBLE
                        textMessChat.visibility = View.GONE
                        Glide.with(context).load(messageModel.getMessage()).into(imageMessChat)
                    }

                }
                if (messageModel.getIsShow()!!) {
                    textMessChatDate.visibility = View.GONE
                } else {
                    textMessChatDate.visibility = View.VISIBLE
                }
                //load image user
                if (urlImage == "default") {
                    Glide.with(context).load(R.mipmap.ic_launcher).circleCrop()
                        .into(imageUserChat)
                } else {
                    Glide.with(context).load(urlImage).circleCrop().into(imageUserChat)
                }


                //time
                val calendar = Calendar.getInstance()
                val simpleDateFormatDate =
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val a = simpleDateFormatDate.format(calendar.time)
                if (messageModel.getDate() == a) {
                    textMessChatDate.text = messageModel.getTime()
                    textChatDate.text = "Hôm nay"
                    textChatDate.visibility = View.VISIBLE
                    textMessChatDate.visibility = View.GONE
                } else {
                    textChatDate.text = messageModel.getDate()
                    textMessChatDate.text =
                        messageModel.getDate().toString() + " " + messageModel.getTime()
                    textChatDate.visibility = View.VISIBLE
                    textMessChatDate.visibility = View.GONE
                }
//                val position = adapterPosition//vi tri position now
                if (position > 0) {
                    val i = position - 1
                    val message = getNoteAt(i)
                    if (i < itemCount && messageModel.getIdSender()
                            .equals(message!!.getIdSender())
                        && messageModel.getIdReceiver().equals(message.getIdReceiver())
                    ) {
                        imageUserChat.visibility = View.VISIBLE
                        viewChat.visibility = View.GONE
                    }
                    if (i <= itemCount && messageModel.getDate()
                            .equals(message!!.getDate())
                    ) {
                        textChatDate.visibility = View.GONE
                    }
                }
                textMessChat.setOnClickListener {
                    if (textMessChatDate.visibility == View.VISIBLE) {
                        textMessChatDate.visibility = View.GONE
                    } else {
                        textMessChatDate.visibility = View.VISIBLE
                    }
                }
                imageSickerMessChat.setOnClickListener {
                    if (textMessChatDate.visibility == View.VISIBLE) {
                        textMessChatDate.visibility = View.GONE
                    } else {
                        textMessChatDate.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    fun getNoteAt(position: Int): MessageModel? {
        return getItem(position)
    }

//    fun downloadFile(messageModel: MessageModel) {
//        nameFileRecord = System.currentTimeMillis().toString() + "Downloading"
//        var request = DownloadManager.Request(Uri.parse(messageModel.getMessage().toString()))
//            .setTitle(nameFileRecord)
//            .setDescription("record")
//            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
//            .setAllowedOverMetered(true)
//            .setDestinationInExternalFilesDir(
//                context,
//                Environment.DIRECTORY_DOWNLOADS,
//                "record.3gp"
//            )
//        var dm = context?.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//        mDowload = dm.enqueue(request)
//        val br = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                var id: Long? = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
//                if (id == mDowload) {
////                    Toast.makeText(context, "load success", Toast.LENGTH_SHORT).show()
//                }
//            }
//
//        }
//        context?.registerReceiver(br, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
//
//    }
//    fun dowloadFile() {
//        val rootPath = File(Environment.getExternalStorageDirectory(), "file_name")
//        if (!rootPath.exists()) {
//            rootPath.mkdirs()
//        }
//        val localFile = File(rootPath, "record.3gp")
//        islandRef.getFile(localFile)
//            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
//                Log.e("firebase ", ";local tem file created  created $localFile")
//                //  updateDb(timestamp,localFile.toString(),position);
//            }).addOnFailureListener(OnFailureListener { exception ->
//                Log.e(
//                    "firebase ",
//                    ";local tem file not created  created $exception"
//                )
//            })
//    }


    override fun getItemViewType(position: Int): Int {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        var id: String = ""
        if (firebaseUser != null) {
            id = firebaseUser.uid
        }
        return if (getNoteAt(position)?.getIdSender().equals(id)) {
            TITLE_RIGHT
        } else
            TITLE_LEFT
    }

    fun setImage(urlImage: String?) {
        this.urlImage = urlImage
    }

    interface OnItemClickListener {
        fun onItemClick(messageModel: MessageModel)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
}

