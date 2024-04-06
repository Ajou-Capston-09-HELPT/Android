package com.ajou.helpt.train

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.helpt.R
import com.ajou.helpt.databinding.ActivityTrainBinding

class TrainActivity : AppCompatActivity() {

    private var _binding : ActivityTrainBinding ?= null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}