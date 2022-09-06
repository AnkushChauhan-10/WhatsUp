package com.example.whatsup

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.whatsup.loginactivity.LoginActicity
import com.example.whatsup.mainscreen.MainScreen

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       sharedPreferences = getSharedPreferences("user", MODE_PRIVATE)
        var phone = sharedPreferences.getString("phone","1")

        if(phone=="1") {
            startActivity(Intent(this, LoginActicity::class.java));
            finish()
        }
        else{
            startActivity(Intent(this, MainScreen::class.java));
            finish()
        }

    }
}