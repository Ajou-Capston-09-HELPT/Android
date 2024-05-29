package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentTrainGuideBinding
import com.bumptech.glide.Glide

class TrainGuideFragment : Fragment() {
    private var _binding : FragmentTrainGuideBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

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
        _binding = FragmentTrainGuideBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(this)
            .load(R.drawable.band_bent_over_row_img)
            .into(binding.image)
        binding.guide.text = Html.fromHtml(resources.getString(R.string.train_guide_band_bent_over_row))
    }
}