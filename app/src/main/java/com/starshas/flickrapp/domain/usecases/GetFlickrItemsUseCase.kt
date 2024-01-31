package com.starshas.flickrapp.domain.usecases

import com.starshas.flickrapp.data.models.FlickrItem

interface GetFlickrItemsUseCase {
    suspend operator fun invoke(): Result<List<FlickrItem>>
}
