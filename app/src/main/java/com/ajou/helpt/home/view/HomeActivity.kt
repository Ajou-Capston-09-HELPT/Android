package com.ajou.helpt.home.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.ActivityHomeBinding
import com.ajou.helpt.home.HomeInfoViewModel
import com.ajou.helpt.mypage.view.MyPageActivity
import com.ajou.helpt.setOnSingleClickListener
import com.ajou.helpt.train.view.TrainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeInfoViewModel by viewModels()
    private val dataStore = UserDataStore()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var hasTicket = false
        CoroutineScope(Dispatchers.IO).launch {
            hasTicket = dataStore.getHasTicket()
        }
        binding.train.setOnSingleClickListener {
            if (hasTicket) {
                val intent = Intent(this, TrainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "회원권을 먼저 등록해주세요", Toast.LENGTH_SHORT).show()
            }
        }
        binding.trainTxt.setOnSingleClickListener {
            if (hasTicket) {
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

        viewModel.isError.observe(this, Observer {
            if (viewModel.isError.value == true) {
                showErrorDialog()
            }
        })
    }
    private fun showErrorDialog() {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("Network request failed.")
                setPositiveButton("종료") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                setCancelable(false)
                show()
            }
        }
    }
}