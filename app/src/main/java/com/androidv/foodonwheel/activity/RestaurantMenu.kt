package com.androidv.foodonwheel.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.adapter.RestaurantMenuRecyclerAdaptor
import com.androidv.foodonwheel.database.RestaurantDatabase
import com.androidv.foodonwheel.database.RestaurantEntity
import com.androidv.foodonwheel.model.RestaurantMenu
import com.androidv.foodonwheel.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_restaurant_menu.*
import org.json.JSONException
import org.json.JSONObject

class RestaurantMenu : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: RestaurantMenuRecyclerAdaptor

    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var restaurantCost: String
    lateinit var restauratRating: String
    lateinit var restaurantImg: String

    lateinit var rlProgressbar: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var restMenuToolbar: Toolbar

    lateinit var imgrestFav: ImageView

    lateinit var rlProceedToCart: RelativeLayout
    lateinit var btnProceedToCart: Button

    var restaurantMenuList = arrayListOf<RestaurantMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        restMenuToolbar = findViewById(R.id.restMenuToolbar)

        imgrestFav = findViewById(R.id.restFav)

        rlProceedToCart = findViewById(R.id.rlProceedToCart)
        btnProceedToCart = findViewById(R.id.btnProceedToCart)
        btnProceedToCart.visibility = View.GONE

        println("Getting Intent Values in Restaurant Menu")
        restaurantId = intent.getStringExtra("rest_id")
        restaurantName = intent.getStringExtra("rest_name")
        restaurantCost = intent.getStringExtra("rest_cost")
        restauratRating = intent.getStringExtra("rest_rating")
        restaurantImg = intent.getStringExtra("rest_img")

        println("Intent Values found successfully")

        setUpToolBar()

        val checkFav = DBAsyncTask(
            this,
            RestaurantEntity(
                restaurantId.toInt(),
                restaurantName,
                restaurantCost,
                restauratRating,
                restaurantImg
            ),
            1
        ).execute()
        val isFav = checkFav.get()

        if (isFav) {
            imgrestFav.setImageResource(R.drawable.ic_fav)
        } else {
            imgrestFav.setImageResource(R.drawable.ic_fav_not)
        }

        imgrestFav.setOnClickListener {
            if (!DBAsyncTask(
                    this, RestaurantEntity(
                        restaurantId.toInt(),
                        restaurantName,
                        restaurantCost,
                        restauratRating,
                        restaurantImg
                    ),
                    1
                ).execute().get()
            ) {
                val async = DBAsyncTask(
                    this, RestaurantEntity(
                        restaurantId.toInt(),
                        restaurantName,
                        restaurantCost,
                        restauratRating,
                        restaurantImg
                    ),
                    2
                ).execute()

                val result = async.get()

                if (result) {
                    Toast.makeText(this, "$restaurantName added to favourites", Toast.LENGTH_SHORT)
                        .show()
                    imgrestFav.setImageResource(R.drawable.ic_fav)
                } else {
                    Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(
                    this, RestaurantEntity(
                        restaurantId.toInt(),
                        restaurantName,
                        restaurantCost,
                        restauratRating,
                        restaurantImg
                    ),
                    3
                ).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        this,
                        "$restaurantName removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    imgrestFav.setImageResource(R.drawable.ic_fav_not)
                } else {
                    Toast.makeText(this, "Some error Occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //restaurantName = intent.getStringExtra("restaurantName")
        //Toast.makeText(this@RestaurantMenu, "The restaurant id is ${restaurantId}", Toast.LENGTH_SHORT).show()

        layoutManager = LinearLayoutManager(this@RestaurantMenu)

        recyclerView = findViewById(R.id.recyclerMenu)

        rlProgressbar = findViewById(R.id.rlLoading_menu)
        progressBar = findViewById(R.id.pb_menu)

        val queue = Volley.newRequestQueue(this@RestaurantMenu)
        val url =
            "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

//        val jsonParams = JSONObject()
//        jsonParams.put("restaurant_id", restaurantId)
        if (ConnectionManager().checkConnectivity(this@RestaurantMenu)) {
            //Connection is Available
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {

                    try {
                        rlProgressbar.visibility = View.GONE
                        println("Response for Menu is $it")
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        println("The value of success for menu is $success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restMenuJsonObject = data.getJSONObject(i)
                                val restMenuObject = RestaurantMenu(
                                    restMenuJsonObject.getString("id"),
                                    restMenuJsonObject.getString("name"),
                                    restMenuJsonObject.getString("cost_for_one")
                                )
                                restaurantMenuList.add(restMenuObject)

                                menuAdapter = RestaurantMenuRecyclerAdaptor(
                                    this@RestaurantMenu,
                                    restaurantMenuList,
                                    restaurantId.toString(),
                                    restaurantName.toString(),
                                    btnProceedToCart
                                )
                                //recyclerView = RestaurantMenuRecyclerAdaptor(this,restaurantMenuList)
                                recyclerMenu.adapter = menuAdapter
                                recyclerMenu.layoutManager = layoutManager
                            }
                        } else {
                            Toast.makeText(
                                this@RestaurantMenu,
                                "Error occurred while getting data from server.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@RestaurantMenu,
                            "Unexpected error occurred.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                },
                Response.ErrorListener {
                    println("In Error Listener")
                    println("ERROR is $it")

                    if (this@RestaurantMenu != null) {
                        Toast.makeText(
                            this@RestaurantMenu,
                            "Error occurred while connecting server",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = getString(R.string.token)
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        } else {
            //Internet is not available
            val dialog = AlertDialog.Builder(this@RestaurantMenu)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                this@RestaurantMenu.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@RestaurantMenu)
            }
            dialog.create()
            dialog.show()
        }


    }

    fun setUpToolBar() {
        setSupportActionBar(restMenuToolbar)
        supportActionBar?.title = restaurantName.toString()
    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


/*         MODE 1 -> Check DB if the rest is favourite or not
         MODE 2 -> Save the rest into DB as favourite
         MODE 3 -> Remove the favourite book*/


        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "rest-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    //Check DB if the rest is favourite or not
                    val rest: RestaurantEntity? =
                        db.restraurantDao().getRestBYId(restaurantEntity.rest_id.toString())
                    db.close()
                    return rest != null
                }

                2 -> {
                    db.restraurantDao().insertRest(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restraurantDao().deleteRest(restaurantEntity)
                    db.close()
                    return true
                }

            }
            return false
        }

    }

    override fun onBackPressed() {
        //super.onBackPressed()

        if (btnProceedToCart.visibility == View.VISIBLE) {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this@RestaurantMenu)
            dialog.setTitle("Exit")
            dialog.setMessage("Are you sure you want to go back? This will clear the items in cart")
            dialog.setPositiveButton("YES") { text, listener ->
                val intent = Intent(this@RestaurantMenu, main_menu::class.java)
                startActivity(intent)
                finish()
            }
            dialog.setNegativeButton("NO") { text, listener ->

            }
            dialog.create()
            dialog.show()
        }
        else{
            val intent = Intent(this@RestaurantMenu, main_menu::class.java)
            startActivity(intent)
            finish()
        }

    }
}