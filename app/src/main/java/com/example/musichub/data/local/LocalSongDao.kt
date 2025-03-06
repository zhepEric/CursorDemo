package com.example.musichub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalSongDao {
    @Query("SELECT * FROM local_songs")
    fun getAllSongs(): Flow<List<LocalSong>>

    @Query("SELECT * FROM local_songs WHERE title LIKE '%' || :query || '%' OR artist LIKE '%' || :query || '%' OR album LIKE '%' || :query || '%'")
    suspend fun searchSongs(query: String): List<LocalSong>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<LocalSong>)

    @Query("DELETE FROM local_songs")
    suspend fun deleteAllSongs()
} 