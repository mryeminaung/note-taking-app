package com.example.notetakingapp.fragments

import android.os.Bundle
import android.view.View
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}