package com.example.secretdiary

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.example.settingspage.Settings2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        // "Daily Reflections" item
        val itemDaily = findViewById<RelativeLayout>(R.id.itemDaily)
        itemDaily.setOnClickListener {
            startActivity(Intent(this, Dailyreflections::class.java))
        }

        // You can do similar for itemPersonal or itemCreative if needed

        val itemPersonal = findViewById<RelativeLayout>(R.id.itemPersonal)
        itemPersonal.setOnClickListener {
            startActivity(Intent(this, personal::class.java))
        }

        val itemCreative = findViewById<RelativeLayout>(R.id.itemCreative)
        itemCreative.setOnClickListener {
            startActivity(Intent(this, Creativeideas::class.java))
        }

        // "Add new entry" button
        val addEntryButton = findViewById<Button>(R.id.gridButton1)
        addEntryButton.setOnClickListener {
            startActivity(Intent(this, addentry::class.java))
        }

        val gridButton3 = findViewById<Button>(R.id.gridButton3)
        gridButton3.setOnClickListener {
            startActivity(Intent(this, changepassword::class.java))
        }

        val gridButton4 = findViewById<Button>(R.id.gridButton4)
        gridButton4.setOnClickListener {
            startActivity(Intent(this, Settings2::class.java))
        }

        val menuIcon: ImageView = findViewById(R.id.menuIcon)

        menuIcon.setOnClickListener {
            val popupView = LayoutInflater.from(this).inflate(R.layout.custom_popup_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            popupWindow.isOutsideTouchable = true
            popupWindow.showAsDropDown(menuIcon, -50, 20)

            val darkModeSwitch = popupView.findViewById<SwitchCompat>(R.id.night_mode_switch)
            val voiceLockSwitch = popupView.findViewById<SwitchCompat>(R.id.voice_lock_switch)
            val logoutText = popupView.findViewById<TextView>(R.id.logoutText)

            darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Handle dark mode on/off
            }

            voiceLockSwitch.setOnCheckedChangeListener { _, isChecked ->
                // Handle voice lock on/off
            }

            logoutText.setOnClickListener {
                // Handle logout
                popupWindow.dismiss()
            }
        }


    }
}
