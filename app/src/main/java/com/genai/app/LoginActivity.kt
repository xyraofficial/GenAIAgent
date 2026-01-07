package com.genai.app
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.genai.app.data.SupabaseClient
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_login)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnToRegister = findViewById<Button>(R.id.btnToRegister)

        btnLogin?.setOnClickListener {
            val email = etEmail?.text?.toString() ?: ""
            val password = etPassword?.text?.toString() ?: ""

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        btnToRegister?.setOnClickListener {
            try {
                startActivity(Intent(this, RegisterActivity::class.java))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun login(email: String, password: String) {
        val progress = findViewById<CircularProgressIndicator>(R.id.loginProgress)
        progress?.visibility = View.VISIBLE
        
        // Path is passed as query param untuk proxy
        val url = "${SupabaseClient.BASE_URL}?path=token&grant_type=password"
        val json = JSONObject().apply {
            put("email", email)
            put("password", password)
        }.toString()

        val body = json.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                
                withContext(Dispatchers.Main) {
                    progress?.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseJson = JSONObject(responseBody ?: "{}")
                        val userJson = responseJson.optJSONObject("user")
                        val userEmail = userJson?.optString("email") ?: ""
                        
                        val sharedPrefs = getSharedPreferences("genai_prefs", MODE_PRIVATE)
                        sharedPrefs.edit()
                            .putString("user_email", userEmail)
                            .putBoolean("is_logged_in", true)
                            .apply()

                        Toast.makeText(this@LoginActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        val error = JSONObject(responseBody ?: "{}").optString("error_description", "Login Failed")
                        Toast.makeText(this@LoginActivity, error, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress?.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}