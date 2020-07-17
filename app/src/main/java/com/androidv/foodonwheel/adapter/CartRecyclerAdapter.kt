package com.androidv.foodonwheel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.androidv.foodonwheel.R
import com.androidv.foodonwheel.model.CartItems
import kotlinx.android.synthetic.main.recycler_cart_single_row.view.*
import org.w3c.dom.Text

class CartRecyclerAdapter(
    val context: Context, val cartItems: ArrayList<CartItems>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCartItemNo: TextView = view.findViewById(R.id.txtCartItemCount)
        val txtCartItemName: TextView = view.findViewById(R.id.txtCartItemName)
        val txtCartItemCost: TextView = view.findViewById(R.id.txtCartItemCost)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cart = cartItems[position]
        holder.txtCartItemNo.text = (position + 1).toString() + "."
        holder.txtCartItemName.text = cart.itemName
        holder.txtCartItemCost.text = cart.itmeCost
    }
}