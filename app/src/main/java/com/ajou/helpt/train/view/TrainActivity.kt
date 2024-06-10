package com.ajou.helpt.train.view

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.ajou.helpt.databinding.ActivityTrainBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.mypage.view.MyPageActivity
import com.ajou.helpt.setOnSingleClickListener

class TrainActivity : AppCompatActivity() {

    private var _binding : ActivityTrainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTrainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.train.setOnSingleClickListener {
            val intent = Intent(this, TrainActivity::class.java)
            startActivity(intent)
        }
        binding.trainTxt.setOnSingleClickListener {
            val intent = Intent(this,TrainActivity::class.java)
            startActivity(intent)
        }

        binding.home.setOnSingleClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.homeTxt.setOnSingleClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        binding.my.setOnSingleClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }

        binding.myTxt.setOnSingleClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

}