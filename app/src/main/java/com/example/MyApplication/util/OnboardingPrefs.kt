package com.example.myapplication.util

import android.content.Context

object OnboardingPrefs {
    private const val PREFS_NAME = "memory_onboarding"
    private const val KEY_WELCOME_SEEN = "welcome_seen"

    fun hasSeenWelcome(context: Context): Boolean =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_WELCOME_SEEN, false)

    fun markWelcomeSeen(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_WELCOME_SEEN, true).apply()
    }
}
