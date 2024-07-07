package com.geralt.food

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.geralt.food.Model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private var name: String? = null
    private lateinit var dont: TextView
    private lateinit var loginButton: Button
    private lateinit var googleButton: Button
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth and Database Reference
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Views
        dont = findViewById(R.id.dont)
        loginButton = findViewById(R.id.login_btn)
        googleButton = findViewById(R.id.googleButton)
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)

        dont.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            // Get text from EditText fields
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        updateUi(user)
                        Toast.makeText(this, "Account signed in successfully", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    saveUserData()
                                    val user = auth.currentUser
                                    updateUi(user)
                                    Toast.makeText(
                                        this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Account creation failed: ${task.exception}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }
            }
        }

        googleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            updateUi(user)
                            Toast.makeText(
                                this,
                                "Account signed in successfully with Google",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Account sign-in failed: ${task.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Account sign-in failed: ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Account sign-in failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun updateUi(user: FirebaseUser?) {
        // Your update UI logic
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun saveUserData() {
        // Your save user data logic
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val user = UserModel(name, email, password)
        val userUid = auth.currentUser?.uid

        if (userUid != null) {
            // Save data into the database
            database.child("Users").child(userUid).setValue(user)
        } else {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
        }
    }
}
