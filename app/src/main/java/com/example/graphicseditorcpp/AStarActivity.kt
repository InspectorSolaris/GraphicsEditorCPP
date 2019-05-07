package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*

class AStarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)
    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            R.id.imageButtonBack -> {
                goToMain()
            }
            R.id.imageButtonAStarSettings -> {

                val popupMenu = android.support.v7.widget.PopupMenu(this, imageButtonAStarSettings)
                popupMenu.inflate(R.menu.a_star_popup_menu)
                popupMenu.show()

            }
            R.id.popupResizeMap -> {
                Toast.makeText(this, "You tried to change size", Toast.LENGTH_SHORT).show()
            }
            R.id.popupManhattanDist -> {
                Toast.makeText(this, "You chose Manhattan Dist as empiric", Toast.LENGTH_SHORT).show()
            }
            R.id.popupEuclidDist -> {
                Toast.makeText(this, "You chose Euclid Dist as empiric", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMain(){
        finish()
    }
}
