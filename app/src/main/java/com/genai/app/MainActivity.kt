package com.genai.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genai.app.data.Message
import com.genai.app.data.OpenAIClient
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val client = OkHttpClient()
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    private lateinit var drawerLayout: DrawerLayout

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val spokenText = results?.get(0) ?: ""
            findViewById<EditText>(R.id.etMessage)?.setText(spokenText)
        }
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val fileUri = data?.data
            val fileName = fileUri?.path?.substringAfterLast('/') ?: "file"
            
            val userMsg = Message("Analyzing image: $fileName", true)
            messages.add(userMsg)
            adapter.notifyItemInserted(messages.size - 1)
            findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
            
            sendMessage("Saya telah mengunggah gambar $fileName. Tolong baca teks di dalamnya dan jelaskan isinya.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val mainPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
        val isLoggedIn = mainPrefs.getBoolean("is_logged_in", false)
        
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        try {
            setContentView(R.layout.activity_chat)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        if (toolbar != null) {
            try {
                setSupportActionBar(toolbar)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        navigationView?.setNavigationItemSelectedListener(this)
        
        val headerView = navigationView?.getHeaderView(0)
        val tvUserEmail = headerView?.findViewById<TextView>(R.id.tvUserEmail)
        
        val emailValue = mainPrefs.getString("user_email", "user@example.com")
        tvUserEmail?.text = emailValue

        if (toolbar != null) {
            val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()
        }

        val rvChat = findViewById<RecyclerView>(R.id.rvChat)
        val etMessage = findViewById<EditText>(R.id.etMessage)
        val btnSend = findViewById<FloatingActionButton>(R.id.btnSend)
        val btnVoice = findViewById<ImageView>(R.id.btnVoice)
        val btnUpload = findViewById<ImageView>(R.id.btnUpload)

        btnVoice?.setOnClickListener {
            startVoiceInput()
        }

        btnUpload?.setOnClickListener {
            startFilePicker()
        }

        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter
        
        val savedHistory = mainPrefs.getString("chat_history", null)
        if (savedHistory != null) {
            val jsonArray = JSONArray(savedHistory)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                messages.add(Message(
                    content = obj.getString("content"),
                    isUser = obj.getBoolean("isUser")
                ))
            }
            adapter.notifyDataSetChanged()
            rvChat.scrollToPosition(messages.size - 1)
        }

        btnSend.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.text.clear()
            }
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something...")
        try {
            speechLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        filePickerLauncher.launch(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val navPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
        when (item.itemId) {
            R.id.nav_new_chat -> {
                messages.clear()
                adapter.notifyDataSetChanged()
            }
            R.id.nav_history -> Toast.makeText(this, "History Coming Soon", Toast.LENGTH_SHORT).show()
            R.id.nav_pinned -> {
                val isPinned = navPrefs.getBoolean("is_pinned", false)
                navPrefs.edit().putBoolean("is_pinned", !isPinned).apply()
                val msg = if (!isPinned) "Chat pinned to favorites" else "Chat unpinned"
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
            R.id.nav_archive -> {
                val isArchived = navPrefs.getBoolean("is_archived", false)
                navPrefs.edit().putBoolean("is_archived", !isArchived).apply()
                if (!isArchived) {
                    messages.clear()
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Chat moved to Archive", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Chat restored from Archive", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
            R.id.nav_clear_chat -> {
                androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_GenAI_Dialog)
                    .setTitle("Clear Chat")
                    .setMessage("Are you sure you want to delete all messages?")
                    .setPositiveButton("Clear") { _, _ ->
                        messages.clear()
                        adapter.notifyDataSetChanged()
                        getSharedPreferences("genai_prefs", MODE_PRIVATE).edit().remove("chat_history").apply()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
            R.id.nav_about -> {
                androidx.appcompat.app.AlertDialog.Builder(this, R.style.Theme_GenAI_Dialog)
                    .setTitle("About GenAI Agent")
                    .setMessage("GenAI Agent v1.0\n\nYour advanced AI companion powered by OpenAI.")
                    .setPositiveButton("OK", null)
                    .show()
            }
            R.id.nav_logout -> {
                val logoutPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
                logoutPrefs.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (::drawerLayout.isInitialized && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun saveHistory() {
        val historyPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
        val jsonArray = JSONArray()
        messages.forEach { msg ->
            if (!msg.isThinking) {
                val obj = JSONObject().apply {
                    put("content", msg.content)
                    put("isUser", msg.isUser)
                }
                jsonArray.put(obj)
            }
        }
        historyPrefs.edit().putString("chat_history", jsonArray.toString()).apply()
    }

    private fun sendMessage(text: String) {
        val userMsg = Message(text, true)
        messages.add(userMsg)
        adapter.notifyItemInserted(messages.size - 1)
        
        val aiMsg = Message(isThinking = true, statusText = "Thinking...")
        messages.add(aiMsg)
        adapter.notifyItemInserted(messages.size - 1)
        findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
        
        val url = OpenAIClient.BASE_URL
        val json = JSONObject().apply {
            put("message", text)
            val persona = getSharedPreferences("genai_prefs", MODE_PRIVATE).getString("ai_persona", "Default")
            put("persona", persona)
            put("web_search", getSharedPreferences("genai_prefs", MODE_PRIVATE).getBoolean("web_search_enabled", false))
        }.toString()

        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                withContext(Dispatchers.Main) {
                    aiMsg.statusText = "Analyzing..."
                    adapter.notifyItemChanged(messages.size - 1)
                }

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val aiResponse = JSONObject(responseBody ?: "{}")
                            .getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        
                        aiMsg.isThinking = false
                        aiMsg.content = aiResponse
                        adapter.notifyItemChanged(messages.size - 1)
                        findViewById<RecyclerView>(R.id.rvChat).smoothScrollToPosition(messages.size - 1)
                        
                        saveHistory()
                    } else {
                        messages.removeAt(messages.size - 1)
                        adapter.notifyDataSetChanged()
                        Toast.makeText(this@MainActivity, "AI Error: ${response.code}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    messages.removeAt(messages.size - 1)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}