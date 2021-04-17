package com.example.mvvmfirst.views.content.profile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.DatePicker
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_editprofile.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {
    private val IMAGE_REQUEST = 1
    private var imageFilePath: File? = null
    private val REQUEST_IMAGE = 100
    var storageReference = FirebaseStorage.getInstance().getReference("profile")//user to upload image
    private var imageUri: Uri? = null
    var uriImage: String? = null
    private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
    var image: String? = null
    lateinit var editProfileViewModel: EditProfileViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_editprofile, container, false)
        editProfileViewModel = ViewModelProviders.of(requireActivity())
            .get<EditProfileViewModel>(EditProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImageViewBack.setOnClickListener { removeFragment() }
        ImageButtonCamera.setOnClickListener { showMenu() }
        ButtonSave.setOnClickListener {
            if (!TextUtils.isEmpty(uriImage)) {
                editProfileViewModel.updateInfoUser("userImgUrl", uriImage)
            }
            var name: String = editTextNameProfile.text.toString().trim()
            var phone: String = editPhoneProfile.text.toString().trim()
            var date: String = editDateOfBirthProfile.text.toString().trim()
            if (TextUtils.isEmpty(name)) {
                name = "default"
            }
            if (TextUtils.isEmpty(phone)) {
                phone = "default"
            }
            if (TextUtils.isEmpty(date)) {
                date = "default"
            }
            if (editProfileViewModel.validatePhoneNumber(phone)!!) {
                editProfileViewModel.updateInfoUser("userName", name)
                editProfileViewModel.updateInfoUser("userPhone", phone)
                editProfileViewModel.updateInfoUser("userDateOfBirth", date)
                Toast.makeText(context, R.string.txt_save_profile_success, Toast.LENGTH_SHORT)
                    .show()
                removeFragmentSave()
            } else {
                Toast.makeText(
                    context,
                    R.string.txt_notification_phone_number,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        editDateOfBirthProfile.setOnClickListener { chooseDate() }
        editProfileViewModel.getInfoUser()
        editProfileViewModel.userMutableLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            editTextNameProfile.setText(it.getUserName())
            if (it.getUserPhone().equals("default")) {
                editPhoneProfile.setText("")
            } else {
                editPhoneProfile.setText(it.getUserPhone())
            }
            if (it.getUserDateOfBirth().equals("default")) {
                editDateOfBirthProfile.setText("")
            } else {
                editDateOfBirthProfile.setText(it.getUserDateOfBirth())
            }
            if (it.getUserImgUrl().equals("default")) {
                CircleImageUserEdit.setImageResource(R.mipmap.ic_launcher)
            } else {
                Glide.with(requireContext()).load(it.getUserImgUrl()).circleCrop()
                    .into(CircleImageUserEdit)
            }
            image = it.getUserImgUrl()
        })

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        } else if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = Uri.fromFile(imageFilePath)
            Log.d("Paths image", imageUri.toString())
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }

    private fun removeFragment() {
        val fragment =
            parentFragmentManager.findFragmentById(R.id.frameLayoutChat)
        val fragmentTransaction =
            parentFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.exit_left, R.anim.pop_exit_left)
        if (BuildConfig.DEBUG && fragment == null) {
            error("Assertion failed")
        }
        fragmentTransaction.remove(fragment!!)
        parentFragmentManager.popBackStack()
        fragmentTransaction.commit()
    }

    private fun removeFragmentSave() {
        val fragment =
            parentFragmentManager.findFragmentById(R.id.frameLayoutChat)
        val fragmentTransaction =
            parentFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        if (BuildConfig.DEBUG && fragment == null) {
            error("Assertion failed")
        }
        fragmentTransaction.remove(fragment!!)
        parentFragmentManager.popBackStack()
        fragmentTransaction.commit()
    }

    private fun openImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String? {
        val contentResolver = requireContext().contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage() {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading")
        progressDialog.show()
        if (imageUri != null) {
            val fileReference = storageReference.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri!!)
            )
            uploadTask = fileReference.putFile(imageUri!!)
            val urlTask = (uploadTask as UploadTask).continueWithTask(
                Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            it
                        }
                    }
                    return@Continuation fileReference.downloadUrl
                })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val mUri = downloadUri.toString()
                    uriImage = mUri
                    image = uriImage
                    Glide.with(requireContext()).load(mUri).circleCrop()
                        .into(CircleImageUserEdit)
                    progressDialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }
                progressDialog.dismiss()
            }
        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
//    private fun createImageFile(): File? {
//        // Create an image file name
//        @SuppressLint("SimpleDateFormat") val timeStamp =
//            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        val imageFileName = "JPEG_" + timeStamp + "_"
//        val storageDir =
//            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        var flie = File.createTempFile(
//            imageFileName,  /* prefix */
//            ".jpg",  /* suffix */
//            storageDir /* directory */
//        )
//        val currentPhotoPath = flie.absolutePath
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val f = File(currentPhotoPath)
//        val contentUri = Uri.fromFile(f)
//        mediaScanIntent.data = contentUri
//        this.sendBroadcast(mediaScanIntent)
//    }

    @SuppressLint("QueryPermissionsNeeded")
//    private fun takePhoto() {
////        if (checkSelfPermission(permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//
////        }
//        val pictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        if (pictureIntent.resolveActivity(requireActivity().packageManager) != null) {
//            var file: File? = null
//        }
//            try {
//                imageFilePath = createImageFile()
//            } catch (e: IOException) {
//                e.printStackTrace()
//                return
//            }
//            val photoUri = FileProvider.getUriForFile(
//                requireContext(),
//                requireContext().packageName.toString() + ".provider",
//                imageFilePath!!
//            )
//            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
//            startActivityForResult(pictureIntent, REQUEST_IMAGE)
/////        }
//    }
//    private fun galleryAddPic() {
//        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
//        val f = File(currentPhotoPath)
//        val contentUri = Uri.fromFile(f)
//        mediaScanIntent.data = contentUri
//        this.sendBroadcast(mediaScanIntent)
//    }

    private fun showMenu() {
        val popupMenu =
            PopupMenu(activity, ImageButtonCamera)
        popupMenu.menuInflater.inflate(R.menu.popmenu_profile_image, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
//                R.id.camera -> takePhoto()
                R.id.choosePhoto -> openImage()
            }
            true
        }
        popupMenu.show()
    }

    private  fun chooseDate(): Unit {
        val calendar = Calendar.getInstance()
        val day = calendar[Calendar.DATE]
        val month = calendar[Calendar.MONTH]
        val year = calendar[Calendar.YEAR]
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            OnDateSetListener { _: DatePicker?, i: Int, i1: Int, i2: Int ->
                calendar[i, i1] = i2
                @SuppressLint("SimpleDateFormat") val simpleDateFormat =
                    SimpleDateFormat("dd/MM/yyyy")
                editDateOfBirthProfile.setText(simpleDateFormat.format(calendar.time))
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = Date().time
        datePickerDialog.show()
    }
}
