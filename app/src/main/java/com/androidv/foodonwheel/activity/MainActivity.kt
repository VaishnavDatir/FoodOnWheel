package com.androidv.foodonwheel.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.util.ConnectionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //requestWindowFeature(Window.FEATURE_NO_TITLE)

       window.decorView.apply {
           systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
           systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
       }

        startUp()
    }

    fun startUp() {

        if (ConnectionManager().checkConnectivity(this)) {
            //Internet is available
            Handler().postDelayed({
                val startAct = Intent(this@MainActivity, Login::class.java)
                startActivity(startAct)
                finish()
            }, 2000)
        } else {
            //Internet is not available
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setCancelable(false)
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }
}