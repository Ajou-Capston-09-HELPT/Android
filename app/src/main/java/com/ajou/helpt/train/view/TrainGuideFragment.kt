package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentTrainGuideBinding
import com.ajou.helpt.train.TrainInfoViewModel
import com.bumptech.glide.Glide

class TrainGuideFragment : Fragment() {
    private var _binding: FragmentTrainGuideBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
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
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        _binding = FragmentTrainGuideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = viewModel.guide.value!!

        val result = viewModel.guide.value!!.exerciseMethod
            .mapIndexed { index, item -> "${index + 1}. $item" }
            .joinToString(separator = "\n")
        binding.name.text = viewModel.train.value!!.equipmentName
        binding.engName.text = viewModel.train.value!!.equipmentNameEng
//        binding.engName.text = "one arm dumbbell lateral raise"
        binding.readyGuide.text = item.exerciseDescription
        binding.guide.text = result
        Glide.with(this)
            .load(item.topImage)
            .into(binding.image)
    }
}