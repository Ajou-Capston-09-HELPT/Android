package com.ajou.helpt.mypage.profile

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.DialogEditGenderBinding
import com.ajou.helpt.databinding.FragmentProfileBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private val dataStore = UserDataStore()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
        }

        clickBackButton(binding.root)
        clickChangeButton(binding.root)
        clickEditNameButton(binding.root)
        clickEditGenderButton(binding.root)
        clickEditHeightButton(binding.root)
        clickEditWeightButton(binding.root)


        setFragmentResultListener("genderRequestKey") { _, bundle ->
            val result = bundle.getString("gender")
            binding.tvGenderContent.text = result
        }

        return binding.root
    }

    private fun clickBackButton(view: View) {
        binding.backBtn.setOnClickListener {
            binding.backBtn.alpha = 0.5f
            binding.backBtn.postDelayed({
                binding.backBtn.alpha = 1f
            }, 100)
            findNavController().popBackStack()
        }
    }

    private fun clickChangeButton(view: View) {
        // TODO : 프로필 변경 후 이전 프래그먼트 이동 구현 (프로필 수정 성공/실패 메시지 띄우기)
        binding.btnChange.setOnClickListener {
            binding.btnChange.alpha = 0.5f
            binding.btnChange.postDelayed({
                binding.btnChange.alpha = 1f
            }, 100)

            findNavController().popBackStack()
        }
    }

    private fun clickEditNameButton(view: View) {
        binding.ivEditName.setOnClickListener {
            val alphaAnimator =
                ObjectAnimator.ofFloat(binding.ivEditName, View.ALPHA, 0.5f, 1f).apply {
                    duration = 100
                }
            alphaAnimator.start()
            showEditNameDialog(binding.root)
        }
    }

    private fun clickEditGenderButton(view: View) {
        binding.ivEditGender.setOnClickListener {
            val alphaAnimator =
                ObjectAnimator.ofFloat(binding.ivEditGender, View.ALPHA, 0.5f, 1f).apply {
                    duration = 100
                }
            alphaAnimator.start()
            showEditGenderDialog(binding.root)
        }
    }

    private fun clickEditHeightButton(view: View) {
        binding.ivEditHeight.setOnClickListener {
            val alphaAnimator =
                ObjectAnimator.ofFloat(binding.ivEditHeight, View.ALPHA, 0.5f, 1f).apply {
                    duration = 100
                }
            alphaAnimator.start()
            showEditHeightDialog(binding.root)
        }
    }

    private fun clickEditWeightButton(view: View) {
        binding.ivEditWeight.setOnClickListener {
            val alphaAnimator =
                ObjectAnimator.ofFloat(binding.ivEditWeight, View.ALPHA, 0.5f, 1f).apply {
                    duration = 100
                }
            alphaAnimator.start()
            showEditWeightDialog(binding.root)
        }
    }


    private fun showEditNameDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_name, null)
        val editText = dialogView.findViewById<EditText>(R.id.editName)
        val buttonClose = dialogView.findViewById<ImageView>(R.id.btnEditNameClose)
        val buttonConfirm = dialogView.findViewById<TextView>(R.id.btnEditNameSave)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        buttonConfirm.setOnClickListener {
            val alphaAnimator = ObjectAnimator.ofFloat(buttonConfirm, View.ALPHA, 0.5f, 1f).apply {
                duration = 100
            }
            alphaAnimator.start()
            val inputText = editText.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.tvNameContent.text = inputText
                dialog.dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditGenderDialog(view: View) {
        val dialog = ProfileEditGenderDialog()
        dialog.show(parentFragmentManager, "ProfileEditGenderDialog")

    }

    private fun showEditHeightDialog(view: View){
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_height, null)
        val editText = dialogView.findViewById<EditText>(R.id.editHeight)
        val buttonClose = dialogView.findViewById<ImageView>(R.id.btnEditHeightClose)
        val buttonConfirm = dialogView.findViewById<TextView>(R.id.btnEditHeightSave)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        buttonConfirm.setOnClickListener {
            val alphaAnimator = ObjectAnimator.ofFloat(buttonConfirm, View.ALPHA, 0.5f, 1f).apply {
                duration = 100
            }
            alphaAnimator.start()
            val inputText = editText.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "키를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.tvHeightContent.text = inputText
                dialog.dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showEditWeightDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_weight, null)
        val editText = dialogView.findViewById<EditText>(R.id.editWeight)
        val buttonClose = dialogView.findViewById<ImageView>(R.id.btnEditWeightClose)
        val buttonConfirm = dialogView.findViewById<TextView>(R.id.btnEditWeightSave)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        buttonConfirm.setOnClickListener {
            val alphaAnimator = ObjectAnimator.ofFloat(buttonConfirm, View.ALPHA, 0.5f, 1f).apply {
                duration = 100
            }
            alphaAnimator.start()
            val inputText = editText.text.toString()
            if (inputText.isEmpty()) {
                Toast.makeText(requireContext(), "몸무게를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                binding.tvWeightContent.text = inputText
                dialog.dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

}