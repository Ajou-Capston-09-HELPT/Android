package com.ajou.helpt.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.helpt.R
import com.ajou.helpt.databinding.ActivityHomeBinding
import com.ajou.helpt.train.TrainActivity

class HomeActivity : AppCompatActivity() {
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
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