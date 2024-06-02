package com.ajou.helpt.auth.view.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.dialog.SelectBirthDialog
import com.ajou.helpt.auth.view.UserInfoViewModel
import com.ajou.helpt.databinding.FragmentSetUserInfoBinding
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
        viewModel = ViewModelProvider(requireActivity())[UserInfoViewModel::class.java]
        _binding = FragmentSetUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var phoneNum: String = ""
        var birth: String = ""
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

        binding.birth.setOnClickListener {
            birthDialog = SelectBirthDialog() { value ->
                viewModel.setDone(true)
                birth = value
                val inputFormat = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

                val date: LocalDate = inputFormat.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                viewModel.setBirthInfo(date)
                binding.birth.text = outputFormat.format(date)
            }
            birthDialog.show(requireActivity().supportFragmentManager, "birth")
        }

        viewModel.done.observe(viewLifecycleOwner) {
            binding.nextBtn.isEnabled = birth != ""
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