package com.example.myapplication.util

import android.media.MediaPlayer

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set

    fun playFile(path: String, onCompletion: () -> Unit) {
        mediaPlayer = MediaPlayer().apply {
            try {
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
