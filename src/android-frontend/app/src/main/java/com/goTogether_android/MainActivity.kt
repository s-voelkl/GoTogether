package com.goTogether_android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.goTogether_android.challenges.ChallengesFragment
import com.goTogether_android.home.HomeFragment
import com.goTogether_android.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * The main container activity that hosts the bottom navigation and fragment containers.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabs)

        bottomNav = findViewById(R.id.bottom_navigation)
        
        setupNavigation()

        // Set default fragment if this is a fresh start
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    private fun setupNavigation() {
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment().apply {
                    // Pass extras to HomeFragment (e.g., for focusing a specific challenge)
                    arguments = intent.extras
                }
                R.id.nav_challenges -> ChallengesFragment()
                R.id.nav_gamification -> GamificationFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Store the new intent so fragments can access updated extras
        setIntent(intent)
        
        // If a specific challenge needs to be focused, redirect to home
        if (intent.hasExtra("focusChallengeId")) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }
}
