package com.example.musichub.data.remote

import com.example.musichub.data.model.Playlist
import com.example.musichub.data.model.Song
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicService {
    @GET("search")
    suspend fun searchMusic(@Query("q") query: String): List<Song>

    @GET("featured-playlists")
    suspend fun getFeaturedPlaylists(): List<Playlist>

    @GET("top-charts")
    suspend fun getTopCharts(): List<Playlist>

    @GET("new-releases")
    suspend fun getNewReleases(): List<Playlist>

    @GET("playlists/{id}/songs")
    suspend fun getPlaylistSongs(@Path("id") playlistId: String): List<Song>
} 