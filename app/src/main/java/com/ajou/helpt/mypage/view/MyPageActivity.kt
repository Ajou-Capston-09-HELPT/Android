package com.ajou.helpt.mypage.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.ActivityMyPageBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.setOnSingleClickListener
import com.ajou.helpt.train.view.TrainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyPageActivity : AppCompatActivity() {
    private var _binding : ActivityMyPageBinding? = null
    private val binding get() = _binding!!
    private val dataStore = UserDataStore()
    private var hasTicket : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMyPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            hasTicket = dataStore.getHasTicket()
        }

        binding.train.setOnSingleClickListener {
            if (hasTicket){
                val intent = Intent(this, TrainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "회원권을 먼저 등록해주세요", Toast.LENGTH_SHORT).show()
            }
        }
        binding.trainTxt.setOnSingleClickListener {
            if (hasTicket){
                val intent = Intent(this, TrainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "회원권을 먼저 등록해주세요", Toast.LENGTH_SHORT).show()
            }
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