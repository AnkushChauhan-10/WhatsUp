package com.example.whatsup.mainscreen.mainfragments.chatfragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.adapter.MessageAdapter
import com.example.whatsup.databinding.FragmentOpenChatBinding
import com.example.whatsup.model.MessageModel
import com.example.whatsup.viewmodel.FirebaseDBViewModel
import com.example.whatsup.viewmodel.FirebaseDBViewModelFactory
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.*

class OpenChatFragment : Fragment() {

    val args: OpenChatFragmentArgs by navArgs()
    private lateinit var binding: FragmentOpenChatBinding
    lateinit var  rec : RecyclerView
    lateinit var phone : String


    private val viewModel: FirebaseDBViewModel by activityViewModels {
        FirebaseDBViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).DBRepo
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().navigate(OpenChatFragmentDirections.actionOpenChatFragment2ToDashBordFragment())
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
      binding = FragmentOpenChatBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter()
        binding.backArrow.setOnClickListener{
            findNavController().navigate(OpenChatFragmentDirections.actionOpenChatFragment2ToDashBordFragment())
        }

        binding.sendButton.setOnClickListener {
            sendMessage()
        }

        val storageReference = FirebaseStorage.getInstance("gs://whats-up-1e69b.appspot.com")
            .getReference("${args.chatePhone}/DP")
        storageReference.downloadUrl.addOnSuccessListener {
            try{
            Glide.with(requireContext())
                .load(it).circleCrop()
                .into(binding.openChatDp)
            Log.i("DownloadUri",it.toString())}
            catch (e:Exception){
                Log.i("OpenchatExp",e.toString())
            }
        }

    }

    private fun adapter(){

        rec = binding.messageRec
        rec.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getMessageFlow(args.chatePhone,viewModel.currentUserData.value?.phoneNo.toString())

        var adapter = MessageAdapter(requireContext(),viewModel.currentUserData.value?.phoneNo.toString())

        rec.adapter = adapter

        setToolBar()
        var j=0
        viewModel.m.observe(viewLifecycleOwner){
            adapter.update(it)
            rec.scrollToPosition(it.size-1)
            Log.i("DATASNAP j",j++.toString())
            viewModel.setMessageSeen(args.chatePhone,viewModel.currentUserData.value?.phoneNo.toString())
        }
    }

    private fun setToolBar() {
        binding.nameOpenChat.text = args.chateName
    }

    private fun sendMessage() {
        viewModel.sendMessage(MessageModel(viewModel.currentUserData.value?.userName.toString(),
            viewModel.currentUserData.value?.phoneNo.toString(),
            args.chateName,
            args.chatePhone,
            getCurrentDateTime(),
            binding.textBox.text.toString(),
        false,
        keyGen()))
        binding.textBox.text.clear()
    }

    fun getCurrentDateTime(): String{
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("hh:mm aaa")
        val dateTime = simpleDateFormat.format(calendar.time).toString()
        return dateTime
    }

    fun keyGen():String{
        val ts = System.currentTimeMillis()/1000
        Log.i("TimeStamp key",ts.toString())
        return ts.toString()
    }

}