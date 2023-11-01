package com.ryan.storyapp.ui.main.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ryan.storyapp.R
import com.ryan.storyapp.databinding.FragmentSettingsBinding
import com.ryan.storyapp.ui.auth.AuthorizationActivity
import com.ryan.storyapp.ui.createstory.CreateStoryActivity

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.settingLanguage.setOnClickListener {
            val localeIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(localeIntent)
        }

        binding.settingMode.setOnClickListener {
            // Belum di setting
        }

        binding.actionLogout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle(getString(R.string.con_logout))
            builder.setMessage(getString(R.string.logout_message))
            builder.setPositiveButton(getString(R.string.confirm)) { _, _ ->
                logoutAndNavigateToAuthorization()
            }
            builder.setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }

        return view
    }

    private fun navigateToCreateStory() {
        val intent = Intent(requireContext(), CreateStoryActivity::class.java)
        startActivity(intent)
    }

    private fun logoutAndNavigateToAuthorization() {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("API_KEY")
        editor.apply()
        navigateToAuthorization()
    }

    private fun navigateToAuthorization() {
        val intent = Intent(requireContext(), AuthorizationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}