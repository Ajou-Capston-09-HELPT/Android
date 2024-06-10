package com.ajou.helpt.auth.view.fragment

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.UserInfoViewModel
import com.ajou.helpt.databinding.FragmentLoginBinding
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import org.json.JSONObject

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val dataStore = UserDataStore()
    private lateinit var viewModel : UserInfoViewModel
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextBtn.setOnClickListener {
            // 카카오톡 설치 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(mContext!!)) {
                // 카카오톡 로그인
                UserApiClient.instance.loginWithKakaoTalk(mContext!!) { token, error ->
                    // 로그인 실패 부분
                    if (error != null) {
                        Log.e(ContentValues.TAG, "로그인 실패 $error")
                        // 사용자가 취소
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }
                        // 다른 오류
                        else {
                            UserApiClient.instance.loginWithKakaoAccount(
                                mContext!!,
                                callback = mCallback
                            ) // 카카오 이메일 로그인
                        }
                    }
                    // 로그인 성공 부분
                    else if (token != null) {
                        UserApiClient.instance.me { user, error ->
                            CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                                dataStore.saveUserName(user?.kakaoAccount?.profile?.nickname.toString())
                                dataStore.saveKakaoId(user?.id.toString())
                                viewModel.setImg(user?.kakaoAccount?.profile?.profileImageUrl.toString())
                                checkLogin(user?.id.toString())
                            }
                        }

                        Log.d(
                            ContentValues.TAG,
                            "카카오톡 로그인 정보 가져옴 ${token.refreshToken} ${token.accessTokenExpiresAt} ${token.refreshTokenExpiresAt}"
                        )

                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(
                    mContext!!,
                    callback = mCallback
                ) // 카카오 이메일 로그인
            }
        }
    }

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e(ContentValues.TAG, "카카오계정으로 로그인 실패", error)
        } else if (token != null) {
            CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                dataStore.saveAccessToken(token.accessToken)
            }
            Log.i(ContentValues.TAG, "카카오계정으로 로그인 성공 ${token.accessToken}")
        }
    }

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("exceptionHandler", exception.message.toString())
        showErrorDialog()
    }

    private fun showErrorDialog() {
        requireActivity().runOnUiThread {
            AlertDialog.Builder(mContext!!).apply {
                setTitle("Error")
                setMessage("Network request failed.")
                setPositiveButton("뒤로 가기") { dialog, _ ->
                    dialog.dismiss()
                    findNavController().popBackStack()
                }
                setCancelable(false)
                show()
            }
        }
    } // TODO 구체적인 오류 메세지로 변경하기

    private fun checkLogin(kakaoId:String){
        var fcmDeviceToken = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            fcmDeviceToken = task.result
        })
        val jsonObject = JsonObject().apply {
            addProperty("kakaoId", kakaoId)
            addProperty("deviceToken", fcmDeviceToken)
        }
        val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), jsonObject.toString())
        CoroutineScope(Dispatchers.IO).launch {
            val checkLoginDeferred = async { memberService.login(requestBody) }
            val checkLoginResponse = checkLoginDeferred.await()
            if(checkLoginResponse.isSuccessful) {
                val tokenBody = JSONObject(checkLoginResponse.body()?.string())
                val accessToken = "Bearer " + tokenBody.getJSONObject("data").getString("accessToken").toString()
                dataStore.saveAccessToken(accessToken)
                dataStore.saveRefreshToken("Bearer " + tokenBody.getJSONObject("data").getString("refreshToken").toString())
                withContext(Dispatchers.Main){
                    val intent = Intent(mContext, HomeActivity::class.java)
                    startActivity(intent)
                }
            } else{
                Log.d("checkLoginResponse fail",checkLoginResponse.errorBody()?.string().toString())

                withContext(Dispatchers.Main){
                    findNavController().navigate(R.id.action_loginFragment_to_startSetInfoFragment)
                }

            }
        }
    }
}