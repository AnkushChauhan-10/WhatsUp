package com.example.whatsup.mainscreen.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.databinding.ActivitySetStatusBinding

class SetStatusActivity : AppCompatActivity() {
    val RQ = 100
    private  var photoUri: Uri?=null
    private var photo: Bitmap?=null
    private lateinit var binding: ActivitySetStatusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        openGallery()
        binding.sendButtonStatus.setOnClickListener {
            var intent = Intent()
            intent.putExtra("uri",photoUri.toString())
            intent.putExtra("text",binding.captionText.text.toString())
            setResult(100,intent)
            finish()
        }
        binding.cancelButton.setOnClickListener {
            var intent = Intent()
            setResult(0,intent)
            finish()
        }
    }

    private fun openGallery(){
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery,RQ)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.size > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            this.startActivityForResult(gallery,RQ)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == RQ && data!=null){
            photoUri = data.data
            if(Build.VERSION.SDK_INT >= 20){
                val source = ImageDecoder.createSource(this.contentResolver,photoUri!!)
                Glide.with(this).load(photoUri).into(binding.setStatusImage)
                if(photoUri == null){
                    finish()
                }
            }else{
                photo = MediaStore.Images.Media.getBitmap(this.contentResolver,photoUri)
                if(photoUri == null){
                    finish()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

}