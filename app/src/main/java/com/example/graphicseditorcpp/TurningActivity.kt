package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class TurningActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_turning)
    }

    external fun imageTurning(
        n: Int, // image height
        m: Int, // image width
        angle: Double
    ): IntArray
}
