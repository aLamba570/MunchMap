package com.geralt.food

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class StartActivity : AppCompatActivity() {
    private lateinit var auth : FirebaseAuth
    private lateinit var nextBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_start)
        auth = FirebaseAuth.getInstance()
        nextBtn = findViewById(R.id.next_btn)
        nextBtn.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser
        if (user != null){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }
}