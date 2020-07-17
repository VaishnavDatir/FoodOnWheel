package com.androidv.foodonwheel.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.androidv.foodonwheel.R

class OrderPlaced : AppCompatActivity() {

    lateinit var btn_ok: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        btn_ok = findViewById(R.id.ok)
        btn_ok.setOnClickListener {
            val intent = Intent(this@OrderPlaced, main_menu::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this@OrderPlaced, main_menu::class.java)
        startActivity(intent)
        finish()
    }
}