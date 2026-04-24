package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Memory
import com.example.myapplication.data.repository.MemoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "Diary.DiaryVM"

class DiaryViewModel(private val repository: MemoryRepository) : ViewModel() {

    var indexDialValue: Float = 0f

    val memories: StateFlow<List<Memory>> = repository.allMemories
        .onEach { list -> Log.d(TAG, "memories updated: count=${list.size}") }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteMemory(memory: Memory) {
        Log.i(TAG, "deleteMemory: id=${memory.id} title='${memory.title}'")
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(memory)
            Log.d(TAG, "deleteMemory: done id=${memory.id}")
        }
    }

    class Factory(private val repository: MemoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            Log.d(TAG, "Factory.create: DiaryViewModel")
            return DiaryViewModel(repository) as T
        }
    }
}
