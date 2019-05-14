package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import kotlinx.android.synthetic.main.activity_scaling.*

class ScalingActivity : AppCompatActivity() {

    var imageForTurningPath: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageForTurningPath = intent.getStringExtra("image")
        imageForTurning.setImageURI(Uri.parse(imageForTurningPath))
    }

    fun scaling(n : Int) {
        val imageBitmap : Bitmap = getBitmap(this.contentResolver, Uri.parse(imageForTurningPath))
        val width = imageBitmap.width
        val height = imageBitmap.height
        val startPixels = IntArray(width*height)
        val endPixels = IntArray( (width*height/(n*n)))
        imageBitmap.getPixels(startPixels, 0, width, 0 , 0, width, height)
        var k = 0
        val newBitmap : Bitmap = Bitmap.createBitmap(width/n, height/n, Bitmap.Config.ARGB_8888)
        for (i in 0 until height-n+1 step n) {
            for (j in 0 until width-n+1 step n) {
                val v1 = startPixels[i*width+j]
                endPixels[k] = v1
                k += 1
            }
        }
        newBitmap.setPixels(endPixels, 0, width/n, 0, 0, width/n, height/n)
        imageForTurning.setImageBitmap(newBitmap)
    }
    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonScale -> {
                scaling(16)
            }
        }
    }
}
