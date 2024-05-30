package com.ajou.helpt.auth.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentStartSetInfoBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StartSetInfoFragment : Fragment() {
    private var _binding : FragmentStartSetInfoBinding?= null
    private val binding get() = _binding!!
    private var mContext : Context ?= null
    private val dataStore = UserDataStore()
    private var accessToken : String? = null
    private var userName : String ?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch(exceptionHandler){
            accessToken = dataStore.getAccessToken()
            userName = dataStore.getUserName()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStartSetInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.name.text = String.format(mContext!!.resources.getString(R.string.auth_set_greet1,userName))

        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startSetInfoFragment_to_setSexInfoFragment)
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