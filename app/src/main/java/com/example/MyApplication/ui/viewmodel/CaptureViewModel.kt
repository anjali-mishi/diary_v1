package com.example.myapplication.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Memory
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.util.EmotionDetector
import com.example.myapplication.util.ImageStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CaptureViewModel(
    application: Application,
    private val repository: MemoryRepository
) : AndroidViewModel(application) {

    private var existingId: String? = null
    private var existingCreatedAt: Long? = null

    fun loadMemory(id: String, onLoaded: (Memory) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val memory = repository.getMemoryById(id)
            if (memory != null) {
                existingId = memory.id
                existingCreatedAt = memory.createdAt
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onLoaded(memory)
                }
            }
        }
    }

    fun saveMemory(
        textContent: String,
        photoUri: String? = null,
        audioUri: String? = null,
        onSuccess: () -> Unit
    ) {
        if (textContent.isBlank() && photoUri == null && audioUri == null) return

        viewModelScope.launch(Dispatchers.IO) {
            val now = System.currentTimeMillis()

            // Copy content:// photo URI to internal storage for permanence
            val persistedPhotoPath = photoUri?.let {
                ImageStorage.copyToInternalStorage(getApplication(), it)
            }

            // Auto-generate title from first line of text, or use default
            val title = textContent.lines().firstOrNull { it.isNotBlank() }?.take(50)
                ?: if (persistedPhotoPath != null) "A photo memory"
                else if (audioUri != null) "A voice memory"
                else "Untitled"

            // Detect emotional tone from text content
            val detectedTone = EmotionDetector.detect(textContent)

            val memory = Memory(
                id = existingId ?: UUID.randomUUID().toString(),
                timestamp = now,
                title = title,
                textContent = textContent.ifBlank { null },
                photoFilePath = persistedPhotoPath,
                audioFilePath = audioUri,
                emotionalTone = detectedTone,
                createdAt = existingCreatedAt ?: now,
                updatedAt = now
            )

            repository.insert(memory)
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
            return CaptureViewModel(application, repository) as T
        }
    }
}
