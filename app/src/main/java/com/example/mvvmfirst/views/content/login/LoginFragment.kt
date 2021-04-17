package com.example.mvvmfirst.views.content.login

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.views.content.MainActivity
import com.example.mvvmfirst.views.content.sign.SignFragment
import kotlinx.android.synthetic.main.fragment_loginn.*

class LoginFragment : Fragment() {
    var isLogin = false
    private lateinit var loginViewModel: LoginViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_loginn, container, false)
        loginViewModel = ViewModelProviders.of(requireActivity()).get<LoginViewModel>(
            LoginViewModel::class.java
        )
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //set button login
        buttonLogin.isEnabled = false
        //set color for substring of register
        val text = textRegister.text.toString()
        var index = text.indexOf('?')
        val textSpan = SpannableString(text)
        textSpan.setSpan(
            ForegroundColorSpan(Color.BLUE),
            ++index,
            textSpan.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textRegister.text = textSpan
        eventEditText()
        textRegister.setOnClickListener { replaceFragment() }
        buttonLogin.setOnClickListener {
//            progressLoading.visibility = View.VISIBLE
//            Thread.sleep(10000)
//            if (loginViewModel.checkEmailAndPassword(
//                    textDescriptionEmailLogin.text.toString(),
//                    textDescriptionPassLogin.text.toString()
//                )
//            ) {
            loginViewModel.loginFirebase(
                textDescriptionEmailLogin.text.toString(),
                textDescriptionPassLogin.text.toString()
            )
//                val intent = Intent(context, MainActivity::class.java)
//                startActivity(intent)
//                activity?.finish()
//            isLogin = true
//                Toast.makeText(context, "Valid email or password", Toast.LENGTH_SHORT).show()
//            } else {
//                textDescriptionEmailLogin.text = null
//                textDescriptionPassLogin.text = null
//                progressLoading.visibility = View.GONE
//                Toast.makeText(context, "Invalid email or password", Toast.LENGTH_SHORT).show()
//            }

        }
        loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginViewModel.LoginStatus.Loading -> {
                    progressLoading.visibility = View.VISIBLE
                }
                is LoginViewModel.LoginStatus.IsOk -> {
                    progressLoading.visibility = View.GONE
//                    Toast.makeText(context, "Login Success!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
                is LoginViewModel.LoginStatus.Failure -> {
                    Toast.makeText(context, "Login Failed!", Toast.LENGTH_SHORT).show()
                    textDescriptionEmailLogin.text = null
                    textDescriptionPassLogin.text = null
                    progressLoading.visibility = View.GONE

                }
                is LoginViewModel.LoginStatus.ErrorPassAndEmail -> {
                    if (it.isError) {
                        Toast.makeText(
                            context,
                            "Invalid Email Or Password! Login",
                            Toast.LENGTH_SHORT
                        ).show()
                        textDescriptionEmailLogin.text = null
                        textDescriptionPassLogin.text = null
                        progressLoading.visibility = View.GONE
                    }
                }
                is LoginViewModel.LoginStatus.Register -> {
                    replaceFragment()
                }
            }
        })


    }


    private fun eventEditText() {
        val editTexts = arrayOf(
            textDescriptionEmailLogin,
            textDescriptionPassLogin
        )
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(s: Editable) {}

                override fun beforeTextChanged(
                    s: CharSequence, start: Int,
                    count: Int, after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence, start: Int,
                    before: Int, count: Int
                ) {
                    setEnableButton()
                }
            })
        }
    }

    private fun replaceFragment() {
        textDescriptionEmailLogin.setText("")
        textDescriptionPassLogin.setText("")
        loginViewModel.resetStatus()

        val fragmentTransaction = parentFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)

        var fragmentSign = parentFragmentManager.findFragmentByTag("fragmentSign")
        if (fragmentSign == null) {
            fragmentSign = SignFragment()
        }
        fragmentTransaction.replace(R.id.FrameLayout, fragmentSign, "fragmentSign")
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    fun setEnableButton() {
        if (textDescriptionEmailLogin.text.toString()
                .isNotEmpty() && textDescriptionPassLogin.text.toString()
                .isNotEmpty()
        ) {
            buttonLogin.isEnabled = true
            buttonLogin.setBackgroundResource(R.drawable.custom_login_enable)
        } else {
            buttonLogin.isEnabled = false
            buttonLogin.setBackgroundResource(R.drawable.custom_login_denable)
        }
    }
}
