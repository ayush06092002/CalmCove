package com.who.calmcove

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.auth.User
data class UserData(
    val name: String = "",
    val email: String = "",
    val dob: String = "",
    val mobileNumber: String = ""
)
class SignUp : AppCompatActivity() {
    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextConfirmPassword: EditText
    private lateinit var editTextDOB: EditText
    private lateinit var editTextMobileNumber: EditText
    private lateinit var buttonSignUp: Button

    private lateinit var database : FirebaseDatabase
    private lateinit var userReference: DatabaseReference
    private lateinit var auth : FirebaseAuth

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        editTextName = findViewById(R.id.supname)
        editTextEmail = findViewById(R.id.supmail)
        editTextPassword = findViewById(R.id.suppass)
        editTextConfirmPassword = findViewById(R.id.supcpass)
        editTextDOB = findViewById(R.id.supdob)
        editTextMobileNumber = findViewById(R.id.supnumber)
        buttonSignUp = findViewById(R.id.supbutton)

        database = FirebaseDatabase.getInstance()
        userReference = database.getReference("users")
        auth = FirebaseAuth.getInstance()

        buttonSignUp.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            val confirmPassword = editTextConfirmPassword.text.toString()
            val dob = editTextDOB.text.toString()
            val mobileNumber = editTextMobileNumber.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || dob.isEmpty() || mobileNumber.isEmpty()
            ) {
                showToast("Please fill in all the fields.")
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Please enter a valid email address.")
            } else if (password.length < 8 || !password.contains(Regex("[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]"))) {
                showToast("Password must be at least 8 characters long and contain a special character.")
            } else if (password != confirmPassword) {
                showToast("Passwords do not match.")
            } else {
                // Save user data to Firebase Database
                signUpWithEmailAndPassword(name, email, password, dob, mobileNumber)

            }
        }

    }

    @SuppressLint("RestrictedApi")
    private fun signUpWithEmailAndPassword(name: String, email: String, password: String, dob: String, mobileNumber: String) {
        // Create user with email and password using Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    val userId = firebaseUser?.uid ?: ""

                    // Save additional user data to Firebase Database
                    val user = UserData(name, email, dob, mobileNumber)
                    userReference.child(userId).setValue(user)

                    showToast("Sign-up successful!") // You can also redirect to a new activity or perform other actions.
                } else {
                    showToast("Sign-up failed: ${task.exception?.message}")
                }
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}