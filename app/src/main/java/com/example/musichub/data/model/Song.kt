package com.example.musichub.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val albumArtUri: Uri?,
    val path: String,
    val dateAdded: Long,
    val size: Long
) 