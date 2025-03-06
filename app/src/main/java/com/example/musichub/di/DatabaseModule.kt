package com.example.musichub.di

import android.content.Context
import androidx.room.Room
import com.example.musichub.data.local.AppDatabase
import com.example.musichub.data.local.LocalSongDao
import com.example.musichub.data.local.SearchQueryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "musichub.db"
        ).build()
    }

    @Provides
    fun provideLocalSongDao(database: AppDatabase): LocalSongDao {
        return database.localSongDao()
    }

    @Provides
    fun provideSearchQueryDao(database: AppDatabase): SearchQueryDao {
        return database.searchQueryDao()
    }
} 