package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_turning.*
import kotlinx.android.synthetic.main.activity_turning.imageForTurning
import java.io.FileInputStream

class TurningActivity : AppCompatActivity() {

    var currentAngle = 0
    var imageForTurningString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turning)

        imageForTurningString = intent.getStringExtra("image")
        imageForTurning.setImageURI(Uri.parse(imageForTurningString))

        seekBarAngle.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                currentAngle = i - 180
                textViewAngle.text = currentAngle.toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // empty fun
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // empty fun
            }
        })
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
        val imageFileStream = FileInputStream(imageForTurningString)

        val imageInfo = BitmapFactory.Options()
        imageInfo.inJustDecodeBounds = true

        BitmapFactory.decodeStream(imageFileStream, null, imageInfo)

        val imageTurnedInd = imageTurning(imageInfo.outHeight, imageInfo.outWidth, currentAngle.toDouble())

        val image = BitmapFactory.decodeStream(imageFileStream)
        val imageTurned = Bitmap.createBitmap(imageTurnedInd[1], imageTurnedInd[0], Bitmap.Config.ARGB_8888)

        for(i in 0..(imageTurnedInd[0] - 1)) {
            for(j in 0..(imageTurnedInd[1] - 1)) {
                val ind = i * imageTurnedInd[1] + j

                if(imageTurnedInd[ind] == 0) {
                    imageTurned.setPixel(i, j, 0x00000000)
                }
                else if(imageTurnedInd[ind] > 0) {

                }
            }
        }
    }
}
