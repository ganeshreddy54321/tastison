package com.example.secretdiary

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class voiceunlock : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_voiceunlock)

        val btn_use_text = findViewById<Button>(R.id.btn_use_text)
        btn_use_text.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}