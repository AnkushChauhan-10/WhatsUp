package com.example.whatsup.loginactivity.loginfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentLogin2Binding

class LoginFragment : Fragment() {

    lateinit var binding: FragmentLogin2Binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogin2Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.createAccountLogin.setOnClickListener {
           createAccount()
       }

        binding.haveAccountLogin.setOnClickListener {
            singIn()
        }

    }

    private fun singIn() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToSingInFragment2())
    }

    private fun createAccount() {
        findNavController().navigate(LoginFragmentDirections.actionLoginFragment2ToSingUpFragment2())
    }

}