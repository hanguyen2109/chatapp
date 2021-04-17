package com.example.mvvmfirst.views.content.message

import android.Manifest
import android.Manifest.permission.*
import android.app.Activity
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.bumptech.glide.Glide
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.R
import com.example.mvvmfirst.services.model.MessageModel
import com.example.mvvmfirst.views.adapters.ChatAdapter
import com.example.mvvmfirst.views.adapters.ImageAdapter
import com.example.mvvmfirst.views.adapters.StickerAdapter
import com.example.mvvmfirst.views.content.MainActivity
import com.example.mvvmfirst.views.content.ViewModelProviderFactory
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_chat.*
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class ChatFragment : Fragment() {

    private val stickerResource = arrayOf(
        R.drawable.cuppy_hi,
        R.drawable.cuppy_battery,
        R.drawable.cuppy_bluescreen,
        R.drawable.cuppy_bye,
        R.drawable.cuppy_curious,
        R.drawable.cuppy_disgusting,
        R.drawable.cuppy_cry,
        R.drawable.cuppy_hmm,
        R.drawable.cuppy_love,
        R.drawable.cuppy_lovewithcookie,
        R.drawable.cuppy_phone,
        R.drawable.cuppy_angry,
        R.drawable.cuppy_angry1,
        R.drawable.cuppy_lol,
        R.drawable.cuppy_rofl,
        R.drawable.cuppy_tired,
        R.drawable.cuppy_upset
    )
    private var chatAdapter: ChatAdapter? = null
    var imageAdapter: ImageAdapter? = null
    var stickerAdapter: StickerAdapter? = null
    var lastPositionChat: Long = 0
    private val IMAGE_REQUEST = 1
    var id: String = ""
    var storageReference = FirebaseStorage.getInstance().getReference("chat")// upload file
    private var imageUri: Uri? = null
    var uriImage: String? = null
    var databaseReference: DatabaseReference? = null
    var listener: ValueEventListener? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    var loadPosition = 0
    var inputMethodManager: InputMethodManager? = null
    var layoutManager =
        LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    private lateinit var chatViewModel: ChatViewModel

    //record
    private var recorder: MediaRecorder? = null
    private var mFileName: String = ""
    private var _record: File? = null
    var requestPermissionCode = 1
    var isRecording: Boolean = false
    var play = false
    var media: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        chatViewModel =
            ViewModelProviders.of(this, ViewModelProviderFactory())
                .get<ChatViewModel>(ChatViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        chatAdapter = ChatAdapter(requireContext())
        recyclerChat.adapter = chatAdapter
        recyclerChat.layoutManager = LinearLayoutManager(activity)
        chatViewModel.getInfoUserChat(id)
        Toast.makeText(requireContext(), id, Toast.LENGTH_SHORT).show()
        recyclerChat.layoutManager = layoutManager
        recyclerChat.setHasFixedSize(true)

        imageBackChat.setOnClickListener { removeFragment() }//

        ImagePhotoChat.setOnClickListener { getImage() }

        imageSendChat.setOnClickListener {
            val bundle = arguments
            var iD: String? = null
            if (bundle != null) {
                iD = bundle.getString("idUser")
            }
            val message: String =
                editInputMess.text.toString().trim()
            if (iD != null &&  !(TextUtils.isEmpty(message.toString()))) {
                chatViewModel.sendMessage(iD, message, "Text")
                editInputMess.text = null
            }
        }

        editInputMess.setOnFocusChangeListener { _, _ ->
            if (recyclerSticker.visibility == View.VISIBLE || recyclerImage.visibility == View.VISIBLE) {
                recyclerSticker.visibility = View.GONE
                recyclerImage.visibility = View.GONE
                imageSticker.setImageResource(R.drawable.sticker)
                ImagePhotoChat.setImageResource(R.drawable.photo_chat)
                imageRecord.setImageResource(R.drawable.ic_record)
            }
        }

        editInputMess.setOnClickListener {
            editInputMess.requestFocus()
            recyclerSticker.visibility = View.GONE
            recyclerImage.visibility = View.GONE
//            Log.d("Vao show key", chatAdapter?.itemCount.toString() + "")
            inputMethodManager!!.showSoftInput(editInputMess, InputMethodManager.SHOW_IMPLICIT)
            imageSticker.setImageResource(R.drawable.sticker)
            ImagePhotoChat.setImageResource(R.drawable.photo_chat)
            imageRecord.setImageResource(R.drawable.ic_recording_enable)
//            Log.d("Count mess", chatAdapter?.itemCount.toString() + "")
            recyclerChat.smoothScrollToPosition(chatAdapter?.itemCount!!)
        }
        editInputMess.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerSticker.visibility = View.GONE
                imageSticker.setImageResource(R.drawable.sticker)
                if (TextUtils.isEmpty(s.toString())) {
                    imageSendChat.isEnabled = false
                    imageSendChat.setImageResource(R.drawable.send_mess_enable)
                } else {
                    imageSendChat.isEnabled = true
                    imageSendChat.setImageResource(R.drawable.send)
                }
            }
        })

        imageSticker.setOnClickListener {
            if (recyclerSticker.visibility == View.VISIBLE) {
                editInputMess.requestFocus()
                recyclerImage.visibility = View.GONE
                recyclerSticker.visibility = View.GONE
                inputMethodManager!!.showSoftInput(editInputMess, InputMethodManager.SHOW_IMPLICIT)
                imageSticker.setImageResource(R.drawable.sticker)
            } else {
                ImagePhotoChat.setImageResource(R.drawable.photo_chat)
                imageRecord.setImageResource(R.drawable.ic_record)
                inputMethodManager!!.hideSoftInputFromWindow(requireView().windowToken, 0)
                editInputMess.clearFocus()
                recyclerImage.visibility = View.GONE
                recyclerSticker.visibility = View.VISIBLE
                imageSticker.setImageResource(R.drawable.ic_smile_blue)
            }
        }

        val bundle = arguments
        if (bundle != null) {
            id = bundle.getString("idUser").toString()
        }
        recyclerChat.layoutManager = layoutManager
        recyclerChat.setHasFixedSize(true)
        chatAdapter?.setOnItemClickListener(object : ChatAdapter.OnItemClickListener {
            override fun onItemClick(messageModel: MessageModel) {
                if (messageModel.getType() == "Image") {
                    val fragmentTransaction = parentFragmentManager.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    val zoomImageFragment = ZoomImageFragment()
                    val bundle = Bundle()
                    bundle.putString("image", messageModel.getMessage())
                    zoomImageFragment.arguments = bundle
                    fragmentTransaction.add(R.id.frameLayoutChat, zoomImageFragment, null).commit()
                    fragmentTransaction.addToBackStack(null)
                }
                if (messageModel.getType() == " Record") {
//                    dowloadFile(messageModel)
                    //play record
//                    Toast.makeText(requireContext(), messageModel.getMessage(), Toast.LENGTH_SHORT).show()
//                    storageReference.child(messageModel.getMessage().toString().substring(33))

                }
            }

        })

        chatViewModel.getInfoUserChat(id)
        chatViewModel.userChatLiveData.observe(viewLifecycleOwner, Observer {
            chatAdapter?.setImage(it.getUserImgUrl())
            val userChat = it
            Log.d("ten", userChat.getUserName().toString())
            if (it.getStatus() == "offline") {
                imageOnline.setImageResource(R.drawable.offline)
                textOnline.text = "offline"
            } else {
                imageOnline.setImageResource(R.drawable.online)
                textOnline.text = "online"
            }
            if (it.getUserImgUrl() == "default") {
                Glide.with(requireContext()).load(R.mipmap.ic_launcher).circleCrop()
                    .into(imageTitleChat)
            } else {
                Glide.with(requireContext()).load(it.getUserImgUrl()).circleCrop()
                    .into(imageTitleChat)
            }
            textUserNameChat.text = it.getUserName().toString()
            recyclerChat.setHasFixedSize(true)
            val itemAnimator =
                (recyclerChat.itemAnimator as SimpleItemAnimator)
            itemAnimator.supportsChangeAnimations = false
        })
        checkSeen(id)
        recyclerChat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                loadPosition = recyclerView.childCount
                val position = layoutManager.findFirstVisibleItemPosition()
                if (position == 0) {
                    chatViewModel.showMessageLast(id, lastPositionChat)
                }
            }
        })
        chatViewModel.getAll()
        chatViewModel.showMessageLast(id, lastPositionChat)
        chatViewModel.messageList.observe(viewLifecycleOwner, Observer {
            var messArray: ArrayList<MessageModel> = ArrayList()
            messArray = it
            if (messArray.size > 0) {
                lastPositionChat = messArray[0].getTimeLong()
                for (i in 0 until messArray.size - 1) {
                    Log.d("mess", messArray[i].getTimeLong().toString())
                    val j = i + 1
                    if (messArray[i].getIdReceiver() == messArray[j].getIdReceiver() &&
                        messArray[i].getIdSender() == messArray[j].getIdSender()
                    ) {
                        messArray[i].setIsShow(true)
                    }
                }
                chatAdapter?.submitList(messArray)
                recyclerChat.smoothScrollToPosition(messArray.size - 1)
            } else {
                chatAdapter?.submitList(messArray)
            }
        })
        stickerAdapter = StickerAdapter(listOf(*stickerResource))
        recyclerSticker.adapter = stickerAdapter
        recyclerSticker.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerSticker.setHasFixedSize(true)
        stickerAdapter!!.setOnItemClickListener(object : StickerAdapter.OnItemClickListener {
            override fun onItemClick(stick: String) {
                chatViewModel.sendMessage(id, stick, "sticker")
            }
        })

        val arrayImage: ArrayList<String> = getAllShownImagesPath(requireActivity())
        imageAdapter = ImageAdapter(arrayImage)
        recyclerImage.adapter = imageAdapter
        recyclerImage.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerImage.setHasFixedSize(true)
        imageAdapter!!.setOnTemClickListener(object : ImageAdapter.OnItemClickListener {
            override fun onItemClick(uri: Uri) {
                imageUri = uri
                upLoadImage()
            }

        })

        //recording
        imageRecord.setOnClickListener {
            if (!isRecording) {
                if (checkPermission()) {
                    try {

                        mFileName =
                            requireContext().externalCacheDir.toString()  + "/" + UUID.randomUUID()
                                .toString() + "_audio_record.mp3"

                        _record = File(mFileName)
                        _record?.parentFile?.mkdir()
                        _record?.createNewFile()
                        if (_record?.exists() == false){
                            Log.d("ChatFragment", "File not exist!")
                            _record?.createNewFile()
                        }
                        else {
                            Log.d("ChatFragment", "File exist!")
                        }
                        if (_record?.exists() == true){
                            // test here
                            Log.d("ChatFragment", "File exist!")
                            recorder = MediaRecorder()
                            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                            recorder?.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)//         THREE_GPP: 3gp  --default: mp3
                            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                            recorder?.setOutputFile(Uri.fromFile( _record).path)
                            recorder?.prepare()
                            // end test
                            //startRecording()
                            recorder?.start()
                        }
                    } catch (e: IllegalStateException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    imageRecord.setImageResource(R.drawable.ic_recording_enable)
                    inputMethodManager!!.hideSoftInputFromWindow(requireView().windowToken, 0)
                    editInputMess.clearFocus()
                    imageSticker.setImageResource(R.drawable.sticker)
                    ImagePhotoChat.setImageResource(R.drawable.photo_chat)
                    isRecording = true
                } else {
                    requestPermissions()
                }
            } else {
                if (_record?.exists() == true) stopRecording()
                editInputMess.requestFocus()
                inputMethodManager!!.showSoftInput(editInputMess, InputMethodManager.SHOW_IMPLICIT)
                imageSticker.setImageResource(R.drawable.sticker)
                ImagePhotoChat.setImageResource(R.drawable.photo_chat)
                imageRecord.setImageResource(R.drawable.ic_record)
                isRecording = false
            }

        }
    }

    override fun onPause() {
        super.onPause()
        databaseReference?.removeEventListener(listener!!)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            Log.d("Show Uri in fragment", imageUri.toString())
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                upLoadImage()
                uploadRecord()
            }
        }
    }

    private fun checkPermission(): Boolean {
        //requestPermissions()
        val result1 = ContextCompat.checkSelfPermission(
            requireActivity(),
            RECORD_AUDIO
        )
        val result2 = ContextCompat.checkSelfPermission(
            requireActivity(),
            WRITE_EXTERNAL_STORAGE
        )
        val result3 = ContextCompat.checkSelfPermission(
            requireActivity(),
            READ_EXTERNAL_STORAGE
        )
        return result1 == PackageManager.PERMISSION_GRANTED
                && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(){
        val permissionsRequired = mutableListOf<String>()

        val hasRecordPermission = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        if (!hasRecordPermission){
            permissionsRequired.add(Manifest.permission.RECORD_AUDIO)
        }

        val hasStoragePermission = ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!hasStoragePermission){
            permissionsRequired.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsRequired.isNotEmpty()){
            ActivityCompat.requestPermissions(requireActivity(), permissionsRequired.toTypedArray(),1)
        }
    }

