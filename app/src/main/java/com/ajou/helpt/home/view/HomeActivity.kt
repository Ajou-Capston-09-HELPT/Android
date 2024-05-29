package com.ajou.helpt.home.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.ajou.helpt.databinding.ActivityHomeBinding
import com.ajou.helpt.home.HomeInfoViewModel
import com.ajou.helpt.train.view.TrainActivity

class HomeActivity : AppCompatActivity() {
    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel : HomeInfoViewModel by viewModels()
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