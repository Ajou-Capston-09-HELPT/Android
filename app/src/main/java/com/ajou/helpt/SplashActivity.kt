package com.ajou.helpt

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.VideoView
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
        var accessToken : String ?= null
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            withContext(Dispatchers.Main){
                if (accessToken != "null"){
                    Log.d("Login!",accessToken.toString())
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Log.d("Login 필요!","")
                    val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                }
            }
        }

//        val test : VideoView = findViewById(R.id.test)
//        val videoPath = "android.resource://" + packageName + "/" + R.raw.squat
//        val uri = Uri.parse(videoPath)
//
//        test.setVideoURI(uri)
//
////        test.setOnCompletionListener { mediaPlayer ->
////            test.stopPlayback()
////        }
//        test.start()

    }
}