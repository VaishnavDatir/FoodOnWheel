package com.androidv.foodonwheel.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity (
    @PrimaryKey val rest_id: Int,
    @ColumnInfo(name = "restaurant_name") val restName: String,
    @ColumnInfo(name ="restaurant_cost") val restCost: String,
    @ColumnInfo(name = "restaurant_rating") val restRating: String,
    @ColumnInfo(name = "restaurant_image") val restImage: String
)