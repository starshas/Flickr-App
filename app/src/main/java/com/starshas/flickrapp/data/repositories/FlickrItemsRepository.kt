package com.starshas.flickrapp.data.repositories

import com.starshas.flickrapp.data.models.FlickrItem

interface FlickrItemsRepository {
    suspend fun getFlickrItems(): Result<List<FlickrItem>>
}
