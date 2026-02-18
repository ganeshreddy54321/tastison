package com.example.settingspage

import android.content.Context
import android.os.Bundle
import android.view.View // ADD THIS LINE
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity // Make sure this is imported for Activity.RESULT_OK/CANCELED
import com.example.secretdiary.R

class EditProfileActivity : BaseActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var voiceGreetingEditText: EditText
    private lateinit var aboutMeEditText: EditText
    private lateinit var reflectionTaglineEditText: EditText
    private lateinit var profileImageView: ImageView
    private lateinit var backButton: ImageView

    private val PREFS_NAME = "VoiceDiaryPrefs"
    private val KEY_NAME = "user_name"
    private val KEY_ABOUT_ME = "user_about_me"
    private val KEY_VOICE_GREETING = "user_voice_greeting"
    private val KEY_REFLECTION_TAGLINE = "user_reflection_tagline"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profile)

        val rootView: View = findViewById(R.id.edit_profile_root_layout)

        profileImageView = findViewById(R.id.edit_profile_image)
        backButton = findViewById(R.id.edit_profile_back_button)
        nicknameEditText = findViewById(R.id.edit_profile_name)
        voiceGreetingEditText = findViewById(R.id.edit_profile_voice_greeting)
        aboutMeEditText = findViewById(R.id.edit_profile_about_me)
        reflectionTaglineEditText = findViewById(R.id.edit_profile_reflection_tagline)
        val saveButton: Button = findViewById(R.id.save_profile_button)

        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        nicknameEditText.setText(sharedPreferences.getString(KEY_NAME, ""))
        voiceGreetingEditText.setText(sharedPreferences.getString(KEY_VOICE_GREETING, ""))
        aboutMeEditText.setText(sharedPreferences.getString(KEY_ABOUT_ME, ""))
        reflectionTaglineEditText.setText(sharedPreferences.getString(KEY_REFLECTION_TAGLINE, ""))

        saveButton.setOnClickListener {
            val newNickname = nicknameEditText.text.toString().trim()
            val newVoiceGreeting = voiceGreetingEditText.text.toString().trim()
            val newAboutMe = aboutMeEditText.text.toString().trim()
            val newReflectionTagline = reflectionTaglineEditText.text.toString().trim()

            sharedPreferences.edit {
                putString(KEY_NAME, newNickname)
                putString(KEY_VOICE_GREETING, newVoiceGreeting)
                putString(KEY_ABOUT_ME, newAboutMe)
                putString(KEY_REFLECTION_TAGLINE, newReflectionTagline)
            }
            Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }

        backButton.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            onBackPressedDispatcher.onBackPressed()
        }

        applyTextSizeToViews(rootView, currentTextSize)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}