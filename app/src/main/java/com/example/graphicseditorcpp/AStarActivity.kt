package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*
import java.lang.Math.max
import java.lang.Math.min

class AStarActivity : AppCompatActivity() {

    private val pixelSize = 50
    private val aStarMapWidth = 800 // aStarMap width
    private val aStarMapHeight = 800 // aStarMap height
    private val aStarMap = Bitmap.createBitmap(aStarMapWidth, aStarMapHeight, Bitmap.Config.ARGB_8888)

    private var empirics = 1    // 1 - manhattan, 2 - euclid
    private var directions = 1  // 1 - 4-directional, 2 - 8-directional, 3 - 8-directional with check
    private var inputState = 3  // 1 - set start, 2 - set finish, 3 - set wall, 4 - erase

    private var startX = -1
    private var startY = -1
    private var finishX = -1
    private var finishY = -1

    private var path: IntArray = intArrayOf(-1)
    private var pathIsDrown = false

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)

        aStarMap.eraseColor(getColor(R.color.aStarColorEmpty))
        drawGrid()

        imageViewAStarMap.layoutParams.width = aStarMap.width
        imageViewAStarMap.layoutParams.height = aStarMap.height
        imageViewAStarMap.setImageBitmap(aStarMap)

        imageViewAStarMap.setOnTouchListener { _, motionEvent ->
            tryClearPath()

            var coordX = (motionEvent.x / pixelSize).toInt() * pixelSize
            var coordY = (motionEvent.y / pixelSize).toInt() * pixelSize

            coordX = min(coordX, aStarMapWidth - pixelSize)
            coordY = min(coordY, aStarMapHeight - pixelSize)
            coordX = max(coordX, 0)
            coordY = max(coordY, 0)

            val resultingColor = when(inputState)
            {
                // trying to set start point
                1 -> {
                    // you can't to set start on finish point
                    if(coordX != finishX || coordY != finishY) {
                        // point already exist
                        if(startX != -1 && startY != -1) {
                            colorizeSquare(startX, startY, getColor(R.color.aStarColorEmpty))
                        }
                        startX = coordX
                        startY = coordY
                        getColor(R.color.aStarColorStart)
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
                            colorizeSquare(finishX, finishY, getColor(R.color.aStarColorEmpty))
                        }
                        finishX = coordX
                        finishY = coordY
                        getColor(R.color.aStarColorFinish)
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
                    getColor(R.color.aStarColorWall)
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
                    getColor(R.color.aStarColorEmpty)
                }
                // unexpected error
                else -> {
                    Toast.makeText(this, "Unexpected error", Toast.LENGTH_LONG).show()
                    null
                }
            }

            if(resultingColor != null) {
                colorizeSquare(coordX, coordY, resultingColor)
            }

            imageViewAStarMap.setImageBitmap(aStarMap)

            true
        }

        imageButtonAStarSettings.setOnClickListener {
            tryClearPath()

            val popupMenu = android.support.v7.widget.PopupMenu(this, it)
            popupMenu.inflate(R.menu.a_star_popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.popupManhattanDist -> {
                        Toast.makeText(this, getString(R.string.astar_activity_toast_manhattan), Toast.LENGTH_SHORT).show()
                        empirics = 1
                        true
                    }
                    R.id.popupEuclidDist -> {
                        Toast.makeText(this, getString(R.string.astar_activity_toast_euclid), Toast.LENGTH_SHORT).show()
                        empirics = 2
                        true
                    }
                    R.id.popup4Directional -> {
                        Toast.makeText(this, getString(R.string.astar_activity_toast_4dir), Toast.LENGTH_LONG).show()
                        directions = 1
                        true
                    }
                    R.id.popup8Directional -> {
                        Toast.makeText(this, getString(R.string.astar_activity_toast_8dir), Toast.LENGTH_LONG).show()
                        directions = 2
                        true
                    }
                    R.id.popup8DirectionalCheck -> {
                        Toast.makeText(this, getString(R.string.astar_activity_toast_8dirc), Toast.LENGTH_LONG).show()
                        directions = 3
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private external fun algorithmAStar(
        bitmap: Bitmap,
        start_x: Int,
        start_y: Int,
        finish_x: Int,
        finish_y: Int,
        empiric: Int,
        directions: Int,
        pixel_size: Int
    ): IntArray

    private fun drawGrid() {
        for(i in pixelSize until aStarMapWidth - 1 step pixelSize) {
            for(j in 0 until aStarMapHeight - 1) {
                aStarMap.setPixel(i, j, getColor(R.color.aStarColorGrid))
            }
        }

        for(i in pixelSize until aStarMapHeight - 1 step pixelSize) {
            for(j in 0 until aStarMapWidth - 1) {
                aStarMap.setPixel(j, i, getColor(R.color.aStarColorGrid))
            }
        }
    }

    private fun colorizeSquare(
        coordX: Int,
        coordY: Int,
        color: Int
    ) {
        for (i in 1 until pixelSize - 1) {
            for (j in 1 until pixelSize - 1) {
                aStarMap.setPixel(coordX + i, coordY + j, color)
            }
        }
    }

    private fun tryClearPath() {
        if(pathIsDrown){
            for(i in path) {
                colorizeSquare(
                    pixelSize * (i % (aStarMapWidth / pixelSize)),
                    pixelSize * (i / (aStarMapWidth / pixelSize)),
                    getColor(R.color.aStarColorEmpty)
                )
            }

            colorizeSquare(startX, startY, getColor(R.color.aStarColorStart))
            colorizeSquare(finishX, finishY, getColor(R.color.aStarColorFinish))
            pathIsDrown = false
        }
    }

    fun processButtonPressing(
        view: View
    ) {
        tryClearPath()
        when(view.id) {
            R.id.imageButtonBack -> {
                finish()
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
            R.id.buttonErase -> {
                inputState = 4
            }
            R.id.buttonRunAStar -> {
                runAStar()
            }
        }
    }

    private fun runAStar() {
        if(startX != -1 && startY != -1 &&
            finishX != -1 && finishY != -1) {
            progressBarAStar.visibility = View.VISIBLE
            Thread {
                path = algorithmAStar(
                    aStarMap,
                    startX / pixelSize,
                    startY / pixelSize,
                    finishX / pixelSize,
                    finishY / pixelSize,
                    empirics,
                    directions,
                    pixelSize
                )

                imageViewAStarMap.post {
                    for (i in path) {
                        colorizeSquare(
                            pixelSize * (i % (aStarMapWidth / pixelSize)),
                            pixelSize * (i / (aStarMapWidth / pixelSize)),
                            getColor(R.color.aStarColorPath)
                        )
                    }

                    colorizeSquare(startX, startY, getColor(R.color.aStarColorStart))
                    colorizeSquare(finishX, finishY, getColor(R.color.aStarColorFinish))
                }

                progressBarAStar.post {
                    progressBarAStar.visibility = View.GONE
                }

                pathIsDrown = true
            }.start()
        }
        else {
            Toast.makeText(this, getString(R.string.astar_activity_toast_setpoints), Toast.LENGTH_LONG).show()
        }
    }
}
