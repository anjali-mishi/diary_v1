package com.example.myapplication.data.repository

import com.example.myapplication.data.dao.MemoryDao
import com.example.myapplication.data.model.Memory
import kotlinx.coroutines.flow.Flow

class MemoryRepository(private val memoryDao: MemoryDao) {

    val allMemories: Flow<List<Memory>> = memoryDao.getAllMemories()

    fun getMemoryById(id: String): Memory? = memoryDao.getMemoryById(id)

    fun insert(memory: Memory) {
        memoryDao.insertMemory(memory)
    }

    fun update(memory: Memory) {
        memoryDao.updateMemory(memory)
    }

    fun delete(memory: Memory) {
        memoryDao.deleteMemory(memory)
    }
}
