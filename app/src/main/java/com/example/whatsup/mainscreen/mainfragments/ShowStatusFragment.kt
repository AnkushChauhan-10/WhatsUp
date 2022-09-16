package com.example.whatsup.mainscreen.mainfragments

import android.content.Context
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.annotation.Px
import androidx.core.view.marginBottom
import androidx.core.view.setMargins
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.R
import com.example.whatsup.databinding.FragmentShowStatusBinding
import com.example.whatsup.model.StatusModel
import com.example.whatsup.viewmodel.StatusViewModel
import com.example.whatsup.viewmodel.StatusViewModelFactory
import kotlinx.coroutines.*

class ShowStatusFragment : Fragment() {

    private lateinit var binding : FragmentShowStatusBinding
    private  var currentProgressBar =  MutableLiveData<Int>()
    var i=0

    private val args : ShowStatusFragmentArgs by navArgs()
    private val progressBars = ArrayList<ProgressBar>()
    private val statusList = ArrayList<StatusModel>()

    private val viewModel : StatusViewModel by activityViewModels{
        StatusViewModelFactory(
            (requireContext().applicationContext as FirebaseAppliction).statusDao
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowStatusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getStatus(args.phone)
        binding.showStatusName.text = args.name
        viewModel.status.observe(viewLifecycleOwner){
            Log.i("StatusCheck3",it.size.toString())
            progressBars.clear()
            binding.bar.removeAllViews()
            statusList.clear()
            statusList.addAll(it)
            addProgressBar(it.size)
        }
        currentProgressBar.value = 0
        currentProgressBar.observe(viewLifecycleOwner){
            if(it<statusList.size){
                setStatus()
            }else{
                viewModel.setStatusSeen(args.phone,args.user)
               findNavController().popBackStack()
            }
        }
    }

    private fun addProgressBar(count:Int) {
        for(i in 1..count){
            val progressBar = ProgressBar(requireContext(),null,android.R.attr.progressBarStyleHorizontal)
            progressBar.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val params = progressBar.layoutParams as LinearLayout.LayoutParams
            params.setMargins(9,8,9,8)
            params.weight = 1f
            progressBar.layoutParams = params
            progressBar.id = i
            progressBar.isIndeterminate = false
            progressBar.max=1000
            progressBar.progress = 0
            binding.bar.addView(progressBar)
         progressBars.add(progressBar)
        }
    }

    fun setStatus(){
        Log.i("setStatus","Start"+currentProgressBar.value)
        binding.statusText.text = statusList[currentProgressBar.value!!].text
        Glide.with(requireContext()).load(statusList[currentProgressBar.value!!].uri).fitCenter().centerCrop()
            .into(binding.showStatus)
        startProgressBar()
    }

    fun startProgressBar(){
        lifecycleScope.launch {
            run()
        }
    }

    suspend fun run(){
        for(j in 0..1000){
            delay(5)
            progressBars[currentProgressBar.value!!].progress = j
        }
        currentProgressBar.value = currentProgressBar.value!!+1
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("State","OnDestroyeView")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i("State","OnDestroye")
        viewModel.det()
    }
}