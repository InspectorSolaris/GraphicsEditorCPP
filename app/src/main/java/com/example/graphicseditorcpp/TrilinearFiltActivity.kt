package com.example.graphicseditorcpp

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_trilinear_filt.*

class TrilinearFiltActivity : AppCompatActivity() {

    private var imageForTrilinearFiltString: String? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trilinear_filt)

        imageForTrilinearFiltString = intent.getStringExtra("image")
        imageForTrilinearFilt.setImageURI(Uri.parse(imageForTrilinearFiltString))
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
