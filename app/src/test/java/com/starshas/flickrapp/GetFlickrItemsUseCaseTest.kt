package com.starshas.flickrapp

import com.starshas.flickrapp.data.FlickrApi
import com.starshas.flickrapp.data.FlickrItemMapper
import com.starshas.flickrapp.data.api.ApiError
import com.starshas.flickrapp.data.db.FlickrDao
import com.starshas.flickrapp.data.models.FlickrDbItem
import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.data.models.FlickrResponse
import com.starshas.flickrapp.data.models.Media
import com.starshas.flickrapp.data.models.MediaEntity
import com.starshas.flickrapp.data.repositories.FlickrItemsRepository
import com.starshas.flickrapp.data.repositories.FlickrItemsRepositoryImpl
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCase
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCaseImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import retrofit2.Response
import java.io.IOException

class GetFlickrItemsUseCaseTest {
    private lateinit var repository: FlickrItemsRepository
    private lateinit var useCase: GetFlickrItemsUseCase
    private lateinit var flickrDao: FlickrDao
    private lateinit var flickrApi: FlickrApi
    private val flickrItemExample = FlickrItem(
        title = "A Cute Cat",
        link = "https://www.flickr.com/photos/cats",
        media = Media(m = "https://farm1.staticflickr.com/photo.jpg"),
        dateTaken = "2023-04-01T18:00:00-05:00",
        description = "Description of a cute cat",
        published = "2023-04-02T08:00:00Z",
        author = "John Doe",
        authorId = "123456789",
        tags = "cute"
    )
    private val flickrDbItemExample = FlickrDbItem(
        title = "A Cute Cat",
        link = "https://www.flickr.com/photos/cats",
        media = MediaEntity(m = "https://farm1.staticflickr.com/photo.jpg"),
        dateTaken = "2023-04-01T18:00:00-05:00",
        description = "Description of a cute cat",
        published = "2023-04-02T08:00:00Z",
        author = "John Doe",
        authorId = "123456789",
        tags = "cute"
    )
    private val flickrResponseExample = FlickrResponse(
        title = "Title",
        link = "http://example.com/tags/beach",
        description = "Description",
        modified = "2024-02-08T19:00:00Z",
        generator = "http://www.flickr.com/",
        items = listOf(flickrItemExample)
    )

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Before
    fun setUp() {
        flickrDao = mockk(relaxed = true)
        flickrApi = mockk(relaxed = true)
        repository = FlickrItemsRepositoryImpl(flickrApi, flickrDao, testCoroutineRule.dispatcher)
        useCase = GetFlickrItemsUseCaseImpl(repository)
    }

    @Test
    fun `invoke returns success with list of items`() = runTest {
        val response = Response.success(flickrResponseExample)
        coEvery { flickrApi.getListFlickrItems() } returns response
        val result: Result<List<FlickrItem>> = useCase().first()
        val expected: Result<List<FlickrItem>> = Result.success(response.body()!!.items)
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns failure on HTTP error`() = runTest {
        val errorCode = 404
        val errorMessage = "Not Found"
        val errorResponseBody = errorMessage.toResponseBody("application/json".toMediaTypeOrNull())
        val response: Response<FlickrResponse> = Response.error(errorCode, errorResponseBody)
        coEvery { flickrApi.getListFlickrItems() } returns response
        val result: Result<List<FlickrItem>> = useCase().first()
        val expected: Result<List<FlickrItem>> = Result.failure(
            ApiError.HttpError(errorCode, errorMessage)
        )
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns failure on generic error`() = runTest {
        val exception: Exception = Exception("Unexpected error")
        coEvery { flickrApi.getListFlickrItems() } throws exception
        val result: Result<List<FlickrItem>> = useCase().first()
        val expected: Result<List<FlickrItem>> = Result.failure(ApiError.GenericError(exception))
        assertEquals(expected, result)
    }

    @Test
    fun `invoke emits cached items on IOException`() = runTest {
        val listFlickrDbItems = listOf(flickrDbItemExample)
        val listExpectedItems = FlickrItemMapper.mapEntityListToModelList(listFlickrDbItems)
        coEvery { flickrApi.getListFlickrItems() } throws IOException("Network error")
        coEvery { flickrDao.getFlickrItems() } returns listFlickrDbItems
        val result: Result<List<FlickrItem>> = useCase().first()
        val expected: Result<List<FlickrItem>> = Result.success(listExpectedItems)
        assertEquals(expected, result)
    }

    @Test
    fun `invoke returns no cached items on network error`() = runTest {
        val listFlickrDbItems = emptyList<FlickrDbItem>()
        coEvery { flickrApi.getListFlickrItems() } throws IOException("Network error")
        coEvery { flickrDao.getFlickrItems() } returns listFlickrDbItems
        val result: Result<List<FlickrItem>> = useCase().first()
        val expected: Result<List<FlickrItem>> = Result.failure(ApiError.NetworkError)
        assertEquals(expected, result)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class TestCoroutineRule(
        val dispatcher: CoroutineDispatcher = StandardTestDispatcher()
    ) : TestWatcher() {
        override fun starting(description: Description) {
            super.starting(description)
            Dispatchers.setMain(dispatcher)
        }

        override fun finished(description: Description) {
            super.finished(description)
            Dispatchers.resetMain()
        }
    }
}
