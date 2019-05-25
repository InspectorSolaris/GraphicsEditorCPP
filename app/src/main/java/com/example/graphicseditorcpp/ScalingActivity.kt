package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.net.Uri
import kotlinx.android.synthetic.main.activity_scaling.*
import android.widget.SeekBar
import android.widget.Toast
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.pow

class ScalingActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scaling)

        imageOriginalString = intent.getStringExtra(getString(R.string.code_image_original))
        imageChangedString = intent.getStringExtra(getString(R.string.code_image_changed))

        imageForScaling.setImageURI(Uri.parse(imageOriginalString))

        seekBarScaling.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                buttonScale.text = 2.0.pow(progress - 4).toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // empty fun
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // empty fun
            }
        })
    }

    private fun resizeBilinear(
        pixels: IntArray,
        w: Int,
        h: Int,
        w2: Int,
        h2: Int
    ): IntArray {
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
        val imageBitmap : Bitmap = BitmapFactory.decodeFile(imageOriginalString)
        val width = imageBitmap.width
        val height = imageBitmap.height

        val new_w: Long = extent * width.toLong()
        val new_h: Long = extent * height.toLong()

        if(new_w * new_h < 16000000) {
            val pixelsArray = IntArray(width * height)
            imageBitmap.getPixels(pixelsArray, 0, width, 0, 0, width, height)
            if (side) {
                val newBitmap: Bitmap = Bitmap.createBitmap(width * extent, height * extent, Bitmap.Config.ARGB_8888)
                newBitmap.setPixels(
                    resizeBilinear(pixelsArray, width, height, width * extent, height * extent),
                    0, width * extent, 0, 0, width * extent, height * extent
                )

                newBitmap.compress(
                    (application as GlobalVal).bitmapCompressFormat,
                    (application as GlobalVal).bitmapCompressQuality,
                    FileOutputStream(imageChangedString)
                )
            } else {
                val newBitmap: Bitmap = Bitmap.createBitmap(width / extent, height / extent, Bitmap.Config.ARGB_8888)
                newBitmap.setPixels(
                    resizeBilinear(pixelsArray, width, height, width / extent, height / extent),
                    0, width / extent, 0, 0, width / extent, height / extent
                )

                newBitmap.compress(
                    (application as GlobalVal).bitmapCompressFormat,
                    (application as GlobalVal).bitmapCompressQuality,
                    FileOutputStream(imageChangedString)
                )
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, getString(R.string.error_image_to_large), Toast.LENGTH_LONG).show()
            }
        }
    }

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
            R.id.buttonScale -> {
                progressBarScaling.visibility = View.VISIBLE
                Thread {
                    with(seekBarScaling) {
                        scale(progress > 4, 2.0.pow(abs(progress - 4)).toInt())
                    }

                    runOnUiThread {
                        imageForScaling.setImageBitmap(BitmapFactory.decodeFile(imageChangedString))
                        imageIsChangedBool = seekBarScaling.progress != 4
                        progressBarScaling.visibility = View.GONE
                    }
                }.start()
            }
        }
    }
}
