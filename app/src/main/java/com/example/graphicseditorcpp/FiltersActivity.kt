package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_filters.*
import java.io.FileOutputStream

class FiltersActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        imageOriginalString = intent.getStringExtra(getString(R.string.code_image_original))
        imageChangedString = intent.getStringExtra(getString(R.string.code_image_changed))

        imageForFilters.setImageURI(Uri.parse(imageOriginalString))
    }

    private external fun imageColorcorrection(
        orig: Bitmap,
        clrd: Bitmap,
        filter: Int
    )

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(
                        getString(R.string.code_image_is_changed),
                        imageIsChangedBool
                    )
                )
                finish()
            }
            R.id.buttonF1 -> {
                runFilter(1)
            }
            R.id.buttonF2 -> {
                runFilter(2)
            }
            R.id.buttonF3 -> {
                runFilter(3)
            }
            R.id.buttonF4 -> {
                runFilter(4)
            }
        }
    }

    private fun runFilter(
        filter: Int
    ) {
        val imageInfo = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageOriginalString, this)
        }
        val imageLocal = Bitmap.createBitmap(
            imageInfo.outWidth,
            imageInfo.outHeight,
            (application as GlobalVal).bitmapConfig
        )

        progressBarFilters.visibility = View.VISIBLE
        Thread {
            imageColorcorrection(
                BitmapFactory.decodeFile(imageOriginalString),
                imageLocal,
                filter
            )

            imageLocal.compress(
                (application as GlobalVal).bitmapCompressFormat,
                (application as GlobalVal).bitmapCompressQuality,
                FileOutputStream(imageChangedString)
            )

            runOnUiThread {
                imageForFilters.setImageBitmap(imageLocal)
                imageIsChangedBool = true
                progressBarFilters.visibility = View.GONE
            }
        }.start()
    }
}


