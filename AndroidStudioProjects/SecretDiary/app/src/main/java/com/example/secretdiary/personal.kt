package com.example.secretdiary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class personal : AppCompatActivity() {

    private lateinit var personalContainer: LinearLayout
    private lateinit var sharedPref: android.content.SharedPreferences
    private var entries: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)

        personalContainer = findViewById(R.id.personalContainer)
        sharedPref = getSharedPreferences("DiaryPrefs", Context.MODE_PRIVATE)
        entries = sharedPref.getStringSet("personal_entries", setOf())?.toMutableSet() ?: mutableSetOf()

        for (entry in entries) {
            addEntryToLayout(entry)
        }
    }

    private fun addEntryToLayout(entryJson: String) {
        val entryView = LayoutInflater.from(this).inflate(R.layout.entry_item, personalContainer, false)

        val entryTitleView = entryView.findViewById<TextView>(R.id.entryTitle)
        val entryDateView = entryView.findViewById<TextView>(R.id.entryDate)
        val entryContentView = entryView.findViewById<TextView>(R.id.entryContent)
        val deleteButton = entryView.findViewById<Button>(R.id.deleteButton)
        val editButton = entryView.findViewById<Button>(R.id.editButton)

        var currentEntry = entryJson

        try {
            val obj = JSONObject(entryJson)
            entryTitleView.text = obj.optString("title", "")
            entryDateView.text = obj.optString("date", "")
            entryContentView.text = obj.optString("content", "")
        } catch (e: Exception) {
            entryContentView.text = entryJson
        }

        // DELETE and UNDO
        deleteButton.setOnClickListener {
            val index = personalContainer.indexOfChild(entryView)
            personalContainer.removeView(entryView)
            entries.remove(currentEntry)
            sharedPref.edit().putStringSet("personal_entries", entries).apply()

            Snackbar.make(personalContainer, "Entry deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    entries.add(currentEntry)
                    sharedPref.edit().putStringSet("personal_entries", entries).apply()
                    addEntryToLayoutAt(currentEntry, index)
                }
                .show()
        }

        // EDIT
        editButton.setOnClickListener {
            try {
                val json = JSONObject(currentEntry)

                val editLayout = layoutInflater.inflate(R.layout.edit_entry_popup, null)
                val editTitle = editLayout.findViewById<EditText>(R.id.editTitleField)
                val editContent = editLayout.findViewById<EditText>(R.id.editContentField)

                editTitle.setText(json.optString("title", ""))
                editContent.setText(json.optString("content", ""))

                AlertDialog.Builder(this)
                    .setTitle("Edit Entry")
                    .setView(editLayout)
                    .setPositiveButton("Save") { _, _ ->
                        val newTitle = editTitle.text.toString().trim()
                        val newContent = editContent.text.toString().trim()
                        val date = json.optString("date", "")

                        if (newTitle.isNotEmpty() && newContent.isNotEmpty()) {
                            entries.remove(currentEntry)

                            val updatedEntry = """{"title":"$newTitle","date":"$date","content":"$newContent"}"""
                            entries.add(updatedEntry)
                            sharedPref.edit().putStringSet("personal_entries", entries).apply()

                            entryTitleView.text = newTitle
                            entryContentView.text = newContent
                            currentEntry = updatedEntry

                            Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error loading entry", Toast.LENGTH_SHORT).show()
            }
        }

        personalContainer.addView(entryView)
    }

    private fun addEntryToLayoutAt(entryJson: String, index: Int) {
        val entryView = LayoutInflater.from(this).inflate(R.layout.entry_item, null)

        val entryTitleView = entryView.findViewById<TextView>(R.id.entryTitle)
        val entryDateView = entryView.findViewById<TextView>(R.id.entryDate)
        val entryContentView = entryView.findViewById<TextView>(R.id.entryContent)
        val deleteButton = entryView.findViewById<Button>(R.id.deleteButton)
        val editButton = entryView.findViewById<Button>(R.id.editButton)

        var currentEntry = entryJson

        try {
            val obj = JSONObject(entryJson)
            entryTitleView.text = obj.optString("title", "")
            entryDateView.text = obj.optString("date", "")
            entryContentView.text = obj.optString("content", "")
        } catch (e: Exception) {
            entryContentView.text = entryJson
        }

        deleteButton.setOnClickListener {
            val indexAgain = personalContainer.indexOfChild(entryView)
            personalContainer.removeView(entryView)
            entries.remove(currentEntry)
            sharedPref.edit().putStringSet("personal_entries", entries).apply()

            Snackbar.make(personalContainer, "Entry deleted", Snackbar.LENGTH_LONG)
                .setAction("UNDO") {
                    entries.add(currentEntry)
                    sharedPref.edit().putStringSet("personal_entries", entries).apply()
                    addEntryToLayoutAt(currentEntry, indexAgain)
                }
                .show()
        }

        editButton.setOnClickListener {
            try {
                val json = JSONObject(currentEntry)
                val editLayout = layoutInflater.inflate(R.layout.edit_entry_popup, null)
                val editTitle = editLayout.findViewById<EditText>(R.id.editTitleField)
                val editContent = editLayout.findViewById<EditText>(R.id.editContentField)

                editTitle.setText(json.optString("title", ""))
                editContent.setText(json.optString("content", ""))

                AlertDialog.Builder(this)
                    .setTitle("Edit Entry")
                    .setView(editLayout)
                    .setPositiveButton("Save") { _, _ ->
                        val updatedTitle = editTitle.text.toString().trim()
                        val updatedContent = editContent.text.toString().trim()
                        val date = json.optString("date", "")

                        if (updatedTitle.isNotEmpty() && updatedContent.isNotEmpty()) {
                            entries.remove(currentEntry)
                            val updatedEntry = """{"title":"$updatedTitle","date":"$date","content":"$updatedContent"}"""
                            entries.add(updatedEntry)
                            sharedPref.edit().putStringSet("personal_entries", entries).apply()

                            entryTitleView.text = updatedTitle
                            entryContentView.text = updatedContent
                            currentEntry = updatedEntry

                            Toast.makeText(this, "Entry updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Title and Content cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error editing entry", Toast.LENGTH_SHORT).show()
            }
        }

        personalContainer.addView(entryView, index)
    }
}
