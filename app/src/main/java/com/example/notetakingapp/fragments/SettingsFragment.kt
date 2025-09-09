package com.example.notetakingapp.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: NotesRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        val db = NoteDatabase.getDatabase(requireContext())
        repository = NotesRepository(db.noteDao())

        fetchNoteCounts()

        binding.logOutBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_loginActivity)
        }

        changeTheme()

        binding.rowChangeUsername.setOnClickListener {
            toggleAccordion(
                binding.userNameCard, binding.userNameArrow,
                binding.pwdCard to binding.pwdArrow,
                binding.themeCard to binding.themeArrow
            )
        }

        binding.rowChangePassword.setOnClickListener {
            toggleAccordion(
                binding.pwdCard, binding.pwdArrow,
                binding.userNameCard to binding.userNameArrow,
                binding.themeCard to binding.themeArrow
            )
        }

        binding.rowChangeTheme.setOnClickListener {
            toggleAccordion(
                binding.themeCard, binding.themeArrow,
                binding.userNameCard to binding.userNameArrow,
                binding.pwdCard to binding.pwdArrow
            )
        }
    }

    private fun changeTheme() {
        binding.darkCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            saveThemePreference("dark")
        }

        binding.lightCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            saveThemePreference("light")
        }

        binding.sysCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            saveThemePreference("system")
        }

        loadThemePreference()
    }

    private fun saveThemePreference(mode: String) {
        val prefs = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        prefs.edit { putString("theme_mode", mode) }
    }

    private fun loadThemePreference() {
        val prefs = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        when (prefs.getString("theme_mode", "system")) {
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        highlightSelectedTheme(prefs.getString("theme_mode", "system"))
    }

    @SuppressLint("ResourceAsColor")
    private fun highlightSelectedTheme(mode: String?) {
        // Reset all cards to white
        binding.darkCard.setBackgroundColor(R.color.white)
        binding.lightCard.setBackgroundColor(R.color.white)
        binding.sysCard.setBackgroundColor(R.color.white)

        // Reset icons tint to transparent
        binding.iconMoon.backgroundTintList = ColorStateList.valueOf(
            android.R.color.transparent
        )
        binding.iconSun.backgroundTintList = ColorStateList.valueOf(
            android.R.color.transparent
        )
        binding.iconSys.backgroundTintList = ColorStateList.valueOf(
            android.R.color.transparent
        )

        when (mode) {
            "dark" -> {
                binding.darkCard.setBackgroundColor(
                    R.color.sticky_gray
                )
                binding.iconMoon.backgroundTintList = ColorStateList.valueOf(
                    R.color.white
                )
            }

            "light" -> {
                binding.lightCard.setBackgroundColor(
                    R.color.sticky_gray
                )
                binding.iconSun.backgroundTintList = ColorStateList.valueOf(
                    R.color.white
                )
            }

            "system" -> {
                binding.sysCard.setBackgroundColor(
                    R.color.sticky_gray
                )
                binding.iconSys.backgroundTintList = ColorStateList.valueOf(
                    R.color.white
                )
            }
        }
    }

    private fun fetchNoteCounts() {
        viewLifecycleOwner.lifecycleScope.launch {
            val totalNotes = repository.countAllNotes()
            val starredNotes = repository.countStarredNotes()

            binding.tvNotesCount.text = totalNotes.toString()
            binding.tvStarredCount.text = starredNotes.toString()
        }
    }

    private fun toggleAccordion(
        openCard: View,
        openArrow: ImageView,
        vararg others: Pair<View, ImageView>
    ) {
        val isOpen = openCard.isVisible
        openCard.visibility = if (isOpen) View.GONE else View.VISIBLE

        openArrow.animate().rotation(if (isOpen) 0f else 90f).setDuration(200).start()

        others.forEach { (card, arrow) ->
            card.visibility = View.GONE
            arrow.animate().rotation(0f).setDuration(200).start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
