package com.example.myapplication.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.myapplication.data.model.Memory
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memories ORDER BY timestamp DESC")
    fun getAllMemories(): Flow<List<Memory>>

    @Query("SELECT * FROM memories WHERE id = :id")
    fun getMemoryById(id: String): Memory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMemory(memory: Memory): Long

    @Update
    fun updateMemory(memory: Memory): Int

    @Delete
    fun deleteMemory(memory: Memory): Int
}
