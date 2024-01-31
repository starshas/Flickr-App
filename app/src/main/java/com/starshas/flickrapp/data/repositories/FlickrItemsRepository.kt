package com.starshas.flickrapp.data.repositories

import com.starshas.flickrapp.data.models.FlickrItem
import kotlinx.coroutines.flow.Flow

interface FlickrItemsRepository {
    fun getFlickrItemsFlow(): Flow<Result<List<FlickrItem>>>
}
