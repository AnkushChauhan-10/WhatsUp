package com.example.whatsup.mainscreen.mainfragments.callfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentCallsLogBinding
import com.example.whatsup.databinding.FragmentChatBinding

class CallsLogFragment : Fragment() {

    private lateinit var binding: FragmentCallsLogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentCallsLogBinding.inflate(inflater)
        return binding.root
    }
}