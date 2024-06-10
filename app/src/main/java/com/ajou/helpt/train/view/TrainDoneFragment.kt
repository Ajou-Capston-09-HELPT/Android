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
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.model.ExercisePosting
import com.ajou.helpt.network.api.RecordService
import com.ajou.helpt.train.TrainInfoViewModel
import kotlinx.coroutines.*
import java.time.LocalDate

class TrainDoneFragment : Fragment() {
    private var _binding: FragmentTrainDoneBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var callback: OnBackPressedCallback
    private lateinit var viewModel: TrainInfoViewModel
    private val dataStore = UserDataStore()
    private var accessToken: String? = null


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