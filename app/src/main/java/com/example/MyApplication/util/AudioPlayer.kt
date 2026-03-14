package com.example.myapplication.util

import android.media.AudioAttributes
import android.media.MediaPlayer

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set

    fun playFile(path: String, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(path)
                prepare()
                start()
                this@AudioPlayer.isPlaying = true
                setOnCompletionListener {
                    this@AudioPlayer.isPlaying = false
                    onCompletion()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                this@AudioPlayer.isPlaying = false
                onCompletion()
            }
        }
    }

    fun stop() {
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
