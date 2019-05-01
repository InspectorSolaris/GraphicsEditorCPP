package com.example.graphicseditorcpp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

        private const val idPickFromGallery = 0
        private const val idPickFromCamera = 1

        private const val permissionGallery = 0
        private const val permissionCamera = 1
    }

    fun processButtonPressing(view: View) {

        if(view.id == pickFromGallery.id) {

            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), permissionGallery)
            }
            else {

                pickFromGallery()
            }
        }
        if(view.id == pickFromCamera.id) {

            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                requestPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE), permissionCamera)
            }
            else {

                pickFromCamera()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {

            permissionGallery -> {

                if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickFromGallery()
                }
                else {

                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show()
                }
            }

            permissionCamera -> {

                if(grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    pickFromCamera()
                }
                else {

                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == idPickFromGallery && resultCode == Activity.RESULT_OK ||
            requestCode == idPickFromCamera && resultCode == Activity.RESULT_OK) {

//            val imageBitmap = data?.extras?.get("data") as Bitmap
//            imageForProcessing.setImageURI(data?.data)

//            val imageBitmap = getCompressedBitmap(data)
//            imageForProcessing.setImageBitmap(imageBitmap)
        }
    }

    private fun pickFromGallery() {

        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, idPickFromGallery)
    }

    private fun pickFromCamera() {

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, idPickFromCamera)
    }

//    private fun getCompressedBitmap(data: Intent?) {
//
//
//    }
}
