package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_splines.*
import java.io.FileOutputStream

class SplinesActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    private var pointRadius = 75
    private var splineRadius = 10

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

        imageOriginalString = intent.getStringExtra(getString(R.string.code_image_original))
        imageChangedString = intent.getStringExtra(getString(R.string.code_image_changed))

        run {
            BitmapFactory.decodeFile(imageOriginalString)
                .compress(
                    (application as GlobalVal).bitmapCompressFormat,
                    (application as GlobalVal).bitmapCompressQuality,
                    FileOutputStream(imageChangedString)
                )
        }

        imageForSplines.layoutParams.width = 800
        imageForSplines.layoutParams.height = 1000
        imageForSplines.setImageURI(Uri.parse(imageOriginalString))

        imageForSplines.setOnTouchListener { _, motionEvent ->
            if(!drawn && (System.currentTimeMillis() - timeCounter) > timeDelay) {
                val imageLocal = BitmapFactory.decodeFile(imageChangedString)

                drawCircle(
                    motionEvent.x.toInt(),
                    motionEvent.y.toInt(),
                    pointRadius,
                    getColor(R.color.splinesColorPoint),
                    imageLocal
                )

                imageLocal.compress(
                    (application as GlobalVal).bitmapCompressFormat,
                    (application as GlobalVal).bitmapCompressQuality,
                    FileOutputStream(imageChangedString)
                )

                pointX.add(motionEvent.x.toInt())
                pointY.add(motionEvent.y.toInt())
                timeCounter = System.currentTimeMillis()

                imageForSplines.setImageBitmap(BitmapFactory.decodeFile(imageChangedString))
            }

            true
        }
    }

    private external fun drawCircle(
        x: Int,
        y: Int,
        r: Int,
        c: Int,
        image: Bitmap
    )

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
        image: Bitmap
    )

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
                setResult(
                    Activity.RESULT_OK,
                    Intent().putExtra(
                        getString(R.string.code_image_is_changed),
                        imageIsChangedBool
                    )
                )
                finish()
            }
            R.id.buttonDrawSplines -> {
                runDrawSplines()
            }
            R.id.buttonClear -> {
                pointX = arrayListOf(-1)
                pointY = arrayListOf(-1)
                imageForSplines.setImageURI(Uri.parse(imageOriginalString))
                BitmapFactory.decodeFile(imageOriginalString)
                    .compress(
                        (application as GlobalVal).bitmapCompressFormat,
                        (application as GlobalVal).bitmapCompressQuality,
                        FileOutputStream(imageChangedString)
                    )
                imageIsChangedBool = false
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

            val imageLocal = BitmapFactory.decodeFile(imageChangedString)

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
                imageLocal
            )

            imageLocal.compress(
                (application as GlobalVal).bitmapCompressFormat,
                (application as GlobalVal).bitmapCompressQuality,
                FileOutputStream(imageChangedString)
            )

            runOnUiThread {
                imageForSplines.setImageBitmap(BitmapFactory.decodeFile(imageChangedString))
                imageIsChangedBool = true
                drawn = true
                progressBarSplines.visibility = View.GONE
            }
        }.start()
    }
}
