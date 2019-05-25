package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_filters.*
import java.io.FileOutputStream

class FiltersActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    private var dialog: BottomSheetDialog? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        dialog = BottomSheetDialog(this)

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
                dialog!!.hide()
            }
            R.id.buttonF2 -> {
                runFilter(2)
                dialog!!.hide()
            }
            R.id.buttonF3 -> {
                runFilter(3)
                dialog!!.hide()
            }
            R.id.buttonF4 -> {
                runFilter(4)
                dialog!!.hide()
            }
            R.id.buttonF5 -> {
                runFilter(5)
                dialog!!.hide()
            }
            R.id.buttonF6 -> {
                runFilter(6)
                dialog!!.hide()
            }
            R.id.buttonF7 -> {
                runFilter(7)
                dialog!!.hide()
            }
            R.id.buttonF8 -> {
                runFilter(8)
                dialog!!.hide()
            }
            R.id.buttonF9 -> {
                runFilter(9)
                dialog!!.hide()
            }
            R.id.buttonFilters -> {
                dialog!!.setContentView(
                    layoutInflater.inflate(
                        R.layout.activity_recyclerview_item_row,
                        null as ViewGroup?
                    )
                )
                dialog!!.show()
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


