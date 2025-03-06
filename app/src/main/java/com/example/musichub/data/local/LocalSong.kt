package com.example.musichub.data.local

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.musichub.data.model.Song

@Entity(tableName = "local_songs")
data class LocalSong(
    @PrimaryKey
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val albumArtUri: String?,
    val path: String,
    val dateAdded: Long,
    val size: Long
) {
    fun toSong() = Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        albumArtUri = albumArtUri?.let { Uri.parse(it) },
        path = path,
        dateAdded = dateAdded,
        size = size
    )

    companion object {
        fun fromSong(song: Song) = LocalSong(
            id = song.id,
            title = song.title,
            artist = song.artist,
            album = song.album,
            duration = song.duration,
            albumArtUri = song.albumArtUri?.toString(),
            path = song.path,
            dateAdded = song.dateAdded,
            size = song.size
        )
    }
} 