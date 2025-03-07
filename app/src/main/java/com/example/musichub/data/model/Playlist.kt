package com.example.musichub.data.model

data class Playlist(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String,
    val songCount: Int,
    val songs: List<Song> = emptyList()
) 