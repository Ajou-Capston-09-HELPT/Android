package com.ajou.helpt.home.view.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentQRCreateDialogBinding
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.QrService
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.*
import org.json.JSONObject


class QRCreateDialogFragment : DialogFragment() {
    private var _binding : FragmentQRCreateDialogBinding?= null
    private val binding get() = _binding!!
    private val qrService = RetrofitInstance.getInstance().create(QrService::class.java)
    private val dataStore = UserDataStore()
    private lateinit var accessToken :String
    private var mContext : Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _binding = FragmentQRCreateDialogBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            val qrDeferred = async { qrService.getQr(accessToken) }
            val qrResponse = qrDeferred.await()
            if(qrResponse.isSuccessful){
                val qrBody = JSONObject(qrResponse.body()?.string())
                val qrToken = "Bearer " +qrBody.getJSONObject("data").getString("qrToken").toString()
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.encodeBitmap(qrToken, BarcodeFormat.QR_CODE, 400, 400)
                withContext(Dispatchers.Main){
                    binding.qrCode.setImageBitmap(bitmap)
                }
            }else{
                Log.d("qrResponse fail",qrResponse.errorBody()?.string().toString())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.exitBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        dialog?.dismiss()
    }
}