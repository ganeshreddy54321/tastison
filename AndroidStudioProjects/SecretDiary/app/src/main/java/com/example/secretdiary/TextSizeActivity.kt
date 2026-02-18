package com.example.settingspage

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.content.edit // Important import for the SharedPreferences.edit extension function
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secretdiary.R
import com.google.android.material.button.MaterialButton

class TextSizeActivity : BaseActivity() {

    private lateinit var previewText: TextView
    private lateinit var btnSmall: MaterialButton
    private lateinit var btnMedium: MaterialButton
    private lateinit var btnLarge: MaterialButton
    private lateinit var btnCustom: MaterialButton
    private lateinit var seekBar: SeekBar
    private lateinit var btnSaveChanges: MaterialButton

    private val BASE_PREVIEW_TEXT_SIZE_SP = 14f

    private val SMALL_SIZE_FACTOR = 0.4f
    private val MEDIUM_SIZE_FACTOR = 0.5f
    private val LARGE_SIZE_FACTOR = 0.6f

    private val MIN_CUSTOM_SIZE_FACTOR = 0.35f
    private val MAX_CUSTOM_SIZE_FACTOR = 0.75f

    private var tempSelectedSizeFactor: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_text_size)

        val rootView: View = findViewById(R.id.main)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton: ImageView = findViewById(R.id.back_button_image)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        btnSmall = findViewById(R.id.btnSmall)
        btnMedium = findViewById(R.id.btnMedium)
        btnLarge = findViewById(R.id.btnLarge)
        btnCustom = findViewById(R.id.btnCustom)
        seekBar = findViewById(R.id.seekBar)
        previewText = findViewById(R.id.previewText)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

        tempSelectedSizeFactor = currentTextSize
        setPreviewTextSize(tempSelectedSizeFactor)
        updateButtonSelection(tempSelectedSizeFactor)

        val initialProgress =
            ((currentTextSize - MIN_CUSTOM_SIZE_FACTOR) / (MAX_CUSTOM_SIZE_FACTOR - MIN_CUSTOM_SIZE_FACTOR) * 100).toInt()
        seekBar.progress = initialProgress.coerceIn(0, seekBar.max)

        btnSmall.setOnClickListener {
            tempSelectedSizeFactor = SMALL_SIZE_FACTOR
            setPreviewTextSize(tempSelectedSizeFactor)
            updateButtonSelection(tempSelectedSizeFactor)
            updateSeekBarForPredefinedSize(SMALL_SIZE_FACTOR)
        }

        btnMedium.setOnClickListener {
            tempSelectedSizeFactor = MEDIUM_SIZE_FACTOR
            setPreviewTextSize(tempSelectedSizeFactor)
            updateButtonSelection(tempSelectedSizeFactor)
            updateSeekBarForPredefinedSize(MEDIUM_SIZE_FACTOR)
        }

        btnLarge.setOnClickListener {
            tempSelectedSizeFactor = LARGE_SIZE_FACTOR
            setPreviewTextSize(tempSelectedSizeFactor)
            updateButtonSelection(tempSelectedSizeFactor)
            updateSeekBarForPredefinedSize(LARGE_SIZE_FACTOR)
        }

        btnCustom.setOnClickListener {
            updateButtonSelection(tempSelectedSizeFactor)
            Toast.makeText(this, "Adjust text size using the slider below.", Toast.LENGTH_SHORT).show()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newSizeFactor = MIN_CUSTOM_SIZE_FACTOR + (progress / 100f) * (MAX_CUSTOM_SIZE_FACTOR - MIN_CUSTOM_SIZE_FACTOR)
                tempSelectedSizeFactor = newSizeFactor
                setPreviewTextSize(tempSelectedSizeFactor)
                updateButtonSelection(tempSelectedSizeFactor)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) { /* No action needed */ }

            override fun onStopTrackingTouch(seekBar: SeekBar?) { /* No action needed */ }
        })

        btnSaveChanges.setOnClickListener {
            if (tempSelectedSizeFactor != currentTextSize) {
                // FIX: Corrected SharedPreferences.Editor usage for putFloat using the KTX extension
                prefs.edit {
                    putFloat("text_size", tempSelectedSizeFactor)
                }

                (application as? WhisperDiaryApp)?.updateTextSize(tempSelectedSizeFactor)

                recreate() // Recreate THIS activity to show the new size immediately.
                Toast.makeText(this, "Text size applied directly!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setPreviewTextSize(sizeFactor: Float) {
        val scaledPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            BASE_PREVIEW_TEXT_SIZE_SP * sizeFactor,
            resources.displayMetrics
        )
        previewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, scaledPx)
    }

    private fun updateButtonSelection(selectedFactor: Float) {
        val buttons = listOf(btnSmall, btnMedium, btnLarge, btnCustom)
        val predefinedFactors = listOf(SMALL_SIZE_FACTOR, MEDIUM_SIZE_FACTOR, LARGE_SIZE_FACTOR)
        val tolerance = 0.005f

        val lgreenColorStateList = ContextCompat.getColorStateList(this, R.color.lgreen)
        val whiteColor = ContextCompat.getColor(this, R.color.white)

        buttons.forEach { button ->
            val isSelected: Boolean
            // Get the default text color for this specific button.
            // This is the most reliable way to get what it's supposed to be when not selected.
            val currentButtonDefaultTextColorStateList = button.textColors

            when (button.id) {
                R.id.btnSmall -> {
                    isSelected = Math.abs(selectedFactor - SMALL_SIZE_FACTOR) < tolerance
                }
                R.id.btnMedium -> {
                    isSelected = Math.abs(selectedFactor - MEDIUM_SIZE_FACTOR) < tolerance
                }
                R.id.btnLarge -> {
                    isSelected = Math.abs(selectedFactor - LARGE_SIZE_FACTOR) < tolerance
                }
                R.id.btnCustom -> {
                    isSelected = !predefinedFactors.any { Math.abs(selectedFactor - it) < tolerance }
                }
                else -> isSelected = false
            }

            if (isSelected) {
                button.setBackgroundTintList(lgreenColorStateList)
                button.setTextColor(whiteColor)
                button.strokeWidth = 0
            } else {
                button.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.transparent))
                button.setTextColor(currentButtonDefaultTextColorStateList)
                button.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics).toInt()
                button.strokeColor = lgreenColorStateList
            }
        }
    }

    private fun updateSeekBarForPredefinedSize(predefinedFactor: Float) {
        val progress = ((predefinedFactor - MIN_CUSTOM_SIZE_FACTOR) / (MAX_CUSTOM_SIZE_FACTOR - MIN_CUSTOM_SIZE_FACTOR) * 100).toInt()
        seekBar.progress = progress.coerceIn(0, seekBar.max)
    }
}