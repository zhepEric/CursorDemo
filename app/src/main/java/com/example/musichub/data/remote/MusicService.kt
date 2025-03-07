package com.example.musichub.data.remote

import com.example.musichub.data.model.Playlist
import com.example.musichub.data.model.Song
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicService {
    @GET(".")
    suspend fun searchMusic(
        @Query("method") method: String = "track.search",
        @Query("track") query: String,
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 30
    ): LastFmSearchResponse

    @GET(".")
    suspend fun getFeaturedPlaylists(
        @Query("method") method: String = "tag.gettoptracks",
        @Query("tag") tag: String = "popular",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): LastFmTopTracksResponse

    @GET(".")
    suspend fun getTopCharts(
        @Query("method") method: String = "chart.gettoptracks",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): LastFmTopTracksResponse

    @GET(".")
    suspend fun getNewReleases(
        @Query("method") method: String = "tag.gettoptracks",
        @Query("tag") tag: String = "new",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("limit") limit: Int = 20
    ): LastFmTopTracksResponse
}

data class LastFmSearchResponse(
    val results: TrackResults
)

data class TrackResults(
    val trackmatches: TrackMatches
)

data class TrackMatches(
    val track: List<LastFmTrack>
)

data class LastFmTopTracksResponse(
    val tracks: Tracks
)

data class Tracks(
    val track: List<LastFmTrack>
)

data class LastFmTrack(
    val name: String,
    val artist: LastFmArtist,
    val url: String,
    val image: List<LastFmImage>,
    val duration: String = "0"
) {
    fun toSong(): Song {
        val imageUrl = image.lastOrNull { it.size == "large" }?.text
        return Song(
            id = url.hashCode().toLong(),
            title = name,
            artist = artist.name ?: artist.text ?: "",
            album = "", // Last.fm API doesn't provide album in search results
            duration = duration.toLongOrDefault(0L),
            albumArtUri = imageUrl?.let { android.net.Uri.parse(it) },
            path = url,
            dateAdded = System.currentTimeMillis(),
            size = 0L
        )
    }
}

data class LastFmArtist(
    val name: String? = null,
    val text: String? = null,
    val url: String? = null,
    val mbid: String? = null
)

data class LastFmImage(
    @SerializedName("#text") val text: String,
    val size: String
)

fun String.toLongOrDefault(default: Long): Long {
    return try {
        this.toLong()
    } catch (e: NumberFormatException) {
        default
    }
} 