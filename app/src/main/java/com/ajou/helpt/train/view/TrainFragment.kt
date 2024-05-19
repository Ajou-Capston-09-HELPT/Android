package com.ajou.helpt.train.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ajou.helpt.BuildConfig
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentTrainBinding
import com.ajou.helpt.train.adapter.TrainingViewPagerAdapter
import java.io.*
import java.net.Socket
import java.net.SocketException
import java.util.*
import kotlin.concurrent.thread
import kotlin.concurrent.timerTask

class TrainFragment : Fragment() {

    private val ip = BuildConfig.PI_IP
    private val port = BuildConfig.PI_PORT.toInt()
    private var _binding : FragmentTrainBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private lateinit var socket : Socket
    private lateinit var sendHandler: Handler
    private lateinit var tts: TextToSpeech
    private var isTTSInitialized = false

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
        _binding = FragmentTrainBinding.inflate(layoutInflater, container, false)
        binding.contentsVp.adapter = TrainingViewPagerAdapter(requireActivity() as TrainActivity)
        binding.dotsIndicator.setViewPager2(binding.contentsVp)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.testBtn.setOnClickListener {
            val receiveThread = ReceiveThread()
            receiveThread.start()
            tts = TextToSpeech(mContext, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS){
                    isTTSInitialized = true
                    val result = tts.setLanguage(Locale.KOREAN)
                    tts.setPitch(3.0f)
                    val voices = tts.voices
                    Log.d("voices",voices.toString())
                    val voice = voices.stream()
                        .filter { v -> v.name.equals("ko-KR-SMTl05") }
                        .findFirst()
                        .orElse(null)
                    tts.voice = voice
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","해당 언어는 지원되지 않습니다.")
                        Toast.makeText(mContext,"해당 언어는 지원되지 않습니다.", Toast.LENGTH_SHORT).show()
                        return@OnInitListener
                    }else{
                        utterTTS("안녕하세요")
                    }
                }
            })
        }

    }

    inner class ReceiveThread : Thread() {
        init {
            Log.d("In Receive Thread", "Receive Thread 1")
        }

        override fun run() {
            try {
                socket = Socket(ip, port)
                Log.d("socket", socket.toString())
                val outputStream = ObjectOutputStream(socket.getOutputStream())
//                outputStream.writeObject(editText.text.toString())
                Log.d("socket ",outputStream.toString())
                outputStream.flush()

                val inputStream = ObjectInputStream(socket.getInputStream())
                val input = inputStream.readObject() as String
                Log.d("socket ",input)
            } catch (e: SocketException) {
                Log.d("socket fail",e.toString())
            } catch (e: Exception) {
                Log.d("socket fail", e.toString()) // 모든 예외를 처리하기 위한 catch 블록
            }
        }
    }

    inner class SendThread : Thread() {
        private lateinit var dataOutputStream: DataOutputStream
        private lateinit var outputStream: OutputStream

        init {
            Log.d("@@@@/ In Send Thread", "Send Thread 1")
        }

        override fun run() {
            try {
                outputStream = socket?.getOutputStream() ?: throw IOException("Socket is null")
                dataOutputStream = DataOutputStream(outputStream)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Looper.prepare()
            sendHandler = object : Handler(Looper.myLooper()!!) {
                override fun handleMessage(msg: Message) {
                    try {
                        dataOutputStream.write(msg.obj.toString().toByteArray())
                        dataOutputStream.flush()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
            Looper.loop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::socket.isInitialized) socket.close()
        if (::tts.isInitialized) {
            shutDownTTS()
        }
    }

    private fun shutDownTTS(){
        if (isTTSInitialized){
            tts.stop()
            tts.shutdown()
            isTTSInitialized = false
        }
    }

    private fun utterTTS(content: String){
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener(){
            override fun onStart(p0: String?) {
            }

            override fun onDone(p0: String?) {
            }

            override fun onError(p0: String?) {
                shutDownTTS()
            }
        })
        tts.speak(content, TextToSpeech.QUEUE_ADD, null, TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID)
    }

}