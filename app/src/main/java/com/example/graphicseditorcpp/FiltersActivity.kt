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
                grayscale()
            }
        }
    }

    private fun grayscale() {
        val imageLocal = imageFilteredBitmap
        var newBitmap: Bitmap? = null
        if (imageLocal != null) {
            val width = imageLocal.width
            val height = imageLocal.height
            newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val srcPixels = IntArray(width * height)
            val newPixels = IntArray(width * height)
            imageLocal.getPixels(srcPixels, 0, width, 0, 0, width, height)

            for (i in 0 until srcPixels.size) {
                val p: Long = srcPixels[i].toLong()
                val r: Long = ((p and 0x00FF0000) shr 16)
                val g: Long = ((p and 0x0000FF00) shr 8)
                val b: Long = ((p and 0x000000FF) shr 0)

                val color = (r + g + b) / 3
                val newPixel = 0xFF000000 or (color shl 16) or (color shl 8) or (color shl 0)

                newPixels[i] = newPixel.toInt()
            }

            newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        }

        imageForFilters.setImageBitmap(newBitmap)
        imageFilteredBitmap = newBitmap
        imageChanged = true
    }
}


