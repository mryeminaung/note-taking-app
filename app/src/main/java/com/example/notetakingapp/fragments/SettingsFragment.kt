package com.example.notetakingapp.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notetakingapp.R
import com.example.notetakingapp.auth.LoginActivity
import com.example.notetakingapp.auth.WelcomeActivity
import com.example.notetakingapp.data.database.NoteDatabase
import com.example.notetakingapp.data.models.Note
import com.example.notetakingapp.data.repository.NotesRepository
import com.example.notetakingapp.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: NotesRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        val db = NoteDatabase.getDatabase(requireContext())
        repository = NotesRepository(db.noteDao())

        setAuthInfo()
        fetchNoteCounts()
        handleLogOut()
        changeTheme()

        binding.syncNotes.setOnClickListener { syncNotesToCloud() }

        binding.editProfileBtn.setOnClickListener {
            Log.d("Profile:", "Image Uploading...")
        }

        binding.usernameChangeBtn.setOnClickListener {
            val newUsername = binding.usernameEdit.text.toString().trim()
            changeUsername(newUsername)
        }

        binding.pwdChangeBtn.setOnClickListener {
            val currentPassword = binding.oldPasswordEdit.text.toString().trim()
            val newPassword = binding.newPasswordEdit.text.toString().trim()
            changePassword(currentPassword, newPassword)
        }

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

    private fun setAuthInfo() {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val username = document.getString("username") ?: "Anonymous"
                    val email = document.getString("email") ?: auth.currentUser?.email ?: ""

                    binding.authName.text = username
                    binding.authEmail.text = email
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to load user: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun changeUsername(newUsername: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId.isNullOrBlank()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (newUsername.isBlank()) {
            Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseFirestore.getInstance()

        lifecycleScope.launch {
            try {
                firestore.collection("users")
                    .document(userId)
                    .update("username", newUsername)
                    .await()

                Toast.makeText(
                    requireContext(),
                    "Username updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                binding.authName.text = newUsername
                binding.usernameEdit.text?.clear()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to update: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(
                requireContext(),
                "Password must be at least 6 characters",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }

        lifecycleScope.launch {
            try {
                val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
                user.reauthenticate(credential).await()

                user.updatePassword(newPassword).await()

                Toast.makeText(
                    requireContext(),
                    "Password updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                showReLoginDialog()

            } catch (e: FirebaseAuthRecentLoginRequiredException) {
                Toast.makeText(requireContext(), "Please login again and retry", Toast.LENGTH_LONG)
                    .show()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to update password: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showReLoginDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Password Updated")
            .setMessage("Your password has been updated successfully. Please log in again.")
            .setCancelable(false)
            .setPositiveButton("OK", null)
            .show()

        val positiveButton =
            dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
        positiveButton.setBackgroundColor(
            ContextCompat.getColor(requireContext(), R.color.deep_blue)
        )
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        positiveButton.setPadding(40, 20, 40, 20)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
            dialog.dismiss()
        }
    }

    private fun syncNotesToCloud() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(requireContext(), "You must be logged in to sync", Toast.LENGTH_SHORT)
                .show()
            return
        }

        lifecycleScope.launch {
            try {
                val noteDao = NoteDatabase.getDatabase(requireContext()).noteDao()
                val firestore = FirebaseFirestore.getInstance()

                // 1. Push unsynced notes
                val unsyncedNotes =
                    noteDao.getUnsyncedNotes(userId)
                for (note in unsyncedNotes) {
                    firestore.collection("users")
                        .document(userId)
                        .collection("notes")
                        .document(note.id)
                        .set(note)
                        .await()

                    noteDao.update(note.copy(isSynced = true))
                }

                // 2. Pull remote notes
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("notes")
                    .get()
                    .await()

                for (doc in snapshot.documents) {
                    val remoteNote = doc.toObject(Note::class.java) ?: continue
                    val localNote = noteDao.getNoteById(remoteNote.id)

                    if (localNote == null || remoteNote.updatedAt > localNote.updatedAt) {
                        noteDao.insert(remoteNote.copy(isSynced = true))
                    }
                }

                Toast.makeText(requireContext(), "Sync complete ✅", Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.e("Sync", "Error syncing notes", e)
                Toast.makeText(requireContext(), "Sync failed ❌", Toast.LENGTH_SHORT).show()
            }
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
