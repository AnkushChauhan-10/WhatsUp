package com.example.whatsup.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.whatsup.mainscreen.mainfragments.CallsLogFragment
import com.example.whatsup.mainscreen.mainfragments.ChatFragment
import com.example.whatsup.mainscreen.mainfragments.ContactListFragment
import com.example.whatsup.mainscreen.mainfragments.StatusFragment

class FragmentAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return ChatFragment()
            1-> return StatusFragment()
        }
        return CallsLogFragment()
    }

}