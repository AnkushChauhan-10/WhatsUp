package com.example.whatsup.model

data class User(val userName: String,
                val mail: String,
                val password: String,
                val phoneNo: String,
                val DP:String?=null
                )
