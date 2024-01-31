package com.starshas.flickrapp.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.starshas.flickrapp.common.AppConstants

@Entity(tableName = AppConstants.TABLE_CACHE_NAME)
data class FlickrDbItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val link: String,
    @Embedded val media: MediaEntity,
    @SerializedName("date_taken") val dateTaken: String,
    val description: String,
    val published: String,
    val author: String,
    @SerializedName("author_id") val authorId: String,
    val tags: String
)

data class MediaEntity(
    val m: String
)
