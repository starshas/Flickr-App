package com.starshas.flickrapp.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.starshas.flickrapp.common.AppConstants

@Entity(tableName = AppConstants.TABLE_CACHE_NAME)
data class FlickrDbItem(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Long = 0,
    @SerializedName("title")
    val title: String,
    @SerializedName("link")
    val link: String,
    @Embedded
    @SerializedName("media")
    val media: MediaEntity,
    @SerializedName("date_taken")
    val dateTaken: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("published")
    val published: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("author_id")
    val authorId: String,
    @SerializedName("tags")
    val tags: String
)

data class MediaEntity(
    @SerializedName("m")
    val m: String
)
