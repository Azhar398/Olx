package com.app.olxapplication.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ScaleGestureDetector
import android.view.View
import com.app.olxapplication.BaseActivity
import com.app.olxapplication.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_preview_image.*

class PreviewImageActivity : BaseActivity() {

    private var mScaleGestureDetector : ScaleGestureDetector?=null
    private var mScaleFactor=1.0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_image)

        val imagePath = intent.extras
        if (imagePath?.containsKey("imageUri")!!){
            val imageUri = imagePath?.getString("imageUri")
            val imageBitmap = BitmapFactory.decodeFile(imageUri)
            imageView.setImageBitmap(imageBitmap)
        }else{
            val imageUrl = imagePath?.getString("imageUrl")
            Glide.with(this).load(imageUrl)
                .placeholder(R.drawable.big_placeholder)
                .into(imageView)
        }

        imageView.scaleX = mScaleFactor
        imageView.scaleY = mScaleFactor

        mScaleGestureDetector = ScaleGestureDetector(this,ScaleListener())

        btnClose.setOnClickListener(View.OnClickListener {
            finish()
        })
    }

 private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
     override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
         mScaleFactor*= detector?.scaleFactor!!
         mScaleFactor=Math.max(0.1f,Math.min(mScaleFactor,10.0f))
         imageView.scaleX=mScaleFactor
         imageView.scaleY=mScaleFactor
         return true
     }


 }
}

