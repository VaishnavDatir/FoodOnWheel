package com.androidv.foodonwheel.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.androidv.foodonwheel.R
import com.android.volley.Request
import com.android.volley.Response
import com.androidv.foodonwheel.adapter.HomeRecyclerAdapter
import com.androidv.foodonwheel.model.Restaurants
import com.androidv.foodonwheel.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.sort_radio_buttons.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment(val contextParam: Context) : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: HomeRecyclerAdapter

    lateinit var radioButtonView: View

    lateinit var et_Search: EditText
    val restaurantInfoList = arrayListOf<Restaurants>()

    lateinit var rlProgressbar: RelativeLayout
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)

        et_Search = view.findViewById(R.id.et_Search)

        rlProgressbar = view.findViewById(R.id.rlLoading_menu)
        progressBar = view.findViewById(R.id.pb_menu)

        recyclerHome = view.findViewById(R.id.recyclerHome)

        layoutManager = LinearLayoutManager(activity)


        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        println("Checking connection and going to Json")

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            //Internet is Available
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET,
                    url,
                    null,
                    Response.Listener {
                        try {
                            rlProgressbar.visibility = View.GONE
                            println("The Response is $it")
                            println("Getting success")

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")
                            println("The value of success is $success")

                            if (success) {
                                println("The value of success is $success")
                                val data = responseJsonObjectData.getJSONArray("data")
                                for (i in 0 until data.length()) {
                                    val restJsonObject = data.getJSONObject(i)
                                    val restObject = Restaurants(
                                        restJsonObject.getString("id"),
                                        restJsonObject.getString("name"),
                                        restJsonObject.getString("rating"),
                                        restJsonObject.getString("cost_for_one"),
                                        restJsonObject.getString("image_url")
                                    )
                                    restaurantInfoList.add(restObject)

                                    recyclerAdapter =
                                        HomeRecyclerAdapter(activity as Context, restaurantInfoList)
                                    recyclerHome.adapter = recyclerAdapter
                                    recyclerHome.layoutManager = layoutManager
                                }
                            } else {
                                Toast.makeText(
                                    activity as Context,
                                    "Error occurred while getting data from server.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(
                                activity as Context,
                                "Unexpected error occurred.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        println("In Error Listener")
                        println("ERROR is $it")

                        if (activity != null) {
                            Toast.makeText(
                                activity as Context,
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

        et_Search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //Toast.makeText(context, "Text Changed", Toast.LENGTH_SHORT).show()

                filterSearch(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })

        return view

    }

    fun filterSearch(styTyped: String) {
        val filteredList = arrayListOf<Restaurants>()

        for (rests in restaurantInfoList) {
            if (rests.restName.toLowerCase().contains(styTyped.toLowerCase())) {
                filteredList.add(rests)
            }
        }
        if (filteredList.size == 0) {
            Toast.makeText(context, "Cannot find the restaurant", Toast.LENGTH_SHORT).show()
        }
        recyclerAdapter.filterList(filteredList)
    }

    var costComparator = Comparator<Restaurants> { rest1, rest2 ->
        rest1.restCost.compareTo(rest2.restCost, true)
    }

    var ratingComparator = Comparator<Restaurants> { rest1, rest2 ->
        if (rest1.restRating.compareTo(rest2.restRating, true) == 0) {
            rest1.restName.compareTo(rest2.restName, true)
        }
        else{
            rest1.restRating.compareTo(rest2.restRating,true)
        }
    }

    var nameComparator = Comparator<Restaurants>{rest1, rest2 ->
        if(rest1.restName.compareTo(rest2.restName,true)==0){
            rest1.restCost.compareTo(rest2.restCost,true)
        }
        else{
            rest1.restName.compareTo(rest2.restName,true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {
            R.id.menu_sort -> {
                radioButtonView = View.inflate(contextParam, R.layout.sort_radio_buttons, null)
                androidx.appcompat.app.AlertDialog.Builder(contextParam).setTitle("Sort by")
                    .setView(radioButtonView).setPositiveButton("OK") { text, listner ->
                        if (radioButtonView.radioName.isChecked) {
                            Collections.sort(restaurantInfoList, nameComparator)
                            recyclerAdapter.notifyDataSetChanged() // update the changes to adapter
                        }
                        if (radioButtonView.radioCostHL.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged() // update the changes to adapter
                        }
                        if (radioButtonView.radioCostLH.isChecked) {
                            Collections.sort(restaurantInfoList, costComparator)
                            recyclerAdapter.notifyDataSetChanged() // update the changes to adapter
                        }
                        if (radioButtonView.radioRating.isChecked) {
                            Collections.sort(restaurantInfoList, ratingComparator)
                            restaurantInfoList.reverse()
                            recyclerAdapter.notifyDataSetChanged() // update the changes to adapter
                        }
                    }
                    .setNegativeButton("Cancel"){text ,listener ->}
                    .create().show()
            }

        }

        return super.onOptionsItemSelected(item)
    }

}