package com.example.whatsup.loginactivity.loginfragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.MainActivity
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentSingInBinding
import com.example.whatsup.mainscreen.MainScreen
import com.example.whatsup.viewmodel.AuthViewModel
import com.example.whatsup.viewmodel.AuthViewModelFactory

class SingInFragment : Fragment() {

    lateinit var binding: FragmentSingInBinding
    var REQUEST_READ_CONTACTS = 79

    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).AuthRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentSingInBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textSingup.setOnClickListener {
            findNavController().navigate(SingInFragmentDirections.actionSingInFragmentToLoginFragment2())
        }

        binding.singInButton.setOnClickListener {
            viewModel.singIn(binding.singinEmail.text.toString(),
            binding.singinPassword.text.toString())
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            Log.i("1per","permiss")
        } else {
            requestPermission()
        }

        setViewModelObserver()

    }

    private fun setViewModelObserver() {
        viewModel.data.observe(viewLifecycleOwner){
            it.let {
                binding.textSingup.text = it.email
            }
        }

        viewModel.loggedIn.observe(viewLifecycleOwner){
            it.let {
                if(it){

                    var sharedPreferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    var editor = sharedPreferences.edit()
                    editor.putString("phone",binding.singInPhoneNo.text.toString())
                    editor.commit()

                    startActivity(Intent(requireContext(), MainScreen::class.java).
                    putExtra("phone",binding.singInPhoneNo.text.toString()))
                    activity?.finish()
                }
            }
        }

    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            )
        ) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            )
        ) {
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                if (grantResults.size > 0 && grantResults[0] === PackageManager.PERMISSION_GRANTED) {
                    Log.i("permission","Dede")
                } else {
                    Log.i("permission","Denide")
                    activity?.finish()
                }
                return
            }
        }
    }


}