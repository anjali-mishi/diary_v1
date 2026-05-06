package com.example.myapplication.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Memory
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.util.ImageStorage
import com.example.myapplication.util.analyzeSentiment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

private const val TAG = "Diary.CaptureVM"

class CaptureViewModel(
    application: Application,
    private val repository: MemoryRepository
) : AndroidViewModel(application) {

    private var existingId: String? = null
    private var existingCreatedAt: Long? = null

    fun loadMemory(id: String, onLoaded: (Memory) -> Unit) {
        Log.d(TAG, "loadMemory: id=$id")
        viewModelScope.launch(Dispatchers.IO) {
            val memory = repository.getMemoryById(id)
            if (memory != null) {
                existingId = memory.id
                existingCreatedAt = memory.createdAt
                Log.d(TAG, "loadMemory: found — title='${memory.title}' tone=${memory.emotionalTone}")
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onLoaded(memory)
                }
            } else {
                Log.w(TAG, "loadMemory: no memory found for id=$id")
            }
        }
    }

    fun saveMemory(
        textContent: String,
        photoUri: String? = null,
        audioUri: String? = null,
        waveformData: String? = null,
        emotionOverride: String? = null,
        onSuccess: () -> Unit
    ) {
        Log.d(TAG, "saveMemory: textLen=${textContent.length}, hasPhoto=${photoUri != null}, hasAudio=${audioUri != null}, hasWaveform=${waveformData != null}, emotionOverride=$emotionOverride")

        if (textContent.isBlank() && photoUri == null && audioUri == null) {
            Log.w(TAG, "saveMemory: aborted — no content provided")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()

            // Copy content:// photo URI to internal storage for permanence
            val persistedPhotoPath = photoUri?.let {
                Log.d(TAG, "saveMemory: copying photo from uri=$it")
                ImageStorage.copyToInternalStorage(getApplication(), it)
            }
            Log.d(TAG, "saveMemory: persistedPhotoPath=$persistedPhotoPath")

            // Auto-generate title from first line of text, or use default
            val title = textContent.lines().firstOrNull { it.isNotBlank() }?.take(50)
                ?: if (persistedPhotoPath != null) "A photo memory"
                else if (audioUri != null) "A voice memory"
                else "Untitled"
            Log.d(TAG, "saveMemory: generated title='$title'")

            // Use user-selected emotion if provided, otherwise auto-detect
            val sentiment = if (emotionOverride != null) {
                Log.d(TAG, "saveMemory: using emotion override=$emotionOverride")
                com.example.myapplication.util.SentimentResult(tone = emotionOverride, intensity = 1f, secondaryTone = null)
            } else {
                analyzeSentiment(textContent).also {
                    Log.d(TAG, "saveMemory: tone=${it.tone} intensity=${it.intensity} secondary=${it.secondaryTone}")
                }
            }

            val isNew = existingId == null
            val memory = Memory(
                id = existingId ?: UUID.randomUUID().toString(),
                timestamp = now,
                title = title,
                textContent = textContent.ifBlank { null },
                photoFilePath = persistedPhotoPath,
                audioFilePath = audioUri,
                waveformData = waveformData,
                emotionalTone = sentiment.tone,
                emotionIntensity = sentiment.intensity,
                secondaryEmotionalTone = sentiment.secondaryTone,
                createdAt = existingCreatedAt ?: now,
                updatedAt = now
            )
            Log.i(TAG, "saveMemory: persisting id=${memory.id} isNew=$isNew")

            repository.insert(memory)
            Log.i(TAG, "saveMemory: success id=${memory.id}")

            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    // Factory now requires Application + MemoryRepository
    class Factory(
        private val application: Application,
        private val repository: MemoryRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            Log.d(TAG, "Factory.create: CaptureViewModel")
            return CaptureViewModel(application, repository) as T
        }
    }
}
