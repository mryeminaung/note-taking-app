package com.example.notetakingapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.notetakingapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

//        NavigationUI.setupWithNavController(binding.bottomNav, navController)
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_notes -> {
                    val currentDest = navController.currentDestination?.id
                    if (currentDest != R.id.notesFragment) {
                        navController.navigate(R.id.action_settingsFragment_to_notesFragment)
                    }
                    true
                }

                R.id.menu_settings -> {
                    val currentDest = navController.currentDestination?.id
                    if (currentDest != R.id.settingsFragment) {
                        navController.navigate(R.id.action_notesFragment_to_settingsFragment)
                    }
                    true
                }

                else -> false
            }
        }
    }
}