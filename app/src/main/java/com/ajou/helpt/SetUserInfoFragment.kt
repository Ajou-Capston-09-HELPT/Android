package com.ajou.helpt

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.databinding.FragmentSetUserInfoBinding
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