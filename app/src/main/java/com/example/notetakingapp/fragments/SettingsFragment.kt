package com.example.notetakingapp.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.notetakingapp.R
import com.example.notetakingapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        binding.logOutBtn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_loginActivity)
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

    private fun toggleAccordion(
        openCard: View,
        openArrow: ImageView,
        vararg others: Pair<View, ImageView>
    ) {
        val isOpen = openCard.isVisible
        openCard.visibility = if (isOpen) View.GONE else View.VISIBLE

        // Animate the arrow rotation
        openArrow.animate().rotation(if (isOpen) 0f else 90f).setDuration(200).start()

        // Close all other cards and reset their arrows
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
