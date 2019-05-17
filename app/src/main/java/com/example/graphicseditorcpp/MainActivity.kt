package com.example.graphicseditorcpp

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import android.os.Environment
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import java.io.*

class MainActivity : AppCompatActivity() {

    private val idPickFromGallery = 1
    private val idPickFromCamera = 2

    private val permissionGallery = 1
    private val permissionCamera = 2

    private var imageForProcessingString: String? = null

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
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
            imageForProcessingString = absolutePath
        }
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
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

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)

        galleryIntent.type = "image/*"

        startActivityForResult(galleryIntent, idPickFromGallery)
    }

    private fun pickFromCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "com.example.graphicseditorcpp.fileprovider",
            createImageFile(getString(R.string.main_activity_imageforprocessingname), getString(R.string.main_activity_imageforprocessingext))))

        startActivityForResult(cameraIntent, idPickFromCamera)
    }

    fun processButtonPressing(
        view: View
    ) {
        when(view.id) {
            R.id.imageButtonPickFromGallery -> {
                tryPickFromGallery()
            }
            R.id.imageButtonPickFromCamera -> {
                tryPickFromCamera()
            }
            R.id.imageButtonTools -> {
                val dialog = BottomSheetDialog(this)
                dialog.setContentView(layoutInflater.inflate(R.layout.tools_layout, null))
                dialog.show()
            }
            R.id.imageButtonAlgorithmAStar -> {
                val aStarIntent = Intent(this, AStarActivity::class.java)
                startActivity(aStarIntent)
            }
            R.id.buttonSplines -> {
                val splinesIntent = Intent(this, SplinesActivity::class.java)
                startActivity(splinesIntent)
            }
            R.id.buttonPictureTurning -> {
                val turnIntent = Intent(this, TurningActivity::class.java)
                if (imageForProcessingString != null) {
                    turnIntent.putExtra("image", imageForProcessingString)
                    startActivity(turnIntent)

                    imageForProcessing.setImageURI(Uri.parse(imageForProcessingString))
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureColorCorrection -> {
                val filterIntent = Intent(this, FiltersActivity::class.java)
                if (imageForProcessingString != null) {
                    filterIntent.putExtra("image", imageForProcessingString)
                    startActivity(filterIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureScaling -> {
                val scaleIntent = Intent(this, ScalingActivity::class.java)
                if (imageForProcessingString != null) {
                    scaleIntent.putExtra("image", imageForProcessingString)
                    startActivity(scaleIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureSegmentation -> {
                val segmentationIntent = Intent(this, SegmentationActivity::class.java)
                if (imageForProcessingString != null) {
                    segmentationIntent.putExtra("image", imageForProcessingString)
                    startActivity(segmentationIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureRetouching -> {
                val retouchingIntent = Intent(this, RetouchingActivity::class.java)
                if (imageForProcessingString != null) {
                    retouchingIntent.putExtra("image", imageForProcessingString)
                    startActivity(retouchingIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureUnsharpMasking -> {
                val maskingIntent = Intent(this, MaskingActivity::class.java)
                if (imageForProcessingString != null) {
                    maskingIntent.putExtra("image", imageForProcessingString)
                    startActivity(maskingIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureBilinearFiltration -> {
                val bilinearFiltIntent = Intent(this, BilinearFiltActivity::class.java)
                if (imageForProcessingString != null) {
                    bilinearFiltIntent.putExtra("image", imageForProcessingString)
                    startActivity(bilinearFiltIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonPictureTrilinearFiltration -> {
                val trilinearFiltIntent = Intent(this, TrilinearFiltActivity::class.java)
                if (imageForProcessingString != null) {
                    trilinearFiltIntent.putExtra("image", imageForProcessingString)
                    startActivity(trilinearFiltIntent)
                }
                else {
                    Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
                }
            }
            R.id.buttonExport -> {
                exportPicture()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            permissionGallery -> {
                if(grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    tryPickFromGallery()
                } else {
                    Toast.makeText(this, getString(R.string.error_main_gallery_permissions), Toast.LENGTH_LONG).show()
                }
            }
            permissionCamera -> {
                if(grantResults.size > 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    tryPickFromCamera()
                } else {
                    Toast.makeText(this, getString(R.string.error_main_camera_permissions), Toast.LENGTH_LONG).show()
                }
            }

        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)

        val imageUri = when {
            requestCode == idPickFromGallery && resultCode == Activity.RESULT_OK -> {
                createImageFile(getString(R.string.main_activity_imageforprocessingname), getString(R.string.main_activity_imageforprocessingext))

                val imageForProcessingFileOut = FileOutputStream(imageForProcessingString)
                val imageForProcessingBitmap = BitmapFactory.decodeFileDescriptor(this.contentResolver.openFileDescriptor(data?.data, "r")?.fileDescriptor)

                imageForProcessingBitmap.compress(Bitmap.CompressFormat.PNG, 100, imageForProcessingFileOut)

                Uri.parse(imageForProcessingString)
            }
            requestCode == idPickFromCamera && resultCode == Activity.RESULT_OK -> {
                Uri.parse(imageForProcessingString)
            }
            else -> {
                null
            }
        }

        if(imageUri != null) {
            imageButtonPickFromGallery.visibility = View.GONE
            imageButtonPickFromGallery.isEnabled = false
            imageButtonPickFromCamera.visibility = View.GONE
            imageButtonPickFromCamera.isEnabled = false

            imageForProcessing.setImageURI(imageUri)
            imageForProcessingString = imageUri.toString()
        }
        else {
            Toast.makeText(this, getString(R.string.error_main_image_pick), Toast.LENGTH_LONG).show()
        }
    }

    private fun exportPicture() {
        MediaStore.Images.Media.insertImage(
            contentResolver,
            BitmapFactory.decodeFile(imageForProcessingString),
            getString(R.string.main_activity_imageforprocessingname) + SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date()) + ".${getString(R.string.main_activity_imageforprocessingext)}",
            getString(R.string.main_activity_imageforprocessingname) + ".${getString(R.string.main_activity_imageforprocessingext)}"
        )
        Toast.makeText(this, getString(R.string.main_activity_imageexportsuccess), Toast.LENGTH_LONG).show()
    }
}

