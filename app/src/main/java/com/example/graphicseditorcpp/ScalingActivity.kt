package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import kotlinx.android.synthetic.main.activity_scaling.*

class ScalingActivity : AppCompatActivity() {

    var imageForTurningPath: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageForTurningPath = intent.getStringExtra("picture")
        imageForTurning.setImageURI(Uri.parse(imageForTurningPath))
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
