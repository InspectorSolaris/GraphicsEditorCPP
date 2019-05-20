package com.example.graphicseditorcpp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.SparseArray
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_segmentation.*
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import java.nio.file.Files.size


class SegmentationActivity : AppCompatActivity() {

    var imageForSegmentationString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_segmentation)

        imageForSegmentationString = intent.getStringExtra("image")
        imageForSegmentation.setImageURI(Uri.parse(imageForSegmentationString))
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonRunSegmentation -> {
                segmentation()
            }
        }
    }

    private fun segmentation() {
        val imageForSegmentationBitmap = BitmapFactory.decodeFile(imageForSegmentationString)

        val myRectPaint = Paint()
        myRectPaint.strokeWidth = 5f
        myRectPaint.color = Color.RED
        myRectPaint.style = Paint.Style.STROKE

        val tempBitmap = Bitmap.createBitmap(imageForSegmentationBitmap.width, imageForSegmentationBitmap.height, Bitmap.Config.ARGB_8888)
        val tempCanvas = Canvas(tempBitmap)
        tempCanvas.drawBitmap(imageForSegmentationBitmap, 0.0F, 0.0F, null)

        val faceDetector = FaceDetector.Builder(applicationContext).setTrackingEnabled(false) .build()
        val frame = Frame.Builder().setBitmap(imageForSegmentationBitmap).build()
        val faces = faceDetector.detect(frame)

        for (i in 0 until faces.size()) {
            val thisFace = faces.valueAt(i)
            val x1 = thisFace.position.x
            val y1 = thisFace.position.y
            val x2 = x1 + thisFace.width
            val y2 = y1 + thisFace.height
            tempCanvas.drawRoundRect(RectF(x1, y1, x2, y2), 2F, 2F, myRectPaint)
        }

        imageForSegmentation.setImageDrawable(BitmapDrawable(resources, tempBitmap))
    }
}
