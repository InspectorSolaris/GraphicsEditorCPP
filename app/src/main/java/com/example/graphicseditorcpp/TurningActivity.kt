package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_turning.*
import kotlinx.android.synthetic.main.activity_turning.imageForTurning
import java.io.FileOutputStream

class TurningActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    private var currentAngle = 0

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turning)

        imageOriginalString = intent.getStringExtra(getString(R.string.code_image_original))
        imageChangedString = intent.getStringExtra(getString(R.string.code_image_changed))

        imageForTurning.setImageURI(Uri.parse(imageOriginalString))

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

    private external fun imageTurning(
        angle: Double,
        orig: Bitmap,
        turn: Bitmap
    )

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
            R.id.buttonClear -> {
                currentAngle = 0
                seekBarAngle.progress = currentAngle + 180
            }
            R.id.buttonTurn -> {
                turnImage()
            }
        }
    }

    private fun turnImage() {
        val imageInfo = BitmapFactory.Options()
        val imageOriginal = BitmapFactory.decodeFile(imageOriginalString, imageInfo)
        val imageTurned = Bitmap.createBitmap(imageInfo.outWidth, imageInfo.outHeight, Bitmap.Config.ARGB_8888)

        progressBarTurning.visibility = View.VISIBLE
        Thread {
            imageTurning(currentAngle.toDouble(), imageOriginal!!, imageTurned!!)

            imageTurned.compress(
                (application as GlobalVal).bitmapCompressFormat,
                (application as GlobalVal).bitmapCompressQuality,
                FileOutputStream(imageChangedString)
            )

            runOnUiThread {
                imageForTurning.setImageBitmap(imageTurned)
                imageIsChangedBool = currentAngle != 0
                progressBarTurning.visibility = View.GONE
            }
        }.start()
    }
}
