package com.example.mvvmfirst.views.content.profile

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.mvvmfirst.R
import com.example.mvvmfirst.language.LocalHelper
import com.example.mvvmfirst.views.content.message.ZoomImageFragment
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*

class ProfileFragment : Fragment() {

    var myLocale: Locale? = null
    private var image: String? = null
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        profileViewModel = ViewModelProviders.of(requireActivity())
            .get<ProfileViewModel>(ProfileViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImageButtonEditProfile.setOnClickListener {
            replaceFragment()
        }
        RelativeLayoutLogout.setOnClickListener {
            val dialog = DialogLogoutFragment()
            dialog.show(parentFragmentManager, null)
        }
        CircleImageViewUser.setOnClickListener {
            val fragmentTransaction =
                parentFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            val zoomImageFragment = ZoomImageFragment()
            val bundle = Bundle()
            bundle.putString("image", image)
            zoomImageFragment.arguments = bundle
            fragmentTransaction.add(R.id.frameLayoutChat, zoomImageFragment, null).commit()
            fragmentTransaction.addToBackStack(null)
        }
        ImageViewImageUser.setOnClickListener {
            val fragmentTransaction =
                parentFragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            val zoomImageFragment = ZoomImageFragment()
            val bundle = Bundle()
            bundle.putString("image", image)
            zoomImageFragment.arguments = bundle
            fragmentTransaction.add(R.id.frameLayoutChat, zoomImageFragment, null).commit()
            fragmentTransaction.addToBackStack(null)
        }
        imageButtonChangeLanguage.setOnClickListener { showMenu()}
        TextViewLanguage.setOnClickListener{showMenu()}
        profileViewModel.getInfoUser()
        profileViewModel.userMutableLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {
                TextViewNameUser.text = it.getUserName()
                TextViewEmailUser.text = it.getUserEmail()
                image = it.getUserImgUrl()
                if (it.getUserImgUrl().equals("default")) {
                    ImageViewImageUser.setImageResource(R.mipmap.ic_launcher)
                    CircleImageViewUser.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(requireContext()).load(it.getUserImgUrl())
                        .into(ImageViewImageUser)
                    Glide.with(requireContext()).load(it.getUserImgUrl()).circleCrop()
                        .into(CircleImageViewUser)
                }
            })
    }

    private fun replaceFragment() {
        val fragmentTransaction =
            parentFragmentManager.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        var fragmentEditProfile =
            parentFragmentManager.findFragmentByTag("fragmentEditProfile")
        if (fragmentEditProfile == null) {
            fragmentEditProfile = EditProfileFragment()
            fragmentTransaction.add(
                R.id.frameLayoutChat,
                fragmentEditProfile,
                "fragmentEditProfile"
            )
        } else {
            fragmentTransaction.replace(
                R.id.frameLayoutChat,
                fragmentEditProfile,
                "fragmentEditProfile"
            )
        }
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun showMenu() {
        val popupMenu =
            PopupMenu(activity, imageButtonChangeLanguage)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_language, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.language_en ->{
                    if(TextViewLanguage.text != "English"){
                        myLocale =Locale("en", "US")
                        Toast.makeText(context, "english", Toast.LENGTH_LONG).show()
                        onChangeLanguage("en")
                        TextViewLanguage.setText(R.string.txt_language_en);
                    }
                }
                R.id.language_vi ->{
                    if(TextViewLanguage.text != "Tiếng Việt"){
                        myLocale = Locale("vi", "VN")
                        Toast.makeText(context, "tieng viet", Toast.LENGTH_LONG).show()
                        onChangeLanguage("vi")
                        TextViewLanguage.setText(R.string.txt_language_vi)
                    }
                }
            }
            return@setOnMenuItemClickListener true

        }
        popupMenu.show()


    }
    private fun onChangeLanguage(language: String?) {
        context?.let { LocalHelper.onReAttach(it, language) }
//        val context1 = context?.let { LocalHelper.setLocale(it, language) }
//        val resources = context1?.resources
        requireActivity().finish()
        val intent = requireActivity().intent
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().overridePendingTransition(R.anim.nope, R.anim.nope)
    }
}
