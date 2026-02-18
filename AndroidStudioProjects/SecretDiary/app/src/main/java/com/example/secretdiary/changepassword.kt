package com.example.secretdiary

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.*

class changepassword : AppCompatActivity() {

    private lateinit var micIcon: ImageView
    private lateinit var listeningText: TextView
    private lateinit var errorMessage: TextView
    private lateinit var saveTextButton: Button
    private lateinit var reRecordButton: Button
    private lateinit var saveVoiceButton: Button
    private var capturedVoicePassword: String = ""

    private lateinit var speechRecognizer: SpeechRecognizer

    // ✅ Permission Launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Microphone permission is required for voice recognition", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changepassword)

        micIcon = findViewById(R.id.mic_icon)
        listeningText = findViewById(R.id.listening_text)
        errorMessage = findViewById(R.id.error_message)
        saveTextButton = findViewById(R.id.btn_use_text)
        reRecordButton = findViewById(R.id.btn_try_again)
        saveVoiceButton = findViewById(R.id.btn_save_voice)

        listeningText.visibility = View.GONE
        errorMessage.visibility = View.GONE

        // ✅ Check permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)

        micIcon.setOnClickListener { startListening() }
        reRecordButton.setOnClickListener { startListening() }
        saveTextButton.setOnClickListener { showTextPasswordDialog() }
        saveVoiceButton.setOnClickListener { saveVoicePassword() }
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        listeningText.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        listeningText.text = "Listening..."

        speechRecognizer.setRecognitionListener(object : android.speech.RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    capturedVoicePassword = matches[0]
                    listeningText.text = "Captured: \"$capturedVoicePassword\""
                    Toast.makeText(applicationContext, "Voice captured. Tap save to confirm.", Toast.LENGTH_SHORT).show()
                } else {
                    listeningText.visibility = View.GONE
                    errorMessage.text = "Didn't catch that. Please try again."
                    errorMessage.visibility = View.VISIBLE
                }
            }

            override fun onError(error: Int) {
                listeningText.visibility = View.GONE
                errorMessage.text = "Error recognizing voice. Try again."
                errorMessage.visibility = View.VISIBLE
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer.startListening(intent)
    }

    private fun saveVoicePassword() {
        if (capturedVoicePassword.isNotEmpty()) {
            val sharedPref = getSharedPreferences("VoiceLockPrefs", MODE_PRIVATE)
            sharedPref.edit().putString("voice_password", capturedVoicePassword).apply()
            Toast.makeText(this, "Voice password saved!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please record your voice first.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showTextPasswordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_text_password, null)
        val editText = dialogView.findViewById<EditText>(R.id.textPasswordField)

        AlertDialog.Builder(this)
            .setTitle("Enter Text Password")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val textPassword = editText.text.toString().trim()
                if (textPassword.isNotEmpty()) {
                    val sharedPref = getSharedPreferences("VoiceLockPrefs", MODE_PRIVATE)
                    sharedPref.edit().putString("text_password", textPassword).apply()
                    Toast.makeText(this, "Text password saved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
