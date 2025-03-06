package com.example.musichub.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musichub.R
import com.example.musichub.data.model.Song
import com.example.musichub.data.repository.MusicRepository
import com.example.musichub.data.repository.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private val _searchResults = MutableStateFlow<List<Song>>(emptyList())
    val searchResults = _searchResults.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            searchHistoryRepository.getSearchHistory()
                .combine(_uiState) { history, state ->
                    state.copy(searchHistory = history)
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val results = musicRepository.searchMusic(query)
                _searchResults.value = results
                searchHistoryRepository.saveSearchQuery(query)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = R.string.network_error) }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clearSearchHistory()
        }
    }
}

data class SearchUiState(
    val searchHistory: List<String> = emptyList(),
    val searchResults: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null
) 