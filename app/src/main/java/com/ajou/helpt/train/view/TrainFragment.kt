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
import kotlinx.coroutines.*
import kotlinx.coroutines.Runnable
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
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
    private var isSpeaking: Boolean? = false
    private var pendingText: String? = null
    private var runnable: Runnable? = null
    private val socketList = mutableListOf<Int>()

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
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(p0: String?) {
                        isSpeaking = true
                    }

                    override fun onDone(p0: String?) {
                        isSpeaking = false;
                        if (pendingText != null) {
                            speakText(pendingText!!);
                            pendingText = null;
                        }
                    }

                    override fun onError(p0: String?) {
                        isSpeaking = false
                        pendingText = null
                        shutDownTTS()
                    }
                })
            }
        })

        val handler = Handler(Looper.getMainLooper())
        var curCount = 0

        runnable = object : Runnable {
            override fun run() {
                if (curCount == customCount) {
                    curSet += 1
                    binding.set.text = String.format(mContext!!.resources.getString(R.string.train_setting_set, curSet))
                    binding.progressbar.progress = (curSet / customSet) * 100
                    val contents = "한 세트가 종료되었습니다."
                    utterTTS(contents)
                }else{
                    curCount += 1
                    utterTTS(curCount.toString())
                    handler.postDelayed(this, 3000)
                    binding.count.text = String.format(mContext!!.resources.getString(R.string.training_count,curCount,customCount))
                }
                if (curSet == customSet) {
                    handler.removeCallbacks(this)
                    val message = Message.obtain()
                    message.obj = "bandventend"
                    sendHandler.sendMessage(message)
                    val contents = "운동이 완료되었습니다. 수고하셨습니다."
                    utterTTS(contents)
                }
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
            viewModel.setTime(pauseTime.toString()) // TODO pauseTime 확인해보기
            viewModel.setDoneCount(curCount)
            viewModel.setDoneSet(curSet)
            findNavController().navigate(R.id.action_trainFragment_to_trainDoneFragment)
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private suspend fun repeatTask(times: Int, delayMillis: Long, task: suspend () -> Unit) {
        repeat(times) {
            task()
            delay(delayMillis)
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
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents =
                                    "잘하셨습니다. 다음은 상체의 각도를 확인하겠습니다. 상체와 하체의 각도가 90도가 되도록 숙여 주세요."
                                Log.d("socket res", "footcheckend 97")
                                withContext(Dispatchers.IO) {
                                    if (!socketList.contains(97)) {
                                        socketList.add(97)
//                                        delay(5000)
                                        utterTTS(contents)
                                        val message = Message.obtain()
                                        message.obj = "bodycheckstart"
                                        sendHandler.sendMessage(message)
                                        Log.d("sendMessage", "97")
                                    }
                                }
                            }
                        }
                        98 -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents =
                                    "잘하셨습니다. 마지막으로 손의 위치를 확인하겠습니다. 손을 아래로 떨어뜨린 후 밴드를 잡아주세요"
                                Log.d("socket res", "bodycheckend 98")
                                withContext(Dispatchers.IO) {
                                    if (!socketList.contains(98)) {
                                        socketList.add(98)
                                        utterTTS(contents)
                                        val message = Message.obtain()
                                        message.obj = "handcheckstart"
                                        sendHandler.sendMessage(message)
//                                        delay(5000)
                                        Log.d("sendMessage", "98")
                                    }
                                }
                            }
                        }
                        99 -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents =
                                    "완벽한 자세입니다. 이제 카운트에 맞게 운동을 시작해보겠습니다."
                                Log.d("socket res", "handcheckend 99")
                                withContext(Dispatchers.IO) {
                                    if (!socketList.contains(99)) {
                                        socketList.add(99)
                                        delay(5000)
                                        utterTTS(contents)
                                        val message = Message.obtain()
                                        message.obj = "bandventrunstart"
                                        sendHandler.sendMessage(message)
                                        Log.d("sendMessage", "99")
                                        delay(5000)
                                        handler.postDelayed(runnable!!, 7000)
                                    }
                                }
                            }
                        }
                        100 -> {
                            val message = Message.obtain()
                            message.obj = "socketclose"
                            sendHandler.sendMessage(message)
                            Log.d("socket res", "bandventalldone 100")
                            socket?.close()
                            Log.d("socket res","close")
                        }
                        101 -> Log.d("socket res", "notfound 101")
                        102 -> Log.d("socket res", "standby 102")
                        108 -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents = "왼쪽으로 치우쳐 있습니다. 자세를 확인해주세요."
                                Log.d("socket res", "l")
                                withContext(Dispatchers.IO) {
                                    delay(1000)
                                    if (socketList.contains(98) && socketList.contains(99)&& !socketList.contains(108)&&!socketList.contains(110)&&!socketList.contains(114)) {
                                        socketList.add(108)
                                        utterTTS(contents)
//                                        speakText(contents)
                                        delay(1000)
                                        socketList.remove(108)
                                    }
                                }
                            }
                        }
                        110 -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents = "올바른 자세로 잘하고 계십니다."
                                Log.d("socket res", "n")
                                withContext(Dispatchers.IO) {
                                    delay(1000)
                                    if (socketList.contains(98) && socketList.contains(99)&&!socketList.contains(108)&&!socketList.contains(110)&&!socketList.contains(114)) {
                                        socketList.add(110)
                                        utterTTS(contents)
//                                        speakText(contents)
                                        delay(1000)
                                        socketList.remove(110)
                                    }
                                }
                            }
                        }
                        114 -> {
                            CoroutineScope(Dispatchers.Main).launch {
                                val contents = "오른쪽으로 치우쳐 있습니다. 자세를 확인해주세요."
                                Log.d("socket res", "r")
                                withContext(Dispatchers.IO) {
                                    delay(1000)
                                    if (socketList.contains(98) && socketList.contains(99)&&!socketList.contains(108)&&!socketList.contains(110)&&!socketList.contains(114)) {
                                        socketList.add(114)
                                        utterTTS(contents)
//                                        speakText(contents)
                                        delay(1000)
                                        socketList.remove(114)
                                    }
                                }
                            }
                        }
                        else -> {
                            Log.d("socket res else", buffer[0].toInt().toString())
                        }
                    }
                }
            } catch (e: SocketException) {
                Log.d("socket fail socketexception", e.toString())
            } catch (e: Exception) {
                Log.d("socket fail exception", e.toString())
            }
        }
    }

    //
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
        runnable?.let { handler.removeCallbacks(it) }
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

    fun utterTTS(text: String) {
        sleep(1000)
        Log.d("tts text",text)
        if (isSpeaking!!) {
            pendingText = text
        } else {
            speakText(text)
        }
    }

    private fun speakText(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "UTTERANCE_ID")
    }

//    private fun speakTextAdd(text: String) {
//        Log.d("socket text","add $text")
//        tts.speak(text, TextToSpeech.QUEUE_ADD, null, "UTTERANCE_ID")
//    }


}