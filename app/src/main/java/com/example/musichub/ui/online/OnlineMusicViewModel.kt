package com.example.musichub.ui.online

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import com.example.musichub.R
import com.example.musichub.data.model.Playlist
import com.example.musichub.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnlineMusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnlineMusicUiState())
    val uiState: StateFlow<OnlineMusicUiState> = _uiState

    init {
        loadData()
    }

    fun refresh() {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val featured = musicRepository.getFeaturedPlaylists()
                val charts = musicRepository.getTopCharts()
                val newReleases = musicRepository.getNewReleases()
                Log.i("OnlineMusicViewModel", "loadData: $charts")
                _uiState.update {
                    it.copy(
                        featuredPlaylists = featured,
                        topCharts = charts,
                        newReleases = newReleases,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = R.string.network_error,
                        isLoading = false
                    )
                }
            }
        }
    }
}

data class OnlineMusicUiState(
    val featuredPlaylists: List<Playlist> = emptyList(),
    val topCharts: List<Playlist> = emptyList(),
    val newReleases: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
    val error: Int? = null
) 