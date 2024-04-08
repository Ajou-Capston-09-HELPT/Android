package com.ajou.helpt.auth.view

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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavArgs
import androidx.navigation.fragment.navArgs
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentSetPhysicInfoBinding
import com.ajou.helpt.home.HomeActivity

class SetPhysicInfoFragment : Fragment() {
    private var _binding: FragmentSetPhysicInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: UserInfoViewModel

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
        _binding = FragmentSetPhysicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var weight: String = ""
        var height: String = ""
        binding.height.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                if(str!!.isNotEmpty()) {
                    weight = str.toString()
                    viewModel.setDone(true)
                }else{
                    weight = ""
                    viewModel.setDone(false)
                }
            }
        })

        binding.weight.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                if(str!!.isNotEmpty()) {
                    height = str.toString()
                    viewModel.setDone(true)
                }else{
                    height = ""
                    viewModel.setDone(false)
                }
            }

        })
        viewModel.done.observe(viewLifecycleOwner) {
            binding.nextBtn.isEnabled = weight != "" && height != ""
        }

        binding.nextBtn.setOnClickListener {
            val intent = Intent(mContext, HomeActivity::class.java)
            startActivity(intent)
            // TODO 추후에 서버에 내용 전송하는 부분 넣기
        }
    }
}