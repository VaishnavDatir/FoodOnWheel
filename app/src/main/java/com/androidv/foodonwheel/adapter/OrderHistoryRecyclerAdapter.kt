package com.androidv.foodonwheel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.model.CartItems
import com.androidv.foodonwheel.model.OrderHistoryInfo
import com.androidv.foodonwheel.util.ConnectionManager
import kotlinx.android.synthetic.main.recycler_history_single_row.view.*
import org.json.JSONException

class OrderHistoryRecyclerAdapter(
    val context: Context,
    val orderedRestList: ArrayList<OrderHistoryInfo>
) :
    RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtHistoryRestName: TextView = view.findViewById(R.id.history_restName)
        val txtHistoryDate: TextView = view.findViewById(R.id.history_date)
        val recyclerHistoryCart: RecyclerView = view.findViewById(R.id.history_cart)
        val txtHistoryTotalAmount :TextView = view.findViewById(R.id.txtHistoryTotalAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_history_single_row, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderedRestList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val orders = orderedRestList[position]
        holder.txtHistoryRestName.text = orders.restaurant_name
        holder.txtHistoryTotalAmount.text = orders.total_cost

        var formattedDate = orders.order_placed_at
        formattedDate = formattedDate.replace("-", "/")
        formattedDate = formattedDate.substring(0, 6) + 20 + formattedDate.substring(6, 8)
        holder.txtHistoryDate.text = formattedDate

        var layoutManager = LinearLayoutManager(context)
        var orderedMenuAdapter: CartRecyclerAdapter

        if (ConnectionManager().checkConnectivity(context)) {
            try {

                val orderedMenuEachRest = ArrayList<CartItems>()

                val sharedPreferences = context.getSharedPreferences(
                    context.getString(R.string.preference_file),
                    Context.MODE_PRIVATE
                )

                val user_id = sharedPreferences.getString(context.getString(R.string.user_id), "0")

                val queue = Volley.newRequestQueue(context)

                val url = "http://13.235.250.119/v2/orders/fetch_result/$user_id"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET, url, null,
                    Response.Listener {

                        val responseJson = it.getJSONObject("data")

                        val success = responseJson.getBoolean("success")

                        if (success) {
                            val data = responseJson.getJSONArray("data")

                            val getRestaurantJsonObject = data.getJSONObject(position)

                            orderedMenuEachRest.clear()

                            val foodOrderdJsonArray =
                                getRestaurantJsonObject.getJSONArray("food_items")

                            for (items in 0 until foodOrderdJsonArray.length()) {
                                val eachFoodItems =
                                    foodOrderdJsonArray.getJSONObject(items) // for each food item

                                val itemObject = CartItems(
                                    eachFoodItems.getString("food_item_id"),
                                    eachFoodItems.getString("name"),
                                    eachFoodItems.getString("cost"),
                                    "000"                   //as no restaurant id is given
                                )
                                orderedMenuEachRest.add(itemObject)
                            }
                            orderedMenuAdapter = CartRecyclerAdapter(context, orderedMenuEachRest)

                            holder.recyclerHistoryCart.adapter =
                                orderedMenuAdapter // bind the recyclerView to the adapter

                            holder.recyclerHistoryCart.layoutManager = layoutManager
                        }

                    }, Response.ErrorListener {
                        println("Error is " + it)
                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = context.getString(R.string.token)
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
        }
    }
}