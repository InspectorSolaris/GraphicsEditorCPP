package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import kotlinx.android.synthetic.main.activity_scaling.*

class ScalingActivity : AppCompatActivity() {

    var imageForScalingPath: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageForScalingPath = intent.getStringExtra("image")
        imageForTurning.setImageURI(Uri.parse(imageForScalingPath))
    }

    fun doSmaller(n : Int) {
        val imageBitmap : Bitmap = getBitmap(this.contentResolver, Uri.parse(imageForScalingPath))
        val width = imageBitmap.width
        val height = imageBitmap.height
        val startPixels = IntArray(width*height)
        val endPixels = IntArray( (width*height/(n*n)))
        imageBitmap.getPixels(startPixels, 0, width, 0 , 0, width, height)
        var k = 0
        val newBitmap : Bitmap = Bitmap.createBitmap(width/n, height/n, Bitmap.Config.ARGB_8888)
        for (i in 0 until height-n+1 step n) {
            for (j in 0 until width-n+1 step n) {
                val red = (Color.red(startPixels[i*width+j]) + Color.red(startPixels[(i+1)*width+j]) +
                        Color.red(startPixels[i*width+j+1]) + Color.red(startPixels[(i+1)*width+j+1])) / 4
                val green = (Color.green(startPixels[i*width+j]) + Color.green(startPixels[(i+1)*width+j]) +
                        Color.green(startPixels[i*width+j+1]) + Color.green(startPixels[(i+1)*width+j+1])) / 4
                val blue = (Color.blue(startPixels[i*width+j]) + Color.blue(startPixels[(i+1)*width+j]) +
                        Color.blue(startPixels[i*width+j+1]) + Color.blue(startPixels[(i+1)*width+j+1])) / 4
                val alpha = (Color.alpha(startPixels[i*width+j]) + Color.alpha(startPixels[(i+1)*width+j]) +
                        Color.alpha(startPixels[i*width+j+1]) + Color.alpha(startPixels[(i+1)*width+j+1])) / 4
                endPixels[k] = Color.argb(alpha, red, green, blue)
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
                doSmaller(16)
            }
        }
    }
}
