package com.starshas.flickrapp.data.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class FlickrResponse(
    @SerializedName("title")
    val title: String,
    @SerializedName("link")
    val link: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("modified")
    val modified: String,
    @SerializedName("generator")
    val generator: String,
    @SerializedName("items")
    val items: List<FlickrItem>
)

@Parcelize
data class FlickrItem(
    @SerializedName("title") val title: String,
    @SerializedName("link") val link: String,
    @SerializedName("media") val media: Media,
    @SerializedName("date_taken") val dateTaken: String,
    @SerializedName("description") val description: String,
    @SerializedName("published") val published: String,
    @SerializedName("author") val author: String,
    @SerializedName("author_id") val authorId: String,
    @SerializedName("tags") val tags: String
) : Parcelable

@Parcelize
data class Media(
    @SerializedName("m") val m: String
): Parcelable
