package com.ajou.helpt.auth.view.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Button
import androidx.annotation.NonNull
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.AuthActivity
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class QuitDialog(@NonNull context : Context) : Dialog(context) {
    init {
        setContentView(R.layout.dialog_quit)

        val stayBtn = findViewById<Button>(R.id.stayBtn)
        val signOutBtn = findViewById<Button>(R.id.exitBtn)
        val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
        val dataStore = UserDataStore()
        var accessToken: String? = null
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken()

        }
        signOutBtn.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val quitDeferred = async { memberService.quit(accessToken!!) }
                val quitResponse = quitDeferred.await()
                if (quitResponse.isSuccessful){
                    UserDataStore().deleteAll() // 유저 데이터 삭제
                }else{
                    Log.d("탈퇴하기 실패",quitResponse.errorBody()?.string().toString())
                }
            }
            val intent = Intent(context, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            dismiss()
        }

        stayBtn.setOnClickListener {
            dismiss()
        }

    }
}