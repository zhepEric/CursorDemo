package com.example.musichub.data.repository

import com.example.musichub.data.local.SearchQuery
import com.example.musichub.data.local.SearchQueryDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DefaultSearchHistoryRepository @Inject constructor(
    private val searchQueryDao: SearchQueryDao
) : SearchHistoryRepository {

    override fun getSearchHistory(): Flow<List<String>> {
        return searchQueryDao.getRecentQueries()
            .map { queries -> queries.map { it.query } }
    }

    override suspend fun saveSearchQuery(query: String) {
        searchQueryDao.insertQuery(SearchQuery(query))
    }

    override suspend fun clearSearchHistory() {
        searchQueryDao.deleteAllQueries()
    }
} 