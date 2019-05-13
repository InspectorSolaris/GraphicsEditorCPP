package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import kotlin.math.max
import kotlin.math.min

class SplinesActivity : AppCompatActivity() {

    private val squareSize = 10
    private val circleRadius = 1.6F
    private val splineLen = 2000

    private var nSize = 1200 // bitmapForSplines width
    private var mSize = 1500 // bitmapForSplines height
    private val bitmapForSplines: Bitmap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    private var pointX: ArrayList<Int> = arrayListOf(-1) // Array of x coordinates of inputed points
    private var pointY: ArrayList<Int> = arrayListOf(-1) // Array of y coordinates of inputed points

    private var timeCounter = System.currentTimeMillis()
    private var timeDelay = 100
    private var drawn = false

    external fun calculateSplinesP1(
        pointsAmount: Int,
        pointsCoords: IntArray
    ): DoubleArray

    external fun calculateSplinesP2(
        pointsAmount: Int,
        pointsCoords: IntArray
    ): DoubleArray

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splines)

        bitmapForSplines.eraseColor(getColor(R.color.splinesColorBackground))

        imageViewSplines.setImageBitmap(bitmapForSplines)
        imageViewSplines.layoutParams.width = nSize
        imageViewSplines.layoutParams.height = mSize

        imageViewSplines.setOnTouchListener { _, motionEvent ->

            if(!drawn && (System.currentTimeMillis() - timeCounter) > timeDelay) {
                drawSquare(motionEvent.x.toInt(), motionEvent.y.toInt())
                pointX.add(motionEvent.x.toInt())
                pointY.add(motionEvent.y.toInt())
                timeCounter = System.currentTimeMillis()

                imageViewSplines.setImageBitmap(bitmapForSplines)
            }

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

                bitmapForSplines.setPixel(px, py, getColor(R.color.splinesColorPoint))
            }
        }
    }

    private fun drawSpline(
        xCoords: IntArray,
        yCoords: IntArray,
        p1x: DoubleArray,
        p1y: DoubleArray,
        p2x: DoubleArray,
        p2y: DoubleArray) {
        val paint = Paint()
        val canvas = Canvas(bitmapForSplines)

        paint.color = getColor(R.color.splinesColorSpline)

        for(i in 0..(xCoords.size - 2)) {
            for(j in 0..splineLen) {

                val t: Double = j.toDouble() / splineLen.toDouble()

                val a = (1 - t) * (1 - t) * (1 - t)
                val b = (1 - t) * (1 - t) * t
                val c = (1 - t) * t * t
                val d = t * t * t

                val x: Float = (a * xCoords[i] + b * 3 * p1x[i] + c * 3 * p2x[i] + d * xCoords[i + 1]).toFloat()
                val y: Float = (a * yCoords[i] + b * 3 * p1y[i] + c * 3 * p2y[i] + d * yCoords[i + 1]).toFloat()

                if (0 <= x && x < nSize &&
                    0 <= y && y < mSize
                ) {
                    canvas.drawCircle(x, y, circleRadius, paint)
                }
            }
        }

        canvas.drawBitmap(bitmapForSplines, 0.0F, 0.0F, paint)
    }

    fun processButtonPressing(
        view: View) {
        when(view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonDrawSplines -> {

                val xArray = IntArray(pointX.size - 1)
                val yArray = IntArray(pointY.size - 1)

                for(i in 1..(pointX.size - 1)) {
                    xArray[i - 1] = pointX[i]
                    yArray[i - 1] = pointY[i]
                }

                val p1x = calculateSplinesP1(xArray.size, xArray)
                val p1y = calculateSplinesP1(yArray.size, yArray)
                val p2x = calculateSplinesP2(xArray.size, xArray)
                val p2y = calculateSplinesP2(yArray.size, yArray)

                drawSpline(xArray, yArray, p1x, p1y, p2x, p2y)

                imageViewSplines.setImageBitmap(bitmapForSplines)
                drawn = true
            }
        }
    }
}
