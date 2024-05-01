package com.ajou.helpt.auth.view.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ajou.helpt.R
import com.ajou.helpt.databinding.DialogSelectBirthBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class SelectBirthDialog(private val callback: (String) -> Unit) : DialogFragment() {
    private lateinit var binding: DialogSelectBirthBinding
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
        binding = DialogSelectBirthBinding.inflate(inflater, container, false)
        isCancelable = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.year.minValue = 1900
        binding.year.maxValue = 2010
        binding.year.value = 2000

        binding.month.minValue = 1
        binding.month.maxValue = 12
        binding.month.value = 1

        binding.day.minValue = 1
        binding.day.maxValue = 31
        binding.day.value = 1

        binding.posBtn.setOnClickListener {
            val format = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
            val data = format.format(
                java.sql.Date.valueOf(
                    LocalDate.of(
                        binding.year.value,
                        binding.month.value,
                        binding.day.value
                    ).toString() // LocalDate -> Date -> String
                )
            )
            callback(data)
            dialog?.dismiss()
        }

        binding.negBtn.setOnClickListener {
            dialog?.dismiss()
        }
    }
}