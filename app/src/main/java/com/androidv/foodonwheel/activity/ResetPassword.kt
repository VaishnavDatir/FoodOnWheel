package com.androidv.foodonwheel.activity

import android.content.Intent
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

class ResetPassword : AppCompatActivity() {

    lateinit var et_otpFP: EditText
    lateinit var et_passFP: EditText
    lateinit var et_cpassFP: EditText
    lateinit var btn_next: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        et_otpFP = findViewById(R.id.et_otpFP)
        et_passFP = findViewById(R.id.et_passFP)
        et_cpassFP = findViewById(R.id.et_cpassFP)

         val mobno = intent.getStringExtra("mobile_number")
        println("ResetPassword: The mobile number is $mobno")

        btn_next = findViewById(R.id.btn_nextFP)
        btn_next.setOnClickListener {
            if (et_otpFP.text.isBlank()) {
                et_otpFP.error = "Enter correct otp"
                et_otpFP.requestFocus()
            } else if (et_passFP.text.isBlank() || et_passFP.text.length < 5) {
                et_passFP.error = "Password must be atleast 6 characters"
                et_passFP.requestFocus()
            } else if (et_cpassFP.text.isBlank() || et_cpassFP.text.length < 5) {
                et_cpassFP.error = "Password must be atleast 6 characters"
                et_cpassFP.requestFocus()
            } else {
                if (et_passFP.text.toString() == et_cpassFP.text.toString()) {
                    if (ConnectionManager().checkConnectivity(this@ResetPassword)) {
                        try {
                            val userData = JSONObject()
                            userData.put("mobile_number", mobno.toString())
                            userData.put("password", et_cpassFP.text)
                            userData.put("otp", et_otpFP.text)

                            val queue = Volley.newRequestQueue(this@ResetPassword)

                            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                            val jsonObjectRequest =
                                object : JsonObjectRequest(Request.Method.POST, url, userData,
                                    Response.Listener {

                                        val responseJsonObjectData = it.getJSONObject("data")

                                        val success = responseJsonObjectData.getBoolean("success")

                                        if(success){
                                            val successMessage = responseJsonObjectData.getString("successMessage")
                                            Toast.makeText(this@ResetPassword, successMessage,Toast.LENGTH_SHORT).show()
                                            openLoginScreen()
                                        }
                                        else{
                                            Toast.makeText(this@ResetPassword, "Password Cannot be changed",Toast.LENGTH_SHORT).show()
                                        }

                                    }, Response.ErrorListener {
                                        /*Toast.makeText(
                                            this@ResetPassword,
                                            "Incorrect OTP",
                                            Toast.LENGTH_SHORT
                                        ).show()*/
                                        et_otpFP.error = "Incorrect OTP"
                                        et_otpFP.requestFocus()
                                    }) {
                                    override fun getHeaders(): MutableMap<String, String> {
                                        val headers = HashMap<String, String>()
                                        headers["content-type"] = "application/json"
                                        headers["token"] = getString(R.string.token)
                                        return headers
                                    }
                                }
                            queue.add(jsonObjectRequest)
                        } catch (e: JSONException) {
                            Toast.makeText(
                                this@ResetPassword,
                                "Error while getting data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }else{
                    et_passFP.error = "Password do not match"
                    et_passFP.setText("")
                    et_cpassFP.setText("")
                    et_passFP.requestFocus()

                }
            }
        }
    }

    fun openLoginScreen(){
        val intent = Intent(this@ResetPassword, Login::class.java)
        startActivity(intent)
        finish()
    }
}