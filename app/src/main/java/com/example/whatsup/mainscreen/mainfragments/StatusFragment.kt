package com.example.whatsup.mainscreen.mainfragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.adapter.StatusAdapter
import com.example.whatsup.databinding.FragmentStatusBinding
import com.example.whatsup.mainscreen.activity.SetStatusActivity
import com.example.whatsup.viewmodel.StatusViewModel
import com.example.whatsup.viewmodel.StatusViewModelFactory

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding

    var status: Boolean = false

    private val viewModel : StatusViewModel by activityViewModels{
        StatusViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).statusDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding = FragmentStatusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressCircular.visibility = View.GONE
        viewModel.getUserByPhone()
        viewModel.currentUser.observe(viewLifecycleOwner){
            viewModel.getStatus(it.phoneNo.toString())
            viewModel.getStatusFlow(it.phoneNo.toString())
            it.profilepic?.let { it1 -> Log.i("StatusUser", it1.toString()) }
            Glide.with(requireContext()).load(it.profilepic.toString()).circleCrop().into(binding.statusProfile)
        }
        viewModel.contact.observe(viewLifecycleOwner){
            setStatusRec()
        }

        binding.setStatusLayout.setOnClickListener {
            result()
        }
        viewModel.statusBar.observe(viewLifecycleOwner){
            if(it){
                binding.progressCircular.visibility = View.GONE
                binding.sending.text = "Send"
            }
        }
        viewModel.status.observe(viewLifecycleOwner){
           // binding.addButtonImg.visibility = View.GONE
            try{
             //   binding.sending.text = it[it.size-1]!!.timeSpan.toString()
              //  Glide.with(requireContext()).load(it[it.size-1]!!.uri.toString()).circleCrop().into(binding.statusProfile)
            }catch (e:Exception){}
        }
    }

    private fun result() {
        startActivityForResult(Intent(requireActivity(),SetStatusActivity::class.java),100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.i("UsersStatus", "Start")
        if(resultCode == 100){
            var extras = data?.extras
            Log.i("UsersStatus",extras?.getString("text").toString())
            Log.i("UsersStatus",extras?.getString("uri").toString())
           uploadStatus(extras?.getString("uri").toString(),extras?.getString("text").toString())
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setStatusRec() {
        val rec = binding.statusRec
        rec.layoutManager = LinearLayoutManager(requireContext())
        var adapter = StatusAdapter(requireContext(),viewModel.contact.value as List)
        rec.adapter = adapter
        viewModel.userPhone.observe(viewLifecycleOwner){
            adapter.upDate(it)
        }
        adapter.setOnclick(object : StatusAdapter.ListAdapterListener{
            override fun onClickStatus(phone: String?, name: String?) {
                try {
                    findNavController().navigate(
                        DashBordFragmentDirections.actionDashBordFragmentToShowStatusFragment(
                            name.toString(),
                            viewModel.currentUser.value?.phoneNo.toString(),
                            phone.toString()
                        )
                    )
                }catch (e:Exception){}
            }

        })
    }





    private fun uploadStatus(uri:String,text:String) {
        viewModel.upLoadStatus(uri,text)
        binding.addButtonImg.visibility = View.GONE
        binding.sending.text = "Sending..."
        binding.statusProfile.background =  requireContext().resources.getDrawable(R.drawable.upload_file_circle)
        binding.progressCircular.visibility = View.VISIBLE
    }
}