package com.example.mvvmfirst.views.content.message

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.mvvmfirst.BuildConfig
import com.example.mvvmfirst.R
import kotlinx.android.synthetic.main.fragment_zoom_image.*

class ZoomImageFragment: Fragment() {
    private var scaleGestureDetector: ScaleGestureDetector? = null
    var mScaleFactor = 1.0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_zoom_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        var image: String? = null
        if (bundle != null) {
            image = bundle.getString("image")
        }
        if (BuildConfig.DEBUG && image == null) {
            error("Assertion failed")
        }
        if (image == "default") {
            imageView_zoom.setImageResource(R.mipmap.ic_launcher)
        } else {
            Glide.with(requireActivity()).load(image).into(imageView_zoom)
        }
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        view.setOnTouchListener { _, event->
            scaleGestureDetector!!.onTouchEvent(event)
            return@setOnTouchListener true
        }
        imageButtonBackImageZoom.setOnClickListener { removeFragment() }
    }
    inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            mScaleFactor *=  scaleGestureDetector.scaleFactor
            mScaleFactor = 0.1f.coerceAtLeast(mScaleFactor.coerceAtMost(10.0f))
            imageView_zoom.scaleX = mScaleFactor
            imageView_zoom.scaleY = mScaleFactor
            return true
        }
    }
    private fun removeFragment() {
        val fragment = parentFragmentManager.findFragmentById(R.id.frameLayoutChat)
        val fragmentTransaction = parentFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        if (BuildConfig.DEBUG && fragment == null) {
            error("Assertion failed")
        }
        fragmentTransaction.remove(fragment!!)
        parentFragmentManager.popBackStack()
        fragmentTransaction.commit()
    }
}
