package com.example.yp_playlist.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.yp_playlist.R
import com.example.yp_playlist.domain.Track
import com.example.yp_playlist.presentation.MainActivity
import com.example.yp_playlist.domain.interactors.media.MediaInteractor
import org.koin.android.ext.android.inject

class MediaPlayerService : Service(), MediaPlayerServiceInterface {

    private val binder = MediaPlayerBinder()
    private var track: Track? = null
    private val mediaInteractor: MediaInteractor by inject()
    private var isForegroundService = false

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    inner class MediaPlayerBinder : Binder() {
        fun getService(): MediaPlayerService = this@MediaPlayerService
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun getNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Playlist Maker")
            .setContentText("${track?.artistName} - ${track?.trackName}")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun startForegroundNotification(track: Track) {
        this.track = track
        if (!isForegroundService) {
            val notification = getNotification()
            startForeground(NOTIFICATION_ID, notification)
            isForegroundService = true
        }
    }

    override fun stopForegroundNotification() {
        if (isForegroundService) {
            stopForeground(true)
            isForegroundService = false
        }
    }

    override fun start() {
        mediaInteractor.start()
    }

    override fun pause() {
        mediaInteractor.pause()
    }

    override fun stop() {
        mediaInteractor.release()
    }

    override fun getPosition(): Int {
        return mediaInteractor.getPosition()
    }

    override fun isPlaying(): Boolean {
        return mediaInteractor.isPlaying()
    }

    companion object {
        const val CHANNEL_ID = "media_playback_channel"
        const val CHANNEL_NAME = "Media Playback"
        const val NOTIFICATION_ID = 1
    }
}
