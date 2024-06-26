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
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentTrainBinding
import com.ajou.helpt.network.PiRetrofitInstance
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.PiService
import com.ajou.helpt.network.api.RecordService
import com.ajou.helpt.network.model.ExercisePosting
import com.ajou.helpt.train.TrainInfoViewModel
import com.ajou.helpt.train.adapter.TrainingViewPagerAdapter
import kotlinx.coroutines.*
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.Socket
import java.net.SocketException
import java.text.SimpleDateFormat
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
    private var endRate: Int? = 0
    private var endDirection: Char? = null
    private var sendThread : Thread? = null
    private var receiveThread : Thread? = null
    private var exerciseId : Int? = -1
    private var comment: String? = null
    private var standByList = mutableListOf<Int>()
    private val dataStore = UserDataStore()
    private val recordService = RetrofitInstance.getInstance().create(RecordService::class.java)

    private val piService = PiRetrofitInstance.getInstance().create(PiService::class.java)


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
        binding.engName.text = viewModel.train.value!!.equipmentNameEng

        return binding.root
    }


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
                    var contents = ""
                    when (viewModel.train.value!!.equipmentName) {
                        "밴드 벤트 오버 로우" -> {
                            exerciseId = 0
                            contents = "밴드벤트 운동을 시작하겠습니다. 먼저, 발의 위치를 확인하겠습니다. 두 발을 어깨너비로 두세요"
//                            utterTTS(contents)
                            // TODO utterTTS(contents)를 when 밖으로 빼도 정상작동하는지 확인해보기
                        }
                        "덤벨 프론트 레이즈" -> {
                            exerciseId = 1
                            contents =
                                "덤벨 프론트 레이즈 운동을 시작하겠습니다. 양손에 덤벨을 들고 어깨너비로 서주세요. 그런 다음, 가슴을 열고 허리를 세워 주세요."
//                            utterTTS(contents)
                        }
                        "원 암 덤벨 레터럴 레이즈" -> {
                            exerciseId = 2
                            contents =
                                "원 암 덤벨 레터럴 레이즈 운동을 시작하겠습니다. 한 손에 덤벨을 들고 어깨너비로 서주세요. 그런 다음, 가슴을 열고 허리를 세워 주세요."
//                            utterTTS(contents)
                        }
                    }
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
                        utterState = 1
                        shutDownTTS()
                    }
                })
            }
        })

        val handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                if (curCount == customCount) {
                    Log.d("socket test count","$curCount $customCount")
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
                        Log.d("socket test set","$curSet $customSet")
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
                    if (!socketList.contains(108) || !socketList.contains(114) || !socketList.contains(110)) {
                        while (utterState == 0);
                        utterTTS(curCount.toString())
                    }
                    if (socketList.contains(108)) socketList.remove(108)
                    else if (socketList.contains(110)) socketList.remove(110)
                    else if (socketList.contains(114)) socketList.remove(114)
                    handler.postDelayed(this, 3000)
                    binding.count.text = String.format(mContext!!.resources.getString(
                            R.string.training_count,
                            curCount,
                            customCount
                        ))
                }
            }
        }

        receiveThread = ReceiveThread()
        receiveThread!!.start()
        sendThread = SendThread()
        sendThread!!.start()

        binding.chronometer.base = SystemClock.elapsedRealtime() + pauseTime
        binding.chronometer.start()

        binding.doneBtn.setOnClickListener {
            pauseTime = SystemClock.elapsedRealtime() - binding.chronometer.base
            val recordTime = getFormattedElapsedTime(pauseTime)
            binding.chronometer.stop()
            handler.removeCallbacks(runnable!!)
            sendThread!!.interrupt()
            receiveThread!!.interrupt()
            CoroutineScope(Dispatchers.IO).launch {
                val accessToken = dataStore.getAccessToken()
                val imgDeferred = async { piService.getImg() }
                val imgResponse = imgDeferred.await()
                if (imgResponse.isSuccessful) {
                    val imgBody = imgResponse.body()
                    val imgByte = imgBody?.byteStream()
                    val imgBitmap = BitmapFactory.decodeStream(imgByte)
                    if (viewModel.rate.value!! < 50) {
                        comment = "자세 정확도가 낮습니다. 자세 정확도에 유의해주세요"
                    } else if(viewModel.rate.value!! > 85) {
                        comment = "올바른 자세로 운동을 잘해내고 있습니다!"
                    } else if(viewModel.direction.value == 'l'){
                        comment = "자세가 왼쪽으로 치우쳐진 경향이 있습니다."
                    } else if (viewModel.direction.value == 'r'){
                        comment = "자세가 오른쪽으로 치우쳐진 경향이 있습니다."
                    } else{
                        comment = "한쪽으로 치우치지 않고 잘하고 있습니다."
                    }

                    val data = ExercisePosting(
                        viewModel.train.value!!.gymEquipmentId,
                        curCount,
                        curSet,
                        viewModel.train.value!!.customWeight,
                        recordTime,
                        endRate!!,
                        comment!!, null
                    )
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    imgBitmap.compress(Bitmap.CompressFormat.PNG, 20, byteArrayOutputStream)
                    val requestBody: RequestBody = RequestBody.create(
                        "image/*".toMediaTypeOrNull(),
                        byteArrayOutputStream.toByteArray()
                    )
                    val fileName =
                        "${getString(R.string.app_name)}_${SimpleDateFormat("MMddHHmm").format(Date())}.png"
                    val img: MultipartBody.Part =
                        MultipartBody.Part.createFormData("snapshotFile", fileName, requestBody)

                    val postRecordDeferred = async { recordService.postRecord(accessToken!!,data, img) }
                    val postRecordResponse = postRecordDeferred.await()
                    val message = Message.obtain()
                    message.obj = "socketclose"
                    sendHandler.sendMessage(message)
                    if (postRecordResponse.isSuccessful) {

                        withContext(Dispatchers.Main) {
                            viewModel.setTime(recordTime)
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
                    } else {
                        Log.d("postRecordResponse fail",postRecordResponse.errorBody()?.string().toString())
                    }
                } else {
                    Log.d("imgResponse fail", imgResponse.errorBody()?.string().toString())
                }
            }
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
                    val buffer = ByteArray(5)
                    socket?.getInputStream()!!.read(buffer)

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
                                        while (utterState == 0);
                                        utterTTS(contents)
                                        delay(500)
                                        while (utterState == 0);
                                        val message = Message.obtain()
                                        message.obj = "runstart"
                                        sendHandler.sendMessage(message)
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
                            endRate = string.substring(2..3).toString().toInt()
                            viewModel.setRate(endRate!!)
                            viewModel.setDoneCount(curCount)
                            viewModel.setDoneSet(curSet)

                            Log.d("socket res", "close")
                        }
                        101 -> Log.d("socket res", "notfound 101")
                        102 -> {
                            standByList.add(1)
                            if (standByList.size == 12) {
                                standByList = mutableListOf()
                                val contents = "자세를 확인해주세요"
                                utterTTS(contents)
                            }
                            Log.d("socket res", "standby 102")
                        }
                        103 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val contents =
                                    "잘하셨습니다. 이제 카운트에 맞게 운동을 시작해보겠습니다. 팔꿈치를 살짝 구부린 채로 덤벨을 어깨높이까지 천천히 들어올렸다가, 내려주시면 됩니다. 어깨에 힘이 풀리지 않게 조심해주세요."
                                Log.d("socket res", "posecheckend 103")

                                if (!socketList.contains(103)) {
                                    socketList.add(103)
                                    while (utterState == 0);
                                    utterTTS(contents)
                                    val message = Message.obtain()
                                    message.obj = "runstart"
                                    sendHandler.sendMessage(message)
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
                                }
                            }
                            job.cancel()
                        }
                        110 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val random = Random().nextInt(4)
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
                            }
                            job.cancel()
                        }
                        114 -> {
                            val job = CoroutineScope(Dispatchers.IO).launch {
                                val contents = "오른쪽으로 치우쳐 있습니다."
                                if (!socketList.contains(103) && !socketList.contains(
                                        108
                                    ) && !socketList.contains(110) && !socketList.contains(114)
                                ) {
                                    socketList.add(114)
                                    utterTTS(contents)
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
        if (!sendThread!!.isInterrupted) {
            sendThread!!.interrupt()
        }
        if (!receiveThread!!.isInterrupted) {
            receiveThread!!.interrupt()
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

    private fun getFormattedElapsedTime(elapsedMillis: Long): String {
        val totalSeconds = elapsedMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}