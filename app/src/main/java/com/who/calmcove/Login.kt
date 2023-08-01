package com.who.calmcove

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.who.calmcove.MainActivity
import com.who.calmcove.R
import com.who.calmcove.SignUp

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button
    private lateinit var buttonSignUpLogin: Button
    private lateinit var sharedPreferences: SharedPreferences
    // Firebase Authentication
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Get references to EditText fields and Buttons
        editTextEmail = findViewById(R.id.editTextEmailLogin)
        editTextPassword = findViewById(R.id.editTextPasswordLogin)
        buttonLogin = findViewById(R.id.buttonLogin)
        buttonSignUpLogin = findViewById(R.id.buttonSignUpLogin)

        // Handle Log In Button click
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Call the custom login function
            signInWithEmailAndPassword(email, password)
        }

        // Handle Sign Up Button click
        buttonSignUpLogin.setOnClickListener {
            // Open the Sign Up Activity
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        // Sign in with email and password using Firebase Authentication
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Log in successful!")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                    setLoginState(true)
                    startActivity(intent)
                } else {
                    showToast("Log in failed: ${task.exception?.message}")
                }
            }
    }
    private fun setLoginState(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("login_state", isLoggedIn)
        editor.apply()
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
