package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Memory
import com.example.myapplication.data.repository.MemoryRepository
import com.example.myapplication.util.EmotionDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class CaptureViewModel(private val repository: MemoryRepository) : ViewModel() {

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

        val now = System.currentTimeMillis()

        // Auto-generate title from first line of text, or use default if empty
        val title = textContent.lines().firstOrNull { it.isNotBlank() }?.take(50) 
            ?: if (photoUri != null) "A photo memory" else if (audioUri != null) "A voice memory" else "Untitled"

        // Detect emotional tone from text content
        val detectedTone = EmotionDetector.detect(textContent)

        val memory = Memory(
            id = existingId ?: UUID.randomUUID().toString(),
            timestamp = now,
            title = title,
            textContent = textContent.ifBlank { null },
            photoFilePath = photoUri,
            audioFilePath = audioUri,
            emotionalTone = detectedTone,
            createdAt = existingCreatedAt ?: now,
            updatedAt = now
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(memory)
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    // Factory to create ViewModel with a repository (no Hilt needed for MVP)
    class Factory(private val repository: MemoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return CaptureViewModel(repository) as T
        }
    }
}
