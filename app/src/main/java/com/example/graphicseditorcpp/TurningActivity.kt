package com.example.graphicseditorcpp

import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_turning.*
import kotlinx.android.synthetic.main.activity_turning.imageForTurning
import java.io.FileInputStream

class TurningActivity : AppCompatActivity() {

    var currentAngle = 0
    var imageForTurningPath: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turning)

        imageForTurningPath = intent.getStringExtra("image")
        imageForTurning.setImageURI(Uri.parse(imageForTurningPath))

        textViewAngle.text = currentAngle.toString()
        seekBarAngle.progress = currentAngle + 180
    }

    external fun imageTurning(
        n: Int, // image height
        m: Int, // image width
        angle: Double
    ): IntArray

    fun processButtonPressing(
        view: View
    ) {
        when(view.id) {
            R.id.imageButtonBack -> {
                finish()
            }
            R.id.buttonClear -> {
                currentAngle = 0
                seekBarAngle.progress = currentAngle + 180
            }
            R.id.buttonTurn -> {
                turnImage()
            }
        }
    }

    private fun turnImage(){
        val imageFileStream = FileInputStream(imageForTurningPath)

        val imageInfo = BitmapFactory.Options()
        imageInfo.inJustDecodeBounds = true

        BitmapFactory.decodeStream(imageFileStream, null, imageInfo)

        val imageTurned = imageTurning(imageInfo.outHeight, imageInfo.outWidth, currentAngle.toDouble())
    }
}
