package com.example.whatsup.model

data class UserData(val userName: String,
                    val phoneNo: String,
                    val profilepic: String,
                    val lastMeg: String,
                    val time: String? = "",
                    val count:Int?=0)
