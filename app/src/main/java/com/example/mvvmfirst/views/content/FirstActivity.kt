package com.example.mvvmfirst.views.content

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.language.LocalHelper
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.example.mvvmfirst.views.content.login.LoginFragment
import com.example.mvvmfirst.views.content.login.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class FirstActivity : AppCompatActivity() {
//    private var onlineOrOffline: FriendViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first)
    val languageToLoad = LocalHelper.getLanguage(this)


    val locale = Locale(languageToLoad)
    Locale.setDefault(locale)
    val config = Configuration()
    config.locale = locale
    baseContext.resources.updateConfiguration(
        config,
        baseContext.resources.displayMetrics
    )

    LocalHelper.setBaseContex(baseContext)
        //check state user logined or not
//    val firebaseUser = FirebaseAuth.getInstance().currentUser
//    Log.d("nguyenha1", FirebaseAuth.getInstance().uid.toString())
//    if (firebaseUser?.uid != null) {
//        val intent = Intent(this@FirstActivity, MainActivity::class.java)
//        startActivity(intent)
//        finish()
//    } else {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.FrameLayout, LoginFragment(), "fragmentLogin").commit()
//    }
    }
        //an ban phim
    fun hideKeyBoard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
