package com.ajou.helpt.train.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentTrainDoneBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.mypage.model.ExerciseRecord
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.ExercisePosting
import com.ajou.helpt.network.api.RecordService
import com.ajou.helpt.train.TrainInfoViewModel
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.time.LocalDate
import kotlin.reflect.typeOf

class TrainDoneFragment : Fragment() {
    private var _binding: FragmentTrainDoneBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var callback: OnBackPressedCallback
    private lateinit var viewModel: TrainInfoViewModel
    private val dataStore = UserDataStore()
    private var accessToken: String? = null
    private var comment: String? = null
    private val recordService = RetrofitInstance.getInstance().create(RecordService::class.java)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(R.id.action_trainDoneFragment_to_defaultTrainFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        _binding = FragmentTrainDoneBinding.inflate(layoutInflater, container, false)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken()
            val today = LocalDate.now()

            if (viewModel.rate.value!! < 50) {
                comment = "자세 정확도가 낮습니다. 자세 정확도에 유의해주세요"
            } else if(viewModel.rate.value!! > 85) {
                comment = "올바른 자세로 운동을 잘해내고 있습니다!"
            } else if(viewModel.direction.value == 'l'){
                comment = "자세가 왼쪽으로 치우쳐진 경향이 있습니다."
            } else if (viewModel.direction.value == 'r'){
                comment = "자세가 오른쪽으로 치우쳐진 경향이 있습니다."
            } else{
                comment = "한쪽으로 치우치지 않고 잘하고 있습니다."
            }

            val data = ExercisePosting(
                viewModel.train.value!!.gymEquipmentId,
                viewModel.doneCount.value!!,
                viewModel.doneSet.value!!,
                viewModel.train.value!!.customWeight,
                viewModel.time.value!!,
                viewModel.rate.value!!,
                comment!!,null
            )
            Log.d("postRecord request type check", viewModel.train.value!!.gymEquipmentId.toString())
            Log.d("postRecord request","data: $data")
            val postRecordDeferred = async { recordService.postRecord(accessToken!!,data) }
            val postRecordResponse = postRecordDeferred.await()

            if (postRecordResponse.isSuccessful) {
                Log.d("postRecordResponse","success")
            } else {
                Log.d("postRecordResponse fail",postRecordResponse.errorBody()?.string().toString())
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigate(R.id.action_trainDoneFragment_to_defaultTrainFragment)
        }
        val content = SpannableString(binding.exitBtn.text.toString())
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        binding.exitBtn.text = content

        binding.exitBtn.setOnClickListener {
            val intent = Intent(mContext, HomeActivity::class.java)
            startActivity(intent)
        }
        binding.name.text = viewModel.train.value!!.equipmentName
//        binding.engName.text = "one arm dumbbell lateral raise"
        binding.engName.text =
            viewModel.train.value!!.equipmentNameEng
        binding.result.text = mContext?.resources?.getString(
            R.string.train_done_result,
            viewModel.time.value,
            viewModel.doneSet.value,
            viewModel.doneCount.value
        )
        val stringRate = "${viewModel.rate.value}%"
        binding.rate.text = stringRate


    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }


}