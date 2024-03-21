package com.ajou.helpt

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ajou.helpt.auth.AuthActivity
import com.ajou.helpt.home.HomeActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val dataStore = UserDataStore()
        var accessToken = ""
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            withContext(Dispatchers.Main){
                if (accessToken != "") {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                }
            }
        }

    }
}