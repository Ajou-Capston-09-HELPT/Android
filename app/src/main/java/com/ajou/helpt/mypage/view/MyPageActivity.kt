package com.ajou.helpt.mypage.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajou.helpt.databinding.ActivityMyPageBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.train.view.TrainActivity

class MyPageActivity : AppCompatActivity() {
    private var _binding : ActivityMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyPageBinding.inflate(layoutInflater)
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
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

    }
}