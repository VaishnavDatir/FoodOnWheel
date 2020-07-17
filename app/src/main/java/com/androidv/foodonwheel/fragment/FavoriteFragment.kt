package com.androidv.foodonwheel.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.adapter.FavouriteRecyclerAdapter
import com.androidv.foodonwheel.database.RestaurantDatabase
import com.androidv.foodonwheel.database.RestaurantEntity

class FavoriteFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavouriteRecyclerAdapter

    var dbRestList = listOf<RestaurantEntity>()

    lateinit var imgFav: ImageView
    lateinit var txt_fav: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)

        layoutManager = LinearLayoutManager(activity)

        dbRestList = RetrieveFavourite(activity as Context).execute().get()

        if(activity !=null){

            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbRestList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        imgFav =view.findViewById(R.id.img_fav)
        txt_fav = view.findViewById(R.id.txt_fav)

        imgFav.visibility = View.GONE
        txt_fav.visibility = View.GONE


        if(recyclerAdapter.restList.isEmpty()){
            //Toast.makeText(context,"There are no Favourite Restaurents",Toast.LENGTH_SHORT).show()
            imgFav.visibility = View.VISIBLE
            txt_fav.visibility = View.VISIBLE
        }

        return view
    }

    class RetrieveFavourite(val context: Context):AsyncTask<Void,Void,List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context,RestaurantDatabase::class.java,"rest-db").build()
            return db.restraurantDao().getAllRests()
        }
    }
}