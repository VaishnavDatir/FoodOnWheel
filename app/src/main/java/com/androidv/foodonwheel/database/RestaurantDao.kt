package com.androidv.foodonwheel.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestaurantDao {
    @Insert
    fun insertRest(restaurantEntity: RestaurantEntity)

    @Delete
    fun deleteRest(restaurantEntity: RestaurantEntity)

    @Query("SELECT * FROM restaurants")
    fun getAllRests():List<RestaurantEntity>

    @Query("SELECT * FROM restaurants WHERE rest_id = :restId")
    fun getRestBYId(restId: String): RestaurantEntity
}