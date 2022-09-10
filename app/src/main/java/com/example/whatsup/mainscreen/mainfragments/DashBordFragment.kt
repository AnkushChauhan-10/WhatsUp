package com.example.whatsup.mainscreen.mainfragments

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.whatsup.R
import com.example.whatsup.adapter.FragmentAdapter
import com.example.whatsup.databinding.FragmentDashBordBinding
import com.example.whatsup.loginactivity.LoginActicity
import com.google.android.material.tabs.TabLayoutMediator

val list = arrayOf("chat","status","call")
class DashBordFragment : Fragment() {

    private lateinit var binding: FragmentDashBordBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashBordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager2 = binding.viewPager
        val tabLayout = binding.tabLayout

        val adapter = FragmentAdapter(requireActivity().supportFragmentManager,lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            tab.text = list[position]
        }.attach()

        binding.settingButton.setOnClickListener {
            menuShow()
        }
    }

    private fun menuShow() {
        val popupMenu = PopupMenu(requireContext(),binding.settingButton)
        popupMenu.inflate(R.menu.main_menu)
        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.setting_button->{
                    settings()
                    true
                }
                R.id.logOut->{
                    logOut()
                    true
                }
                else-> true
            }
        }
        popupMenu.show()
    }

    private fun settings() {
        findNavController().navigate(DashBordFragmentDirections.actionDashBordFragmentToSettingsFragment())
    }

    private fun logOut() {
        var sharedPreferences = requireContext().getSharedPreferences("user", AppCompatActivity.MODE_PRIVATE)
        var editor = sharedPreferences.edit()
        editor.remove("phone").apply()
        editor.commit()
        startActivity(Intent(requireActivity().baseContext,LoginActicity::class.java))
        requireActivity().finish()
    }
}