package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_turning.*
import kotlinx.android.synthetic.main.activity_turning.imageForTurning
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class TurningActivity : AppCompatActivity() {

    var currentAngle = 0
    var imageForTurningString: String? = null
    var imageTurnedString: String? = null

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

    private fun createImageFile(
        name: String,
        ext: String
    ): File {
        val imageFileName = name + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileExt = ".$ext"
        val imageFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            imageFileExt,
            imageFileDir
        ).apply {
            imageTurnedString = absolutePath
        }
    }

    private fun tryCopyImageFile(){
        if(imageTurnedString != null) {
            BitmapFactory.decodeFile(imageTurnedString).compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageForTurningString))
        }
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
                tryCopyImageFile()
                setResult(Activity.RESULT_OK)
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
        val imageInfo = BitmapFactory.Options()

        val imageOriginal = BitmapFactory.decodeFile(imageForTurningString, imageInfo)
        val imageTurned = Bitmap.createBitmap(imageInfo.outWidth, imageInfo.outHeight, Bitmap.Config.ARGB_8888)

        imageTurning(currentAngle.toDouble(), imageOriginal, imageTurned)

        imageForTurning.setImageBitmap(imageTurned)

        if(imageTurnedString == null) {
            createImageFile(getString(R.string.turning_activity_imageturnedname), getString(R.string.turning_activity_imageturnedext))
        }

        imageTurned.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageTurnedString))
    }
}
