package com.example.myapplication.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "memories")
data class Memory(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val title: String, // User-defined or auto-generated from first line of text
    val textContent: String? = null,
    val audioFilePath: String? = null,
    val photoFilePath: String? = null, // Reference to phone storage path
    val emotionalTone: String? = null, // Storing enum name as String for simplicity in MVP
    val isHidden: Boolean = false,
    val lastViewedAt: Long? = null, // For "pick up where you left off"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val waveformData: String? = null // JSON-encoded List<Float> of amplitude samples
)

enum class EmotionalTone {
    HAPPY,      // Yellow/Gold
    SAD,        // Blue
    ANXIOUS,    // Purple
    CALM,       // Green
    EXCITED,    // Orange
    NEUTRAL     // Beige/Cream
}
