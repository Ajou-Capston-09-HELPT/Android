package com.ajou.helpt.train.view

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentDefaultTrainBinding
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.ExerciseService
import com.ajou.helpt.network.api.GymEquipmentService
import com.ajou.helpt.train.TrainInfoViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DefaultTrainFragment : Fragment() {
    private var _binding: FragmentDefaultTrainBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: TrainInfoViewModel
    private val dataStore = UserDataStore()
    private val gymEquipmentService =
        RetrofitInstance.getInstance().create(GymEquipmentService::class.java)
    private val exerciseService = RetrofitInstance.getInstance().create(ExerciseService::class.java)

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

        viewModel.guide.observe(viewLifecycleOwner, Observer {
            if (viewModel.train.value != null && viewModel.guide.value != null) {
                findNavController().navigate(R.id.action_defaultTrainFragment_to_readyTrainFragment)
            }
        })
    }

    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {

        } else {
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
            val getTrainDetailDeferred = async { exerciseService.getExerciseInfo(accessToken!!, id) }
            val getTrainDetailResponse = getTrainDetailDeferred.await()

            if (getSelectedTrainResponse.isSuccessful && getTrainDetailResponse.isSuccessful) {
                viewModel.setTrain(getSelectedTrainResponse.body()!!.data)
                viewModel.setGuide(getTrainDetailResponse.body()!!.data)
//                        && getTrainDetailResponse.isSuccessful
            } else {
                requireActivity().runOnUiThread{
                    Toast.makeText(mContext!!,"AI 코칭 서비스 미지원 기구입니다",Toast.LENGTH_SHORT).show()
                }
                Log.d(
                    "getSelectedTrainResponse fail",
                    getSelectedTrainResponse.errorBody()?.string().toString()
                )
                Log.d("getTrainDetailResponse fail",getTrainDetailResponse.errorBody()?.string().toString())
            }
        }
    }

}