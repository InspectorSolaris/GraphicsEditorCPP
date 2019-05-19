package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import android.provider.MediaStore.Images.Media.getBitmap
import kotlinx.android.synthetic.main.activity_scaling.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener

class ScalingActivity : AppCompatActivity() {

    private var imageForScalingPath: String? = null


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        seekBarScaling.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                var scaleNum = ""
                when (progress)  {
                    0 -> scaleNum = "*0,0625"
                    1 -> scaleNum = "*0.125"
                    2 -> scaleNum = "*0.25"
                    3 -> scaleNum = "*0.5"
                    4 -> scaleNum = "*1"
                    5 -> scaleNum = "*2"
                    6 -> scaleNum = "*4"
                    7 -> scaleNum = "*8"
                    8 -> scaleNum  ="*16"
                }
                buttonScale.text = scaleNum
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        imageForScalingPath = intent.getStringExtra("image")
        imageForScaling.setImageURI(Uri.parse(imageForScalingPath))
    }


    private fun resizeBilinear(pixels: IntArray, w: Int, h: Int, w2: Int, h2: Int): IntArray {
        //code from http://tech-algorithm.com/articles/bilinear-image-scaling/

        val temp = IntArray(w2 * h2)
        var a: Int
        var b: Int
        var c: Int
        var d: Int
        var x: Int
        var y: Int
        var index: Int
        val xRatio = (w - 1).toFloat() / w2
        val yRatio = (h - 1).toFloat() / h2
        var xDiff: Float
        var yDiff: Float
        var blue: Float
        var red: Float
        var green: Float
        var alpha: Float
        var offset = 0
        for (i in 0 until h2) {
            for (j in 0 until w2) {
                x = (xRatio * j).toInt()
                y = (yRatio * i).toInt()
                xDiff = xRatio * j - x
                yDiff = yRatio * i - y
                index = y * w + x
                a = pixels[index]
                b = pixels[index + 1]
                c = pixels[index + w]
                d = pixels[index + w + 1]
                    // Yb = Ab(1-w)(1-h) + Bb(w)(1-h) + Cb(h)(1-w) + Db(wh)
                    blue =
                        Color.blue(a).toFloat() * (1 - xDiff) * (1 - yDiff) + Color.blue(b).toFloat() * xDiff * (1 - yDiff) +
                                Color.blue(c).toFloat() * yDiff * (1 - xDiff) + Color.blue(d).toFloat() * (xDiff * yDiff)
                    green =
                        Color.green(a).toFloat() * (1 - xDiff) * (1 - yDiff) + Color.green(b).toFloat() * xDiff * (1 - yDiff) +
                                Color.green(c).toFloat() * yDiff * (1 - xDiff) + Color.green(d).toFloat() * (xDiff * yDiff)

                    red =
                        Color.red(a).toFloat() * (1 - xDiff) * (1 - yDiff) + Color.red(b).toFloat() * xDiff * (1 - yDiff) +
                                Color.red(c).toFloat() * yDiff * (1 - xDiff) + Color.red(d).toFloat() * (xDiff * yDiff)
                    alpha =
                        Color.alpha(a).toFloat() * (1 - xDiff) * (1 - yDiff) + Color.alpha(b).toFloat() * xDiff * (1 - yDiff) +
                                Color.alpha(c).toFloat() * yDiff * (1 - xDiff) + Color.alpha(d).toFloat() * (xDiff * yDiff)


                    temp[offset] = Color.argb(alpha.toInt(), red.toInt(), green.toInt(), blue.toInt())
                offset += 1
            }
        }
        return temp
    }

    private fun scale(side : Boolean, extent: Int) {
        val imageBitmap : Bitmap = BitmapFactory.decodeFile(imageForScalingPath)
        val width = imageBitmap.width
        val height = imageBitmap.height
        val pixelsArray = IntArray(width*height)
        imageBitmap.getPixels(pixelsArray, 0, width, 0 , 0, width, height)
        if (side) {
            val newBitmap : Bitmap = Bitmap.createBitmap(width*extent, height*extent, Bitmap.Config.ARGB_8888)
            newBitmap.setPixels(resizeBilinear(pixelsArray, width, height, width*extent, height*extent),
                0, width*extent, 0, 0, width*extent, height*extent)
            imageForScaling.setImageBitmap(newBitmap)
        }
        else {
            val newBitmap : Bitmap = Bitmap.createBitmap(width/extent, height/extent, Bitmap.Config.ARGB_8888)
            newBitmap.setPixels(resizeBilinear(pixelsArray, width, height, width/extent, height/extent),
                0, width/extent, 0, 0, width/extent, height/extent)
            imageForScaling.setImageBitmap(newBitmap)
        }
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
                    0 -> scale(false, 16)
                    1 -> scale(false, 8)
                    2 -> scale(false, 4)
                    3 -> scale(false, 2)
                    4 -> scale(false, 1)
                    5 -> scale(true, 2)
                    6 -> scale(true, 4)
                    7 -> scale(true, 8)
                    8 -> scale(true, 16)
                }

            }
        }
    }
}
