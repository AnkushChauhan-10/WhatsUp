package com.example.whatsup.model

data class MessageModel(
    val senderName:String?="",
    val senderPhone:String?="",
    val receiverName:String?="",
    val receiverPhone: String?="",
    val time: String?="",
    val msg: String?="",
    val seen:Boolean?=false,
    val key: String?=" ")
