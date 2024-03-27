package com.ajou.helpt.auth

import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.*

class LoginFragment : Fragment() {
    private var _binding : FragmentLoginBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context ?= null
    private val dataStore = UserDataStore()
    private var accessToken : String? = null

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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.button.setOnClickListener {
            // 카카오톡 설치 확인
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(mContext!!)) {
                Log.d(ContentValues.TAG,"유저가 카카오톡 설치했음")
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
                            Log.d(ContentValues.TAG,"${error.message}")
                            Log.d(ContentValues.TAG,"유저가 카카오톡으로 로그인하기 실패함")
                            UserApiClient.instance.loginWithKakaoAccount(
                                mContext!!,
                                callback = mCallback
                            ) // 카카오 이메일 로그인
                        }
                    }
                    // 로그인 성공 부분
                    else if (token != null) {
                        CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
                            dataStore.saveAccessToken(token.accessToken)
                            withContext(Dispatchers.Main){
                                findNavController().navigate(R.id.action_loginFragment_to_setUserInfoFragment)
//                                findNavController().navigate(R.id.action_loginFragment_to_setUserInfoFragment)
                            }
                        }
                        Log.e(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
                        Log.d(ContentValues.TAG,"카카오톡 로그인 정보 가져옴 ${token.refreshToken} ${token.accessTokenExpiresAt} ${token.refreshTokenExpiresAt}")
                        UserApiClient.instance.me { user, error ->
                            Log.d(ContentValues.TAG,"${user?.id}") // accessToken, user.id 추후에 보내면 됨.
                            Log.d(ContentValues.TAG,"${user?.kakaoAccount?.email} ${user?.kakaoAccount?.profile?.nickname}")
                        }
                    }
                }
            } else {
                Log.d(ContentValues.TAG,"유저가 카카오톡 설치안가")
                UserApiClient.instance.loginWithKakaoAccount(mContext!!, callback = mCallback) // 카카오 이메일 로그인
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
        Log.d("exceptionHandler",exception.message.toString())
        showErrorDialog()
    }
    private fun showErrorDialog() {
        requireActivity().runOnUiThread{
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
}