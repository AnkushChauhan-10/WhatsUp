package com.example.whatsup.mainscreen.mainfragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentStatusBinding

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentStatusBinding.inflate(inflater)
        return binding.root
    }
}