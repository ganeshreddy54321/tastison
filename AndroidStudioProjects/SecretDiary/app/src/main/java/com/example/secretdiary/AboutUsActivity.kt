package com.example.settingspage

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.secretdiary.R

class AboutUsActivity : BaseActivity() { // Assuming you extend BaseActivity for common functionality like textSize and preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_about_us) // Ensure this matches your XML file name

        val rootView: View = findViewById(R.id.about_us_root_layout)

        // Handle back button click
        val backButton: ImageView = findViewById(R.id.about_us_back_button)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle email link click
        val emailLayout: LinearLayout = findViewById(R.id.email_layout)
        emailLayout.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:" + getString(R.string.contact_email)) // Get email from strings.xml
                putExtra(Intent.EXTRA_SUBJECT, "WhisperDiary App Inquiry")
            }
            if (emailIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(emailIntent, getString(R.string.chooser_title_send_email)))
            } else {
                // Optionally, show a Toast or AlertDialog if no email app is found
                // Toast.makeText(this, getString(R.string.no_email_app_found), Toast.LENGTH_SHORT).show()
            }
        }

        // --- IMPORTANT: Replace with your ACTUAL URLs where you host your Privacy Policy and Terms of Use ---
        // These are placeholders. You MUST replace them with your live URLs.
        val privacyPolicyUrl = "https://yourdomain.com/privacy-policy.html"
        val termsOfUseUrl = "https://yourdomain.com/terms-of-use.html"
        // --- END IMPORTANT ---

        // Handle Privacy Policy link click
        val privacyPolicyLink: TextView = findViewById(R.id.privacy_policy_link)
        privacyPolicyLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
            startActivity(browserIntent)
        }

        // Handle Terms of Use link click
        val termsOfUseLink: TextView = findViewById(R.id.terms_of_use_link)
        termsOfUseLink.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfUseUrl))
            startActivity(browserIntent)
        }

        // Apply text size and window insets (assuming these functions are defined in your BaseActivity)
        applyTextSizeToViews(rootView, currentTextSize)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}