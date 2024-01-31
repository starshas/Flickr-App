package com.starshas.flickrapp.presentation.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starshas.flickrapp.R
import com.starshas.flickrapp.data.api.ApiError
import com.starshas.flickrapp.data.models.FlickrItem
import com.starshas.flickrapp.domain.usecases.GetFlickrItemsUseCase
import com.starshas.flickrapp.utils.HtmlFormatter.removeImageTagsFromHtml
import com.starshas.flickrapp.utils.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    useCaseGetFlickrItems: GetFlickrItemsUseCase,
    private val stringProvider: StringProvider,
) : ViewModel() {
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    val stateFlowListFlickr: StateFlow<List<FlickrItem>> = refreshTrigger
        .flatMapLatest {
            getFlickrItems(useCaseGetFlickrItems).map { result ->
                result.getOrElse {
                    onRepositoryError(it as ApiError)
                    emptyList()
                }.sortedBy { it.published }
                    .map {
                        it.copy(description = removeImageTagsFromHtml(it.description))
                    }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STATEFLOW_TIMEOUT_MILLIS),
            emptyList()
        )
    private val _errorMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        refreshListData()
    }

    private fun getFlickrItems(useCaseGetFlickrItems: GetFlickrItemsUseCase)
            : Flow<Result<List<FlickrItem>>> = useCaseGetFlickrItems()

    private fun onRepositoryError(apiError: ApiError) {
        handleError(apiError)
    }

    private fun handleError(apiError: ApiError) {
        _errorMessage.update {
            when (apiError) {
                is ApiError.GenericError -> stringProvider.getString(R.string.main_error_while_loading_the_list)
                is ApiError.HttpError -> stringProvider.getHttpErrorMessage(
                    R.string.main_http_error,
                    apiError.code,
                    apiError.errorMessage
                )

                is ApiError.NetworkError -> stringProvider.getString(R.string.main_network_error)
            }
        }
    }

    fun resetErrorMessage() {
        _errorMessage.update { null }
    }

    fun refreshListData() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    private companion object {
        const val STATEFLOW_TIMEOUT_MILLIS = 5000L
    }
}
