package com.example.whatsup.mainscreen.mainfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentFullImageBinding
import com.example.whatsup.databinding.FragmentProfileBinding
import com.google.firebase.storage.FirebaseStorage

class FullImageFragment : Fragment() {

    private lateinit var binding: FragmentFullImageBinding
    private val args : FullImageFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFullImageBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.showImageName.text = args.name

        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com")
            .getReference("${args.uri}/DP")
        storageReference.downloadUrl.addOnSuccessListener {
            try{
                Glide.with(requireContext())
                    .load(it).fitCenter().centerCrop()
                    .into(binding.showImage)
                Log.i("DownloadUri",it.toString())}
            catch (e:Exception){
                Log.i("OpenchatExp",e.toString())
            }
        }

        binding.showImageBack.setOnClickListener {
            findNavController().navigate(FullImageFragmentDirections.actionFullImageFragmentToDashBordFragment())
        }

    }
}