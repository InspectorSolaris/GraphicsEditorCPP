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
import android.os.Environment
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import android.view.ViewGroup
import java.io.*
import java.lang.Math.*

class MainActivity : AppCompatActivity() {

    private val idPickFromGallery = 1
    private val idPickFromCamera = 2
    private val idImageChange = 3

    private val permissionGallery = 1
    private val permissionCamera = 2

    private var dialog: BottomSheetDialog? = null

    private var imageForProcessingInd: Int = 0
    private var imageForProcessingHistory: MutableList<String> = mutableListOf("")
    private var imageForProcessingString: String? = null

    companion object {
        init {
            System.loadLibrary("image-algorithms")
        }
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dialog = BottomSheetDialog(this)

        imageButtonPickNew.setOnClickListener {
            val popupMenu = android.support.v7.widget.PopupMenu(this, it)

            popupMenu.inflate(R.menu.popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.popupPickFromGallery -> {
                        tryPickFromGallery()
                        true
                    }
                    R.id.popupPickFromCamera -> {
                        tryPickFromCamera()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popupMenu.show()
        }
    }

    private fun createImageFile(): File {
        val imageFileName = getString(R.string.image_for_processing_name) + SimpleDateFormat(getString(R.string.image_for_processing_time), Locale.US).format(Date())
        val imageFileExt = ".${getString(R.string.image_for_processing_ext)}"
        val imageFileDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
            imageFileName,
            imageFileExt,
            imageFileDir
        ).apply {
            imageForProcessingHistory = imageForProcessingHistory.subList(0, imageForProcessingInd + 1)

            imageForProcessingInd++
            imageForProcessingHistory.add(absolutePath)
        }
    }

    private fun tryExportImage() {
        if (imageForProcessingString != null) {
            val imageFileTime = SimpleDateFormat(getString(R.string.image_for_processing_time), Locale.US).format(Date())
            val imageFileName = getString(R.string.image_for_processing_name) + imageFileTime + ".${getString(R.string.image_for_processing_ext)}"
            val imageFileDescription = getString(R.string.image_for_processing_name) + ".${getString(R.string.image_for_processing_ext)}"

            MediaStore.Images.Media.insertImage(
                contentResolver,
                BitmapFactory.decodeFile(imageForProcessingString),
                imageFileName,
                imageFileDescription
            )

            Toast.makeText(this, getString(R.string.main_activity_imageexportsuccess), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
        }
    }

    private fun tryPickFromGallery() {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                permissionGallery
            )
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
                ),
                permissionCamera
            )
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
        val imageFile = if (imageForProcessingInd == 0) {
            createImageFile()
        } else {
            File(imageForProcessingHistory[imageForProcessingInd])
        }

        cameraIntent.putExtra(
            MediaStore.EXTRA_OUTPUT,
            FileProvider.getUriForFile(this, "com.example.graphicseditorcpp.fileprovider",
                imageFile
            )
        )

        startActivityForResult(cameraIntent, idPickFromCamera)
    }

    private fun runActivity(
        activity: Activity
    ) {
        val activityIntent = Intent(this, activity::class.java)

        activityIntent.putExtra(getString(R.string.code_image_original), imageForProcessingString)
        activityIntent.putExtra(getString(R.string.code_image_changed), imageForProcessingHistory[imageForProcessingInd])
        startActivityForResult(activityIntent, idImageChange)
    }

    fun processToolsButtons(
        view: View
    ) {
        val resultActivity = when {
            imageForProcessingString == null -> {
                null
            }
            view.id == R.id.buttonSplines -> {
                SplinesActivity()
            }
            view.id == R.id.buttonPictureTurning -> {
                TurningActivity()
            }
            view.id == R.id.buttonPictureColorCorrection -> {
                FiltersActivity()
            }
            view.id == R.id.buttonPictureScaling -> {
                ScalingActivity()
            }
            view.id == R.id.buttonPictureSegmentation -> {
                SegmentationActivity()
            }
            view.id == R.id.buttonPictureRetouching -> {
                RetouchingActivity()
            }
            view.id == R.id.buttonPictureUnsharpMasking -> {
                MaskingActivity()
            }
            view.id == R.id.buttonPictureBilinearFiltration -> {
                BilinearFiltActivity()
            }
            view.id == R.id.buttonPictureTrilinearFiltration -> {
                TrilinearFiltActivity()
            }
            else -> {
                null
            }
        }

        if(resultActivity != null) {
            runActivity(resultActivity)

            dialog!!.hide()
        } else {
            Toast.makeText(this, getString(R.string.main_activity_nophoto), Toast.LENGTH_LONG).show()
        }
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
            R.id.imageButtonRedo -> {
                if (imageForProcessingInd != 0) {
                    imageForProcessingInd = min(imageForProcessingInd + 1, imageForProcessingHistory.size - 2)
                    imageForProcessingString = imageForProcessingHistory[imageForProcessingInd]

                    imageForProcessing.setImageBitmap(BitmapFactory.decodeFile(imageForProcessingString))
                }
            }
            R.id.imageButtonUndo -> {
                if (imageForProcessingInd != 0) {
                    imageForProcessingInd = max(imageForProcessingInd - 1, 1)
                    imageForProcessingString = imageForProcessingHistory[imageForProcessingInd]

                    imageForProcessing.setImageBitmap(BitmapFactory.decodeFile(imageForProcessingString))
                }
            }
            R.id.imageButtonTools -> {
                dialog!!.setContentView(layoutInflater.inflate(R.layout.tools_layout, null as ViewGroup?))
                dialog!!.show()
            }
            R.id.imageButtonAlgorithmAStar -> {
                val aStarIntent = Intent(this, AStarActivity::class.java)

                startActivity(aStarIntent)
            }
            R.id.buttonExport -> {
                tryExportImage()
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
            resultCode != Activity.RESULT_OK -> {
                null
            }
            requestCode == idPickFromGallery -> {
                if(imageForProcessingInd == 0) {
                    createImageFile()
                }

                imageForProcessingString = imageForProcessingHistory[imageForProcessingInd]

                val fileDescriptor = this.contentResolver.openFileDescriptor(data?.data as Uri, "r")?.fileDescriptor
                val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)

                bitmap.compress(
                    (application as GlobalVal).bitmapCompressFormat,
                    (application as GlobalVal).bitmapCompressQuality,
                    FileOutputStream(imageForProcessingString)
                )
                Uri.parse(imageForProcessingString)
            }
            requestCode == idPickFromCamera -> {
                imageForProcessingString = imageForProcessingHistory[imageForProcessingInd]
                Uri.parse(imageForProcessingString)
            }
            else -> {
                null
            }
        }

        when {
            imageUri != null -> {
                imageButtonPickFromGallery.visibility = View.GONE
                imageButtonPickFromGallery.isEnabled = false
                imageButtonPickFromCamera.visibility = View.GONE
                imageButtonPickFromCamera.isEnabled = false

                createImageFile()
                imageForProcessing.setImageBitmap(BitmapFactory.decodeFile(imageForProcessingString))
            }
            requestCode != idImageChange -> {
                Toast.makeText(this, getString(R.string.error_main_image_pick), Toast.LENGTH_LONG).show()
            }
            data?.extras!!.getBoolean(getString(R.string.code_image_is_changed)) -> {
                imageForProcessingString = imageForProcessingHistory[imageForProcessingInd]

                imageForProcessing.setImageURI(Uri.parse(imageForProcessingString))
                createImageFile()
            }
        }
    }
}

