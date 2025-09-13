package com.example.notetakingapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notetakingapp.R
import com.example.notetakingapp.auth.WelcomeActivity
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
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
        binding.authEmail.text = FirebaseAuth.getInstance().currentUser?.email

        fetchNoteCounts()

        handleLogOut()

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

    private fun handleLogOut() {
        binding.logOutBtn.setOnClickListener {
            val dialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("Logout ?")
                .setMessage("You will have to log in again to see your notes!")
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Logout") { dialog, _ ->
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(requireActivity(), WelcomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                }
                .show()

            val positiveButton =
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.deep_blue)
            )
            positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            positiveButton.setPadding(40, 20, 40, 20)

            val negativeButton =
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setBackgroundColor(
                ContextCompat.getColor(requireContext(), R.color.sticky_gray)
            )
            negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            negativeButton.setPadding(40, 20, 40, 20)
        }
    }

    private fun changeTheme() {
        binding.darkCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            saveThemePreference("dark")
            highlightSelectedTheme("dark")
        }

        binding.lightCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            saveThemePreference("light")
            highlightSelectedTheme("light")
        }

        binding.sysCard.setOnClickListener {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            saveThemePreference("system")
            highlightSelectedTheme("system")
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

    private fun highlightSelectedTheme(mode: String?) {
        val stickyGray = ContextCompat.getColor(requireContext(), R.color.sticky_gray)
        val white = ContextCompat.getColor(requireContext(), R.color.white)

        when (mode) {
            "dark" -> {
                binding.darkCard.setBackgroundColor(white)
                binding.lightCard.setBackgroundColor(stickyGray)
                binding.sysCard.setBackgroundColor(stickyGray)
            }

            "light" -> {
                binding.lightCard.setBackgroundColor(white)
                binding.darkCard.setBackgroundColor(stickyGray)
                binding.sysCard.setBackgroundColor(stickyGray)
            }

            "system" -> {
                binding.sysCard.setBackgroundColor(white)
                binding.darkCard.setBackgroundColor(stickyGray)
                binding.lightCard.setBackgroundColor(stickyGray)
            }
        }
    }

    private fun fetchNoteCounts() {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            val totalNotes = repository.countAllNotes(
                userId = currentUserId
            )
            val starredNotes = repository.countStarredNotes(
                userId = currentUserId
            )

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
