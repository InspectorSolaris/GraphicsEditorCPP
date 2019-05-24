package com.example.graphicseditorcpp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_masking.*
import java.io.File
import java.io.FileOutputStream
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class MaskingActivity : AppCompatActivity() {

    private var imageChanged = false

    private var imageForMaskingString: String? = null
    private var imageMaskingBitmap: Bitmap? = null

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_masking)

        imageForMaskingString = intent.getStringExtra("image")
        imageMaskingBitmap = BitmapFactory.decodeFile(imageForMaskingString)
        imageForMasking.setImageURI(Uri.parse(imageForMaskingString))

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
            imageForMaskingString = absolutePath
        }
    }

    private fun tryCopyImageFile(){
        if(imageMaskingBitmap != null) {
            val imageLocal = imageMaskingBitmap
            imageLocal?.compress(Bitmap.CompressFormat.PNG, 100, FileOutputStream(imageForMaskingString))
        }
    }

    private fun gaussBlur(startBitmap: Bitmap, radius: Int): Bitmap? {
        val bitmap = startBitmap.copy(startBitmap.config, true)

        if (radius < 1) {
            return null
        }

        val w = bitmap.width
        val h = bitmap.height

        val pix = IntArray(w * h)
        bitmap.getPixels(pix, 0, w, 0, 0, w, h)

        val wm = w - 1
        val hm = h - 1
        val wh = w * h
        val div = radius + radius + 1

        val r = IntArray(wh)
        val g = IntArray(wh)
        val b = IntArray(wh)
        var rsum: Int
        var gsum: Int
        var bsum: Int
        var x: Int
        var y: Int
        var i: Int
        var p: Int
        var yp: Int
        var yi: Int
        var yw: Int
        val vmin = IntArray(Math.max(w, h))

        var divsum = div + 1 shr 1
        divsum *= divsum
        val dv = IntArray(256 * divsum)
        i = 0
        while (i < 256 * divsum) {
            dv[i] = i / divsum
            i++
        }

        yi = 0
        yw = yi

        val stack = Array(div) { IntArray(3) }
        var stackpointer: Int
        var stackstart: Int
        var sir: IntArray
        var rbs: Int
        val r1 = radius + 1
        var routsum: Int
        var goutsum: Int
        var boutsum: Int
        var rinsum: Int
        var ginsum: Int
        var binsum: Int

        y = 0
        while (y < h) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            i = -radius
            while (i <= radius) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))]
                sir = stack[i + radius]
                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff
                rbs = r1 - abs(i)
                rsum += sir[0] * rbs
                gsum += sir[1] * rbs
                bsum += sir[2] * rbs
                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }
                i++
            }
            stackpointer = radius

            x = 0
            while (x < w) {

                r[yi] = dv[rsum]
                g[yi] = dv[gsum]
                b[yi] = dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm)
                }
                p = pix[yw + vmin[x]]

                sir[0] = p and 0xff0000 shr 16
                sir[1] = p and 0x00ff00 shr 8
                sir[2] = p and 0x0000ff

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer % div]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi++
                x++
            }
            yw += w
            y++
        }
        x = 0
        while (x < w) {
            bsum = 0
            gsum = bsum
            rsum = gsum
            boutsum = rsum
            goutsum = boutsum
            routsum = goutsum
            binsum = routsum
            ginsum = binsum
            rinsum = ginsum
            yp = -radius * w
            i = -radius
            while (i <= radius) {
                yi = Math.max(0, yp) + x

                sir = stack[i + radius]

                sir[0] = r[yi]
                sir[1] = g[yi]
                sir[2] = b[yi]

                rbs = r1 - Math.abs(i)

                rsum += r[yi] * rbs
                gsum += g[yi] * rbs
                bsum += b[yi] * rbs

                if (i > 0) {
                    rinsum += sir[0]
                    ginsum += sir[1]
                    binsum += sir[2]
                } else {
                    routsum += sir[0]
                    goutsum += sir[1]
                    boutsum += sir[2]
                }

                if (i < hm) {
                    yp += w
                }
                i++
            }
            yi = x
            stackpointer = radius
            y = 0
            while (y < h) {
                pix[yi] = -0x1000000 and pix[yi] or (dv[rsum] shl 16) or (dv[gsum] shl 8) or dv[bsum]

                rsum -= routsum
                gsum -= goutsum
                bsum -= boutsum

                stackstart = stackpointer - radius + div
                sir = stack[stackstart % div]

                routsum -= sir[0]
                goutsum -= sir[1]
                boutsum -= sir[2]

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w
                }
                p = x + vmin[y]

                sir[0] = r[p]
                sir[1] = g[p]
                sir[2] = b[p]

                rinsum += sir[0]
                ginsum += sir[1]
                binsum += sir[2]

                rsum += rinsum
                gsum += ginsum
                bsum += binsum

                stackpointer = (stackpointer + 1) % div
                sir = stack[stackpointer]

                routsum += sir[0]
                goutsum += sir[1]
                boutsum += sir[2]

                rinsum -= sir[0]
                ginsum -= sir[1]
                binsum -= sir[2]

                yi += w
                y++
            }
            x++
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h)

        return bitmap
    }

    private fun changeContrast(pix : Int, contrast : Double) : Int {
        var newPix = ((((pix / 255.0) - 0.5) * contrast) + 0.5) * 255.0
        if(newPix < 0) { newPix = 0.0; }
        else if(newPix > 255) { newPix = 255.0; }
        return newPix.toInt()
    }

    private fun unsharpMasking(value : Double) : Bitmap? {
        val imageBitmap : Bitmap = BitmapFactory.decodeFile(imageForMaskingString)
        val width = imageBitmap.width
        val height = imageBitmap.height
        val pixelsArray = IntArray(width*height)
        imageBitmap.getPixels(pixelsArray, 0, width, 0 , 0, width, height)


        val gaussPic : Bitmap? = gaussBlur(imageBitmap, 10)
        val gaussArray = IntArray(width*height)
        gaussPic!!.getPixels(gaussArray, 0, width, 0 , 0, width, height)

        val contrast = Math.pow((100 + value) / 100, 2.0)
        val diffArray = IntArray(width*height)
        for (i in 0 until width*height) {
            diffArray[i] = Math.min(pixelsArray[i] - (gaussArray[i]*1.1).toInt(), 0)
            if (diffArray[i] != 0) {
                val alpha = Color.alpha(pixelsArray[i])
                val red = changeContrast(Color.red(pixelsArray[i]), contrast)
                val green = changeContrast(Color.green(pixelsArray[i]), contrast)
                val blue = changeContrast(Color.blue(pixelsArray[i]), contrast)
                pixelsArray[i] = Color.argb(alpha, red, green, blue)
            }
        }

        val newBitmap : Bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        newBitmap.setPixels(pixelsArray, 0, width, 0, 0, width, height)
        if(imageForMaskingString == null){
            createImageFile(
                getString(R.string.imageforprocessingname),
                getString(R.string.imageforprocessingext)
            )
        }
        return newBitmap
    }

    fun processButtonPressing(
        view: View
    ) {
        when (view.id) {
            R.id.imageButtonBack -> {
                tryCopyImageFile()
                setResult(Activity.RESULT_OK, Intent().putExtra("changed", imageChanged))
                finish()
            }
            R.id.buttonMask -> {
                imageMaskingBitmap = unsharpMasking(5.0)
                imageChanged = true
                imageForMasking.setImageBitmap(imageMaskingBitmap)
                Toast.makeText(this, getString(R.string.masking_activity_toast), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
