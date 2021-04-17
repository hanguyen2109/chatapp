package com.example.mvvmfirst.views.content.login

import android.util.Patterns
import android.util.Patterns.EMAIL_ADDRESS
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mvvmfirst.services.repositories.LoginReponsitory
import java.util.regex.Pattern

class LoginViewModel : ViewModel() {
        var email = MutableLiveData<String>()
//    var password = MutableLiveData<String>()
    var loginReponsitory = LoginReponsitory
    var loginStatus = MutableLiveData<LoginStatus>()

    fun checkEmailAndPassword(email: String, password: String): Boolean {
//        val stringEmail =
//            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@" +
//                    "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
//                    "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." +
//                    "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" +
//                    "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|" +
//                    "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
//        val stringEmail = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
//                "\\@" +
//                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
//                "(" +
//                "\\." +
//                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
//                ")+"
        val stringPassword = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$"
//        val stringPassword =
//            "^" + "(?=.*[0-9]" + "(?=.*[a-z]" + "(?=.*[A-Z]" + "(?=.*[a-zA-Z]" + "(?=.*[@#$%^&+=]" + "(?=\\s+$" + ".{6,}" + "$"
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                Pattern.compile(stringPassword).matcher(password).matches()
    }

    fun loginFirebase(email: String, password: String) {
        loginStatus.value = LoginStatus.Loading(true)
        loginReponsitory.login(email, password)
            .addOnSuccessListener {
                loginStatus.value = LoginStatus.IsOk(true)
                loginStatus.value = LoginStatus.Loading(false)
            }
            .addOnFailureListener {
                loginStatus.value = LoginStatus.Failure(it)
            }
    }

    fun resetStatus() {
        loginStatus.value = LoginStatus.ErrorPassAndEmail(false)
    }

    sealed class LoginStatus {

        data class Loading(var loading: Boolean) : LoginStatus()

        data class IsOk(var isLogin: Boolean) : LoginStatus()

        data class Failure(var e: Throwable) : LoginStatus()

        data class ErrorPassAndEmail(var isError: Boolean) : LoginStatus()

        data class Register(var isRegister: Boolean) : LoginStatus()
    }
}

