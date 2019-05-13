package com.example.graphicseditorcpp

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import android.graphics.BitmapFactory

class ScalingActivity : AppCompatActivity() {


    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        val byteArray = intent.getByteArrayExtra("picture")
        val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        imageViewSplines.setImageBitmap(bmp)


    }
    fun processButtonPressing(
        view: View) {
        when (view.id) {
            R.id.imageButtonBack -> {
                val finishScalingIntent = Intent(this, MainActivity::class.java)
                startActivity(finishScalingIntent)
            }
        }
    }

}
