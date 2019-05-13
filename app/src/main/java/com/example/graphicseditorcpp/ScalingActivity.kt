package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.android.synthetic.main.activity_scaling.*

class ScalingActivity : AppCompatActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        val temp = intent.getStringExtra("picture")
        val pic : Uri = Uri.parse(temp)
        imageForTurning.setImageURI(pic)
    }

    fun processButtonPressing(
        view: View) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
        }
    }
}
