package com.example.whatsup.model

import com.google.firebase.database.DataSnapshot

data class StatusDetails(val phone:String?=""
                         ,val timeSpan:String?=""
                         ,val uri: String?=""
                         ,val name:String?=""
                         ,val seen:Boolean?=false)