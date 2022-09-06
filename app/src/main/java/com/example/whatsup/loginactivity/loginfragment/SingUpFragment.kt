package com.example.whatsup.loginactivity.loginfragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.MainActivity
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentSingInBinding
import com.example.whatsup.databinding.FragmentSingUpBinding
import com.example.whatsup.mainscreen.MainScreen
import com.example.whatsup.viewmodel.AuthViewModel
import com.example.whatsup.viewmodel.AuthViewModelFactory


class SingUpFragment : Fragment() {

    lateinit var binding: FragmentSingUpBinding
    lateinit var progress: ProgressDialog

    private val viewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).AuthRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSingUpBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        progress = ProgressDialog(requireContext())
        progress.setTitle("Creating Account")

        binding.textSingIn.setOnClickListener {
            findNavController().navigate(SingUpFragmentDirections.actionSingUpFragment2ToLoginFragment2())
        }

        viewModel.data.observe(viewLifecycleOwner){
            it.let {
                binding.textSingIn.text = it.email
            }
        }

        viewModel.loggedIn.observe(viewLifecycleOwner){
            it.let {
                if(it){

                    var sharedPreferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
                    var editor = sharedPreferences.edit()
                    editor.putString("phone",binding.phoneNo.text.toString())
                    editor.commit()

                    startActivity(Intent(requireContext(),MainScreen::class.java).
                    putExtra("phone",binding.phoneNo.text.toString()))
                    activity?.finish()
                }
            }
        }

       binding.singUpButton.setOnClickListener {
           viewModel.singUp(
               binding.name.text.toString(),
               binding.email.text.toString(),
               binding.password.text.toString(),
               binding.phoneNo.text.toString()
           )
       }
    }
}