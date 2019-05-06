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

    fun processPopupMenuItem(view: View) {
        when(view.id) {
            R.id.imageButtonAStarSettingsButton -> {
                val popupMenu = android.support.v7.widget.PopupMenu(this, imageButtonAStarSettingsButton)
                popupMenu.inflate(R.menu.a_star_popup_menu)
                popupMenu.show()
            }
            R.id.popupResizeAStarMap -> {
                Toast.makeText(this, "You tried to change size", Toast.LENGTH_SHORT).show()
            }
            R.id.popupManhattanDist -> {
                Toast.makeText(this, "You chose first empiric", Toast.LENGTH_SHORT).show()
            }
            R.id.popupEuclidDist -> {
                Toast.makeText(this, "You chose second empiric", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun processButtonPressing(view: View) {
        when(view.id) {
            imageButtonBackToMainActivity.id -> {
                returnToPreviousActivity()
            }
        }
    }

    private fun returnToPreviousActivity(){
        finish()
    }
}
