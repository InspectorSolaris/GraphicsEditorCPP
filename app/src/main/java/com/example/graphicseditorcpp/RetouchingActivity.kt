package com.example.graphicseditorcpp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_retouching.*

class RetouchingActivity : AppCompatActivity() {

    var imageForRetouchingString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        imageForRetouchingString = intent.getStringExtra("image")
        imageForRetouching.setImageURI(Uri.parse(imageForRetouchingString))
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
