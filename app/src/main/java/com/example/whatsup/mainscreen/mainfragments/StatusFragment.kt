package com.example.whatsup.mainscreen.mainfragments

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.adapter.StatusAdapter
import com.example.whatsup.databinding.FragmentStatusBinding
import com.example.whatsup.viewmodel.StatusViewModel
import com.example.whatsup.viewmodel.StatusViewModelFactory
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_profile.*

class StatusFragment : Fragment() {

    private lateinit var binding: FragmentStatusBinding
    val RQ = 100
    private  var photoUri: Uri?=null
    private var photo: Bitmap?=null
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
            openGallery()
        }
        viewModel.statusBar.observe(viewLifecycleOwner){
            if(it){
                binding.progressCircular.visibility = View.GONE
                binding.sending.text = "Send"
            }
        }
        viewModel.status.observe(viewLifecycleOwner){
            binding.addButtonImg.visibility = View.GONE
            try{
                binding.sending.text = it[it.size-1]!!.timeSpan.toString()
                Glide.with(requireContext()).load(it[it.size-1]!!.uri.toString()).circleCrop().into(binding.statusProfile)
            }catch (e:Exception){}
        }
    }

    private fun setStatusRec() {
        val rec = binding.statusRec
        rec.layoutManager = LinearLayoutManager(requireContext())
        var adapter = StatusAdapter(requireContext(),viewModel.contact.value as List)
        rec.adapter = adapter
        viewModel.userPhone.observe(viewLifecycleOwner){
            Log.i("UsersStatus",it.toString())
            adapter.upDate(it)
        }
        adapter.setOnclick(object : StatusAdapter.ListAdapterListener{
            override fun onClickStatus(phone: String?, name: String?) {
                findNavController().navigate(DashBordFragmentDirections.actionDashBordFragmentToShowStatusFragment(name.toString()
                    ,viewModel.currentUser.value?.phoneNo.toString(),phone.toString()))
            }

        })
    }

    private fun openGallery(){
        if(ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }else{
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            startActivityForResult(gallery,RQ)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(grantResults.size > 0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            val gallery = Intent(Intent.ACTION_PICK)
            gallery.type = "image/*"
            requireActivity().startActivityForResult(gallery,RQ)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK && requestCode == RQ && data!=null){
            photoUri = data.data
            if(Build.VERSION.SDK_INT >= 20){
                val source = ImageDecoder.createSource(requireContext().contentResolver,photoUri!!)
                uploadStatus()
            }else{
                photo = MediaStore.Images.Media.getBitmap(requireContext().contentResolver,photoUri)
                uploadStatus()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadStatus() {
        viewModel.upLoadStatus(photoUri!!)
        binding.addButtonImg.visibility = View.GONE
        binding.sending.text = "Sending..."
        binding.statusProfile.background =  requireContext().resources.getDrawable(R.drawable.upload_file_circle)
        binding.progressCircular.visibility = View.VISIBLE
    }
}