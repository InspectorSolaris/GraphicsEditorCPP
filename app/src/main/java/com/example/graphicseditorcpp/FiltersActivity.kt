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

    private var imageChanged = false

    private var imageForFiltersString: String? = null
    private var imageFilteredBitmap: Bitmap? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        imageForFiltersString = intent.getStringExtra("image")
        imageFilteredBitmap = BitmapFactory.decodeFile(imageForFiltersString)
        imageForFilters.setImageURI(Uri.parse(imageForFiltersString))
    }

    private fun tryCopyImageFile(){
        if(imageFilteredBitmap != null) {
            val imageLocal = imageFilteredBitmap
            imageLocal?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageForFiltersString))
        }
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
                tryCopyImageFile()
                setResult(Activity.RESULT_OK, Intent().putExtra("changed", imageChanged))
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
        }
    }

    private fun runFilter(
        filter: Int
    ) {
        val imageLocal = Bitmap.createBitmap(imageFilteredBitmap!!.width, imageFilteredBitmap!!.height, Bitmap.Config.ARGB_8888)
        imageColorcorrection(
            imageFilteredBitmap!!,
            imageLocal,
            filter
        )
        imageFilteredBitmap = imageLocal
    }
}


