package com.example.musichub.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.musichub.R
import com.example.musichub.databinding.ActivityMainBinding
import com.example.musichub.ui.player.MiniPlayerFragment
import com.example.musichub.ui.player.PlayerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val playerViewModel: PlayerViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Snackbar.make(
                binding.root,
                R.string.permission_granted,
                Snackbar.LENGTH_SHORT
            ).show()
        } else {
            Snackbar.make(
                binding.root,
                R.string.permission_denied,
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        // Set up bottom navigation
        binding.bottomNavView.setupWithNavController(navController)

        // Set up mini player if it's not already added
        if (savedInstanceState == null) {
            setupMiniPlayer()
        }

        // Request permissions
        requestPermissions()
    }

    private fun setupMiniPlayer() {
        // Check if the fragment is already added
        if (supportFragmentManager.findFragmentById(R.id.mini_player_container) == null) {
            supportFragmentManager.commit {
                replace(R.id.mini_player_container, MiniPlayerFragment())
            }
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf<String>()

        // Storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (permissions.isNotEmpty()) {
            requestPermissionLauncher.launch(permissions.toTypedArray())
        }
    }
} 