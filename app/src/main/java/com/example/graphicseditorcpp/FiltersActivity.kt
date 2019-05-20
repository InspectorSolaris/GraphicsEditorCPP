package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_filters.*

class FiltersActivity : AppCompatActivity() {

    private var imageForFiltersString: String? = null

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
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(imageForFiltersString)
                grayscale(imageBitmap)
            }
            R.id.buttonF2 -> {
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(imageForFiltersString)
                sepia(imageBitmap)
            }
            R.id.buttonF3 -> {
                val imageBitmap: Bitmap = BitmapFactory.decodeFile(imageForFiltersString)
                negative(imageBitmap)
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

    private fun sepia(
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
                var r: Long = ((p and 0x00FF0000) shr 16)
                var g: Long = ((p and 0x0000FF00) shr 8)
                var b: Long = ((p and 0x000000FF) shr 0)

                val tr: Double = (0.393 * r + 0.769 * g + 0.189 * b)
                val tg: Double = (0.349 * r + 0.686 * g + 0.168 * b)
                val tb: Double = (0.272 * r + 0.534 * g + 0.131 * b)

                r = if(tr>255) 255 else tr.toLong()
                g = if(tg>255) 255 else tg.toLong()
                b = if(tb>255) 255 else tb.toLong()

                val newPixel = 0xFF000000 or (r shl 16) or (g shl 8) or b

                newPixels[i] = newPixel.toInt()
            }

            newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        }
        imageForFilters.setImageBitmap(newBitmap)
    }

    private fun negative(
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
                var r: Long = ((p and 0x00FF0000) shr 16)
                var g: Long = ((p and 0x0000FF00) shr 8)
                var b: Long = ((p and 0x000000FF) shr 0)


                r = 255 - r
                g = 255 - g
                b = 255 - b

                val newPixel = 0xFF000000 or (r shl 16) or (g shl 8) or b

                newPixels[i] = newPixel.toInt()
            }

            newBitmap.setPixels(newPixels, 0, width, 0, 0, width, height)
        }
        imageForFilters.setImageBitmap(newBitmap)
    }
}


