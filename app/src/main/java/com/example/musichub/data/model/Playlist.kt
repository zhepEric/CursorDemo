package com.example.musichub.data.model

data class Playlist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val songCount: Int,
    val songs: List<Song> = emptyList()
) 