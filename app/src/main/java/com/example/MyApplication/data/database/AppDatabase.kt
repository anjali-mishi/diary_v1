package com.example.myapplication.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.data.dao.MemoryDao
import com.example.myapplication.data.model.Memory

private const val TAG = "Diary.Database"

@Database(entities = [Memory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Log.d(TAG, "getDatabase: creating new instance")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "memory_database"
                ).build()
                INSTANCE = instance
                Log.i(TAG, "getDatabase: instance created")
                instance
            }.also {
                if (INSTANCE != null) Log.d(TAG, "getDatabase: returning existing instance")
            }
        }
    }
}
