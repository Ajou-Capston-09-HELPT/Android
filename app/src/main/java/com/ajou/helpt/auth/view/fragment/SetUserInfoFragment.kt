package com.ajou.helpt.auth.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.dialog.SelectBirthDialog
import com.ajou.helpt.auth.view.UserInfoViewModel
import com.ajou.helpt.databinding.FragmentSetUserInfoBinding
import com.ajou.helpt.getFileName
import java.util.Date
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class SetUserInfoFragment : Fragment() {
    private var _binding: FragmentSetUserInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: UserInfoViewModel
    private val dataStore = UserDataStore()
//    private lateinit var calendarDialog: CalendarDialog
    private lateinit var birthDialog: SelectBirthDialog
//    private lateinit var launcher: ActivityResultLauncher<Intent>

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
//            if (result.resultCode == AppCompatActivity.RESULT_OK) {
//                val intent = checkNotNull(result.data)
//                val imageUri = intent.data // 갤러리에서 선택한 사진 받아옴
//                if (imageUri != null) {
//                    Log.d("check imageuri",imageUri.toString())
//                    viewModel.setProfileImg(imageUri)
//                    val fileName = getFileName(imageUri!!, requireActivity())
//                    binding.bizImg.text = fileName.toString()
//                    binding.bizImg.setTextColor(resources.getColor(R.color.black))
//                    binding.bizImgIcon.visibility = View.GONE
//                    binding.bizImgIconRemove.visibility = View.VISIBLE
//                    binding.bizImg.isSelected = true
//                }
//            }
//        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]
        _binding = FragmentSetUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var phoneNum: String = ""
        var birth: String = ""

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

//        binding.phoneNum.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//            }
//
//            override fun afterTextChanged(str: Editable?) {
//                if(str!!.isNotEmpty()) {
//                    phoneNum = str.toString()
//                    binding.phoneNum.isSelected = true
//                    viewModel.setDone(true)
//                } else {
//                    phoneNum = ""
//                    binding.phoneNum.isSelected = false
//                    viewModel.setDone(false)
//                }
//            }
//        })

//        binding.bizImg.setOnClickListener {
//            val intent = Intent().also { intent ->
//                intent.type = "image/"
//                intent.action = Intent.ACTION_GET_CONTENT
//            }
//            launcher.launch(intent)
//        }
//
//        binding.bizImgIconRemove.setOnClickListener {
//            viewModel.setProfileImg(null)
//            viewModel.setDone(false)
//
//            binding.bizImgIconRemove.visibility = View.GONE
//            binding.bizImgIcon.visibility = View.VISIBLE
//            binding.bizImg.text = "이미지를 업로드해주세요"
//            binding.bizImg.isSelected = false
//            // TODO imageuri 제거
//        }

        binding.birth.setOnClickListener {
            birthDialog = SelectBirthDialog() { value ->
                viewModel.setDone(true)
                birth = value
                val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

                val date: Date = inputFormat.parse(value)

                binding.birth.text = outputFormat.format(date)

                val localDate : LocalDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                viewModel.setBirthInfo(localDate)
            }
            birthDialog.show(requireActivity().supportFragmentManager, "birth")
        }

        viewModel.done.observe(viewLifecycleOwner) {
            binding.nextBtn.isEnabled = birth != "" && viewModel.profileImg.value == null
        }
//
//        binding.phoneNum.setOnEditorActionListener { view, id, keyEvent ->
//            if (id == EditorInfo.IME_ACTION_DONE) {
//                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//                imm.hideSoftInputFromWindow(requireActivity().window.decorView.applicationWindowToken, 0)
//
//                if (binding.nextBtn.isEnabled) findNavController().navigate(R.id.action_setUserInfoFragment_to_setPhysicInfoFragment)
//
//                return@setOnEditorActionListener true
//            } else return@setOnEditorActionListener false
//        }

        binding.nextBtn.setOnClickListener {
            // TODO 추후에 서버에 내용 전송하는 부분 넣기
            findNavController().navigate(R.id.action_setUserInfoFragment_to_setPhysicInfoFragment)
        }
    }

}