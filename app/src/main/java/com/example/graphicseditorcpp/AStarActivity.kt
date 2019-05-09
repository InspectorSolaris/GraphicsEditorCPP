package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*

class AStarActivity : AppCompatActivity() {

    private val pixelSize = 50
    private val nSize = 1000
    private val mSize = 1000
    private val aStarMap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    private var empirics = 1
    private var directions = 1

    private var inputState = 3

    private var startX = -1
    private var startY = -1
    private var finishX = -1
    private var finishY = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)

        aStarMap.eraseColor(Color.WHITE)

        for(i in pixelSize..(nSize - 1) step pixelSize) {
            for(j in 0..(mSize - 1)) {
                aStarMap.setPixel(i, j, Color.GRAY)
            }
        }

        for(i in pixelSize..(mSize - 1) step pixelSize) {
            for(j in 0..(nSize - 1)) {
                aStarMap.setPixel(j, i, Color.GRAY)
            }
        }

        imageViewAStarMap.layoutParams.width = aStarMap.width
        imageViewAStarMap.layoutParams.height = aStarMap.height
        imageViewAStarMap.setImageBitmap(aStarMap)

        imageViewAStarMap.setOnTouchListener { _, motionEvent ->
            val coordX = (motionEvent.x / pixelSize).toInt() * pixelSize
            val coordY = (motionEvent.y / pixelSize).toInt() * pixelSize

            val color = when(inputState)
            {
                // trying to set start point
                1 -> {
                    // you can't to set start on finish point
                    if(coordX != finishX || coordY != finishY) {
                        // point already exist
                        if(startX != -1 && startY != -1) {
                            colorizeSquare(aStarMap, startX, startY, Color.WHITE, pixelSize)
                        }
                        startX = coordX
                        startY = coordY
                        Color.GREEN
                    }
                    else {
                        null
                    }
                }
                // trying to set finish point
                2 -> {
                    // you can't to set finish on start point
                    if(coordX != startX || coordY != startY) {
                        // point already exist
                        if(finishX != -1 && finishY != -1) {
                            colorizeSquare(aStarMap, finishX, finishY, Color.WHITE, pixelSize)
                        }
                        finishX = coordX
                        finishY = coordY
                        Color.RED
                    }
                    else {
                        null
                    }
                }
                // trying to set wall
                3 -> {
                    // wall setting on start point
                    if(coordX == startX && coordY == startY) {
                        startX = -1
                        startY = -1
                    }
                    // wall setting on finish point
                    else if(coordX == finishX && coordY == finishY) {
                        finishX = -1
                        finishY = -1
                    }
                    Color.BLACK
                }
                // erase point
                4 -> {
                    // erase start coords
                    if(coordX == startX && coordY == startY) {
                        startX = -1
                        startY = -1
                    }
                    // erase finish coords
                    else if(coordX == finishX && coordY == finishY) {
                        finishX = -1
                        finishY = -1
                    }
                    Color.WHITE
                }
                // unexpected error
                else -> {
                    Toast.makeText(this, "Unexpected error", Toast.LENGTH_LONG).show()
                    null
                }
            }

            if(color != null) {
                colorizeSquare(aStarMap, coordX, coordY, color, pixelSize)
            }

            imageViewAStarMap.setImageBitmap(aStarMap)

            true
        }

        imageButtonAStarSettings.setOnClickListener {
            val popupMenu = android.support.v7.widget.PopupMenu(this, it)
            popupMenu.inflate(R.menu.a_star_popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.popupManhattanDist -> {
                        Toast.makeText(this, "You chose Manhattan Dist as empiric", Toast.LENGTH_SHORT).show()
                        empirics = 1
                        true
                    }
                    R.id.popupEuclidDist -> {
                        Toast.makeText(this, "You chose Euclid Dist as empiric", Toast.LENGTH_SHORT).show()
                        empirics = 2
                        true
                    }
                    R.id.popup4Directional -> {
                        Toast.makeText(this, "4 directions", Toast.LENGTH_LONG).show()
                        directions = 1
                        true
                    }
                    R.id.popup8Directional -> {
                        Toast.makeText(this, "8 directions", Toast.LENGTH_LONG).show()
                        directions = 2
                        true
                    }
                    R.id.popup8DirectionalCheck -> {
                        Toast.makeText(this, "8 directions with check", Toast.LENGTH_LONG).show()
                        directions = 3
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun colorizeSquare(
        aStarMap: Bitmap,
        coordX: Int,
        coordY: Int,
        color: Int,
        pixelSize: Int) {
        for (i in 1..(pixelSize - 1)) {
            for (j in 1..(pixelSize - 1)) {
                aStarMap.setPixel(coordX + i, coordY + j, color)
            }
        }
    }

    external fun algorithmAStar(
        bitmap: Bitmap,
        start_x: Int,
        start_y: Int,
        finish_x: Int,
        finish_y: Int,
        empirics: Int,
        directions: Int
    )

    fun processButtonPressing(view: View) {
        when(view.id) {
            R.id.imageButtonBack -> {
                backToPreviousActivity()
            }
            R.id.buttonSetStart -> {
                inputState = 1
            }
            R.id.buttonSetFinish -> {
                inputState = 2
            }
            R.id.buttonSetWall -> {
                inputState = 3
            }
            R.id.buttonRunAStar -> {
                algorithmAStar(aStarMap, startX, startY, finishX, finishY, empirics, directions)
            }
        }
    }

    private fun backToPreviousActivity(){
        finish()
    }
}
