package com.example.musichub.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Query("SELECT * FROM search_queries ORDER BY timestamp DESC LIMIT 10")
    fun getRecentQueries(): Flow<List<SearchQuery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuery(query: SearchQuery)

    @Query("DELETE FROM search_queries")
    suspend fun deleteAllQueries()
} 