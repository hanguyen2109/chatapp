package com.example.mvvmfirst.views.content

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.example.mvvmfirst.R
import com.example.mvvmfirst.language.LocalHelper
import com.example.mvvmfirst.views.content.friend.FriendViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {
    private var onlineOrOffline: FriendViewModel? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        onlineOrOffline = ViewModelProviders.of(this).get<FriendViewModel>(FriendViewModel::class.java)
        onlineOrOffline?.updateStatusUser("status", "online")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("nguyenha", FirebaseAuth.getInstance().uid.toString())
        if (firebaseUser?.uid== null) {
            val intent = Intent(this@MainActivity, FirstActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val fragmentSupportTransaction = supportFragmentManager.beginTransaction()
            fragmentSupportTransaction.add(R.id.frameLayoutChat, MainFragment(), "mainFragment")
                .commit()
        }
//        //check permission
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions(permission, 10)
        }
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
    }

    override fun onPause() {
        super.onPause()
        onlineOrOffline?.updateStatusUser("status", "offline")
    }

    fun hideKeyBoard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
