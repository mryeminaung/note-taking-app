package com.example.notetakingapp.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.notetakingapp.MainActivity
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.signUpButton.setOnClickListener {
            val username = binding.userName.toString().trim()
            val email = binding.userEmail.text.toString().trim()
            val pwd = binding.password.text.toString().trim()
            val pwdConfirm = binding.passwordConfirm.text.toString().trim()

            if (email.isNotEmpty() && pwd.isNotEmpty() && pwdConfirm.isNotEmpty()) {
                if (pwd != pwdConfirm) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                binding.signUpButton.isEnabled = false
                binding.signUpButton.text = ""
                binding.signUpButton.icon = null
                binding.signUpProgress.visibility = View.VISIBLE

                auth.createUserWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener { task ->
                        binding.signUpButton.isEnabled = true
                        binding.signUpButton.text = "Sign Up"
                        binding.signUpProgress.visibility = View.GONE
                        binding.signUpButton.setIconResource(R.drawable.ic_login)

                        if (task.isSuccessful) {
                            Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "Error: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        binding.signinLink.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}