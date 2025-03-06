package com.example.musichub.player

import com.example.musichub.data.model.Song

interface MusicPlayer {
    fun play(song: Song)
    fun play(songs: List<Song>, startIndex: Int = 0)
    fun pause()
    fun resume()
    fun stop()
    fun next()
    fun previous()
    fun seekTo(position: Long)
} 