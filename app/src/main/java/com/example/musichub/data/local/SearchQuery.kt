package com.example.musichub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_queries")
data class SearchQuery(
    @PrimaryKey
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
) 