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

    var imageForScalingString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageForScalingString = intent.getStringExtra("image")
        imageForScaling.setImageURI(Uri.parse(imageForScalingString))
    }

    private fun doSmaller(n : Int) {
        val imageBitmap : Bitmap = getBitmap(this.contentResolver, Uri.parse(imageForScalingString))
        val width = imageBitmap.width
        val height = imageBitmap.height
        val startPixels = IntArray(width*height)
        val endPixels = IntArray( (width*height/(n*n)))
        imageBitmap.getPixels(startPixels, 0, width, 0 , 0, width, height)
        var k = 0
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
        val newBitmap : Bitmap = Bitmap.createBitmap(width/n, height/n, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(endPixels, 0, width/n, 0, 0, width/n, height/n)
        imageForScaling.setImageBitmap(newBitmap)
    }

    private fun doNothing() {
        imageForScalingString = intent.getStringExtra("image")
        val imageBitmap : Bitmap = getBitmap(this.contentResolver, Uri.parse(imageForScalingString))
        imageForScaling.setImageBitmap(imageBitmap)
    }

    private fun doBigger(n : Int) {
        val imageBitmap : Bitmap = getBitmap(this.contentResolver, Uri.parse(imageForScalingString))
        var width = imageBitmap.width
        var height = imageBitmap.height
        val startPixels = IntArray(width*height)
        val endPixels = IntArray( (width*height*(n*n)))
        imageBitmap.getPixels(startPixels, 0, width, 0 , 0, width, height)
        width *= n
        height *= n
        val m : Int = width / n
        var k = 0
        while (k < startPixels.size) {
            val forInt : Int = k / m
            val firstIt = width * n * forInt + (k % m) * n
            for (i in 0 until n) {
                for (j in 0 until n) {
                    endPixels[i*width+j+firstIt] = startPixels[k]
                }
            }
            k += 1
        }
        val newBitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(endPixels, 0, width, 0, 0, width, height)
        imageForScaling.setImageBitmap(newBitmap)
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonScale -> {
                when (seekBarScaling.progress) {
                    0 -> doSmaller(16)
                    1 -> doSmaller(8)
                    2 -> doSmaller(4)
                    3 -> doSmaller(2)
                    4 -> doNothing()
                    5 -> doBigger(2)
                    6 -> doBigger(4)
                    7 -> doBigger(8)
                }

            }
        }
    }
}
