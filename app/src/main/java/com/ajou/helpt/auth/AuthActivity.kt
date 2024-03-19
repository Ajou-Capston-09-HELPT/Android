package com.ajou.helpt.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.helpt.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private var _binding : ActivityAuthBinding ?= null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}