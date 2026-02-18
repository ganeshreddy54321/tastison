package com.example.secretdiary

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class Dailyreflections : AppCompatActivity() {

    private lateinit var reflectionContainer: LinearLayout
    private lateinit var sharedPref: SharedPreferences
    private var entries: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_reflections)

        reflectionContainer = findViewById(R.id.reflectionContainer)
        sharedPref = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        entries = sharedPref.getStringSet("daily_entries", setOf())?.toMutableSet() ?: mutableSetOf()

        for (entry in entries) {
            addEntryToLayout(entry)
        }
    }

    private fun addEntryToLayout(entryJson: String) {
        val entryView = LayoutInflater.from(this).inflate(R.layout.entry_item, reflectionContainer, false)

        val titleTextView = entryView.findViewById<TextView>(R.id.entryTitle)
        val dateTextView = entryView.findViewById<TextView>(R.id.entryDate)
        val contentTextView = entryView.findViewById<TextView>(R.id.entryContent)
        val deleteButton = entryView.findViewById<Button>(R.id.deleteButton)
        val editButton = entryView.findViewById<Button>(R.id.editButton)

        var currentEntry = entryJson

        try {
            val json = JSONObject(entryJson)
            titleTextView.text = json.getString("title")
            dateTextView.text = json.getString("date")
            contentTextView.text = json.getString("content")
        } catch (e: Exception) {
            contentTextView.text = entryJson
        }

        editButton.setOnClickListener {
            val json = JSONObject(currentEntry)
            val editLayout = layoutInflater.inflate(R.layout.edit_entry_popup, null)
            val editTitle = editLayout.findViewById<EditText>(R.id.editTitleField)
            val editContent = editLayout.findViewById<EditText>(R.id.editContentField)

            editTitle.setText(json.getString("title"))
            editContent.setText(json.getString("content"))

            AlertDialog.Builder(this)
                .setTitle("Edit Entry")
                .setView(editLayout)
                .setPositiveButton("Save") { _, _ ->
                    val updatedTitle = editTitle.text.toString().trim()
                    val updatedContent = editContent.text.toString().trim()
                    val date = json.getString("date")

                    if (updatedTitle.isNotEmpty() && updatedContent.isNotEmpty()) {
                        entries.remove(currentEntry)
                        val updatedJson = """{"title":"$updatedTitle","date":"$date","content":"$updatedContent"}"""
                        entries.add(updatedJson)
                        sharedPref.edit().putStringSet("daily_entries", entries).apply()

                        titleTextView.text = updatedTitle
                        contentTextView.text = updatedContent
                        currentEntry = updatedJson

                        Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Title and Content can't be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        deleteButton.setOnClickListener {
            val index = reflectionContainer.indexOfChild(entryView)
            reflectionContainer.removeView(entryView)
            entries.remove(currentEntry)
            sharedPref.edit().putStringSet("daily_entries", entries).apply()

            Snackbar.make(reflectionContainer, "Entry deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    entries.add(currentEntry)
                    sharedPref.edit().putStringSet("daily_entries", entries).apply()
                    addEntryToLayoutAt(currentEntry, index)
                }
                .show()
        }

        reflectionContainer.addView(entryView)
    }

    private fun addEntryToLayoutAt(entryJson: String, index: Int) {
        val entryView = LayoutInflater.from(this).inflate(R.layout.entry_item, null)

        val titleTextView = entryView.findViewById<TextView>(R.id.entryTitle)
        val dateTextView = entryView.findViewById<TextView>(R.id.entryDate)
        val contentTextView = entryView.findViewById<TextView>(R.id.entryContent)
        val deleteButton = entryView.findViewById<Button>(R.id.deleteButton)
        val editButton = entryView.findViewById<Button>(R.id.editButton)

        var currentEntry = entryJson

        try {
            val json = JSONObject(entryJson)
            titleTextView.text = json.getString("title")
            dateTextView.text = json.getString("date")
            contentTextView.text = json.getString("content")
        } catch (e: Exception) {
            contentTextView.text = entryJson
        }

        deleteButton.setOnClickListener {
            val indexAgain = reflectionContainer.indexOfChild(entryView)
            reflectionContainer.removeView(entryView)
            entries.remove(currentEntry)
            sharedPref.edit().putStringSet("daily_entries", entries).apply()

            Snackbar.make(reflectionContainer, "Entry deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    entries.add(currentEntry)
                    sharedPref.edit().putStringSet("daily_entries", entries).apply()
                    addEntryToLayoutAt(currentEntry, indexAgain)
                }
                .show()
        }

        editButton.setOnClickListener {
            val json = JSONObject(currentEntry)
            val editLayout = layoutInflater.inflate(R.layout.edit_entry_popup, null)
            val editTitle = editLayout.findViewById<EditText>(R.id.editTitleField)
            val editContent = editLayout.findViewById<EditText>(R.id.editContentField)

            editTitle.setText(json.getString("title"))
            editContent.setText(json.getString("content"))

            AlertDialog.Builder(this)
                .setTitle("Edit Entry")
                .setView(editLayout)
                .setPositiveButton("Save") { _, _ ->
                    val updatedTitle = editTitle.text.toString().trim()
                    val updatedContent = editContent.text.toString().trim()
                    val date = json.getString("date")

                    if (updatedTitle.isNotEmpty() && updatedContent.isNotEmpty()) {
                        entries.remove(currentEntry)
                        val updatedJson = """{"title":"$updatedTitle","date":"$date","content":"$updatedContent"}"""
                        entries.add(updatedJson)
                        sharedPref.edit().putStringSet("daily_entries", entries).apply()

                        titleTextView.text = updatedTitle
                        contentTextView.text = updatedContent
                        currentEntry = updatedJson

                        Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Title and Content can't be empty", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        // ✅ Add view to container at correct index
        reflectionContainer.addView(entryView, index)
    }
}
