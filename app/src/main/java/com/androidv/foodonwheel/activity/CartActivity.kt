package com.androidv.foodonwheel.activity

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.adapter.CartRecyclerAdapter
import com.androidv.foodonwheel.model.CartItems
import com.androidv.foodonwheel.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var txtOrderingForm: TextView
    lateinit var btnPlaceOrder: Button
    lateinit var cartRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var cartAdapter: CartRecyclerAdapter
    lateinit var txtTotalAmount: TextView

    lateinit var sharedPreferences: SharedPreferences

    var totalAmount = 0

    var cartListItems = arrayListOf<CartItems>()

    lateinit var restaurent_Id: String
    lateinit var restaurent_Name: String
    lateinit var menuSelected_Id: ArrayList<String>

    //FOR NOTIFICATION
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var notificationBuilder: Notification.Builder
    val channelId = "com.androidv.foodonwheel.activity"
    val description = "Order Successfully Placed"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        txtTotalAmount = findViewById(R.id.txtTotalAmount)

        txtOrderingForm = findViewById(R.id.txtCartRestName)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        cartRecyclerView = findViewById(R.id.CartRecyclerLayout)
        layoutManager = LinearLayoutManager(this@CartActivity)

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file), Context.MODE_PRIVATE)

        restaurent_Id = intent.getStringExtra("restaurant_Id")
        restaurent_Name = intent.getStringExtra("restaurant_Name")
        menuSelected_Id = intent.getStringArrayListExtra("menuSelected_id")
        println("CartActivity: menuSelected_Id= $menuSelected_Id")

        txtOrderingForm.text = restaurent_Name.toString()

        val used_Id = sharedPreferences.getString(getString(R.string.user_id), "0")

        viewCart()

        btnPlaceOrder.setOnClickListener {
            println("CartActivity: PlacingOrder")
            placeOrder()
        }

    }

    fun viewCart() {

        if (ConnectionManager().checkConnectivity(this)) {
            try {
                val queue = Volley.newRequestQueue(this)
                val url =
                    "http://13.235.250.119/v2/restaurants/fetch_result/$restaurent_Id"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url, null,
                    Response.Listener {

                        val responseJsonData = it.getJSONObject("data")
                        val success = responseJsonData.getBoolean("success")

                        if (success) {
                            val data = responseJsonData.getJSONArray("data")

                            cartListItems.clear()
                            totalAmount = 0
                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (menuSelected_Id.contains(cartItemJsonObject.getString("id"))) {
                                    val menuObject = CartItems(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()

                                    cartListItems.add(menuObject)
                                }

                                cartAdapter = CartRecyclerAdapter(this, cartListItems)
                                cartRecyclerView.adapter = cartAdapter
                                cartRecyclerView.layoutManager = layoutManager
                            }
                            txtTotalAmount.text = "$totalAmount/-"
                        }

                    }, Response.ErrorListener {
                        println("In Error Listener")
                        println("ERROR is $it")

                        if (this@CartActivity != null) {
                            Toast.makeText(
                                this@CartActivity,
                                "Error occurred while connecting server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            } catch (e: JSONException) {
                Toast.makeText(this@CartActivity, "Something went wrong!", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            //Internet is not available
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()
        }

    }

    fun placeOrder() {
        println("CartActivity: In placeOrder")

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (ConnectionManager().checkConnectivity(this@CartActivity)) {
            println("CartActivity: placeOrder Connection Found Successfully")
            try {

                val queue = Volley.newRequestQueue(this@CartActivity)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                val foodJsonArray = JSONArray()
                for (foodItem in menuSelected_Id) {
                    val singleItemObject = JSONObject()
                    singleItemObject.put("food_item_id", foodItem)
                    foodJsonArray.put(singleItemObject)
                }
                println("CartActivity: placeOrder foodJsonArray passed")
                println("Data i foodJson array is: $foodJsonArray")

                val sendOrder = JSONObject()
                println("CartActivity: sendOrder = JSONObject Passed")

                sendOrder.put(
                    "user_id",
                    sharedPreferences.getString(getString(R.string.user_id), "0")
                )
                sendOrder.put("restaurant_id", restaurent_Id.toString())
                sendOrder.put("total_cost", totalAmount)
                sendOrder.put("food", foodJsonArray)

                println("CartActivity: sendOrder = $sendOrder")
                println("CartActivity: The value successfully assigned in sendOrder")


                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, sendOrder,
                    Response.Listener {
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")

                        if (success) {
                            println("CartActivity: The value of success is: $success")
                            Toast.makeText(
                                this,
                                "Order Placed Successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                            orderPlacedSuccessfully()
                        }
                    }, Response.ErrorListener {
                        println("In Error Listener")
                        println("CartActivity:ERROR is $it")

                        if (this@CartActivity != null) {
                            Toast.makeText(
                                this@CartActivity,
                                "Error occurred while connecting server",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = getString(R.string.token)
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(this@CartActivity, "Something went wrong!", Toast.LENGTH_SHORT)
                    .show()
            }

        } else {
            //Internet is not available
            val dialog = AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@CartActivity)
            }
            dialog.create()
            dialog.show()

        }


    }

    fun showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.YELLOW
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder =
                Notification.Builder(this, channelId).setContentTitle("Food on Wheel")
                    .setContentText("Order Placed Successfully!")
                    .setSmallIcon(R.drawable.food)
        } else {
            notificationBuilder =
                Notification.Builder(this).setContentText("Order Placed Successfully")
                    .setSmallIcon(R.drawable.food)
        }
        notificationManager.notify(1234, notificationBuilder.build())
    }

    fun orderPlacedSuccessfully() {
        print("CartActivity: OrderPlaced Successfully")
        val intent = Intent(this@CartActivity, OrderPlaced::class.java)
        showNotification()
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {

        val dialog = AlertDialog.Builder(this@CartActivity)
        dialog.setTitle("Cancel Order")
        dialog.setMessage("Do you want to cancel order?")
        dialog.setPositiveButton("YES") { text, listner ->
            val intent = Intent(this@CartActivity, main_menu::class.java)
            startActivity(intent)
            finish()
        }
        dialog.setNegativeButton("No") { text, listner ->

        }
        dialog.create()
        dialog.show()
    }
}
