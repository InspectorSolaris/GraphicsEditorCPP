package com.example.graphicseditorcpp

import android.app.Application
import android.graphics.Bitmap

class GlobalVal : Application() {

    val bitmapCompressFormat = Bitmap.CompressFormat.JPEG
    val bitmapCompressQuality = 100
    val bitmapConfig = Bitmap.Config.ARGB_8888

}