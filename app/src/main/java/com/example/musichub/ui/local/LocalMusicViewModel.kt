package com.example.musichub.ui.local

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musichub.data.model.Song
import com.example.musichub.data.repository.MusicRepository
import com.example.musichub.player.MusicPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalMusicViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val musicPlayer: MusicPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocalMusicUiState())
    val uiState: StateFlow<LocalMusicUiState> = _uiState

    init {
        loadMusic()
    }

    fun loadMusic() {
        viewModelScope.launch {
            try {
                val songs = musicRepository.getLocalMusic()
                    .collect { songs ->
                        _uiState.update {
                            it.copy(
                                songs = songs.sortedBy { it.title },
                                isLoading = false
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to load music",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun playSong(song: Song) {
        val songs = uiState.value.songs
        val index = songs.indexOf(song)
        if (index != -1) {
            musicPlayer.play(songs, index)
        } else {
            musicPlayer.play(song)
        }
    }
}

data class LocalMusicUiState(
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
) 