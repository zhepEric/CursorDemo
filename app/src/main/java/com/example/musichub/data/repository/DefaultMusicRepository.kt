package com.example.musichub.data.repository

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.example.musichub.data.local.LocalSong
import com.example.musichub.data.local.LocalSongDao
import com.example.musichub.data.model.Playlist
import com.example.musichub.data.model.Song
import com.example.musichub.data.remote.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DefaultMusicRepository @Inject constructor(
    private val contentResolver: ContentResolver,
    private val localSongDao: LocalSongDao,
    private val musicService: MusicService
) : MusicRepository {

    override fun getLocalMusic(): Flow<List<Song>> {
        return localSongDao.getAllSongs().map { songs ->
            songs.map { it.toSong() }
        }
    }

    override suspend fun scanLocalMusic() = withContext(Dispatchers.IO) {
        val songs = mutableListOf<LocalSong>()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        contentResolver.query(
            collection,
            projection,
            selection,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )

                val song = LocalSong(
                    id = id,
                    title = cursor.getString(titleColumn),
                    artist = cursor.getString(artistColumn),
                    album = cursor.getString(albumColumn),
                    duration = cursor.getLong(durationColumn),
                    albumArtUri = albumArtUri.toString(),
                    path = cursor.getString(pathColumn),
                    dateAdded = cursor.getLong(dateAddedColumn),
                    size = cursor.getLong(sizeColumn)
                )
                songs.add(song)
            }
        }

        localSongDao.deleteAllSongs()
        localSongDao.insertSongs(songs)
    }

    override suspend fun searchMusic(query: String): List<Song> {
        return withContext(Dispatchers.IO) {
            val localResults = localSongDao.searchSongs(query).map { it.toSong() }
            val onlineResults = try {
                musicService.searchMusic(query)
            } catch (e: Exception) {
                emptyList()
            }
            localResults + onlineResults
        }
    }

    override suspend fun getFeaturedPlaylists(): List<Playlist> {
        return withContext(Dispatchers.IO) {
            musicService.getFeaturedPlaylists()
        }
    }

    override suspend fun getTopCharts(): List<Playlist> {
        return withContext(Dispatchers.IO) {
            musicService.getTopCharts()
        }
    }

    override suspend fun getNewReleases(): List<Playlist> {
        return withContext(Dispatchers.IO) {
            musicService.getNewReleases()
        }
    }

    override suspend fun getPlaylistSongs(playlistId: String): List<Song> {
        return withContext(Dispatchers.IO) {
            musicService.getPlaylistSongs(playlistId)
        }
    }
} 