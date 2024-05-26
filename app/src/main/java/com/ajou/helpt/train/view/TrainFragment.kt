package com.ajou.helpt.train.view

import android.content.Context
import android.os.*
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.BuildConfig
import com.ajou.helpt.R
import com.ajou.helpt.databinding.FragmentTrainBinding
import com.ajou.helpt.train.adapter.TrainingViewPagerAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.*
import java.lang.Thread.sleep
import java.net.Socket
import java.net.SocketException
import java.util.*

class TrainFragment : Fragment() {

    private val ip = BuildConfig.PI_IP
    private val port = BuildConfig.PI_PORT.toInt()
    private var _binding: FragmentTrainBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private var socket: Socket? = null
    private lateinit var sendHandler: Handler
    private lateinit var tts: TextToSpeech
    private var isTTSInitialized = false
    private lateinit var viewModel: TrainInfoViewModel
    private var curCount: Int = 1
    private var curSet: Int = 0
    private val handler = Handler(Looper.myLooper()!!)
    private lateinit var thread: Thread

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
        viewModel = ViewModelProvider(requireActivity())[TrainInfoViewModel::class.java]
        binding.contentsVp.adapter = TrainingViewPagerAdapter(requireActivity() as TrainActivity)
        binding.dotsIndicator.setViewPager2(binding.contentsVp)
        return binding.root
    }


    // TODO 세트 끝나면 +1로 strings/train_setting_set로 글자 표기, progressbar의 progress는 현재 세트 수 / 전체 세트
    // TODO 카운트는 +1씩 증가시키고, 현재 카운트 == 전체 카운트 되면 세트 +1시키기, 카운트는 training_count로 값 두 개 넣기
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var pauseTime = 0L

        val customCount = viewModel.train.value!!.customCount
        val customWeight = viewModel.train.value!!.customWeight
        val customSet = viewModel.train.value!!.customSet

        binding.set.text =
            String.format(mContext!!.resources.getString(R.string.train_setting_set), 0)
        binding.count.text =
            String.format(mContext!!.resources.getString(R.string.training_count), 0, customCount)
//        binding.progressbar.progress = 6 / customCount * 100


        tts = TextToSpeech(mContext, TextToSpeech.OnInitListener {
            if (it == TextToSpeech.SUCCESS) {
                isTTSInitialized = true
                val result = tts.setLanguage(Locale.KOREAN)
                val voices = tts.voices
                val voice = voices.stream()
                    .filter { v -> v.name.equals("ko-KR-SMTl05") }
                    .findFirst()
                    .orElse(null)
                tts.voice = voice
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "해당 언어는 지원되지 않습니다.")
                    return@OnInitListener
                } else {
                    val contents = "밴드벤트 운동을 시작하겠습니다. 먼저, 발의 위치를 확인하겠습니다. 두 발을 어깨너비로 두세요"
                    utterTTS(contents)
                }
            }
        })

        thread = object : Thread() {
            override fun run() {
                super.run()
                curCount += 1
                CoroutineScope(Dispatchers.IO).launch {
                    utterTTS(curCount.toString())
                }
//                utterTTS(curCount.toString())
                handler.postDelayed(this, 3000)
            }
        }

        val receiveThread = ReceiveThread()
        receiveThread.start()
        val sendThread = SendThread()
        sendThread.start()

        binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
        binding.chronometer.start()

        binding.trainBtn.setOnClickListener {
            pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()
            handler.removeCallbacks(thread)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
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
                while (true) {
                    while (socket?.getInputStream()?.available()!! < 0);
                    val buffer = ByteArray(4)
                    socket!!.getInputStream().read(buffer)
                    Log.d("socket response thread", buffer.contentToString())
                    when (buffer[0].toInt()) {
                        0 -> break
                        97 -> {
                            val contents =
                                "잘하셨습니다. 다음은 상체의 각도를 확인하겠습니다. 상체와 하체의 각도가 90도가 되도록 숙여 주세요."
                            CoroutineScope(Dispatchers.IO).launch {
                                utterTTS(contents)
                                val message = Message.obtain()
                                message.obj = "bodycheckstart"
                                sendHandler.sendMessage(message)
                                Log.d("socket res", "footcheckend 97")
                            }
                        }
                        98 -> {
                            val contents =
                                "잘하셨습니다. 마지막으로 손의 위치를 확인하겠습니다. 손을 아래로 떨어뜨린 후 밴드를 잡아주세요"
                            CoroutineScope(Dispatchers.IO).launch {
                                utterTTS(contents)
                                val message = Message.obtain()
                                message.obj = "handcheckstart"
                                sendHandler.sendMessage(message)
                                Log.d("socket res", "bodycheckend 98")
                            }
                        }
                        99 -> {
                            val contents =
                                "완벽한 자세입니다. 이제 카운트에 맞게 운동을 시작해보겠습니다."
                            CoroutineScope(Dispatchers.IO).launch {
                                utterTTS(contents)
                                val message = Message.obtain()
                                message.obj = "bandventrunstart"
                                sendHandler.sendMessage(message)
                                Log.d("socket res", "handcheckend 99")
                                handler.postDelayed(thread, 2000)
                            }
//                            handler.postDelayed(thread, 2000)
                        }
                        100 -> {
                            Log.d("socket res", "bandventalldone 100")
                        }
                        101 -> Log.d("socket res", "notfound 101")
                        102 -> Log.d("socket res", "standby 102")
                        108 -> {
                            Log.d("socket res", "l")

                        }
                        110 -> {
                            Log.d("socket res", "n")
                        }
                        114 -> {
                            Log.d("socket res", "r")
                        }
                        else -> {
                            Log.d("socket res else", buffer[0].toInt().toString())
                        }
                    }
                }
            } catch (e: SocketException) {
                Log.d("socket fail socketexception", e.toString())
            } catch (e: Exception) {
                Log.d("socket fail exception", e.toString()) // 모든 예외를 처리하기 위한 catch 블록
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
            while (socket == null);
            try {
                Log.d("socket send thread run", "")
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
            Log.d("socket sendhandler", "called")
            val message = Message.obtain()
            message.obj = "bandventcheckstart"
            sendHandler.sendMessage(message)
            Log.d("send message", "")
            Looper.loop()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(thread)
        if (socket != null) {
            socket?.close()
            socket = null
        }
        if (::tts.isInitialized) {
            shutDownTTS()
        }
    }

    private fun shutDownTTS() {
        if (isTTSInitialized) {
            tts.shutdown()
            isTTSInitialized = false
        }
    }

    private fun utterTTS(content: String) {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(p0: String?) {
            }

            override fun onDone(p0: String?) {
            }

            override fun onError(p0: String?) {
                Log.d("onerror", p0.toString())
                shutDownTTS()
            }
        })
        tts.speak(content, TextToSpeech.QUEUE_ADD, null, null)
        //TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID
    }

}