package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import kotlin.math.max
import kotlin.math.min

class SplinesActivity : AppCompatActivity() {

    private val squareSize = 10

    private var nSize = 1200
    private var mSize = 1500
    private val bitmap : Bitmap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    private var pointX : ArrayList<Int> = arrayListOf(-1)
    private var pointY : ArrayList<Int> = arrayListOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splines)

        bitmap.eraseColor(getColor(R.color.splinesColorBackground))

        imageViewSplines.setImageBitmap(bitmap)
        imageViewSplines.layoutParams.width = nSize
        imageViewSplines.layoutParams.height = mSize

        imageViewSplines.setOnTouchListener { _, motionEvent ->

            drawSquare(motionEvent.x.toInt(), motionEvent.y.toInt())
            pointX.add(motionEvent.x.toInt())
            pointY.add(motionEvent.y.toInt())

            imageViewSplines.setImageBitmap(bitmap)

            true
        }
    }

    private fun drawSquare(
        x: Int,
        y: Int) {
        for(i in (-squareSize / 2)..(+squareSize / 2)) {
            for(j in (-squareSize / 2)..(+squareSize / 2)) {
                val px = max(min(x + i, nSize), 0)
                val py = max(min(y + j, mSize), 0)

                bitmap.setPixel(px, py, getColor(R.color.splinesColorPoint))
            }
        }
    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
        }
    }
}
