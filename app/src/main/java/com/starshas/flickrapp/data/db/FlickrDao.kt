package com.starshas.flickrapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.starshas.flickrapp.data.models.FlickrDbItem

@Dao
interface FlickrDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlickrItems(items: List<FlickrDbItem>)

    @Query("SELECT * FROM flickr_table")
    suspend fun getFlickrItems(): List<FlickrDbItem>
}
