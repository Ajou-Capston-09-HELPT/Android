package com.ajou.helpt.auth.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.ajou.helpt.databinding.ActivityAuthBinding

class AuthActivity : AppCompatActivity() {
    private var _binding : ActivityAuthBinding ?= null
    private val binding get() = _binding!!
    private val viewModel: UserInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}