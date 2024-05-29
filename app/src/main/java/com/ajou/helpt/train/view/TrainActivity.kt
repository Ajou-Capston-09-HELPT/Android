package com.ajou.helpt.train.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.helpt.databinding.ActivityTrainBinding
import com.ajou.helpt.home.view.HomeActivity

class TrainActivity : AppCompatActivity() {

    private var _binding : ActivityTrainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.train.setOnClickListener {
            val intent = Intent(this, TrainActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.my.setOnClickListener {

        }
    }
}