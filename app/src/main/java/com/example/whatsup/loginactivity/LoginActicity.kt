package com.example.whatsup.loginactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.mainscreen.MainScreen
import com.example.whatsup.viewmodel.AuthViewModel

class LoginActicity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_acticity)
        supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.loginNavHost) as NavHostFragment
        navController = navHostFragment.navController
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}