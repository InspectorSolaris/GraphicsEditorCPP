package com.example.graphicseditorcpp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import java.io.File
import android.net.Uri
import android.os.Environment
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val idPickFromGallery = 1
    private val idPickFromCamera = 2

    private val permissionGallery = 1
    private val permissionCamera = 2

    private var imageForProcessingFile : File? = null
    private var imageForProcessingBitmap : Bitmap? = null

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
            System.loadLibrary("algorithms")
        }

    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            pickFromGallery.id -> {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), permissionGallery)

                } else {

                    pickFromGallery()

                }
            }
            pickFromCamera.id -> {

                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), permissionCamera
                    )

                } else {

                    pickFromCamera()

                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {

            permissionGallery -> {

                if(grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery()
                } else {
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show()
                }
            }
            permissionCamera -> {

                if(grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    pickFromCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == idPickFromGallery && resultCode == Activity.RESULT_OK ||
            requestCode == idPickFromCamera && resultCode == Activity.RESULT_OK) {

            imageForProcessingBitmap = getCompressedBitmap()
            imageForProcessing.setImageBitmap(imageForProcessingBitmap)
        }
    }

    private fun pickFromGallery() {
        val imageDate = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageDir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("processedImg_$(imageDate)", ".png", imageDir)

        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
        startActivityForResult(galleryIntent, idPickFromGallery)

        imageForProcessingFile = imageFile
    }

    private fun pickFromCamera() {
        val imageDate = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageDir  = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile("processedImg_$(imageDate)", ".png", imageDir)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile))
        startActivityForResult(cameraIntent, idPickFromCamera)

        imageForProcessingFile = imageFile
    }

    private fun getCompressedBitmap() : Bitmap {
        val bfOptions : BitmapFactory.Options = BitmapFactory.Options()
        bfOptions.inJustDecodeBounds = true

        var fiStream = FileInputStream(imageForProcessingFile)
        BitmapFactory.decodeStream(fiStream, null, bfOptions)
        fiStream.close()

        bfOptions.inJustDecodeBounds = false
        bfOptions.inSampleSize = binarySearch(bfOptions.outHeight as Double, bfOptions.outWidth as Double, 1024)

        fiStream = FileInputStream(imageForProcessingFile)
        val resultBitmap = BitmapFactory.decodeStream(fiStream, null, bfOptions) as Bitmap
        fiStream.close()

        return resultBitmap
    }

    private fun binarySearch(a : Double, b : Double, maxSize : Int) : Int {
        var l = 1
        var scale = 0
        var r = 100

        while(l < r) {

            scale = r - (r - l) / 2

            if(a / scale > maxSize ||
                b / scale > maxSize) {
                l = scale + 1
            } else {
                r = scale - 1
            }
        }

        return scale
    }
}
