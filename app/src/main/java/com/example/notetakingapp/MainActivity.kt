package com.example.notetakingapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import com.example.notetakingapp.auth.WelcomeActivity
import com.example.notetakingapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        applySavedTheme()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleBottomNavigation()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }
    }

    private fun handleBottomNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_notes -> {
                    if (navController.currentDestination?.id != R.id.notesFragment) {
                        navController.navigate(R.id.notesFragment)
                    }
                    true
                }

                R.id.menu_settings -> {
                    if (navController.currentDestination?.id != R.id.settingsFragment) {
                        navController.navigate(R.id.settingsFragment)
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun applySavedTheme() {
        val prefs = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        when (prefs.getString("theme_mode", "system")) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
}