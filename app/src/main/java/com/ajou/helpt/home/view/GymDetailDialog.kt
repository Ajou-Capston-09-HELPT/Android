package com.ajou.helpt.home.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.DialogGymDetailBinding
import com.ajou.helpt.databinding.DialogSelectBirthBinding
import com.ajou.helpt.databinding.DialogTrainSettingBinding
import com.ajou.helpt.getWindowSize
import com.ajou.helpt.home.model.Gym
import com.ajou.helpt.home.view.fragment.HomeFragmentDirections
import com.ajou.helpt.home.view.fragment.QRCreateDialogFragment
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.ProductService
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.List

class GymDetailDialog : DialogFragment() {
    private lateinit var binding: DialogGymDetailBinding
    private var mContext: Context? = null
    private val dataStore = UserDataStore()
    private var gymId : Int? = null
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private val productService = RetrofitInstance.getInstance().create(ProductService::class.java)
    private var gymDetailInfo : Gym? = null
    private lateinit var qrDialog : QRCreateDialogFragment

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NORMAL, R.style.CustomDialog) // 배경 transparent
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogGymDetailBinding.inflate(inflater, container, false)
        CoroutineScope(Dispatchers.IO).launch {
            gymId = dataStore.getGymId()
            val accessToken = dataStore.getAccessToken()
            val getOneGymDeferred = async { gymService.getOneGym(accessToken!!, gymId!!) }
            val getOnGymResponse = getOneGymDeferred.await()
            val equipDeferred = async { gymService.getGymEquips(accessToken!!, gymId!!) }
            val equipResponse = equipDeferred.await()
            val productDeferred = async { productService.getGymProducts(accessToken!!, gymId!!)}
            val productResponse = productDeferred.await()
            if (getOnGymResponse.isSuccessful && equipResponse.isSuccessful && productResponse.isSuccessful) {
                withContext(Dispatchers.Main){
                    val gymBody = getOnGymResponse.body()?.data
                    val equipBody = equipResponse.body()?.data
                    val productBody = productResponse.body()?.data
                    binding.detailBtn.text = String.format(getString(R.string.dialog_gym_name_detail), gymBody!!.gymName)
                    gymDetailInfo = Gym(gymBody, equipBody!!, productBody!!)
                    binding.detailBtn.visibility = View.VISIBLE
                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val point = getWindowSize(mContext!!)
        val deviceWidth = point.x
        params?.width = (deviceWidth * 0.9).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailBtn.setOnClickListener {
            dialog?.dismiss()
            val action = HomeFragmentDirections.actionHomeFragmentToGymDetailInfoFragment(gymDetailInfo!!)
            findNavController().navigate(action)
        }

        binding.enterBtn.setOnClickListener {
            qrDialog = QRCreateDialogFragment()
            qrDialog.show(requireActivity().supportFragmentManager, "QRCreateDialog")
            dialog?.dismiss()
        }
    }

    override fun onPause() {
        super.onPause()
        dialog?.dismiss()
    }
    override fun onDestroy() {
        super.onDestroy()
//        dialog?.dismiss()
//        if (::qrDialog.isInitialized) qrDialog.dismiss()
    }
}