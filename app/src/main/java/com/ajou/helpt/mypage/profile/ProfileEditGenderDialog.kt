package com.ajou.helpt.mypage.profile

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResult
import com.ajou.helpt.R
import com.ajou.helpt.databinding.DialogEditGenderBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfileEditGenderDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogEditGenderBinding
    private var selectedGender: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogEditGenderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnGenderMan.setOnClickListener{
            binding.btnGenderMan.setImageResource(R.drawable.auth_man_on)
            binding.manSelectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.btnGenderWoman.setImageResource(R.drawable.auth_woman_off)
            binding.womanSelectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_off))
            selectedGender = "남성"
        }
        binding.btnGenderWoman.setOnClickListener{
            binding.btnGenderWoman.setImageResource(R.drawable.auth_woman_on)
            binding.womanSelectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary))
            binding.btnGenderMan.setImageResource(R.drawable.auth_man_off)
            binding.manSelectText.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray_off))
            selectedGender = "여성"
        }

        binding.btnEditGenderClose.setOnClickListener {
            dismiss()
        }

        binding.btnEditGenderSave.setOnClickListener {
            val alphaAnimator = ObjectAnimator.ofFloat(binding.btnEditGenderSave, "alpha", 0.5f, 1f).apply{
                duration = 100
            }
            alphaAnimator.start()
            if(selectedGender != null) {
                val result = Bundle().apply { putString("gender", selectedGender) }
                setFragmentResult("genderRequestKey", result)
                dismiss()
            } else{
                Toast.makeText(requireContext(), "성별을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

}