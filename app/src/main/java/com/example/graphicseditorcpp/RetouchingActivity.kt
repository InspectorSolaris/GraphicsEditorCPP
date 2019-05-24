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

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    private var retouchingRadius = 50

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retouching)

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

        imageForRetouching.setImageURI(Uri.parse(imageOriginalString))

        seekBarRetouchingSize.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                i: Int,
                b: Boolean
            ) {
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
            progressBarRetouching.visibility = View.VISIBLE



            Thread {
                val imageLocal = BitmapFactory.decodeFile(imageChangedString)

                if(imageLocal != null) {
                    imageRetouching(
                        motionEvent.x.toInt(),
                        motionEvent.y.toInt(),
                        retouchingRadius,
                        imageLocal
                    )

                    imageLocal.compress(
                        (application as GlobalVal).bitmapCompressFormat,
                        (application as GlobalVal).bitmapCompressQuality,
                        FileOutputStream(imageChangedString)
                    )

                    runOnUiThread {
                        imageForRetouching.setImageBitmap(BitmapFactory.decodeFile(imageChangedString))
                        imageIsChangedBool = true
                        progressBarRetouching.visibility = View.GONE
                    }
                }
            }.start()

            true
        }
    }

    private external fun imageRetouching(
        x: Int,
        y: Int,
        r: Int,
        image: Bitmap
    )

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
            R.id.buttonDismiss -> {
                imageForRetouching.setImageURI(Uri.parse(imageOriginalString))
                imageIsChangedBool = false
            }
        }
    }
}
