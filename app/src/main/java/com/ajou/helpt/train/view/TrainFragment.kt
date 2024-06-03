package com.ajou.helpt.train.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.ajou.helpt.train.TrainInfoViewModel
import com.ajou.helpt.train.adapter.TrainingViewPagerAdapter
import kotlinx.coroutines.*
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
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
    private var curCount: Int = 0
    private var curSet: Int = 0
    private val handler = Handler(Looper.myLooper()!!)
    private var isSpeaking: Boolean? = false
    private var pendingText: String? = null
    private var runnable: Runnable? = null
    private val socketList = mutableListOf<Int>()
    private var utterState = 0
    private var takePhoto = false
    private var endRate: Int? = 0
    private var endDirection: Char? = null

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
        binding.name.text = viewModel.train.value!!.equipmentName
        binding.engName.text = "one arm dumbbell lateral raise"
//        binding.engName.text = viewModel.train.value!!.equipmentNameEng

//        if (viewModel.train.value!!.equipmentName == "")
        // TODO 운동 구분하기
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
                    val contents =
                        "원 암 덤벨 레터럴 레이즈 운동을 시작하겠습니다. 한 손에 덤벨을 들고 어깨너비로 서주세요. 그런 다음, 가슴을 열고 허리를 세워 주세요."
//                    val contents = "밴드벤트 운동을 시작하겠습니다. 먼저, 발의 위치를 확인하겠습니다. 두 발을 어깨너비로 두세요"
                    utterTTS(contents)
                }
                tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(p0: String?) {
                        utterState = 0
                        isSpeaking = true
                    }

                    override fun onDone(p0: String?) {
                        isSpeaking = false
                        utterState = 1
                        if (pendingText != null) {
                            speakText(pendingText!!)
                            pendingText = null

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

        runnable = object : Runnable {
            override fun run() {
                if (curCount == customCount) {
                    curSet += 1
                    binding.set.text = String.format(
                        mContext!!.resources.getString(
                            R.string.train_setting_set,
                            curSet
                        )
                    )
                    binding.progressbar.progress = ((curSet.toDouble() / customSet) * 100).toInt()
                    val contents = "$curSet 세트"
                    utterTTS(contents)
                    while (utterState == 0);
                    if (curSet == customSet) {
                        handler.removeCallbacks(this)
                        val message = Message.obtain()
                        message.obj = "end"
                        sendHandler.sendMessage(message)
                        while (utterState == 0);
                        val contents = "운동이 완료되었습니다. 수고하셨습니다."
                        utterTTS(contents)
                        handler.removeCallbacks(this)
                    } else {
                        curCount = 1
                        while (utterState == 0);
                        utterTTS(curCount.toString())
                        binding.count.text = String.format(
                            mContext!!.resources.getString(
                                R.string.training_count,
                                curCount,
                                customCount
                            )
                        )
                        handler.postDelayed(this, 3000)
                    }
                } else {
                    curCount += 1
                    if (!socketList.contains(108) || !socketList.contains(114) || !socketList.contains(
                            110
                        )
                    ) {
                        while (utterState == 0);
                        utterTTS(curCount.toString())
                    }
                    if (socketList.contains(108)) socketList.remove(108)
                    else if (socketList.contains(110)) socketList.remove(110)
                    else if (socketList.contains(114)) socketList.remove(114)
                    handler.postDelayed(this, 3000)
                    binding.count.text = String.format(
                        mContext!!.resources.getString(
                            R.string.training_count,
                            curCount,
                            customCount
                        )
                    )
                }
            }
        }

        val receiveThread = ReceiveThread()
        receiveThread.start()
        val sendThread = SendThread()
        sendThread.start()

        binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
        binding.chronometer.start()

        binding.doneBtn.setOnClickListener {
            pauseTime = binding.chronometer.base - SystemClock.elapsedRealtime()
            binding.chronometer.stop()
            val recordTime = getFormattedElapsedTime(pauseTime)
            Log.d("pauseTime", pauseTime.toString())
            viewModel.setTime(recordTime.toString())
            viewModel.setDoneCount(curCount)
            viewModel.setDoneSet(curSet)
            if (curSet == customSet && curCount == customCount) {
                viewModel.setRate(endRate!!)
                viewModel.setDirection(endDirection!!)
            } else {
                viewModel.setRate(0)
                viewModel.setDirection('b')
            }
            findNavController().navigate(R.id.action_trainFragment_to_trainDoneFragment)
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
                    val buffer = ByteArray(1024 * 1024)
//                    val input = socket!!.getInputStream()
//                    input.read(buffer)
                    socket?.getInputStream()!!.read(buffer)
//                    val byteArray = inputStream.readBytes()
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
//                                        delay(5000)
                                        while (utterState == 0);
                                        utterTTS(contents)
                                        delay(500)
                                        while (utterState == 0);
                                        val message = Message.obtain()
                                        message.obj = "runstart"
                                        sendHandler.sendMessage(message)
                                        Log.d("sendMessage", "99")
//                                        delay(5000)
                                        handler.postDelayed(runnable!!, 500)
                                    }
                                }
                            }
                        }
                        100 -> {
                            val string = buffer.toString(Charsets.UTF_8)
                            Log.d("socket buffer", string)
                            endDirection = string[1]
                            viewModel.setDirection(endDirection!!)
                            val message = Message.obtain()
                            endRate = string.substring(2..3).toString().toInt()
                            viewModel.setRate(endRate!!)
                            viewModel.setDoneCount(curCount)
                            viewModel.setDoneSet(curSet)
                            Log.d("socket buffer test", "$endRate")
                            message.obj = "socketclose"
                            sendHandler.sendMessage(message)
                            socket?.close()
                            Log.d("socket res", "close")
                        }
                        101 -> Log.d("socket res", "notfound 101")
                        102 -> Log.d("socket res", "standby 102")
                        103 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val contents =
                                    "잘하셨습니다. 이제 카운트에 맞게 운동을 시작해보겠습니다. 팔꿈치를 살짝 구부린 채로 덤벨을 어깨높이까지 천천히 들어올렸다가, 내려주시면 됩니다. 어깨에 힘이 풀리지 않게 조심해주세요."
                                Log.d("socket res", "posecheckend 103")

                                if (!socketList.contains(103)) {
                                    socketList.add(103)
//                                        delay(5000)
                                    while (utterState == 0);
                                    utterTTS(contents)
                                    val message = Message.obtain()
                                    message.obj = "runstart"
                                    sendHandler.sendMessage(message)
//                                        delay(5000)
                                    handler.postDelayed(runnable!!, 500)
                                }
                            }
                            job.cancel()
                        }
                        108 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val contents = "왼쪽으로 치우쳐 있습니다."
