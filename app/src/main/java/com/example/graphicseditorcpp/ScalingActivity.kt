package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import kotlinx.android.synthetic.main.activity_scaling.*
import kotlinx.android.synthetic.main.activity_scaling.imageForTurning
import kotlinx.android.synthetic.main.activity_turning.*

class ScalingActivity : AppCompatActivity() {

    var imageForScalingPath: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageForScalingPath = intent.getStringExtra("image")
        imageForTurning.setImageURI(Uri.parse(imageForScalingPath))
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
        }
    }
}
