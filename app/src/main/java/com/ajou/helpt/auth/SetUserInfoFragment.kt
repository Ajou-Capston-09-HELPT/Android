package com.ajou.helpt.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentSetUserInfoBinding
import com.ajou.helpt.home.HomeActivity
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SetUserInfoFragment : Fragment() {
    private var _binding : FragmentSetUserInfoBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context ?= null
    private val dataStore = UserDataStore()
    private var accessToken : String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch(exceptionHandler){
            accessToken = dataStore.getAccessToken()
            Log.d("accessToken",accessToken.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSetUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var sex = ""
        binding.confirmBtn.setOnClickListener {
            Log.d("사용자 데이터","이름 ${binding.name.text} 키 ${binding.height.text} 체중 ${binding.weight.text} 성별 $sex 나이 ${binding.age.text}")
            if (binding.name.text != null && binding.height.text != null && binding.weight.text != null && binding.age.text != null && sex != "") {
                Log.d("사용자 데이터 모두 입력됨","이름 ${binding.name.text} 키 ${binding.height.text} 체중 ${binding.weight.text} 성별 $sex 나이 ${binding.age.text}")
                val intent = Intent(mContext, HomeActivity::class.java)
                startActivity(intent)
            }else{
                Toast.makeText(mContext,"모든 정보를 입력해주세요.",Toast.LENGTH_SHORT).show()
            }
        }
        binding.manBtn.setOnClickListener {
            sex = "남성"
        }
        binding.womanBtn.setOnClickListener {
            sex = "여성"
        }

    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("exceptionHandler",exception.message.toString())
        showErrorDialog()
    }
    private fun showErrorDialog() {
        requireActivity().runOnUiThread{
            AlertDialog.Builder(mContext!!).apply {
                setTitle("Error")
                setMessage("Network request failed.")
                setPositiveButton("뒤로 가기") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                setCancelable(false)
                show()
            }
        }
    } // TODO 구체적인 오류 메세지로 변경하기
}