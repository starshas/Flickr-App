package com.starshas.flickrapp.data.repositories

import com.starshas.flickrapp.data.FlickrItemMapper
import com.starshas.flickrapp.data.api.ApiError
import com.starshas.flickrapp.data.FlickrApi
import com.starshas.flickrapp.data.db.FlickrDao
import com.starshas.flickrapp.data.models.FlickrItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class FlickrItemsRepositoryImpl(
    private val flickrApi: FlickrApi,
    private val flickrDao: FlickrDao,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) : FlickrItemsRepository {
    override suspend fun getFlickrItems(): Result<List<FlickrItem>> =
        withContext(coroutineContext) {
            try {
                val response = flickrApi.getListFlickrItems()
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    flickrDao.insertFlickrItems(FlickrItemMapper.mapModelListToEntityList(body.items))
                    Result.success(body.items)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    Result.failure(ApiError.HttpError(response.code(), errorMessage))
                }
            } catch (e: CancellationException) {
                throw e
            }
            catch (e: IOException) {
                Timber.e(e, "Got IOException while fetching flickr items")
                val cachedResponse = FlickrItemMapper.mapEntityListToModelList(flickrDao.getFlickrItems())
                if (cachedResponse.isNotEmpty()) {
                    Result.success(cachedResponse)
                } else {
                    Result.failure(ApiError.NetworkError)
                }
            } catch (e: Exception) {
                Timber.e(e, "Got Exception while fetching flickr items")
                Result.failure(ApiError.GenericError(e))
            }
        }
}
