package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentReadyTrainBinding
import com.ajou.helpt.home.model.GymEquipment
import com.ajou.helpt.train.TrainInfoViewModel
import com.bumptech.glide.Glide

class ReadyTrainFragment : Fragment() {
    private var _binding: FragmentReadyTrainBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TrainInfoViewModel
    private var mContext: Context? = null
    private lateinit var dialog: TrainSettingDialog
    private lateinit var callback: OnBackPressedCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.setTrain(null)
                findNavController().popBackStack()
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
        _binding = FragmentReadyTrainBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var setting = listOf<Int>(
            viewModel.train.value!!.customSet,
            viewModel.train.value!!.customWeight,
            viewModel.train.value!!.customCount
        ) // 운동 초기값 설정

        binding.set.text =
            String.format(resources.getString(R.string.train_setting_set), setting[0])
        binding.count.text =
            String.format(resources.getString(R.string.train_setting_count), setting[2])
        binding.weight.text =
            String.format(resources.getString(R.string.train_setting_weight), setting[1])
        binding.name.text = viewModel.train.value!!.equipmentName

        binding.engName.text =
            viewModel.train.value!!.equipmentNameEng
        Glide.with(mContext!!)
            .load(viewModel.guide.value!!.topImage)
            .into(binding.image)
        binding.trainSetting.setOnClickListener {
            dialog = TrainSettingDialog(setting) { value ->
                setting = value
                binding.set.text = String.format(
                    resources.getString(R.string.train_setting_set),
                    value[0]
                )
                binding.weight.text = String.format(
                    resources.getString(R.string.train_setting_weight),
                    value[1]
                )
                binding.count.text = String.format(
                    resources.getString(R.string.train_setting_count),
                    value[2]
                )
                viewModel.setTrain(
                    GymEquipment(
                        viewModel.train.value!!.gymEquipmentId,
                        viewModel.train.value!!.equipmentName,
                        "one arm dumbbell lateral raise",
                        value[2],
                        value[0],
                        value[1]
                    )
                )
            }
            dialog.show(requireActivity().supportFragmentManager, "setting")
        }

        binding.qMark.setOnClickListener {
            findNavController().navigate(R.id.action_readyTrainFragment_to_trainGuideFragment)
        }
        binding.nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_readyTrainFragment_to_trainFragment)
        }
        binding.backBtn.setOnClickListener {
            viewModel.setTrain(null)
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::dialog.isInitialized) dialog.dismiss()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}