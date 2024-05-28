package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentDefaultTrainBinding
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymEquipmentService
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class DefaultTrainFragment : Fragment() {
    private var _binding: FragmentDefaultTrainBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: TrainInfoViewModel
    private val dataStore = UserDataStore()
    private val gymEquipmentService =
        RetrofitInstance.getInstance().create(GymEquipmentService::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDefaultTrainBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gifDrawable =
            pl.droidsonroids.gif.GifDrawable(mContext!!.resources, R.drawable.train_qr_guide)
        binding.qrGif.setImageDrawable(gifDrawable)

        binding.qrCode.setOnClickListener {
            qrScan()
        }

        viewModel.train.observe(viewLifecycleOwner, Observer {
            if (viewModel.train.value != null) {
                Log.d("onBackpressed", "navigate")
                findNavController().navigate(R.id.action_defaultTrainFragment_to_readyTrainFragment)
            }
        })
    }

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {

        } else {
            Log.d("contents", result.contents)
            getTrainInfo(result.contents.toInt())
//            viewModel.setTrain(result.contents)
//            Log.d("contents",result.contents)
        }
    }

    private fun qrScan() {
        barcodeLauncher.launch(ScanOptions())
    }

    private fun getTrainInfo(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val accessToken = dataStore.getAccessToken()
            val getSelectedTrainDeferred =
                async { gymEquipmentService.getSelectedTrain(accessToken!!, id) }
            val getSelectedTrainResponse = getSelectedTrainDeferred.await()
            if (getSelectedTrainResponse.isSuccessful) {
                Log.d("getSelectedTrainResponse ", "")
                viewModel.setTrain(getSelectedTrainResponse.body()!!.data)
            } else {
                Log.d(
                    "getSelectedTrainResponse faill",
                    getSelectedTrainResponse.errorBody()?.string().toString()
                )
            }
        }
    }
}