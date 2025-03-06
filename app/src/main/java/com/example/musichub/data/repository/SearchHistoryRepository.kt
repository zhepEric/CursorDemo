package com.example.musichub.data.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getSearchHistory(): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
    suspend fun clearSearchHistory()
} 