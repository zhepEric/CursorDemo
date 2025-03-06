package com.example.musichub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        LocalSong::class,
        SearchQuery::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun localSongDao(): LocalSongDao
    abstract fun searchQueryDao(): SearchQueryDao
} 