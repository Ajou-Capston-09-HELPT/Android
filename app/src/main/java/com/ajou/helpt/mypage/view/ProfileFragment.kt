package com.ajou.helpt.mypage.view

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.ajou.helpt.databinding.FragmentProfileBinding
import com.ajou.helpt.mypage.model.MyInfo
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
    private val dataStore = UserDataStore()
    private lateinit var accessToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            if(accessToken.isNotEmpty()) {
                val memberInfoDeferred =
                    async { memberService.getMyInfo(accessToken) }
                val memberInfoResponse = memberInfoDeferred.await()
                if (memberInfoResponse.isSuccessful) {
                    val member = memberInfoResponse.body()!!.data
                    Log.d("member data",member.toString())
                    val translateGender = when(member.gender){
                        "WOMEN" -> "여성"
                        "MAN" -> "남성"
                        else -> member.gender
                    }
                    binding.tvNameContent.text = member.userName
                    binding.tvGenderContent.text = translateGender
                    binding.tvHeightContent.text = member.height.toString() + " cm"
                    binding.tvWeightContent.text = member.weight.toString() + " kg"
                }
                else{
                    Log.d("myInfo fail",memberInfoResponse.errorBody()?.string().toString())
                    withContext(Dispatchers.Main){
                        Toast.makeText(requireContext(), "프로필을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    }

                }
            }
            else{
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "프로필을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().popBackStack()
                }
            }
        }

        clickBackButton(binding.root)
        clickChangeButton(binding.root)
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
        binding.btnChange.setOnClickListener {
            binding.btnChange.alpha = 0.5f
            binding.btnChange.postDelayed({
                binding.btnChange.alpha = 1f
            }, 100)

            CoroutineScope(Dispatchers.IO).launch {
                val accessToken = dataStore.getAccessToken().toString()
                val gymId = dataStore.getGymId()
                val kakaoId = dataStore.getKakaoId()
                val name = binding.tvNameContent.text.toString()
                val gender = binding.tvGenderContent.text.toString()
                val translateGender = when (gender) {
                    "여성" -> "WOMEN"
                    "남성" -> "MAN"
                    else -> gender
                }

                val height = extractNumber(binding.tvHeightContent.text.toString())
                val weight = extractNumber(binding.tvWeightContent.text.toString())
                val member = MyInfo(
                    gymId = gymId!!,
                    userName = name,
                    gender = translateGender,
                    height = height,
                    weight = weight,
                    kakaoId = kakaoId!!
                )
                Log.d("ProfileFragment", "Member: $member")

                if (accessToken.isNotEmpty()) {
                    val memberInfoDeferred =
                        async { memberService.updateMyInfo(accessToken, member) }
                    val memberInfoResponse = memberInfoDeferred.await()
                    if (memberInfoResponse.isSuccessful) {
                        val info = JSONObject(memberInfoResponse.body()!!.string())
                        Log.d("ProfileFragment", "Member info: ${info}")
                        withContext(Dispatchers.Main){
                            Toast.makeText(requireContext(), "프로필이 변경되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                    else{
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                requireContext(),
                                "프로필을 변경하는데 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                            findNavController().popBackStack()
                        }
                    }
                } else{
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "프로필을 변경하는데 실패했습니다.", Toast.LENGTH_SHORT)
                            .show()
                        findNavController().popBackStack()
                    }
                }
            }
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

    @SuppressLint("SetTextI18n")
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
                binding.tvHeightContent.text = "$inputText cm"
                dialog.dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

    }

    @SuppressLint("SetTextI18n")
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
                binding.tvWeightContent.text = "$inputText kg"
                dialog.dismiss()
            }
        }

        buttonClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun extractNumber(input: String): Int{
        return input.replace("[^0-9]".toRegex(), "").toInt()
    }
}