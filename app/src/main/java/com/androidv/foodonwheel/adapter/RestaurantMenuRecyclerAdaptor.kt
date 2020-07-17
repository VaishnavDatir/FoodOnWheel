package com.androidv.foodonwheel.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.transition.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.activity.CartActivity
import com.androidv.foodonwheel.model.RestaurantMenu

//      val restId: String,
//    val restName: String,
//    val proceedToCartPass: RelativeLayout,
//    val btnProceedToCart: Button,
class RestaurantMenuRecyclerAdaptor(
    val context: Context,
    val menuList: ArrayList<RestaurantMenu>,
    val restaurantId: String,
    val restaurantName: String,
    val btnProceedToCart: Button
) :
    RecyclerView.Adapter<RestaurantMenuRecyclerAdaptor.RestaurantMenuViewHolder>() {

    var menuSelectedCount: Int = 0
    lateinit var proceedToCart: RelativeLayout

    var menuSelectedId = arrayListOf<String>()

    class RestaurantMenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtMenuName: TextView = view.findViewById(R.id.txtItemName)
        val txtMenuPrice: TextView = view.findViewById(R.id.txtItemPrice)
        val txtMenuCount: TextView = view.findViewById(R.id.txtSrNo)
        val llContent_Menu: LinearLayout = view.findViewById(R.id.llItem)
        val btnAddToCart: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_restaurant_menu_single_row, parent, false)
        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {
        val restaurantMenuItem = menuList[position]
        //println("Restaurant Menu Adapter: The menu list is: $menuList")
        //println("Restaurant Menu Adapter: ${restaurantMenuItem.menuId}")
        holder.txtMenuName.text = restaurantMenuItem.menuName
        holder.txtMenuPrice.text = restaurantMenuItem.menuCost
        holder.txtMenuCount.text = (position + 1).toString()

        holder.btnAddToCart.setOnClickListener {
            if (holder.btnAddToCart.text.toString().equals("REMOVE")) {
                menuSelectedCount--
                // menuSelectedId.remove(holder.btnAddToCart.getTag().toString())
                //Toast.makeText(context,"ID is ${restaurantMenuItem.menuId}",Toast.LENGTH_SHORT).show()
                menuSelectedId.remove(restaurantMenuItem.menuId)
                println("Restaurant Menu Adapter: Array of menu selected: $menuSelectedId")
                println("Restaurant Menu Adapter: Number of menu selected: $menuSelectedCount")
                holder.btnAddToCart.text = "ADD"
                if (menuSelectedCount > 0) {
                    btnProceedToCart.visibility = View.VISIBLE
                } else {
                    btnProceedToCart.visibility = View.GONE
                }
                holder.btnAddToCart.setBackgroundColor(Color.rgb(243, 123, 59))
            } else {
                menuSelectedCount++
                //menuSelectedId.add(holder.btnAddToCart.getTag().toString())
                //Toast.makeText(context,"ID is ${restaurantMenuItem.menuId}",Toast.LENGTH_SHORT).show()
                menuSelectedId.add(restaurantMenuItem.menuId)
                println("Restaurant Menu Adapter: Array of menu selected: $menuSelectedId")
                println("Restaurant Menu Adapter: Number of menu selected: $menuSelectedCount")
                holder.btnAddToCart.text = "REMOVE"
                if (menuSelectedCount > 0) {
                    btnProceedToCart.visibility = View.VISIBLE
                } else {
                    btnProceedToCart.visibility = View.GONE
                }
                holder.btnAddToCart.setBackgroundColor(Color.rgb(255, 207, 0))
            }
        }

        btnProceedToCart.setOnClickListener {
            //Toast.makeText(context,"Restaurant Id: $restaurantId",Toast.LENGTH_SHORT).show()
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurant_Id", restaurantId)
            intent.putExtra("restaurant_Name", restaurantName)
            intent.putExtra("menuSelected_id", menuSelectedId)
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

}