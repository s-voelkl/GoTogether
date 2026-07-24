package com.goTogether_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.goTogether_android.home.HomeFragment
import com.goTogether_android.challenges.ChallengesFragment
import com.goTogether_android.profile.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabs)

        bottomNav = findViewById(R.id.bottom_navigation)
        
        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment().apply { arguments = intent.extras }
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

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.hasExtra("focusChallengeId")) {
            bottomNav.selectedItemId = R.id.nav_home
        }
    }
}
