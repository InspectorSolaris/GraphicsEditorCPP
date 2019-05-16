package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import kotlinx.android.synthetic.main.activity_filters.*
import kotlinx.android.synthetic.main.activity_scaling.*

class FiltersActivity : AppCompatActivity() {

    var imageForFiltersString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filters)

        imageForFiltersString = intent.getStringExtra("image")
        imageForFilters.setImageURI(Uri.parse(imageForFiltersString))
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonF1 -> {
                val imageBitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(imageForFiltersString))
                grayscale(imageBitmap)
            }
        }
    }

    private fun grayscale(
        imageBitmap: Bitmap?
    ) {
        var newBitmap: Bitmap? = null
        if (imageBitmap != null) {
            val width = imageBitmap.width
            val height = imageBitmap.height
            newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            val srcPixels = IntArray(width * height)
            val newPixels = IntArray(width * height)
            imageBitmap.getPixels(srcPixels, 0, width, 0, 0, width, height)

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
    }
}