//    private fun requestPermission() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(WRITE_EXTERNAL_STORAGE, RECORD_AUDIO),
//            requestPermissionCode
//        )
//    }

    private fun startRecording() {
        try {
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)//         THREE_GPP: 3gp  --default: mp3
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder?.setOutputFile(mFileName)


        } catch (e: IllegalStateException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopRecording() {
        try {
            recorder?.stop()
            recorder?.reset()
            recorder?.release()
            recorder = null
        }catch (e: IOException){
            Log.d("error", "error")
        }
        uploadRecord()
    }

    private fun uploadRecord() {
        val uri: Uri = Uri.fromFile(_record)
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Send...")
        progressDialog.show()
        val filePath = storageReference.child(
            System.currentTimeMillis()
                .toString() + "." + _record?.extension
        )
        uploadTask = filePath.putFile(uri)
        (uploadTask as UploadTask).continueWithTask(
            Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val dowloadUri = task.result
                val mUri = dowloadUri.toString()
//                uriImage = mUri
                chatViewModel.sendMessage(id, mUri, "Record")
                progressDialog.dismiss()
            } else {
                Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }
//        filePath.putFile(uri).addOnSuccessListener {
//            chatViewModel.sendMessage(id, filePath.toString(), "Record")
//            progressDialog.dismiss()
//        }
    }

    private fun removeFragment() {
        inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
        listener?.let { databaseReference?.removeEventListener(it) }
        val fragment = parentFragmentManager.findFragmentById(R.id.frameLayoutChat)
        val fragmentTransaction = parentFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.exit_left, R.anim.pop_exit_left)
        if (BuildConfig.DEBUG && fragment == null) {
            error("Assertion failed")
        }
        fragmentTransaction.remove(fragment!!)
        parentFragmentManager.popBackStack()
        fragmentTransaction.commit()
    }

    private fun getImage() {
        imageSticker.setImageResource(R.drawable.sticker)
        if (recyclerImage.visibility == View.VISIBLE) {
            editInputMess.requestFocus()
            recyclerImage.visibility = View.GONE
            recyclerSticker.visibility = View.GONE
            ImagePhotoChat.setImageResource(R.drawable.photo_chat)
            inputMethodManager?.showSoftInput(
                editInputMess,
                InputMethodManager.SHOW_IMPLICIT
            )
        } else {
            inputMethodManager?.hideSoftInputFromWindow(requireView().windowToken, 0)
            editInputMess.clearFocus()
            recyclerImage.visibility = View.VISIBLE
            recyclerSticker.visibility = View.GONE
            ImagePhotoChat.setImageResource(R.drawable.ic_photo_blue)
        }
    }

    private fun checkSeen(id: String) {
        val myID = FirebaseAuth.getInstance().currentUser?.uid
        databaseReference = chatViewModel.checkSeen(id)
        listener = databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                for (keyOne in snapshot.children) {
                    val messModel = keyOne.getValue(MessageModel::class.java)
                    if (BuildConfig.DEBUG && messModel == null) {
                        error("Assertion failed")
                    }
                    if (messModel?.getIdReceiver() == myID && messModel?.getIdSender() == id) {
                        val hashMap = HashMap<String, Any>()
                        hashMap["checkSeen"] = true
                        keyOne.ref.updateChildren(hashMap)
                    }

                }
            }

        })
    }

    private fun getAllShownImagesPath(activity: Activity): ArrayList<String> {
        val uri: Uri
        val cursor: Cursor
        val colum_index_data: Int
        val colum_index_folder_name: Int
        val listAllImage: ArrayList<String> = ArrayList()
        var absolutePathOfImage: String? = null
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            txtNotAllowPer.visibility = View.VISIBLE
        } else {
            txtNotAllowPer.visibility = View.GONE
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            cursor = activity.contentResolver.query(uri, projection, null, null, null)!!
            colum_index_data = cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            colum_index_folder_name =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                absolutePathOfImage = cursor.getString(colum_index_data)
                listAllImage.add(absolutePathOfImage)
            }
        }
        return listAllImage
    }

    //lay duong dan
    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    fun upLoadImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Sending")
        progressDialog.show()
        if (imageUri != null) {
            val fileReference = storageReference.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri!!)
            )
            uploadTask = fileReference.putFile(imageUri!!)
            (uploadTask as UploadTask).continueWithTask(
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            it
                        }
                    }
                    return@Continuation fileReference.downloadUrl
                }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dowloadUri = task.result
                    val mUri = dowloadUri.toString()
                    uriImage = mUri
                    chatViewModel.sendMessage(id, mUri, "Image")
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
