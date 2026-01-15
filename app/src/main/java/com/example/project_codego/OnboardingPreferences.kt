package com.example.project_codego

import android.content.Context
import android.content.SharedPreferences

class OnboardingPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "onboarding_prefs"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_TRACKER_INTRO_SHOWN = "tracker_intro_shown"
    }

    fun isOnboardingCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted() {
        sharedPreferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
    }

    fun isTrackerIntroShown(): Boolean {
        return sharedPreferences.getBoolean(KEY_TRACKER_INTRO_SHOWN, false)
    }

    fun setTrackerIntroShown() {
        sharedPreferences.edit().putBoolean(KEY_TRACKER_INTRO_SHOWN, true).apply()
    }
}
