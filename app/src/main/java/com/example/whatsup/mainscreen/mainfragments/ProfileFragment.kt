package com.example.whatsup.mainscreen.mainfragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentProfileBinding
import com.example.whatsup.viewmodel.FirebaseDBViewModel
import com.example.whatsup.viewmodel.FirebaseDBViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    val RQ =100
    private  var photoUri: Uri?=null
    private var photo: Bitmap?=null

    private val viewModel: FirebaseDBViewModel by activityViewModels {
        FirebaseDBViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).DBRepo
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com")
            .getReference("${viewModel.currentUserData.value?.phoneNo.toString()}/DP")
        storageReference.downloadUrl.addOnSuccessListener {
            try{
            Glide.with(requireContext())
                .load(it).circleCrop()
                .into(binding.imageProfile)
            Log.i("DownloadUri",it.toString())}catch (e:Exception){
                Log.i("DownloadUri",e.toString())
            }
        }
        binding.imageProfile.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val gallery =Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery,RQ)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.size > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            val gallery =Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            requireActivity().startActivityForResult(gallery,RQ)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == RQ && data!=null){
            photoUri = data.data
            if(Build.VERSION.SDK_INT >= 20){
                val source = ImageDecoder.createSource(requireContext().contentResolver,photoUri!!)
                viewModel.uploadPhoto(photoUri!!)
                Glide.with(requireContext())
                    .load(photoUri).circleCrop()
                    .into(binding.imageProfile)
            }else{
                photo = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,photoUri)
                viewModel.uploadPhoto(photoUri!!)
                Glide.with(requireContext())
                    .load(photoUri).circleCrop()
                    .into(binding.imageProfile)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}