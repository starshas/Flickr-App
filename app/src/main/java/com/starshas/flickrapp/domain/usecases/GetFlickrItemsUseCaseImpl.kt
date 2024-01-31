package com.starshas.flickrapp.domain.usecases

import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.data.repositories.FlickrItemsRepository

class GetFlickrItemsUseCaseImpl(
    private val repositoryFlickrItems: FlickrItemsRepository
) : GetFlickrItemsUseCase {
    override suspend operator fun invoke(): Result<List<FlickrItem>> =
        repositoryFlickrItems.getFlickrItems()
}
