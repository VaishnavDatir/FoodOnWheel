package com.androidv.foodonwheel.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.androidv.foodonwheel.R

class ProfileFragment(val contextParam: Context) : Fragment() {

    lateinit var userName: TextView
    lateinit var userEmail: TextView
    lateinit var userPhone: TextView
    lateinit var userAddress: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userName = view.findViewById(R.id.userName)
        userPhone = view.findViewById(R.id.mobileNo)
        userEmail = view.findViewById(R.id.emailId)
        userAddress = view.findViewById(R.id.address)

        sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )

        userName.text = sharedPreferences.getString(getString(R.string.user_name), "")
        userEmail.text = sharedPreferences.getString(getString(R.string.mail_id), "")
        userPhone.text = sharedPreferences.getString(getString(R.string.mobile_no),"")
        userAddress.text = sharedPreferences.getString(getString(R.string.address),"")

        return view
    }

}