package com.example.notetakingapp.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.notetakingapp.MainActivity
import com.example.notetakingapp.databinding.ActivityWelcomeBinding
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.signupBtn.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // prevent back navigation to WelcomeActivity
        }
    }
}