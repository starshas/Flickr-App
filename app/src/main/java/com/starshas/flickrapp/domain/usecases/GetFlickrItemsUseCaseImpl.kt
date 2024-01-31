package com.starshas.flickrapp.domain.usecases

import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.data.repositories.FlickrItemsRepository
import kotlinx.coroutines.flow.Flow

class GetFlickrItemsUseCaseImpl(
    private val repositoryFlickrItems: FlickrItemsRepository
) : GetFlickrItemsUseCase {
    override operator fun invoke(): Flow<Result<List<FlickrItem>>> =
        repositoryFlickrItems.getFlickrItemsFlow()
}
