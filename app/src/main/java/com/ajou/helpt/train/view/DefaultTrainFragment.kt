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
        binding.backBtn.setOnClickListener {
//            downloadImgFromUrl("https://www.google.com/imgres?q=%ED%83%9C%EC%97%B0&imgurl=https%3A%2F%2Fcdn.slist.kr%2Fnews%2Fphoto%2F202107%2F265403_441389_657.jpg&imgrefurl=https%3A%2F%2Fwww.slist.kr%2Fnews%2FarticleView.html%3Fidxno%3D265403&docid=yQahqf2S7Ph4xM&tbnid=u0QAykxZ6lEchM&vet=12ahUKEwjP8t3r3byGAxV2ZPUHHRIxDXYQM3oECGMQAA..i&w=600&h=400&hcb=2&ved=2ahUKEwjP8t3r3byGAxV2ZPUHHRIxDXYQM3oECGMQAA")
        }

        viewModel.train.observe(viewLifecycleOwner, Observer {
            if (viewModel.train.value != null) {
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
            val getTrainDetailDeferred = async { exerciseService.getExerciseInfo(accessToken!!, 3) }
            val getTrainDetailResponse = getTrainDetailDeferred.await()
            if (getSelectedTrainResponse.isSuccessful) {
                viewModel.setTrain(getSelectedTrainResponse.body()!!.data)
                viewModel.setGuide(getTrainDetailResponse.body()!!.data)
//                        && getTrainDetailResponse.isSuccessful
            } else {
                Log.d(
                    "getSelectedTrainResponse fail",
                    getSelectedTrainResponse.errorBody()?.string().toString()
                )
                Log.d("getTrainDetailResponse fail",getTrainDetailResponse.errorBody()?.string().toString())
            }
        }
    }

//    private fun downloadImgFromUrl(url: String) {
//        Log.d("checkPermission","")
//        if (checkPermission()) {
//            val fileName =
//                "/${getString(R.string.app_name)}/${SimpleDateFormat("yyyyMMddHHmmss").format(Date())}.jpg" // 이미지 파일 명
//
//
//            val req = DownloadManager.Request(Uri.parse(url))
//
//            req.setTitle(fileName) // 제목
//                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) // 알림 설정
//                .setMimeType("image/*")
//                .setDestinationInExternalPublicDir(
//                    Environment.DIRECTORY_PICTURES,
//                    fileName
//                )
//
//            val manager = mContext!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
//
//            manager.enqueue(req)
//        } else requestPermission()
//    }
//
//    private fun checkPermission() =
//        (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.READ_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED)
//                &&
//                (ContextCompat.checkSelfPermission(mContext!!, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_GRANTED)
//
//    private fun requestPermission() {
//        Log.d("requestPermission","")
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//            ),
//            0
//        )
//    }
}