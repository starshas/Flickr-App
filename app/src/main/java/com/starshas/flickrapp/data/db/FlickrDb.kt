package com.starshas.flickrapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.starshas.flickrapp.data.models.FlickrDbItem

@Database(entities = [FlickrDbItem::class], version = 1, exportSchema = false)
abstract class FlickrDb : RoomDatabase() {
    abstract fun getFlickrDao(): FlickrDao
}
