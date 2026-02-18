package com.example.settingspage

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration // Important import
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

open class BaseActivity : AppCompatActivity() {

    protected lateinit var prefs: SharedPreferences
    protected var currentTextSize: Float = 0.5f // Default to 0.5f, which maps to 1.0x font scale

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ensure "app_prefs" is used consistently for SharedPreferences name
        prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        currentTextSize = prefs.getFloat("text_size", 0.5f)

        val isNightMode = prefs.getBoolean("night_mode", false)
        AppCompatDelegate.setDefaultNightMode(
            if (isNightMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    /**
     * Overrides the base context for the Activity to apply the custom font scale.
     * This ensures that all views in this Activity (and its children) that use 'sp' units
     * will automatically scale according to the `fontScale` set here.
     */
    override fun attachBaseContext(newBase: Context?) {
        val app = newBase?.applicationContext as? WhisperDiaryApp
        val fontSizeFactor = app?.appTextSizeFactor ?: 0.5f

        val config = Configuration(newBase?.resources?.configuration)
        // Calculate the desired fontScale. If 0.5f is your "normal" factor,
        // then fontScale = factor / 0.5f.
        config.fontScale = fontSizeFactor / 0.5f
        val context = newBase?.createConfigurationContext(config) ?: newBase
        super.attachBaseContext(context)
    }

    /**
     * Recursively applies the given text size multiplier to all TextViews, EditTexts, and Buttons
     * within a view hierarchy. This is primarily for views whose text sizes are not in 'sp'
     * or for dynamic views that need explicit re-scaling.
     *
     * @param view The starting view (e.g., the root layout of an Activity or Fragment).
     * @param sizeFactor The scaling factor to apply (e.g., 0.5f for medium, 0.4f for smaller).
     */
    protected fun applyTextSizeToViews(view: View, sizeFactor: Float) {
        val effectiveScale = sizeFactor / 0.5f

        if (view is TextView) {
            val originalTextSizeSp = view.textSize / resources.displayMetrics.scaledDensity
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSizeSp * effectiveScale)
        } else if (view is EditText) {
            val originalTextSizeSp = view.textSize / resources.displayMetrics.scaledDensity
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSizeSp * effectiveScale)
        } else if (view is Button) {
            val originalTextSizeSp = view.textSize / resources.displayMetrics.scaledDensity
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalTextSizeSp * effectiveScale)
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val child = view.getChildAt(i)
                applyTextSizeToViews(child, sizeFactor)
            }
        }
    }
}