package com.genai.app
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
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

class RegisterActivity : AppCompatActivity() {
    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_register)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val etEmail = findViewById<EditText>(R.id.etRegEmail)
        val etPassword = findViewById<EditText>(R.id.etRegPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnToLogin = findViewById<Button>(R.id.btnToLogin)

        btnRegister?.setOnClickListener {
            val email = etEmail?.text?.toString() ?: ""
            val password = etPassword?.text?.toString() ?: ""

            if (email.isNotEmpty() && password.isNotEmpty()) {
                register(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        btnToLogin?.setOnClickListener {
            finish()
        }
    }

    private fun register(email: String, password: String) {
        val progress = findViewById<CircularProgressIndicator>(R.id.registerProgress)
        progress?.visibility = View.VISIBLE
        
        // Path is passed as query param for the proxy
        val url = "${SupabaseClient.BASE_URL}?path=signup"
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
                        Toast.makeText(this@RegisterActivity, "Registration Successful. Please check your email.", Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        val error = JSONObject(responseBody ?: "{}").optString("msg", "Registration Failed")
                        Toast.makeText(this@RegisterActivity, error, Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progress?.visibility = View.GONE
                    Toast.makeText(this@RegisterActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}