package com.example.whatsup.mainscreen.mainfragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.adapter.ChatsAdapter
import com.example.whatsup.databinding.FragmentChatBinding
import com.example.whatsup.model.UserData
import com.example.whatsup.viewmodel.FirebaseDBViewModel
import com.example.whatsup.viewmodel.FirebaseDBViewModelFactory

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    lateinit var adapter: ChatsAdapter

    private val viewModel: FirebaseDBViewModel by activityViewModels{
        FirebaseDBViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).DBRepo
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        var sharedPreferences = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)
        var phone = sharedPreferences.getString("phone","1")
        viewModel.getChats(phone.toString())

        var rec = binding.chatRecycleView
        rec.layoutManager = LinearLayoutManager(requireContext())

        adapter = ChatsAdapter(requireContext())
        rec.adapter = adapter


        setOnClickChat()
        setViewModelObserver()



        binding.contact.setOnClickListener{
            findNavController().navigate(DashBordFragmentDirections.actionDashBordFragmentToContactListFragment())
        }

    }

    private fun setViewModelObserver() {
        viewModel.chats.observe(viewLifecycleOwner){
            adapter.upDate(it)
            Log.i("ViewModelChats",it.toString())
        }
    }


    private fun setOnClickChat(){
        adapter.setOnclick(object : ChatsAdapter.ListAdapterListener{
            override fun onClickChat(chat: String,phone: String) {
                findNavController().navigate(DashBordFragmentDirections.actionDashBordFragmentToOpenChatFragment2(chat,phone))
            }
            override fun onClickDp(name: String?, uri: String?) {
                findNavController().navigate(DashBordFragmentDirections.actionDashBordFragmentToFullImageFragment(
                    name.toString(),uri.toString()))
            }
        })
    }

}