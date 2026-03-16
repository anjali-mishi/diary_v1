package com.example.myapplication.util

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log

private const val TAG = "Diary.AudioPlayer"

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set

    fun playFile(path: String, onCompletion: () -> Unit) {
        Log.d(TAG, "playFile: path=$path")
        mediaPlayer = MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(path)
                Log.d(TAG, "playFile: preparing…")
                prepare()
                start()
                this@AudioPlayer.isPlaying = true
                Log.i(TAG, "playFile: playback started")
                setOnCompletionListener {
                    Log.d(TAG, "playFile: playback completed")
                    this@AudioPlayer.isPlaying = false
                    onCompletion()
                }
            } catch (e: Exception) {
                Log.e(TAG, "playFile: error — ${e.message}", e)
                this@AudioPlayer.isPlaying = false
                onCompletion()
            }
        }
    }

    val currentPosition: Int get() = mediaPlayer?.currentPosition ?: 0
    val duration: Int get() = mediaPlayer?.duration?.takeIf { it > 0 } ?: 1

    fun stop() {
        Log.d(TAG, "stop: releasing player")
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    fun release() {
        Log.d(TAG, "release: releasing player")
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
