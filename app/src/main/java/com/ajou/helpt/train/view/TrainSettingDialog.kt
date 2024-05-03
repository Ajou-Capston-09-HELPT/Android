package com.ajou.helpt.train.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.ajou.helpt.R
import com.ajou.helpt.databinding.DialogSelectBirthBinding
import com.ajou.helpt.databinding.DialogTrainSettingBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.List

class TrainSettingDialog(val setting: List<Int>, private val callback: (kotlin.collections.List<Int>) -> Unit) : DialogFragment() {
    private lateinit var binding: DialogTrainSettingBinding
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialog) // 배경 transparent
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogTrainSettingBinding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.set.minValue = 1
        binding.set.maxValue = 2010
        binding.set.value = setting[0]

        binding.set.wrapSelectorWheel = false
        binding.set.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        binding.weight.minValue = 1
        binding.weight.maxValue = 60
        binding.weight.value = setting[1]

        binding.weight.wrapSelectorWheel = false
        binding.weight.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        binding.count.minValue = 1
        binding.count.maxValue = 100
        binding.count.value = setting[2]

        binding.count.wrapSelectorWheel = false
        binding.count.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        binding.posBtn.setOnClickListener {
            val list = listOf<Int>(binding.set.value, binding.weight.value, binding.count.value)
            callback(list)
            dialog?.dismiss()
        }

        binding.negBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }
}