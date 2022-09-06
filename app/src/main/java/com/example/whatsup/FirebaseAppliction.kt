package com.example.whatsup

import android.app.Application
import com.example.whatsup.firebaserpository.AuthenticationRepository
import com.example.whatsup.firebaserpository.FirebaseDB

class FirebaseAppliction: Application() {

    val AuthRepo: AuthenticationRepository by lazy{ AuthenticationRepository(this) }

    val DBRepo: FirebaseDB by lazy{ FirebaseDB(this) }

}