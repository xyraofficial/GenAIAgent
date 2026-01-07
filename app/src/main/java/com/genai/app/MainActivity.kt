package com.genai.app
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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
    private val SPEECH_REQUEST_CODE = 100
    private val FILE_PICK_CODE = 101
    private val client = OkHttpClient()
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val prefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean("is_logged_in", false)
        
        if (!isLoggedIn) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        try {
            setContentView(R.layout.activity_chat)
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback or log if needed
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
        
        val prefs_ = getSharedPreferences("genai_prefs", MODE_PRIVATE)
        val userEmailStrVal = prefs_.getString("user_email", "user@example.com")
        tvUserEmail?.text = userEmailStrVal

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
            startActivityForResult(intent, SPEECH_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(this, "Voice input not supported", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, FILE_PICK_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                SPEECH_REQUEST_CODE -> {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenText = result?.get(0) ?: ""
                    findViewById<EditText>(R.id.etMessage)?.setText(spokenText)
                }
                FILE_PICK_CODE -> {
                    val fileUri = data.data
                    val fileName = fileUri?.path?.substringAfterLast('/') ?: "file"
                    
                    // Simulating file upload to chat
                    val userMsg = Message("Uploaded file: $fileName", true)
                    messages.add(userMsg)
                    adapter.notifyItemInserted(messages.size - 1)
                    findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
                    
                    // AI Response to file
                    sendMessage("Saya telah mengunggah file $fileName. Bisa tolong dianalisis?")
                }
            }
        }
    }

        adapter = ChatAdapter(messages)
        rvChat.layoutManager = LinearLayoutManager(this)
        rvChat.adapter = adapter

        btnSend.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isNotEmpty()) {
                sendMessage(text)
                etMessage.text.clear()
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_new_chat -> {
                messages.clear()
                adapter.notifyDataSetChanged()
            }
            R.id.nav_history -> Toast.makeText(this, "History Coming Soon", Toast.LENGTH_SHORT).show()
            R.id.nav_settings -> {
                startActivity(android.content.Intent(this, SettingsActivity::class.java))
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
        }.toString()

        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Update status animation
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
                        findViewById<RecyclerView>(R.id.rvChat).scrollToPosition(messages.size - 1)
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