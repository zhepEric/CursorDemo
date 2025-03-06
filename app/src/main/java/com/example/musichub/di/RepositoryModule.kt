package com.example.musichub.di

import android.content.ContentResolver
import com.example.musichub.data.local.LocalSongDao
import com.example.musichub.data.local.SearchQueryDao
import com.example.musichub.data.remote.MusicService
import com.example.musichub.data.repository.DefaultMusicRepository
import com.example.musichub.data.repository.DefaultSearchHistoryRepository
import com.example.musichub.data.repository.MusicRepository
import com.example.musichub.data.repository.SearchHistoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMusicRepository(
        contentResolver: ContentResolver,
        localSongDao: LocalSongDao,
        musicService: MusicService
    ): MusicRepository {
        return DefaultMusicRepository(contentResolver, localSongDao, musicService)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(
        searchQueryDao: SearchQueryDao
    ): SearchHistoryRepository {
        return DefaultSearchHistoryRepository(searchQueryDao)
    }
}