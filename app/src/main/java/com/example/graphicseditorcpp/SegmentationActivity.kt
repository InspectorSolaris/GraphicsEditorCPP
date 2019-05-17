package com.example.graphicseditorcpp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_segmentation.*

class SegmentationActivity : AppCompatActivity() {

    var imageForSegmentationString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        imageForSegmentationString = intent.getStringExtra("image")
        imageForSegmentation.setImageURI(Uri.parse(imageForSegmentationString))
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
