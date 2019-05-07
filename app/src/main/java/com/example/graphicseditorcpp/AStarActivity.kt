package com.example.graphicseditorcpp

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*

class AStarActivity : AppCompatActivity() {

    private var nSize = 10
    private var mSize = 10
    private var aStarMap = Bitmap.createBitmap(nSize, mSize, Bitmap.Config.ARGB_8888)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)

        aStarMap.eraseColor(android.graphics.Color.WHITE)
        imageViewAStarMap.setImageBitmap(aStarMap)

        imageButtonAStarSettings.setOnClickListener {
            val popupMenu = android.support.v7.widget.PopupMenu(this, it)
            popupMenu.inflate(R.menu.a_star_popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.popupResizeMap -> {
                        Toast.makeText(this, "You tried to change size", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.popupManhattanDist -> {
                        Toast.makeText(this, "You chose Manhattan Dist as empiric", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.popupEuclidDist -> {
                        Toast.makeText(this, "You chose Euclid Dist as empiric", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.popup4Directional -> {
                        Toast.makeText(this, "4 directions", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.popup8Directional -> {
                        Toast.makeText(this, "8 directions", Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.popup8DirectionalCheck -> {
                        Toast.makeText(this, "8 directions with check", Toast.LENGTH_LONG).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            R.id.imageButtonBack -> {
                backToPreviousActivity()
            }
            R.id.imageButtonAStarSettings -> {

                val popupMenu = android.support.v7.widget.PopupMenu(this, imageButtonAStarSettings)
                popupMenu.inflate(R.menu.a_star_popup_menu)
                popupMenu.show()

            }
            R.id.popupResizeMap -> {
                Toast.makeText(this, "You tried to change size", Toast.LENGTH_LONG).show()
            }
            R.id.popupManhattanDist -> {
                Toast.makeText(this, "You chose Manhattan Dist as empiric", Toast.LENGTH_LONG).show()
            }
            R.id.popupEuclidDist -> {
                Toast.makeText(this, "You chose Euclid Dist as empiric", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun backToPreviousActivity(){
        finish()
    }
}
