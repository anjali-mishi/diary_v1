package com.example.myapplication.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

private const val TAG = "Diary.AudioRecorder"

class AudioRecorder(private val context: Context) {

    private var recorder: MediaRecorder? = null
    private var currentOutputFile: File? = null

    fun startRecording() {
        val audioDir = File(context.filesDir, "audio_memos").also { it.mkdirs() }
        currentOutputFile = File(audioDir, "memo_${System.currentTimeMillis()}.m4a")
        Log.d(TAG, "startRecording: output=${currentOutputFile?.absolutePath}")

        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        recorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(currentOutputFile?.absolutePath)

            try {
                prepare()
                start()
                Log.i(TAG, "startRecording: recording started")
            } catch (e: IOException) {
                Log.e(TAG, "startRecording: prepare/start failed — ${e.message}", e)
            }
        }
    }

    fun maxAmplitude(): Int = recorder?.maxAmplitude ?: 0

    fun stopRecording(): String? {
        Log.d(TAG, "stopRecording: stopping recorder")
        try {
            recorder?.apply {
                stop()
                release()
            }
            Log.i(TAG, "stopRecording: done — file=${currentOutputFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "stopRecording: error — ${e.message}", e)
        } finally {
            recorder = null
        }
        return currentOutputFile?.absolutePath
    }
}
