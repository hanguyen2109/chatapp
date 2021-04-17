//package com.example.mvvmfirst.utils
//
//import android.widget.ImageView
//import com.bumptech.glide.Glide
//import com.example.mvvmfirst.R
//
//fun ImageView.loadImage(image: String) {
//    Glide.with(context).load(image)
//        .error(R.drawable.ic_broken_image)
//        .placeholder(R.drawable.ic_place_holder)
//        .into(this)
//}
//
//fun ImageView.loadImageCircle(image: String) {
//    Glide.with(context).load(image)
//        .error(R.drawable.ic_broken_image)
//        .circleCrop()
//        .placeholder(R.drawable.ic_place_holder)
//        .into(this)
//}
