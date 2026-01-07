package com.genai.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

import android.widget.LinearLayout
import android.widget.Toast
import android.view.View

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val prefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            val logoutPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
            logoutPrefs.edit().clear().apply()
            
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // AI Persona Selection
        findViewById<View>(R.id.llAIPersona)?.setOnClickListener {
            val personas = arrayOf("Default", "Friendly Assistant", "Professional Expert", "Coding Mentor")
            androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_GenAI_Dialog)
                .setTitle("Select AI Persona")
                .setItems(personas) { _, which ->
                    val selected = personas[which]
                    prefs.edit().putString("ai_persona", selected).apply()
                    findViewById<TextView>(R.id.tvCurrentPersona)?.text = "AI Persona: $selected"
                    Toast.makeText(this, "Persona set to $selected", Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        findViewById<TextView>(R.id.tvCurrentPersona)?.text = "AI Persona: ${prefs.getString("ai_persona", "Default")}"

        // Web Search Toggle
        val switchWebSearch = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.switchWebSearch)
        switchWebSearch?.isChecked = prefs.getBoolean("web_search_enabled", false)
        switchWebSearch?.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("web_search_enabled", isChecked).apply()
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(this, "Web Search $status", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.tvUserEmail)?.apply {
            val emailPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
            text = emailPrefs.getString("user_email", "user@example.com")
        }

        // Implementation for other settings
        findViewById<android.view.View>(R.id.llMainLanguage)?.setOnClickListener {
            val languages = arrayOf("English", "Indonesian", "Spanish", "French")
            androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_GenAI_Dialog)
                .setTitle("Select Language")
                .setItems(languages) { _, which ->
                    val selected = languages[which]
                    prefs.edit().putString("app_language", selected).apply()
                    android.widget.Toast.makeText(this, "Language set to $selected", android.widget.Toast.LENGTH_SHORT).show()
                }
                .show()
        }

        findViewById<android.view.View>(R.id.llHaptic)?.setOnClickListener {
            val isEnabled = prefs.getBoolean("haptic_enabled", true)
            prefs.edit().putBoolean("haptic_enabled", !isEnabled).apply()
            val status = if (!isEnabled) "Enabled" else "Disabled"
            android.widget.Toast.makeText(this, "Haptic Feedback $status", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<android.view.View>(R.id.llSubscription)?.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_GenAI_Dialog)
                .setTitle("Subscription")
                .setMessage("You are currently on the Free Plan. Upgrade to Pro for unlimited messages?")
                .setPositiveButton("Upgrade") { _, _ ->
                    android.widget.Toast.makeText(this, "Redirecting to payment...", android.widget.Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Later", null)
                .show()
        }
    }
}

// Add these IDs to activity_settings.xml or use findViewByParent logic if IDs are missing