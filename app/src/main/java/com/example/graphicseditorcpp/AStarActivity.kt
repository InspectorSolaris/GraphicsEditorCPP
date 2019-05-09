package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*

class AStarActivity : AppCompatActivity() {

    private var nSize = 1000
    private var mSize = 1000
    private var aStarMap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    private var empirics = 1
    private var directions = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)

        aStarMap.eraseColor(Color.WHITE)
        imageViewAStarMap.layoutParams.width = aStarMap.width
        imageViewAStarMap.layoutParams.height = aStarMap.height
        imageViewAStarMap.setImageBitmap(aStarMap)

        imageViewAStarMap.setOnTouchListener { _, motionEvent ->

            for(i in 0..0)
                for(j in 0..0)
                    aStarMap.setPixel(motionEvent.x.toInt() + i, motionEvent.y.toInt() + j, Color.BLACK)

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
        }
    }

    private fun backToPreviousActivity(){
        finish()
    }
}
