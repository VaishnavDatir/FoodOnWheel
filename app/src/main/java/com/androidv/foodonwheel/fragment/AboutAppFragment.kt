package com.androidv.foodonwheel.fragment

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidv.foodonwheel.BuildConfig
import com.androidv.foodonwheel.R

class AboutAppFragment : Fragment() {

    lateinit var txtAppVersion: TextView
    lateinit var txtAboutApp: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_about_app, container, false)

       txtAboutApp = view.findViewById(R.id.info)
        txtAppVersion = view.findViewById(R.id.appversion)

         txtAboutApp.text = "Food on Wheel is a food delivery service " +
                "that does Home Deliveries to it's customers."

        txtAppVersion.text = BuildConfig.VERSION_NAME

        return view
    }

}