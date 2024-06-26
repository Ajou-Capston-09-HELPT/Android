package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentTrainingImageBinding
import com.ajou.helpt.train.TrainInfoViewModel
import com.bumptech.glide.Glide

class TrainingImageFragment : Fragment() {
    private var _binding : FragmentTrainingImageBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private lateinit var viewModel : TrainInfoViewModel

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
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        _binding = FragmentTrainingImageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(viewModel.guide.value!!.topImage)
            .into(binding.image)
    }
}