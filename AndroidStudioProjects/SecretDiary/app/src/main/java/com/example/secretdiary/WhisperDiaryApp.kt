package com.example.settingspage

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.core.content.edit // Important import for the SharedPreferences.edit extension function

class WhisperDiaryApp : Application() {

    // Store the last known text size factor for quick access across the application.
    var appTextSizeFactor: Float = 0.5f // Default to medium (halved factor)

    override fun onCreate() {
        super.onCreate()
        // Initialize the appTextSizeFactor from SharedPreferences when the app process starts.
        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        appTextSizeFactor = prefs.getFloat("text_size", 0.5f) // Default to 0.5f if not set
        Log.d("WhisperDiaryApp", "onCreate: Initial appTextSizeFactor loaded: $appTextSizeFactor")
    }

    /**
     * This method is called very early in the application lifecycle, even before Activities are created.
     * It's the ideal place to apply a custom configuration (like font scale) to the application's base context.
     * All subsequent Activities will inherit this modified context.
     */
    override fun attachBaseContext(base: Context?) {
        val prefs = base?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedTextSize = prefs?.getFloat("text_size", 0.5f) ?: 0.5f

        appTextSizeFactor = savedTextSize

        val contextWithOverride = applyTextSizeOverride(base, savedTextSize)
        super.attachBaseContext(contextWithOverride)
    }

    /**
     * Creates a new Context with an overridden Configuration, specifically setting the fontScale.
     */
    private fun applyTextSizeOverride(baseContext: Context?, sizeFactor: Float): Context {
        if (baseContext == null) return this

        val config = Configuration(baseContext.resources.configuration)
        // Calculate fontScale: if 0.5f is your "normal" (1.0x), then scale accordingly.
        config.fontScale = sizeFactor / 0.5f
        Log.d("WhisperDiaryApp", "applyTextSizeOverride: Setting fontScale to ${config.fontScale} for sizeFactor: $sizeFactor")

        return baseContext.createConfigurationContext(config)
    }

    /**
     * Public method to update the app-wide text size.
     * Called from TextSizeActivity when the user saves a new text size.
     */
    fun updateTextSize(newSizeFactor: Float) {
        appTextSizeFactor = newSizeFactor

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        // FIX: Corrected SharedPreferences.Editor usage for putFloat using the KTX extension
        prefs.edit {
            putFloat("text_size", newSizeFactor)
        }
        Log.d("WhisperDiaryApp", "updateTextSize: Saved new appTextSizeFactor: $newSizeFactor")

        // WARNING: 'updateConfiguration' is deprecated post-Android N.
        // The primary mechanism for Activities to pick up changes is via attachBaseContext.
        val config = Configuration(resources.configuration)
        config.fontScale = newSizeFactor / 0.5f
        resources.updateConfiguration(config, resources.displayMetrics)
        Log.d("WhisperDiaryApp", "updateTextSize: Resources configuration updated.")
    }
}