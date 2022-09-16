package com.example.whatsup.mainscreen.mainfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentSettingsBinding
import com.example.whatsup.viewmodel.FirebaseDBViewModel
import com.example.whatsup.viewmodel.FirebaseDBViewModelFactory
import com.google.firebase.storage.FirebaseStorage

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

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
        binding = FragmentSettingsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.profileLayoutClick.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToProfileFragment())
        }
        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com")
            .getReference("${viewModel.currentUserData.value?.phoneNo.toString()}/DP")
        storageReference.downloadUrl.addOnSuccessListener {
            try{Glide.with(requireContext())
                .load(it).circleCrop()
                .into(binding.settingDp)
            Log.i("DownloadUri",it.toString())}catch (e:Exception){
                Log.i("DownloadUri",e.toString())
            }
        }
        binding.settingName.text = viewModel.currentUserData.value?.userName.toString()
    }

}