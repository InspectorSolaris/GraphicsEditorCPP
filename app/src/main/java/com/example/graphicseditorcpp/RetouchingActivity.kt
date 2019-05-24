package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.TouchDelegate
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_retouching.*
import java.io.FileOutputStream
import java.util.*

class RetouchingActivity : AppCompatActivity() {

    private var imageOriginalString: String? = null
    private var imageChangedString: String? = null
    private var imageIsChangedBool: Boolean = false

    private var taskState = false
    private val taskQueue: Queue<Thread> = ArrayDeque<Thread>()
    private val xQueue: Queue<Int> = ArrayDeque<Int>()
    private val yQueue: Queue<Int> = ArrayDeque<Int>()

    private var touchRatio = 1.0
    private val touchDelay = 100
    private var touchTime = System.currentTimeMillis()
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

        val screenSize = Point()
        windowManager.defaultDisplay.getSize(screenSize)

        val imageInfo = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            BitmapFactory.decodeFile(imageOriginalString, this)
        }
        val maxWidth = (application as GlobalVal).imageScreenWidthRatio * screenSize.x
        val maxHeight = (application as GlobalVal).imageScreenHeightRatio * screenSize.y

        val ratio = imageInfo.outHeight.toDouble() / imageInfo.outWidth.toDouble()
        var width = maxWidth
        var height = maxWidth * ratio

        if(maxHeight < height) {
            height = maxHeight
            width = maxHeight / ratio
        }

        touchRatio = imageInfo.outWidth.toDouble() / width

        imageForRetouching.layoutParams.width = width.toInt()
        imageForRetouching.layoutParams.height = height.toInt()
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
            val x = (motionEvent.x * touchRatio).toInt()
            val y = (motionEvent.y * touchRatio).toInt()

            if(System.currentTimeMillis() - touchTime > touchDelay) {
                xQueue.add(x)
                yQueue.add(y)
                taskQueue.add(Thread {
                    val imageLocal = BitmapFactory.decodeFile(imageChangedString)

                    if (imageLocal != null) {
                        val blurR = touchRatio * retouchingRadius

                        imageRetouching(
                            xQueue.remove(),
                            yQueue.remove(),
                            blurR.toInt(),
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

                            taskQueue.remove()
                            if (taskQueue.isNotEmpty()) {
                                taskQueue.peek().start()
                            } else {
                                taskState = false
                            }
                        }
                    }
                })
            }

            if (!taskState) {
                progressBarRetouching.visibility = View.VISIBLE

                taskState = true
                taskQueue.peek().start()
            }

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
