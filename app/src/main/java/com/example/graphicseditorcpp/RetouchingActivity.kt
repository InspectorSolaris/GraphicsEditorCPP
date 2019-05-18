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
import kotlinx.android.synthetic.main.activity_retouching.*
import java.io.FileOutputStream

class RetouchingActivity : AppCompatActivity() {

    var retouchingRadius = 50
    var imageForRetouchingString: String? = null
    var imageRetouchedBitmap: Bitmap? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

        imageForRetouchingString = intent.getStringExtra("image")
        imageRetouchedBitmap = BitmapFactory.decodeFile(imageForRetouchingString)
        imageForRetouching.setImageURI(Uri.parse(imageForRetouchingString))

        seekBarRetouchingSize.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                retouchingRadius = i
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // empty fun
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // empty fun
            }
        })

        imageForRetouching.setOnTouchListener { _, motionEvent ->
            val imageLocal = imageRetouchedBitmap

            imageRetouching(retouchingRadius, motionEvent.x.toInt(), motionEvent.y.toInt(), imageLocal)
            imageForRetouching.setImageBitmap(imageRetouchedBitmap)
            imageRetouchedBitmap = imageLocal

            true
        }
    }

    private fun tryCopyImageFile(){
        if(imageRetouchedBitmap != null) {
            val imageLocal = imageRetouchedBitmap
            imageLocal?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageForRetouchingString))
        }
    }

    private external fun imageRetouching(
        r: Int,
        x: Int,
        y: Int,
        image: Bitmap?
    )

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                tryCopyImageFile()
                setResult(Activity.RESULT_OK, Intent().putExtra("image", imageForRetouchingString))
                finish()
            }
            R.id.buttonDismiss -> {
                imageRetouchedBitmap = BitmapFactory.decodeFile(imageForRetouchingString)
                imageForRetouching.setImageBitmap(imageRetouchedBitmap)
            }
        }
    }
}
