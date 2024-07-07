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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {

    private lateinit var userName: EditText
    private lateinit var userEmail: EditText
    private lateinit var userPassword: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var already: TextView
    private lateinit var signUpBtn : Button
    private lateinit var googleBtn : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)

        database = Firebase.database.reference
        auth = Firebase.auth

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        already = findViewById(R.id.already)
        signUpBtn = findViewById(R.id.signup_btn)
        userName = findViewById(R.id.name)
        userEmail = findViewById(R.id.email)
        userPassword = findViewById(R.id.password)
        googleBtn = findViewById(R.id.google_btn2)

        already.setOnClickListener {

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        signUpBtn.setOnClickListener {

            // Get test from editText filled
            val name = userName.text.toString().trim()
            val email = userEmail.text.toString().trim()
            val password = userPassword.text.toString().trim()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveUserData(name, email, password)
                            updateUi()
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT)
                                .show()
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

        googleBtn.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            launcher.launch(signInIntent)
        }



    }

    private fun saveUserData(name: String, email: String, password: String) {
        val user = UserModel(name, email, password)
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Save data to Firebase database
            database.child("Users").child(userId).setValue(user)
        } else {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)

                if (task.isSuccessful) {
                    val account: GoogleSignInAccount? = task.result
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { tasek ->
                        if (tasek.isSuccessful) {
                            updateUi()
                            Toast.makeText(
                                this,
                                "Account sign-in successfully with Google",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Account creation failed : ${tasek.exception}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Account creation failed : ${task.exception}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            } else {
                Toast.makeText(
                    this,
                    "Account creation failed",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    private fun updateUi() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}