package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.FaceDetector
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_segmentation.*
import java.io.FileOutputStream

class SegmentationActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        imageOriginalString = intent.getStringExtra(getString(R.string.code_image_original))
        imageChangedString = intent.getStringExtra(getString(R.string.code_image_changed))

        imageForSegmentation.setImageURI(Uri.parse(imageOriginalString))
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
            R.id.buttonSegmentation -> {
                segmentation()
            }
            R.id.buttonDismiss -> {
                imageIsChangedBool = false
                imageForSegmentation.setImageURI(Uri.parse(imageOriginalString))
            }
        }
    }

    private fun segmentation() {
        progressBarSegmentation.visibility = View.VISIBLE
        Thread {
            val imageInfo = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeFile(imageOriginalString, this)
            }
            val imageOriginal = BitmapFactory.decodeFile(imageOriginalString)

            val myRectPaint = Paint().apply {
                strokeWidth = 5f
                color = Color.RED
                style = Paint.Style.STROKE
            }

            val tempBitmap = Bitmap.createBitmap(
                imageInfo.outWidth,
                imageInfo.outHeight,
                (application as GlobalVal).bitmapConfig
            )
            val tempCanvas = Canvas(tempBitmap).apply {
                drawBitmap(imageOriginal, 0.0F, 0.0F, null)
            }

            val faceDetector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false).build()
            val frame = Frame.Builder().setBitmap(imageOriginal).build()
            val faces = faceDetector.detect(frame)

            for (i in 0 until faces.size()) {
                val thisFace = faces.valueAt(i)
                val x1 = thisFace.position.x
                val y1 = thisFace.position.y
                val x2 = x1 + thisFace.width
                val y2 = y1 + thisFace.height
                tempCanvas.drawRoundRect(RectF(x1, y1, x2, y2), 2F, 2F, myRectPaint)
            }

            tempBitmap.compress(
                (application as GlobalVal).bitmapCompressFormat,
                (application as GlobalVal).bitmapCompressQuality,
                FileOutputStream(imageChangedString)
            )

            runOnUiThread {
                imageForSegmentation.setImageBitmap(BitmapFactory.decodeFile(imageChangedString))
                imageIsChangedBool = true
                progressBarSegmentation.visibility = View.GONE
            }
        }.start()
    }
}
