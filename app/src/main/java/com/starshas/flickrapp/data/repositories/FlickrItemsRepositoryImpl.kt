package com.starshas.flickrapp.data.repositories

import com.starshas.flickrapp.data.FlickrApi
import com.starshas.flickrapp.data.FlickrItemMapper
import com.starshas.flickrapp.data.api.ApiError
import com.starshas.flickrapp.data.db.FlickrDao
import com.starshas.flickrapp.data.models.FlickrItem
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.IOException

class FlickrItemsRepositoryImpl(
    private val flickrApi: FlickrApi,
    private val flickrDao: FlickrDao,
) : FlickrItemsRepository {
    override fun getFlickrItemsFlow(): Flow<Result<List<FlickrItem>>> = flow {
        try {
            val response = flickrApi.getListFlickrItems()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                flickrDao.insertFlickrItems(FlickrItemMapper.mapModelListToEntityList(body.items))
                emit(Result.success(body.items))
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                emit(Result.failure(ApiError.HttpError(response.code(), errorMessage)))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Timber.e("IO Exception while getting list")
            val cachedItems = flickrDao.getFlickrItems()
            if (cachedItems.isNotEmpty()) {
                emit(Result.success(FlickrItemMapper.mapEntityListToModelList(cachedItems)))
            } else {
                emit(Result.failure(ApiError.NetworkError))
            }
        } catch (e: Exception) {
            Timber.e("Exception while getting list")
            emit(Result.failure(ApiError.GenericError(e)))
        }
    }.flowOn(Dispatchers.IO)
}
