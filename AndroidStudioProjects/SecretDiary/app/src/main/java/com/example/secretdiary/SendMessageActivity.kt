package com.example.settingspage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar // Import RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secretdiary.R

class SendMessageActivity : BaseActivity() {

    private lateinit var subjectEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: Button
    private lateinit var feedbackRatingBar: RatingBar // Declare RatingBar
    private var currentRating: Float = 0.0f // Store the current rating

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_send_message)

        val rootView: View = findViewById(R.id.send_message_root_layout)
        applyTextSizeToViews(rootView, currentTextSize) // Assuming BaseActivity provides this

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backButton: ImageView = findViewById(R.id.back_button_image)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        subjectEditText = findViewById(R.id.edit_text_subject)
        messageEditText = findViewById(R.id.edit_text_message)
        sendButton = findViewById(R.id.send_message_button)
        feedbackRatingBar = findViewById(R.id.feedback_rating_bar) // Initialize RatingBar

        // Set listener for RatingBar changes
        feedbackRatingBar.setOnRatingBarChangeListener { _, rating, _ ->
            currentRating = rating
            // Optionally, provide immediate feedback to the user, e.g., Toast
            // Toast.makeText(this, "Rated: $rating stars", Toast.LENGTH_SHORT).show()
        }

        sendButton.setOnClickListener {
            val subject = subjectEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            // You might want to make rating optional, or require a minimum rating.
            // For now, let's allow sending even with 0 rating if other fields are filled.
            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, getString(R.string.email_validation_error), Toast.LENGTH_SHORT).show()
            } else {
                // Construct the email body to include the rating
                val emailBody = buildString {
                    append("Rating: $currentRating out of ${feedbackRatingBar.numStars} stars\n\n")
                    append("Message:\n")
                    append(message)
                }

                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("upapireddygari@gmail.com")) // Your recipient email
                    putExtra(Intent.EXTRA_SUBJECT, "[WhisperDiary Feedback] $subject")
                    putExtra(Intent.EXTRA_TEXT, emailBody)
                }

                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(Intent.createChooser(intent, getString(R.string.chooser_title_send_email)))
                    // Clear fields and reset rating after sending
                    subjectEditText.text.clear()
                    messageEditText.text.clear()
                    feedbackRatingBar.rating = 0.0f // Reset rating
                    currentRating = 0.0f
                } else {
                    Toast.makeText(this, getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}