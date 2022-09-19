package com.example.whatsup.mainscreen.mainfragments.chatfragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.adapter.ContactAdapter
import com.example.whatsup.databinding.FragmentContactListBinding
import com.example.whatsup.viewmodel.FirebaseDBViewModel
import com.example.whatsup.viewmodel.FirebaseDBViewModelFactory

class ContactListFragment : Fragment() {

    private lateinit var binding: FragmentContactListBinding
    var REQUEST_READ_CONTACTS = 79;
    lateinit var adapter: ContactAdapter
    private val viewModel: FirebaseDBViewModel by activityViewModels {
        FirebaseDBViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).DBRepo
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentContactListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var rec = binding.contactRecycleView
        rec.layoutManager = LinearLayoutManager(requireContext())

        adapter = ContactAdapter(requireContext())

        rec.adapter = adapter

        binding.contactBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            viewModel.getAllContactsList()
            Log.i("1per","permiss")
        }


        viewModel.usersPhone.observe(viewLifecycleOwner){
            it.let{
                adapter.update(it)
            }
        }

        adapter.setOnclick(object: ContactAdapter.ListAdapterListener{
            override fun onClickContact(chat: String?,phone: String?) {
                findNavController().navigate(ContactListFragmentDirections.actionContactListFragmentToOpenChatFragment2(
                    chat.toString(),
                    phone.toString()
                ))
            }
        })

    }
}