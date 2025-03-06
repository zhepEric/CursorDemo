package com.example.musichub.di

import android.content.Context
import com.example.musichub.data.remote.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.example.com/" // Replace with actual API base URL
    private const val CACHE_SIZE = 10L * 1024L * 1024L // 10MB cache

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        return Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(cache: Cache): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMusicService(retrofit: Retrofit): MusicService {
        return retrofit.create(MusicService::class.java)
    }
} 