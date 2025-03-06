package com.example.musichub.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musichub.data.model.Song
import com.example.musichub.player.DefaultMusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val musicPlayer: DefaultMusicPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState

    init {
        observePlaybackState()
    }

    private fun observePlaybackState() {
        viewModelScope.launch {
            musicPlayer.playbackState.collectLatest { state ->
                _uiState.update { 
                    it.copy(
                        isPlaying = state.isPlaying,
                        currentSong = state.currentSong
                    )
                }
            }
        }
    }

    fun playPause() {
        val isPlaying = _uiState.value.isPlaying
        if (isPlaying) {
            musicPlayer.pause()
        } else {
            musicPlayer.resume()
        }
    }

    fun skipToNext() {
        musicPlayer.next()
    }

    fun skipToPrevious() {
        musicPlayer.previous()
    }
}

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val currentSong: Song? = null
) 