package com.starshas.flickrapp.presentation.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starshas.flickrapp.R
import com.starshas.flickrapp.data.api.ApiError
import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCase
import com.starshas.flickrapp.utils.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCaseGetFlickrItems: GetFlickrItemsUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {
    private var _listFlickrItems: MutableLiveData<List<FlickrItem>> = MutableLiveData()
    val listFlickrItems: LiveData<List<FlickrItem>> = _listFlickrItems
    private val _errorMessage: MutableLiveData<String?> = MutableLiveData()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        fetchFlickrItemsList()
    }

    fun fetchFlickrItemsList() {
        viewModelScope.launch {
            val result: Result<List<FlickrItem>> = useCaseGetFlickrItems()

            result.fold({ items: List<FlickrItem> ->
                _listFlickrItems.value = items.sortedBy { it.published }
                    .map {
                        it.copy(description = removeImageTagsFromHtml(it.description))
                    }
                resetErrorMessage()
            }, { throwable: Throwable ->
                val error = throwable as ApiError
                _errorMessage.value = when (error) {
                    is ApiError.GenericError -> stringProvider.getString(R.string.main_error_while_loading_the_list)
                    is ApiError.HttpError -> stringProvider.getHttpErrorMessage(
                        R.string.main_http_error,
                        httpCode = error.code,
                        message = error.errorMessage
                    )
                    ApiError.NetworkError -> stringProvider.getString(R.string.main_network_error)
                }
            })
        }
    }

    private fun removeImageTagsFromHtml(htmlContent: String): String {
        val document = Jsoup.parse(htmlContent)
        val imageTag = "img"
        val paragraphTag = "p"
        document.select(imageTag).remove()
        document.select(paragraphTag).forEach {
            if (it.text().trim().isEmpty()) {
                it.remove()
            }
        }
        return document.body().html()
    }

    fun resetErrorMessage() {
        _errorMessage.value = null
    }
}
