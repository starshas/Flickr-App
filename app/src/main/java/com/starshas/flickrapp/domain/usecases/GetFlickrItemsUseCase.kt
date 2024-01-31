package com.starshas.flickrapp.domain.usecases

import com.starshas.flickrapp.data.models.FlickrItem
import kotlinx.coroutines.flow.Flow

interface GetFlickrItemsUseCase {
    operator fun invoke(): Flow<Result<List<FlickrItem>>>
}
