package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentDefaultTrainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions


class DefaultTrainFragment : Fragment() {
    private var _binding : FragmentDefaultTrainBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private lateinit var viewModel: TrainInfoViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDefaultTrainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gifDrawable =
            pl.droidsonroids.gif.GifDrawable(mContext!!.resources, R.drawable.train_qr_guide)
        binding.qrGif.setImageDrawable(gifDrawable)

        binding.qrGif.setOnClickListener {
            findNavController().navigate(R.id.action_defaultTrainFragment_to_readyTrainFragment)
        }
        binding.qrCode.setOnClickListener {
            qrScan()
        }

        viewModel.train.observe(viewLifecycleOwner, Observer {
            if(viewModel.train.value != null){
                findNavController().navigate(R.id.action_defaultTrainFragment_to_readyTrainFragment)
            }
        })
    }

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Log.d("contents", result.contents)
        } else {
            viewModel.setTrain(result.contents)
            Log.d("contents",result.contents)
        }
    }

    private fun qrScan() {
        barcodeLauncher.launch(ScanOptions())
    }

}