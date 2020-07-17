package com.androidv.foodonwheel.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.adapter.OrderHistoryRecyclerAdapter
import com.androidv.foodonwheel.model.OrderHistoryInfo
import com.androidv.foodonwheel.util.ConnectionManager
import org.json.JSONException

class OrderHistory(val contextParam: Context) : Fragment() {

    lateinit var layoutManager1: RecyclerView.LayoutManager
    lateinit var menuAdapter1: OrderHistoryRecyclerAdapter
    lateinit var recyclerViewAllOrders: RecyclerView

    lateinit var img_hist: ImageView
    lateinit var txt_hist: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerViewAllOrders = view.findViewById(R.id.rv_history)

        layoutManager1 = LinearLayoutManager(activity)

        img_hist = view.findViewById(R.id.img_hist)
        txt_hist = view.findViewById(R.id.txt_hist)

        img_hist.visibility = View.GONE
        txt_hist.visibility = View.GONE

        val orderedRestList = ArrayList<OrderHistoryInfo>()

        val sharedPreferences = contextParam.getSharedPreferences(
            getString(R.string.preference_file),
            Context.MODE_PRIVATE
        )

        val user_id = sharedPreferences.getString(getString(R.string.user_id), "0")


        if (ConnectionManager().checkConnectivity(activity as Context)) {

            try {
                val queue = Volley.newRequestQueue(context)

                val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"

                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener {

                        val responseJson = it.getJSONObject("data")

                        val success = responseJson.getBoolean("success")

                        if (success) {
                            val data = responseJson.getJSONArray("data")

                            if (data.length() > 0) {

                                for(i in 0 until data.length()){
                                    val restaurantItemJsonObject = data.getJSONObject(i)

                                    val eachRestaurantObject = OrderHistoryInfo(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at").substring(0,10))

                                        orderedRestList.add(eachRestaurantObject)

                                    menuAdapter1= OrderHistoryRecyclerAdapter(contextParam,orderedRestList) //setting the adapter with data

                                    recyclerViewAllOrders.adapter = menuAdapter1
                                    recyclerViewAllOrders.layoutManager = layoutManager1
                                }

                            }else{
                             /*   Toast.makeText(
                                    context,
                                    "No Orders Placed yet!!!",
                                    Toast.LENGTH_SHORT
                                ).show()*/
                                img_hist.visibility = View.VISIBLE
                                txt_hist.visibility = View.VISIBLE
                            }

                        }

                    }, Response.ErrorListener {
                        println("Error is " + it)
                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
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
                    context,
                    "Some Unexpected error occured!!!",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            //Internet is not available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("ERROR")
            dialog.setMessage("Internet Connection is not available")
            dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }


        return view
    }

}