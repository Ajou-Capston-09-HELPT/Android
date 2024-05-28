package com.ajou.helpt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ajou.helpt.auth.view.AuthActivity
import com.ajou.helpt.home.view.HomeActivity
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.MemberService
import com.ajou.helpt.network.api.MemberShipService
import kotlinx.coroutines.*
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
    private val dataStore = UserDataStore()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        var accessToken: String? = null
        var refreshToken: String? = null
        val logoGif = findViewById<ImageView>(R.id.logo)
        val gifDrawable =
            pl.droidsonroids.gif.GifDrawable(resources, R.drawable.splash_logo)
        logoGif.setImageDrawable(gifDrawable)
        Handler().postDelayed({
            CoroutineScope(Dispatchers.IO).launch {
                accessToken = dataStore.getAccessToken().toString()
                refreshToken = dataStore.getRefreshToken().toString()
                if (accessToken != null && refreshToken != null && accessToken != "null" && refreshToken != "null") {
                    val tokenDeferred = async { memberService.getMyInfo(accessToken!!)}
                    val tokenResponse = tokenDeferred.await()
                    if (!tokenResponse.isSuccessful){
                        val newTokenDeferred = async { memberService.getNewToken(refreshToken!!) }
                        val newTokenResponse = newTokenDeferred.await()
                        if (newTokenResponse.isSuccessful){
                            val tokenBody = JSONObject(newTokenResponse.body()?.string())
                            Log.d("tokenBody",tokenBody.toString())
                            dataStore.saveAccessToken("Bearer " + tokenBody.getJSONObject("data").getString("accessToken").toString())
                            dataStore.saveRefreshToken("Bearer " + tokenBody.getJSONObject("data").getString("refreshToken").toString())
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (accessToken != null && refreshToken != null && accessToken != "null" && refreshToken != "null") {
                            Log.d("Login!", accessToken.toString())
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
        }, 2000)
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