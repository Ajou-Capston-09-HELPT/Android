package com.ajou.helpt

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ajou.helpt.auth.view.AuthActivity
import com.ajou.helpt.home.view.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val dataStore = UserDataStore()
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