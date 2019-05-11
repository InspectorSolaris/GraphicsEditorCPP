package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import kotlin.math.max
import kotlin.math.min

class SplinesActivity : AppCompatActivity() {

    private val colorBackgorund = Color.WHITE
    private val colorPoints = Color.BLACK
    private val colorLines = Color.BLACK

    private val squraeSize = 10

    private val bitmapWidth = 600
    private val bitmapHeight = 600
    private val bitmap : Bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)

    private var pointX : ArrayList<Int> = arrayListOf(-1)
    private var pointY : ArrayList<Int> = arrayListOf(-1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splines)

        bitmap.eraseColor(colorBackgorund)
        imageViewSplines.setImageBitmap(bitmap)
        imageViewSplines.layoutParams.width = bitmapWidth
        imageViewSplines.layoutParams.height = bitmapHeight

        imageViewSplines.setOnTouchListener { view, motionEvent ->

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
        for(i in (-squraeSize / 2)..(+squraeSize / 2)) {
            for(j in (-squraeSize / 2)..(+squraeSize / 2)) {
                val px = max(min(x + i, bitmapWidth), 0)
                val py = max(min(y + j, bitmapHeight), 0)

                bitmap.setPixel(px, py, colorPoints)
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
