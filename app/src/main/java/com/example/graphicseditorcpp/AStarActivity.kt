package com.example.graphicseditorcpp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_astar.*

class AStarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_astar)

        aStarSettingsButton.setOnClickListener {
            val popupMenu = android.support.v7.widget.PopupMenu(this, it)
            popupMenu.inflate(R.menu.a_star_popup_menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when(item.itemId){
                    R.id.resizeItem -> {
                        Toast.makeText(this, "You tried to change size", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.firstEmpiric -> {
                        Toast.makeText(this, "You chose first empiric", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.secondEmpiric -> {
                        Toast.makeText(this, "You chose second empiric", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
