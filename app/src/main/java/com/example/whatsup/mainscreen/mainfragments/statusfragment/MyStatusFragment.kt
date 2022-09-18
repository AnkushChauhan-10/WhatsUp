package com.example.whatsup.mainscreen.mainfragments.statusfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.adapter.MyStatusAdapter
import com.example.whatsup.databinding.ActivitySetStatusBinding
import com.example.whatsup.databinding.FragmentMyStatusBinding
import com.example.whatsup.viewmodel.StatusViewModel
import com.example.whatsup.viewmodel.StatusViewModelFactory

class MyStatusFragment : Fragment() {

    private lateinit var binding: FragmentMyStatusBinding
    private val args : MyStatusFragmentArgs by navArgs()

    private val viewModel : StatusViewModel by activityViewModels {
        StatusViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).statusDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyStatusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus(args.phone)
        binding.myStatusBack.setOnClickListener {
            findNavController().popBackStack()
        }
        setRec()
    }

    private fun setRec() {
        binding.myStatusRec.layoutManager = LinearLayoutManager(requireContext())
        var adapter = MyStatusAdapter(requireContext())
        binding.myStatusRec.adapter = adapter
        viewModel.status.observe(viewLifecycleOwner){
            adapter.update(it)
        }
        adapter.setOnclick(object : MyStatusAdapter.ListAdapterListener{
            override fun onClickStatus(position: Int) {
              findNavController().navigate(MyStatusFragmentDirections.actionMyStatusFragmentToShowStatusFragment(
                  "My Status",args.phone,args.phone,args.dp,position
              ))
            }
        })
    }

}