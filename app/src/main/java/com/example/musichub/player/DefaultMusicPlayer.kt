package com.example.musichub.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musichub.data.model.Song
import com.example.musichub.service.MusicPlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMusicPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicPlayer {

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private val controller: MediaController?
        get() = if (controllerFuture?.isDone == true) controllerFuture?.get() else null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState

    private var currentSongList: List<Song> = emptyList()
    private var currentSongIndex: Int = -1

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken)
            .buildAsync()
            .apply {
                addListener(
                    {
                        try {
                            val controller = get()
                            controller.addListener(object : Player.Listener {
                                override fun onPlaybackStateChanged(playbackState: Int) {
                                    updatePlaybackState()
                                }

                                override fun onIsPlayingChanged(isPlaying: Boolean) {
                                    updatePlaybackState()
                                }

                                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                                    updateCurrentSong(mediaItem)
                                }
                            })
                        } catch (e: Exception) {
                            Log.e("MusicPlayer", "Error initializing controller", e)
                        }
                    },
                    MoreExecutors.directExecutor()
                )
            }
    }

    override fun play(song: Song) {
        currentSongList = listOf(song)
        currentSongIndex = 0
        val mediaItem = song.toMediaItem()
        controller?.run {
            setMediaItem(mediaItem)
            prepare()
            play()
        } ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun play(songs: List<Song>, startIndex: Int) {
        currentSongList = songs
        currentSongIndex = startIndex
        val mediaItems = songs.map { it.toMediaItem() }
        controller?.run {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        } ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun pause() {
        controller?.pause() ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun resume() {
        controller?.play() ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun stop() {
        controller?.stop() ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun next() {
        controller?.seekToNext() ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun previous() {
        controller?.seekToPrevious() ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    override fun seekTo(position: Long) {
        controller?.seekTo(position) ?: Log.e("MusicPlayer", "Controller not initialized")
    }

    private fun updatePlaybackState() {
        controller?.let { controller ->
            _playbackState.update { 
                it.copy(
                    isPlaying = controller.isPlaying,
                    playbackState = controller.playbackState,
                    currentPosition = controller.currentPosition,
                    duration = controller.duration
                )
            }
        }
    }

    private fun updateCurrentSong(mediaItem: MediaItem?) {
        mediaItem?.mediaId?.toLongOrNull()?.let { songId ->
            val song = currentSongList.find { it.id == songId }
            _playbackState.update { 
                it.copy(currentSong = song)
            }
        }
    }

    private fun Song.toMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(Uri.parse(path))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(title)
                    .setArtist(artist)
                    .setAlbumTitle(album)
                    .setArtworkUri(albumArtUri)
                    .build()
            )
            .build()
    }

    fun release() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
    }
}

data class PlaybackState(
    val isPlaying: Boolean = false,
    val playbackState: Int = Player.STATE_IDLE,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val currentSong: Song? = null
) 