package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import java.io.FileOutputStream

class SplinesActivity : AppCompatActivity() {

    private var imageChanged = true

    private var pointRadius = 75
    private var splineRadius = 10

    private var imageForSplinesString: String? = null
    private var imageForSplinesBitmap: Bitmap? = null

    private var pointX: ArrayList<Int> = arrayListOf(-1) // Array of x coordinates of inputed points
    private var pointY: ArrayList<Int> = arrayListOf(-1) // Array of y coordinates of inputed points

    private var timeCounter = System.currentTimeMillis()
    private var timeDelay = 200
    private var drawn = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splines)

        imageForSplinesString = intent.getStringExtra("image")
        imageForSplinesBitmap = BitmapFactory.decodeFile(imageForSplinesString)

        imageForSplines.layoutParams.width = 800
//        imageForSplines.layoutParams.width = imageForSplinesBitmap!!.width
//        imageForSplines.layoutParams.height = imageForSplinesBitmap!!.height
        imageForSplines.layoutParams.height = 1000
        imageForSplines.setImageBitmap(imageForSplinesBitmap)

        imageForSplines.setOnTouchListener { _, motionEvent ->
            if(!drawn && (System.currentTimeMillis() - timeCounter) > timeDelay) {


                drawCircle(
                    motionEvent.x.toInt(),
                    motionEvent.y.toInt(),
                    pointRadius,
                    getColor(R.color.splinesColorPoint),
                    imageForSplinesBitmap!!
                )

                pointX.add(motionEvent.x.toInt())
                pointY.add(motionEvent.y.toInt())
                timeCounter = System.currentTimeMillis()

                imageForSplines.setImageBitmap(imageForSplinesBitmap)
            }

            true
        }
    }

    private fun tryCopyImageFile() {
        if (imageForSplinesBitmap != null) {
            imageForSplinesBitmap!!
                .compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageForSplinesString))
        }
    }

    private external fun drawCircle(
        x: Int,
        y: Int,
        r: Int,
        c: Int,
        image: Bitmap)

    private external fun drawSplines(
        n: Int,
        r: Int,
        c: Int,
        xCoords: IntArray,
        yCoords: IntArray,
        p1x: DoubleArray,
        p1y: DoubleArray,
        p2x: DoubleArray,
        p2y: DoubleArray,
        image: Bitmap)

    private external fun calculateSplinesP1(
        pointsAmount: Int,
        pointsCoords: IntArray
    ): DoubleArray

    private external fun calculateSplinesP2(
        pointsAmount: Int,
        pointsCoords: IntArray
    ): DoubleArray

    fun processButtonPressing(
        view: View
    ) {
        when(view.id) {
            R.id.imageButtonBack -> {
                tryCopyImageFile()
                setResult(Activity.RESULT_OK, Intent().putExtra("changed", imageChanged))
                finish()
            }
            R.id.buttonDrawSplines -> {
                runDrawSplines()
            }
            R.id.buttonClear -> {
                pointX = arrayListOf(-1)
                pointY = arrayListOf(-1)
                imageForSplinesBitmap = BitmapFactory.decodeFile(imageForSplinesString)
                imageForSplines.setImageBitmap(imageForSplinesBitmap)
                drawn = false
            }
        }
    }

    private fun runDrawSplines() {
        progressBarSplines.visibility = View.VISIBLE
        Thread {
            val xArray = IntArray(pointX.size - 1)
            val yArray = IntArray(pointY.size - 1)

            for (i in 1 until pointX.size) {
                xArray[i - 1] = pointX[i]
                yArray[i - 1] = pointY[i]
            }

            val p1x = calculateSplinesP1(xArray.size, xArray)
            val p1y = calculateSplinesP1(yArray.size, yArray)
            val p2x = calculateSplinesP2(xArray.size, xArray)
            val p2y = calculateSplinesP2(yArray.size, yArray)

            drawSplines(
                xArray.size,
                splineRadius,
                getColor(R.color.splinesColorSpline),
                xArray,
                yArray,
                p1x,
                p1y,
                p2x,
                p2y,
                imageForSplinesBitmap!!
            )

            imageForSplines.post {
                imageForSplines.setImageBitmap(imageForSplinesBitmap!!)
                drawn = true
                progressBarSplines.visibility = View.GONE
            }
        }.start()
    }
}
