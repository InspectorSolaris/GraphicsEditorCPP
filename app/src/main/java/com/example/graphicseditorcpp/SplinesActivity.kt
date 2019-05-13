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

    private var nSize = 1200
    private var mSize = 1500
    private val bitmap: Bitmap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    private var pointX: ArrayList<Int> = arrayListOf(-1)
    private var pointY: ArrayList<Int> = arrayListOf(-1)

    private var timeCounter = System.currentTimeMillis()
    private var timeDelay = 200
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

        bitmap.eraseColor(getColor(R.color.splinesColorBackground))

        imageViewSplines.setImageBitmap(bitmap)
        imageViewSplines.layoutParams.width = nSize
        imageViewSplines.layoutParams.height = mSize

        imageViewSplines.setOnTouchListener { _, motionEvent ->

            if(!drawn && (System.currentTimeMillis() - timeCounter) > timeDelay) {
                drawSquare(motionEvent.x.toInt(), motionEvent.y.toInt())
                pointX.add(motionEvent.x.toInt())
                pointY.add(motionEvent.y.toInt())
                timeCounter = System.currentTimeMillis()

                imageViewSplines.setImageBitmap(bitmap)
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

                bitmap.setPixel(px, py, getColor(R.color.splinesColorPoint))
            }
        }
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

                val path = Path()
                val paint = Paint()
                val canvas = Canvas(bitmap)

                paint.color = getColor(R.color.splinesColorSpline)

                path.reset()
                for(i in 0..(xArray.size - 2)) {
                    path.moveTo(xArray[i].toFloat(), yArray[i].toFloat())
                    path.quadTo(p1x[i].toFloat(), p1y[i].toFloat(), p2x[i].toFloat(), p2y[i].toFloat())
                    path.moveTo(xArray[i + 1].toFloat(), yArray[i + 1].toFloat())
                }
                path.close()

                canvas.drawPath(path, paint)
                canvas.drawBitmap(bitmap, 0.0F, 0.0F, paint)

                imageViewSplines.setImageBitmap(bitmap)
                drawn = true
            }
        }
    }
}
