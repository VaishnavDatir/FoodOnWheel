package com.androidv.foodonwheel.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    lateinit var btn_login: Button
    lateinit var forgot_pass: TextView
    lateinit var signin: TextView

    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText

    lateinit var chk_loggedin: CheckBox

    lateinit var sharedPreferences: SharedPreferences

    lateinit var rlprogressBar: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        rlprogressBar = findViewById(R.id.LayoutprogressBar)
        progressBar = findViewById(R.id.progressBar)

        rlprogressBar.visibility = View.GONE

        title = "LOGIN"

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

/*        val mob_no = sharedPreferences.getString(getString(R.string.mobile_no), "")
        val password = sharedPreferences.getString(getString(R.string.user_password), "")*/

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)


        btn_login = findViewById(R.id.btn_login)
        forgot_pass = findViewById(R.id.tv_fp)
        signin = findViewById(R.id.tv_signin)

        etMobileNumber = findViewById(R.id.et_mn)
        etPassword = findViewById(R.id.et_password)

        chk_loggedin = findViewById(R.id.chk_Logged_in)



        forgot_pass.setOnClickListener {
            val startAct = Intent(this@Login, Forgot_password::class.java)
            startActivity(startAct)
        }

        signin.setOnClickListener {
            val startAct = Intent(this@Login, Signin::class.java)
            startActivity(startAct)
        }

        if (isLoggedIn) {
            userSuccessfullyLoggedIn()
        }

        btn_login.setOnClickListener {

            when {
                etMobileNumber.text.isBlank() -> {
                    etMobileNumber.error = "Mobile Number Missing"
                    etMobileNumber.requestFocus()
                }
                etPassword.text.isBlank() -> {
                    etPassword.error = "Password Missing"
                    etPassword.requestFocus()
                }
                else -> {
                    rlprogressBar.visibility = View.VISIBLE
                    loginUser()
                }
            }

        }
    }

    fun loginUser() {
        if (ConnectionManager().checkConnectivity(this@Login)) {

            val loginUser = JSONObject()

            loginUser.put("mobile_number", etMobileNumber.text)
            loginUser.put("password", etPassword.text)

            val queue = Volley.newRequestQueue(this@Login)

            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.POST,
                url,
                loginUser,
                Response.Listener {

                    try {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

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
                                getString(R.string.mail_id),
                                data.getString("email")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.mobile_no),
                                data.getString("mobile_number")
                            ).apply()
                            sharedPreferences.edit().putString(
                                getString(R.string.address),
                                data.getString("address")
                            ).apply()

                            if (chk_loggedin.isChecked) {
                                sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                            } else {
                                sharedPreferences.edit().putBoolean("isLoggedIn", false).apply()
                            }
                            userSuccessfullyLoggedIn()

                        } else {
                            Toast.makeText(this@Login, "User not found!", Toast.LENGTH_SHORT).show()
                            etMobileNumber.setText("")
                            etPassword.setText("")
                            rlprogressBar.visibility = View.GONE
                            etMobileNumber.requestFocus()

                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Login,
                            "Error while getting data",
                            Toast.LENGTH_SHORT
                        ).show()
                        rlprogressBar.visibility = View.GONE
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@Login, "Volley Error: $it", Toast.LENGTH_SHORT).show()
                    rlprogressBar.visibility = View.GONE
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()

                    headers["Content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)

                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            //Internet is not available
            rlprogressBar.visibility = View.GONE
            val dialog = AlertDialog.Builder(this@Login)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@Login)
            }
            dialog.create()
            dialog.show()
        }

    }

    fun userSuccessfullyLoggedIn() {
        /*if (chk_loggedin.isChecked) {
            sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        }*/
        val intent = Intent(this@Login, main_menu::class.java)
        val name = sharedPreferences.getString(getString(R.string.user_name),"")
        Toast.makeText(this@Login, "Welcome $name",Toast.LENGTH_SHORT).show()
        startActivity(intent)
        finish()
        rlprogressBar.visibility = View.GONE
    }
}
