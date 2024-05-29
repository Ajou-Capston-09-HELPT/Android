package com.ajou.helpt

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ajou.helpt.auth.view.AuthActivity
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
    private val dataStore = UserDataStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoGif = findViewById<ImageView>(R.id.logo)
        val gifDrawable = pl.droidsonroids.gif.GifDrawable(resources, R.drawable.splash_logo)
        logoGif.setImageDrawable(gifDrawable)

        // Delay and perform the necessary checks
        lifecycleScope.launch(Dispatchers.IO) {
            kotlinx.coroutines.delay(2000)

            val accessToken = dataStore.getAccessToken().toString()
            val refreshToken = dataStore.getRefreshToken().toString()

            var finalAccessToken = accessToken
            var finalRefreshToken = refreshToken

            if (accessToken.isNotEmpty() && refreshToken.isNotEmpty() && accessToken != "null" && refreshToken != "null") {
                val tokenResponse = memberService.getMyInfo(accessToken)
                if (!tokenResponse.isSuccessful) {
                    val newTokenResponse = memberService.getNewToken(refreshToken)
                    if (newTokenResponse.isSuccessful) {
                        val tokenBody = JSONObject(newTokenResponse.body()?.string())
                        Log.d("tokenBody", tokenBody.toString())

                        finalAccessToken = "Bearer " + tokenBody.getJSONObject("data").getString("accessToken").toString()
                        finalRefreshToken = "Bearer " + tokenBody.getJSONObject("data").getString("refreshToken").toString()

                        dataStore.saveAccessToken(finalAccessToken)
                        dataStore.saveRefreshToken(finalRefreshToken)
                    }
                }
            }
            withContext(Dispatchers.Main) {
                if (finalAccessToken.isNotEmpty() && finalRefreshToken.isNotEmpty() && finalAccessToken != "null" && finalRefreshToken != "null") {
                    Log.d("Login!", finalAccessToken)
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                } else {
                    Log.d("Login 필요!", "")
                    val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }
}
