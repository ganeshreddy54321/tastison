package com.example.settingspage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton // Import AppCompatButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secretdiary.R

class Settings2 : BaseActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var bioEditText: EditText

    private val PREFS_NAME = "VoiceDiaryPrefs"
    private val KEY_NAME = "user_name" // This will store the name displayed on activity_settings2
    private val KEY_ABOUT_ME = "user_about_me" // This will store the bio displayed on activity_settings2

    // Request code for starting EditProfileActivity
    private val EDIT_PROFILE_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings2)

        val rootView: View = findViewById(R.id.main_root_layout)

        nameEditText = findViewById(R.id.edit_display_name)
        bioEditText = findViewById(R.id.edit_profile_bio)

        // Load data initially
        loadProfileData()

        applyTextSizeToViews(rootView, currentTextSize)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val nightSwitch = findViewById<SwitchCompat>(R.id.night_mode_switch)
        nightSwitch.isChecked = prefs.getBoolean("night_mode", false)

        nightSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit {
                putBoolean("night_mode", isChecked)
            }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }

        val backButton: ImageView = findViewById(R.id.back_button_image)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Set up the "Edit Profile" button to launch EditProfileActivity
        val editProfileButton: AppCompatButton = findViewById(R.id.edit_profile_button)
        editProfileButton.setOnClickListener {
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST_CODE) // Use startActivityForResult to know when it returns
        }


        findViewById<RelativeLayout>(R.id.security_privacy_row).setOnClickListener {
            startActivity(Intent(this, SecurityPrivacyActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.text_size_row).setOnClickListener {
            startActivity(Intent(this, TextSizeActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.language_row).setOnClickListener {
            startActivity(Intent(this, LanguageActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.export_entries_row).setOnClickListener {
            startActivity(Intent(this, ExportEntriesActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.send_message_row).setOnClickListener {
            startActivity(Intent(this, SendMessageActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.about_us_row).setOnClickListener {
            startActivity(Intent(this, AboutUsActivity::class.java))
        }

        findViewById<RelativeLayout>(R.id.logout_row).setOnClickListener {
            Toast.makeText(this, "Logout functionality will be implemented here.", Toast.LENGTH_SHORT).show()
        }
    }

    // Called when EditProfileActivity returns a result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST_CODE && resultCode == RESULT_OK) {
            // If EditProfileActivity finished successfully, reload the profile data
            loadProfileData()
            Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to load profile data from SharedPreferences
    private fun loadProfileData() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        nameEditText.setText(sharedPreferences.getString(KEY_NAME, "Dreamer")) // Default "Dreamer"
        bioEditText.setText(sharedPreferences.getString(KEY_ABOUT_ME, "About Me")) // Default "About Me"
    }

    override fun onResume() {
        super.onResume()
        loadProfileData()
    }
}