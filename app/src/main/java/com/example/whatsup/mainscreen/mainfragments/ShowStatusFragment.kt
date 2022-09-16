package com.example.whatsup.mainscreen.mainfragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.whatsup.FirebaseAppliction
import com.example.whatsup.adapter.ShowStatusViewPagerAdapter
import com.example.whatsup.databinding.FragmentShowStatusBinding
import com.example.whatsup.model.StatusModel
import com.example.whatsup.viewmodel.StatusViewModel
import com.example.whatsup.viewmodel.StatusViewModelFactory
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ShowStatusFragment : Fragment() {

    private lateinit var binding : FragmentShowStatusBinding
    var i=0

    private lateinit var viewpager : ViewPager2
    private val args : ShowStatusFragmentArgs by navArgs()
    private val progressBars = ArrayList<ProgressBar>()
    private val statusList = ArrayList<StatusModel>()
    private lateinit var job : CoroutineContext

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
        viewModel.getStatus(args.user)
        binding.showStatusName.text = args.name
        viewModel.status.observe(viewLifecycleOwner){
            Log.i("StatusCheck3",it.size.toString())
            progressBars.clear()
            binding.bar.removeAllViews()
            statusList.clear()
            statusList.addAll(it)
            addProgressBar(it.size)
            setStatus()
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
        viewpager = binding.showStatusViewPager
        var adapter = ShowStatusViewPagerAdapter(requireContext())
        viewpager.adapter = adapter
        adapter.update(statusList)
        viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.i("setStatus","Start")
                try{
                    job.cancel()
                }catch (e:Exception){
                    Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT)
                }
                for(i in 0..progressBars.size-1){
                    if(i<position){
                        progressBars[i].progress = 1000
                    }else{
                        progressBars[i].progress = 0
                    }
                }
                try{
                startProgressBar(position)
                }catch (e:Exception){
                    Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_SHORT)
                }
            }
        })
    }

    fun startProgressBar(position:Int){
        job = lifecycleScope.launch {
            run(position)
        }
    }

    suspend fun run(position:Int){
        val ans = position+1
        for(j in 0..1000){
            delay(5)
            progressBars[position].progress = j
        }
        if(ans>=statusList.size){
           back()
        }
       viewpager.setCurrentItem(ans)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i("State","OnDestroyeView")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        Log.i("State","OnDestroye")
    }

    private fun back() {
        findNavController().popBackStack()
    }
}