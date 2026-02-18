package com.example.secretdiary

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class addentry : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var editContent: EditText
    private val RECORD_AUDIO_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_addentry)

        val editTitle = findViewById<EditText>(R.id.editTitle)
        editContent = findViewById(R.id.editContent)
        val textDate = findViewById<EditText>(R.id.textDate)
        val micIcon = findViewById<ImageView>(R.id.micIcon)
        val btnPrivate = findViewById<Button>(R.id.btnPrivate)
        val btnPublic = findViewById<Button>(R.id.btnPublic)
        val btnCreative = findViewById<Button>(R.id.btnCreative)

        // Set current date
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
        val currentDateTime = Calendar.getInstance().time
        textDate.setText("Date: ${formatter.format(currentDateTime)}")

        // Date picker dialog
        textDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(y, m, d)
                textDate.setText("Date: ${formatter.format(selectedCalendar.time)}")
            }, year, month, day)

            datePicker.show()
        }

        //Speech
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }

        micIcon.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    RECORD_AUDIO_REQUEST_CODE
                )
            } else {
                speechRecognizer.startListening(speechIntent)
            }
        }

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches.joinToString(" ")
                    editContent.append("$recognizedText ")
                }
            }

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(applicationContext, "Speech error: $error", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        // Save functions
        fun saveEntry(category: String) {
            val title = editTitle.text.toString().trim()
            val content = editContent.text.toString().trim()

            // current date & time
            val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.getDefault())
            val currentDateTime = Calendar.getInstance().time
            val date = "Date: ${formatter.format(currentDateTime)}"
            textDate.setText(date)  // update in UI as well

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val entryJson = """{"title":"$title","date":"$date","content":"$content"}"""
                val sharedPref = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
                val entries =
                    sharedPref.getStringSet(category, setOf())?.toMutableSet() ?: mutableSetOf()
                entries.add(entryJson)
                sharedPref.edit().putStringSet(category, entries).apply()

                Toast.makeText(
                    this,
                    "Saved to ${category.replace("_", " ").capitalize()}",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Please enter title and content", Toast.LENGTH_SHORT).show()
            }
        }


        btnPrivate.setOnClickListener { saveEntry("daily_entries") }
        btnPublic.setOnClickListener { saveEntry("personal_entries") }
        btnCreative.setOnClickListener { saveEntry("creative_entries") }
    }
}
