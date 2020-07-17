package com.androidv.foodonwheel.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.activity.RestaurantMenu
import com.androidv.foodonwheel.activity.main_menu
import com.androidv.foodonwheel.database.RestaurantDatabase
import com.androidv.foodonwheel.database.RestaurantEntity
import com.androidv.foodonwheel.model.Restaurants
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(val context: Context, val restList: List<RestaurantEntity>) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {

    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestName: TextView = view.findViewById(R.id.txtRestName_home)
        val txtRestPrice: TextView = view.findViewById(R.id.txtRestPrice_home)
        val txtRestRating: TextView = view.findViewById(R.id.txtRestRating_home)
        val imgRestImage: ImageView = view.findViewById(R.id.imgRestImage_home)
        val imgRestFav: ImageView = view.findViewById(R.id.imgIsFav_home)

        val llContent: LinearLayout = view.findViewById(R.id.llContent_home)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return FavouriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restList.size
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val restaurants = restList[position]
        holder.txtRestName.text = restaurants.restName
        holder.txtRestPrice.text = restaurants.restCost
        holder.txtRestRating.text = restaurants.restRating

        Picasso.get().load(restaurants.restImage).error(R.drawable.ic_restro)
            .into(holder.imgRestImage)
        restaurants.rest_id



        holder.llContent.setOnClickListener {

            /*Toast.makeText(
                context,
                "Id is ${restaurants.rest_id}, name is ${restaurants.restName}, cost is ${restaurants.restCost}, rating is: ${restaurants.restRating}",
                Toast.LENGTH_SHORT
            ).show()*/
            val intent = Intent(context, RestaurantMenu::class.java)
            intent.putExtra("rest_id", restaurants.rest_id.toString())
            intent.putExtra("rest_name", restaurants.restName)
            intent.putExtra("rest_cost", restaurants.restCost)
            intent.putExtra("rest_rating", restaurants.restRating)
            intent.putExtra("rest_img", restaurants.restImage)
            println("Going to Restaurant Menu from Fav")
            context.startActivity(intent)
            (context as main_menu).finish() // to finish activity

        }

        val restaurantEntity = RestaurantEntity(
            restaurants.rest_id.toInt(),
            restaurants.restName,
            restaurants.restCost,
            restaurants.restRating,
            restaurants.restImage
        )

        val checkFav = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.imgRestFav.setImageResource(R.drawable.ic_fav)
        } else {
            holder.imgRestFav.setImageResource(R.drawable.ic_fav_not)
        }

        holder.imgRestFav.setOnClickListener {
            if (!HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurants.restName} added to favourites",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    holder.imgRestFav.setImageResource(R.drawable.ic_fav)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(
                        context,
                        "${restaurants.restName} removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.imgRestFav.setImageResource(R.drawable.ic_fav_not)
                } else {
                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /*class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


        *//* MODE 1 -> Check DB if the rest is favourite or not
         MODE 2 -> Save the rest into DB as favourite
         MODE 3 -> Remove the favourite book*//*


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

    }*/
}