//                                Log.d("socket res", "l")
                                if (socketList.contains(103) && !socketList.contains(
                                        108
                                    ) && !socketList.contains(110) && !socketList.contains(114)
                                ) {
                                    socketList.add(108)
                                    while (utterState == 0);
                                    utterTTS(contents)
//                                        speakText(contents)
//                                        delay(1000)
                                }
                            }
                            job.cancel()
                        }
                        110 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                if (takePhoto) {
                                    val random = Random().nextInt(6)
                                    if (random == 1) {
                                        val contents = "올바른 자세로 잘하고 계십니다."
//                                Log.d("socket res", "n")
                                        if (socketList.contains(103) && !socketList.contains(108) && !socketList.contains(
                                                110
                                            ) && !socketList.contains(114)
                                        ) {
                                            socketList.add(110)
                                            while (utterState == 0);
                                            utterTTS(contents)
                                        }
                                    }
                                } else {
                                    val img = buffer.copyOfRange(1, buffer.size)
                                    Log.d("socket buffer ", img.toString())
                                    takePhoto = true
                                    if (img != null) {
                                        val bmp: Bitmap =
                                            BitmapFactory.decodeByteArray(img, 0, img.size)
                                        withContext(Dispatchers.Main) {
                                            binding.testImg.setImageBitmap(bmp)
                                        }

                                    } else {
                                        Log.d("socket img", "null")
                                    }

                                }
                            }
                            job.cancel()
                        }
                        114 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val contents = "오른쪽으로 치우쳐 있습니다."
//                                    delay(1000)
                                if (!socketList.contains(103) && !socketList.contains(
                                        108
                                    ) && !socketList.contains(110) && !socketList.contains(114)
                                ) {
                                    socketList.add(114)
                                    utterTTS(contents)
//                                        speakText(contents)
//                                        delay(1000)
                                }
                            }
                            job.cancel()
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
            while (utterState == 0);
            Log.d("socket sendhandler", "called")
            val message1 = Message.obtain()
            message1.obj = "onearm"
            sendHandler.sendMessage(message1)
            val message2 = Message.obtain()
            message2.obj = "checkstart"
            sendHandler.sendMessage(message2)
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
        if (isSpeaking!!) {
            Log.d("pendingText", pendingText.toString())
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

    private fun getFormattedElapsedTime(elapsedMillis: Long): String {
        val totalSeconds = elapsedMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}