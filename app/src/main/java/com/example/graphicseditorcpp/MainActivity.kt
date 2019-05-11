package com.example.graphicseditorcpp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import java.io.File
import android.os.Environment
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.FileProvider
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
                    R.id.popupPickFromGallery -> {
                        tryPickFromGallery()
                        true
                    }
                    R.id.popupPickFromCamera -> {
                        tryPickFromCamera()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private val idPickFromGallery = 1
    private val idPickFromCamera = 2

    private val permissionGallery = 1
    private val permissionCamera = 2

    private var imageForProcessingPath : String? = null

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */

    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }

    }

    private fun tryPickFromGallery() {
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

    private fun tryPickFromCamera() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

            requestPermissions(
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), permissionCamera)

        } else {
            pickFromCamera()
        }
    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            R.id.imageButtonPickFromGallery -> {
                tryPickFromGallery()
            }
            R.id.imageButtonPickFromCamera -> {
                tryPickFromCamera()
            }
            R.id.imageButtonAlgorithmAStar -> {
                val aStarIntent = Intent(this, AStarActivity::class.java)
                startActivity(aStarIntent)
            }
            R.id.buttonSplines -> {
                val splinesIntent = Intent(this, SplinesActivity::class.java)
                startActivity(splinesIntent)
            }
            R.id.imageButtonTools -> {

                val dialog = BottomSheetDialog(this)
                dialog.setContentView(layoutInflater.inflate(R.layout.tools_layout, null))
                dialog.show()

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
                    tryPickFromGallery()
                } else {
                    Toast.makeText(this, "Gallery permission denied", Toast.LENGTH_LONG).show()
                }

            }
            permissionCamera -> {

                if(grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    tryPickFromCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show()
                }

            }

        }
    }

    private fun createImageFile() : File {
        val imageFileName = "processedImg_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}"
        val imageFileExt = ".png"
        val imageFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            imageFileExt,
            imageFileDir
        ).apply {
            imageForProcessingPath = absolutePath
        }
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        galleryIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.graphicseditorcpp.fileprovider", createImageFile()))
        startActivityForResult(galleryIntent, idPickFromGallery)
    }

    private fun pickFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.graphicseditorcpp.fileprovider", createImageFile()))
        startActivityForResult(cameraIntent, idPickFromCamera)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUri : Uri? = if(requestCode == idPickFromGallery && resultCode == Activity.RESULT_OK) {
            data?.data
        }
        else if(requestCode == idPickFromCamera && resultCode == Activity.RESULT_OK) {
            Uri.fromFile(File(imageForProcessingPath))
        }
        else {
            null
        }

        if(imageUri != null) {
            imageButtonPickFromGallery.visibility = View.GONE
            imageButtonPickFromGallery.isEnabled = false
            imageButtonPickFromCamera.visibility = View.GONE
            imageButtonPickFromCamera.isEnabled = false

            imageForProcessing.setImageURI(imageUri)

            imageForProcessingPath = imageUri.toString()
        }
        else {
            Toast.makeText(this, "Unexpected error. onActivityResult. Image pick", Toast.LENGTH_LONG).show()
        }
    }
}
