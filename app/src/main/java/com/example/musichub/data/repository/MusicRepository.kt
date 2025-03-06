package com.example.musichub.data.repository

import com.example.musichub.data.model.Playlist
import com.example.musichub.data.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getLocalMusic(): Flow<List<Song>>
    suspend fun scanLocalMusic()
    suspend fun searchMusic(query: String): List<Song>
    suspend fun getFeaturedPlaylists(): List<Playlist>
    suspend fun getTopCharts(): List<Playlist>
    suspend fun getNewReleases(): List<Playlist>
    suspend fun getPlaylistSongs(playlistId: String): List<Song>
} 