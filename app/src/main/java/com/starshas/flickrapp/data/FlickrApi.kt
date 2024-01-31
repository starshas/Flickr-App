package com.starshas.flickrapp.data

import com.starshas.flickrapp.common.AppConstants
import com.starshas.flickrapp.data.models.FlickrResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrApi {
    @GET(AppConstants.URL_PATH_IMAGES)
    suspend fun getListFlickrItems(
        @Query(AppConstants.PARAM_FORMAT) format: String = AppConstants.PARAMETER_VALUE_FORMAT_JSON,
        @Query(AppConstants.PARAM_NO_JSON_CALLBACK) nojsoncallback: String
        = AppConstants.PARAMETER_VALUE_NO_JSON_CALLBACK,
        @Query(AppConstants.PARAM_TAGS) tags: String = AppConstants.PARAMETER_VALUE_TAGS
    ): Response<FlickrResponse>
}
