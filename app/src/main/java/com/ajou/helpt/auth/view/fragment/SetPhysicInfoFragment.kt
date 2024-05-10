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
import androidx.lifecycle.ViewModelProvider
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.UserInfoViewModel
import com.ajou.helpt.databinding.FragmentSetPhysicInfoBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import com.ajou.helpt.network.model.Member
import kotlinx.coroutines.*
import org.json.JSONObject

class SetPhysicInfoFragment : Fragment() {
    private var _binding: FragmentSetPhysicInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private lateinit var viewModel: UserInfoViewModel
    private val dataStore = UserDataStore()
    private var userName : String? = null
    private var kakaoId : String? = null
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)

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
        CoroutineScope(Dispatchers.IO).launch {
            userName = dataStore.getUserName()
            kakaoId = dataStore.getKakaoId()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var weight: Int = 0
        var height: Int = 0
        binding.height.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(str: Editable?) {
                if(str!!.isNotEmpty()) {
                    weight = str.toString().toInt()
                    binding.height.isSelected = true
                    viewModel.setDone(true)
                }else{
                    weight = 0
                    binding.height.isSelected = false
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
                    height = str.toString().toInt()
                    binding.height.isSelected = true
                    viewModel.setDone(true)
                }else{
                    height = 0
                    binding.height.isSelected = false
                    viewModel.setDone(false)
                }
            }

        })
        viewModel.done.observe(viewLifecycleOwner) {
            binding.nextBtn.isEnabled = weight != 0 && height != 0
        }

        binding.weight.setOnEditorActionListener { view, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE){
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().window.decorView.applicationWindowToken, 0)

                if (binding.nextBtn.isEnabled) callLoginApi(weight, height)

                return@setOnEditorActionListener true
            }else return@setOnEditorActionListener false
        }

        binding.nextBtn.setOnClickListener {
            // TODO 추후에 서버에 내용 전송하는 부분 넣기
            callLoginApi(weight, height)
        }
    }

    private fun callLoginApi(weight: Int, height: Int) {
        Log.d("UserData","userName $userName  sex ${viewModel.sex.value}  height $height  weight $weight  kakaoId $kakaoId")
        val memberInfo = Member(null,userName!!,viewModel.sex.value.toString(),height, weight,kakaoId!!)
        CoroutineScope(Dispatchers.IO).launch{
            val loginDeferred = async {memberService.register(memberInfo) }
            val loginResponse = loginDeferred.await()
            if (loginResponse.isSuccessful) {
                val tokenBody = JSONObject(loginResponse.body()?.string())
                Log.d("tokenBody",tokenBody.toString())
                dataStore.saveAccessToken("Bearer " + tokenBody.getJSONObject("data").getString("accessToken").toString())
                dataStore.saveRefreshToken("Bearer " + tokenBody.getJSONObject("data").getString("refreshToken").toString())
                withContext(Dispatchers.Main){
                    val intent = Intent(mContext, HomeActivity::class.java)
                    startActivity(intent)
                }
            }else{
                Log.d("fail",loginResponse.errorBody()?.string().toString())
            }
        }
    }
}