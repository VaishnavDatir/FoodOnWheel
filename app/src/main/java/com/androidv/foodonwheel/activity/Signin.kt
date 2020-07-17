package com.androidv.foodonwheel.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_signin.*
import org.json.JSONException
import org.json.JSONObject

class Signin : AppCompatActivity() {

    lateinit var txt_name: EditText
    lateinit var txt_emailid: EditText
    lateinit var txt_mobilno: EditText
    lateinit var txt_address: EditText
    lateinit var txt_pass: EditText
    lateinit var txt_cpass: EditText

    lateinit var btn_register: Button

    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)
        title = "Register Yourself"

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        txt_name = findViewById(R.id.et_name)
        txt_emailid = findViewById(R.id.et_email)
        txt_mobilno = findViewById(R.id.et_mn)
        txt_address = findViewById(R.id.et_address)
        txt_pass = findViewById(R.id.et_pass)
        txt_cpass = findViewById(R.id.et_cpass)

        btn_register = findViewById(R.id.btn_rm)

        btn_register.setOnClickListener {
            if (txt_name.text.length < 3 || txt_name.text.isBlank()) {
                txt_name.error = "Invalid Name"
                txt_name.requestFocus()
            } else if (txt_mobilno.text.length < 10 || txt_mobilno.text.isBlank()) {
                txt_mobilno.error = "Enter correct Mobile Number"
                txt_mobilno.requestFocus()
            } else if (!txt_emailid.text.contains("@") || txt_emailid.text.isBlank()) {
                txt_emailid.error = "Enter correct email id"
                txt_emailid.requestFocus()
            } else if (txt_address.text.isBlank()) {
                txt_address.error = "Enter Address"
                txt_address.requestFocus()
            } else if (txt_pass.text.length < 6 || txt_pass.text.isBlank()) {
                txt_pass.error = "Minimum 6 characters are required!"
                txt_pass.requestFocus()
            } else if (txt_cpass.text.length < 6 || txt_cpass.text.isBlank()) {
                txt_cpass.error = "Minimum 6 characters are required!"
                txt_cpass.requestFocus()
            } else {
                if (txt_pass.text.toString() == txt_cpass.text.toString()) {
                    saveUserData()
                } else {
                    txt_pass.error = "Password do not match"
                    txt_pass.requestFocus()
                    txt_pass.setText("")
                    txt_cpass.setText("")

                }
            }
        }
    }

    fun saveUserData() {
        // println("Password Same")

        if (ConnectionManager().checkConnectivity(this@Signin)) {

            val registerUser = JSONObject()
            registerUser.put("name", txt_name.text)
            registerUser.put("mobile_number", txt_mobilno.text)
            registerUser.put("password", txt_cpass.text)
            registerUser.put("address", txt_address.text)
            registerUser.put("email", txt_emailid.text)

            val queue = Volley.newRequestQueue(this@Signin)

            val url = "http://13.235.250.119/v2/register/fetch_result"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url,
                registerUser,
                Response.Listener {
                    try {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        println("Signin: The value of success is $success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONObject("data")
                            sharedPreferences.edit().putString(
                                getString(R.string.user_id),
                                data.getString("user_id")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.user_name),
                                data.getString("name")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.mail_id), data.getString("email")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.mobile_no),
                                data.getString("mobile_number")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.address),
                                data.getString("address")
                            ).apply()

                            Toast.makeText(this@Signin, "Registration Successful", Toast.LENGTH_SHORT).show()
                            finish()

                        } else {

                            Toast.makeText(this@Signin, "Mobile number Or Email id already exist", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Signin,
                            "Error while getting data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@Signin, "Volley Error: $it", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)

                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }

    }

}