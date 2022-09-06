package com.example.whatsup.mainscreen

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.whatsup.R
import com.example.whatsup.adapter.FragmentAdapter
import com.example.whatsup.databinding.ActivityMainScreenBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import java.util.jar.Manifest


class MainScreen : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)
        supportActionBar?.hide()

       val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_main_screen_container) as NavHostFragment
        navController = navHostFragment.navController
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}