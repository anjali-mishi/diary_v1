package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.dao.MemoryDao
import com.example.myapplication.data.model.Memory
import kotlinx.coroutines.flow.Flow

private const val TAG = "Diary.Repository"

class MemoryRepository(private val memoryDao: MemoryDao) {

    val allMemories: Flow<List<Memory>> = memoryDao.getAllMemories()

    fun getMemoryById(id: String): Memory? {
        Log.d(TAG, "getMemoryById: id=$id")
        val result = memoryDao.getMemoryById(id)
        Log.d(TAG, "getMemoryById: ${if (result != null) "found title='${result.title}'" else "not found"}")
        return result
    }

    fun insert(memory: Memory) {
        Log.d(TAG, "insert: id=${memory.id} title='${memory.title}' tone=${memory.emotionalTone}")
        memoryDao.insertMemory(memory)
        Log.d(TAG, "insert: done id=${memory.id}")
    }

    fun update(memory: Memory) {
        Log.d(TAG, "update: id=${memory.id} title='${memory.title}'")
        memoryDao.updateMemory(memory)
        Log.d(TAG, "update: done id=${memory.id}")
    }

    fun delete(memory: Memory) {
        Log.i(TAG, "delete: id=${memory.id} title='${memory.title}'")
        memoryDao.deleteMemory(memory)
        Log.d(TAG, "delete: done id=${memory.id}")
    }
}
