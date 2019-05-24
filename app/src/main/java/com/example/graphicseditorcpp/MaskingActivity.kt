package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_masking.*

class MaskingActivity : AppCompatActivity() {

    private var imageChanged = false

    private var imageForMaskingString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masking)

        imageForMaskingString = intent.getStringExtra("image")
        imageForMasking.setImageURI(Uri.parse(imageForMaskingString))

        val imageBitmap : Bitmap = BitmapFactory.decodeFile(imageForMaskingString)
        val width = imageBitmap.width
        val height = imageBitmap.height
        val pixelsArray = IntArray(width*height)
        imageBitmap.getPixels(pixelsArray, 0, width, 0 , 0, width, height)
        val newBitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(gaussBlur(pixelsArray, width, height, 5), 0, width, 0, 0, width, height)
        imageForMasking.setImageBitmap(newBitmap)

    }

    private fun gaussBlur (scl : IntArray, w : Int, h : Int, r : Int) : IntArray {
        val rs  = Math.ceil(r * 2.57).toInt()
        val tcl = IntArray(w * h)
        for (i in 0 until h) {
            for (j in 0 until w) {
                var vall = 0.0
                var wsum = 0.0
                for (iy in (i - rs) until (i + rs + 1)) {
                    for (ix in (j - rs) until (j + rs + 1)) {
                        val x = Math.min(w - 1, Math.max(0, ix))
                        val y = Math.min(h - 1, Math.max(0, iy))
                        val dsq = ((ix - j) * (ix - j) + (iy - i) * (iy - i)).toDouble()
                        val wght = Math.exp(-dsq / (2 * r * r)) / (Math.PI * 2 * r * r)
                        vall += scl[y*w+x] * wght
                        wsum += wght
                    }
                }
                tcl[i * w + j] = (Math.round(vall / wsum)).toInt()
            }
        }
        return tcl
    }


    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                setResult(Activity.RESULT_OK, Intent().putExtra("changed", imageChanged))
                finish()
            }
        }
    }
}
