package com.example.graphicseditorcpp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_bilinear_filt.*

class BilinearFiltActivity : AppCompatActivity() {

    var imageForBilinearFiltString: String? = null

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
                finish()
            }
        }
    }
}
