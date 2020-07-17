package com.androidv.foodonwheel.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Forgot_password : AppCompatActivity() {

    lateinit var et_mobno: EditText
    lateinit var et_mailid: EditText

    lateinit var sharedPreferences: SharedPreferences

    lateinit var btn_next: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        title = "Forgot Password"

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)
/*
        val mob_no = sharedPreferences.getString(getString(R.string.mobile_no),"")
        val password = sharedPreferences.getString(getString(R.string.user_password),"")
*/

        et_mobno = findViewById(R.id.et_mobnoFP)
        et_mailid = findViewById(R.id.et_mailidFP)
        btn_next = findViewById(R.id.btn_next)

        btn_next.setOnClickListener {
            if (et_mobno.text.isBlank()) {
                et_mobno.error = "Mobile Number missing"
                et_mobno.requestFocus()
            } else if (et_mobno.text.length < 10) {
                et_mobno.error = "Enter correct Mobile Number"
                et_mobno.requestFocus()
            } else if (et_mailid.text.isBlank()) {
                et_mailid.error = "Email Id missing"
                et_mailid.requestFocus()
            } else if (!et_mailid.text.contains("@")) {
                et_mailid.error = "Enter correnct Email Id"
                et_mailid.requestFocus()
            } else {
                if (ConnectionManager().checkConnectivity(this@Forgot_password)) {
                    try {
                        val userInfo = JSONObject()
                        userInfo.put("mobile_number", et_mobno.text)
                        userInfo.put("email", et_mailid.text)

                        val queue = Volley.newRequestQueue(this@Forgot_password)

                        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
                            url,
                            userInfo,
                            Response.Listener {

                                val responseJsonObjectData = it.getJSONObject("data")

                                val success = responseJsonObjectData.getBoolean("success")

                                if (success) {
                                    val first_try = responseJsonObjectData.getBoolean("first_try")

                                    if (first_try) {
                                        Toast.makeText(
                                            this@Forgot_password,
                                            "OTP sent to registered Email Id",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            this@Forgot_password,
                                            "Already OTP sent to registered Email Id \n Use that!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    openRestPassword()
                                } else {
                                    Toast.makeText(
                                        this@Forgot_password,
                                        "User not Found",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            },
                            Response.ErrorListener {
                                Toast.makeText(
                                    this@Forgot_password,
                                    "Volley Error: $it",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = getString(R.string.token)
                                return headers
                            }
                        }
                        queue.add(jsonObjectRequest)
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Forgot_password,
                            "Error while getting data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun openRestPassword() {
        val intent = Intent(this@Forgot_password, ResetPassword::class.java)
        intent.putExtra("mobile_number", et_mobno.text.toString())
        startActivity(intent)
        finish()
        println("Forgot_password: Going to Reset Password")
    }

}
