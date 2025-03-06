package com.example.musichub.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.musichub.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@UnstableApi
@AndroidEntryPoint
class MusicPlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer
    
    private var mediaSession: MediaSession? = null
    private lateinit var notificationManager: PlayerNotificationManager
    
    private val channelId = "music_playback_channel"
    private val notificationId = 1

    override fun onCreate() {
        super.onCreate()
        
        // Create notification channel
        createNotificationChannel()
        
        // Configure ExoPlayer
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
            /* handleAudioFocus = */ true
        )
        
        // Create MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, Class.forName("com.example.musichub.ui.MainActivity")),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()
            
        // Setup notification manager
        setupNotificationManager()
            
        // Add player listener
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> { updatePlaybackState() }
                    Player.STATE_READY -> {
                        if (player.isPlaying) {
                            updatePlaybackState()
                        } else {
                            updatePlaybackState()
                        }
                    }
                    Player.STATE_ENDED -> { updatePlaybackState() }
                    Player.STATE_IDLE -> { updatePlaybackState() }
                }
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
            }
        })
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
            }
            
            getSystemService<NotificationManager>()?.createNotificationChannel(channel)
        }
    }
    
    private fun setupNotificationManager() {
        notificationManager = PlayerNotificationManager.Builder(this, notificationId, channelId)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return player.currentMediaItem?.mediaMetadata?.title ?: "Unknown"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return PendingIntent.getActivity(
                        this@MusicPlaybackService,
                        0,
                        Intent(this@MusicPlaybackService, Class.forName("com.example.musichub.ui.MainActivity")),
                        PendingIntent.FLAG_IMMUTABLE
                    )
                }

                override fun getCurrentContentText(player: Player): CharSequence {
                    return player.currentMediaItem?.mediaMetadata?.artist ?: "Unknown Artist"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return null // You can load album art here if needed
                }
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    stopSelf()
                }

                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        startForeground(notificationId, notification)
                    } else {
                        stopForeground(false)
                    }
                }
            })
            .build()
            .apply {
                setPlayer(player)
            }
    }
    
    private fun updatePlaybackState() {
        // Update notification
        notificationManager.invalidate()
    }
    
    fun playMedia(mediaItem: MediaItem) {
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }
    
    fun playMediaList(mediaItems: List<MediaItem>, startIndex: Int = 0) {
        player.setMediaItems(mediaItems, startIndex, 0L)
        player.prepare()
        player.play()
    }
} 
