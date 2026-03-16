package com.example.myapplication.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.dao.MemoryDao
import com.example.myapplication.data.model.Memory

private const val TAG = "Diary.Database"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE memories ADD COLUMN waveformData TEXT")
    }
}

@Database(entities = [Memory::class], version = 2, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2).build()
                INSTANCE = instance
                Log.i(TAG, "getDatabase: instance created")
                instance
            }.also {
                if (INSTANCE != null) Log.d(TAG, "getDatabase: returning existing instance")
            }
        }
    }
}
