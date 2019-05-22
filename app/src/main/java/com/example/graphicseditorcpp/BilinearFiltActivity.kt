package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_bilinear_filt.*

class BilinearFiltActivity : AppCompatActivity() {

    private var imageChanged = false

    private var imageForBilinearFiltString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bilinear_filt)

        imageForBilinearFiltString = intent.getStringExtra("image")
        imageForBilinearFilt.setImageURI(Uri.parse(imageForBilinearFiltString))
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                setResult(Activity.RESULT_OK, Intent().putExtra("changed", imageChanged))
                finish()
            }
        }
    }
}
