package com.example.graphicseditorcpp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import java.io.File
import android.net.Uri
import android.util.Log
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageButtonPickNew.setOnClickListener {
            val popupMenu = android.support.v7.widget.PopupMenu(this, it)
            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.galleryItem -> {
                        Toast.makeText(this, " GALLERY", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.cameraItem -> {
                        Toast.makeText(this, " CAMERA", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        imageForProcessingFileDir = filesDir
    }

    private val idPickFromGallery = 1
    private val idPickFromCamera = 2

    private val permissionGallery = 1
    private val permissionCamera = 2

    private val imageForProcessingFileName : String = "processedImg_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
    private val imageForProcessingFileExt : String = ".tmp"
    private var imageForProcessingFileDir : File? = null

    private var imageForProcessingFile = File.createTempFile(
        imageForProcessingFileName,
        imageForProcessingFileExt,
        imageForProcessingFileDir
    )

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
            imageButtonPickFromGallery.id -> {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), permissionGallery)

                } else {
                    pickFromGallery()
                }

            }
            imageButtonPickFromCamera.id -> {

                if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                        checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {

                    requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CAMERA,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), permissionCamera)

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

            imageButtonPickFromGallery.visibility = View.GONE
            imageButtonPickFromGallery.isEnabled = false
            imageButtonPickFromCamera.visibility = View.GONE
            imageButtonPickFromCamera.isEnabled = false

            imageForProcessing.setImageURI(Uri.fromFile(imageForProcessingFile))
        }
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageForProcessingFile))
        startActivityForResult(galleryIntent, idPickFromGallery)
    }

    private fun pickFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageForProcessingFile))
        startActivityForResult(cameraIntent, idPickFromCamera)
    }

}
