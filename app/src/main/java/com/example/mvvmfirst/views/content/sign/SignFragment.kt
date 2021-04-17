package com.example.mvvmfirst.views.content.sign

import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.language.LocalHelper
import com.example.mvvmfirst.views.content.login.LoginFragment
import com.example.mvvmfirst.views.content.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_sign.*


class SignFragment : Fragment() {
    lateinit var signViewModel: SignViewModel
    lateinit var loginViewModel1: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sign, container, false)
        signViewModel =
            ViewModelProviders.of(requireActivity()).get<SignViewModel>(SignViewModel::class.java)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity?.let { LocalHelper.getLanguage(it) } == "vi") {
            val htmlContentCheckBox =
                "   " + " Tôi đồng ý với các<font color=\"#4356B4\"> chính sách</font> và <font color=\"#4356B4\"> điều khoản</font>"
            textCheckboxSign.text = Html.fromHtml(
                htmlContentCheckBox
            )
        } else {
            val htmlContentCheckBox =
                "   " + " I agree with the <font color=\"#4356B4\"> policies</font> and <font color=\"#4356B4\"> terms</font>"
            textCheckboxSign.text = Html.fromHtml(
                htmlContentCheckBox
            )
        }
        //set button login
        buttonSign.isEnabled = false
        //set color for substring of register
        val text = textLoginNow.text.toString()
        var index = text.indexOf('?')
        val textSpan = SpannableString(text)
        textSpan.setSpan(
            ForegroundColorSpan(Color.BLUE),
            ++index,
            textSpan.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textLoginNow.text = textSpan
        eventTextAndCheckBox()
        textLoginNow.setOnClickListener { replaceFragment() }
        buttonSignBack.setOnClickListener { replaceFragment() }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loginViewModel1 = ViewModelProviders.of(requireActivity()).get<LoginViewModel>(
            LoginViewModel::class.java
        )
        super.onActivityCreated(savedInstanceState)
        buttonSign.setOnClickListener {
//            if (!loginViewModel1.checkEmailAndPassword(
//                    textDescriptionEmailSign.text.toString(),
//                    textDescriptionPassSign.text.toString()
//                )
//            ) {
                signViewModel.createUserFirebase(
                    textDescriptionNameSign.text.toString(),
                    textDescriptionEmailSign.text.toString(),
                    textDescriptionPassSign.text.toString())
//                Toast.makeText(context, "Valid email or password", Toast.LENGTH_SHORT).show()
//                )
//            } else {
                textDescriptionNameSign.text = null
                textDescriptionEmailSign.text = null
                textDescriptionPassSign.text = null
//                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
//            }
        }
    }

    fun replaceFragment() {
        textDescriptionNameSign.text = null
        textDescriptionEmailSign.text = null
        textDescriptionPassSign.text = null
        checkboxSign.isChecked = false
        signViewModel.resetStatus()
        val fragmentTransition = parentFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        FirebaseAuth.getInstance().signOut()
        var fragmentLogin = parentFragmentManager.findFragmentByTag("fragmentLogin")
        if (fragmentLogin == null) {
            fragmentLogin = LoginFragment()
        }
        fragmentTransition.replace(R.id.FrameLayout, fragmentLogin, "fragmentLogin")
        parentFragmentManager.popBackStack()
        fragmentTransition.commit()
    }

    fun eventTextAndCheckBox() {
        val editText =
            arrayOf(textDescriptionNameSign, textDescriptionEmailSign, textDescriptionPassSign)
        for (i in editText.indices) {
            editText[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    setButtonAndCheck()
                }

            })
        }

    }

    fun setButtonAndCheck() {
        if ( textDescriptionNameSign.text.toString()
                .isNotEmpty() && textDescriptionEmailSign.text.toString()
                .isNotEmpty() && textDescriptionPassSign.text.toString().isNotEmpty()
        ) {
            buttonSign.isEnabled = true
            buttonSign.setBackgroundResource(R.drawable.custom_login_enable)
        } else {
            buttonSign.isEnabled = false
            buttonSign.setBackgroundResource(R.drawable.custom_login_denable)
        }
    }

}