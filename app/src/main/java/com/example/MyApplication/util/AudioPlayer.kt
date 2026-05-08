package com.example.myapplication.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.util.Log

private const val TAG = "Diary.AudioPlayer"

class AudioPlayer(context: Context) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var mediaPlayer: MediaPlayer? = null
    var isPlaying = false
        private set
    var isPaused = false
        private set
    private var wasPlayingBeforeLoss = false
    private var audioFocusRequest: AudioFocusRequest? = null

    private val audioFocusListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        Log.d(TAG, "audioFocusListener: focusChange=$focusChange")
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                wasPlayingBeforeLoss = isPlaying
                if (isPlaying) {
                    pause()
                    Log.d(TAG, "audioFocusListener: paused for transient loss")
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (wasPlayingBeforeLoss && isPaused) {
                    resume()
                    Log.d(TAG, "audioFocusListener: resumed after transient loss")
                }
                wasPlayingBeforeLoss = false
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                stop()
                Log.d(TAG, "audioFocusListener: stopped for permanent loss")
            }
        }
    }

    private fun requestAudioFocus() {
        Log.d(TAG, "requestAudioFocus: requesting…")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                .setOnAudioFocusChangeListener(audioFocusListener)
                .build()
            audioFocusRequest = request
            val result = audioManager.requestAudioFocus(request)
            Log.d(TAG, "requestAudioFocus: result=$result")
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(
                audioFocusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
            Log.d(TAG, "requestAudioFocus: (pre-API 26)")
        }
    }

    private fun abandonAudioFocus() {
        Log.d(TAG, "abandonAudioFocus: abandoning…")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let {
                audioManager.abandonAudioFocusRequest(it)
                audioFocusRequest = null
            }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(audioFocusListener)
        }
    }

    fun playFile(path: String, onCompletion: () -> Unit) {
        Log.d(TAG, "playFile: path=$path")
        mediaPlayer?.release()
        requestAudioFocus()
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
                this@AudioPlayer.isPaused = false
                Log.i(TAG, "playFile: playback started")
                setOnCompletionListener {
                    Log.d(TAG, "playFile: playback completed")
                    this@AudioPlayer.isPlaying = false
                    this@AudioPlayer.isPaused = false
                    abandonAudioFocus()
                    onCompletion()
                }
            } catch (e: Exception) {
                Log.e(TAG, "playFile: error — ${e.message}", e)
                this@AudioPlayer.isPlaying = false
                this@AudioPlayer.isPaused = false
                abandonAudioFocus()
                onCompletion()
            }
        }
    }

    fun pause() {
        Log.d(TAG, "pause: pausing player at position ${mediaPlayer?.currentPosition}")
        mediaPlayer?.pause()
        isPlaying = false
        isPaused = true
    }

    fun resume() {
        Log.d(TAG, "resume: resuming player from position ${mediaPlayer?.currentPosition}")
        mediaPlayer?.start()
        isPlaying = true
        isPaused = false
    }

    val currentPosition: Int get() = mediaPlayer?.currentPosition ?: 0
    val duration: Int get() = mediaPlayer?.duration?.takeIf { it > 0 } ?: 1

    fun stop() {
        Log.d(TAG, "stop: releasing player")
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        isPaused = false
        abandonAudioFocus()
    }

    fun release() {
        Log.d(TAG, "release: releasing player")
        mediaPlayer?.release()
        mediaPlayer = null
        isPlaying = false
        isPaused = false
        abandonAudioFocus()
    }
}
