package com.ajou.helpt.mypage.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.dialog.LogOutDialog
import com.ajou.helpt.auth.view.dialog.QuitDialog
import com.ajou.helpt.databinding.FragmentMyPageBinding
import com.ajou.helpt.home.adapter.MyPageViewModel
import com.ajou.helpt.mypage.model.ExerciseRecord
import com.ajou.helpt.mypage.ExerciseRecordAdapter
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.EquipmentService
import com.ajou.helpt.network.api.MemberService
import com.ajou.helpt.network.api.RecordService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.lang.reflect.Member
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null

    private val dataStore = UserDataStore()
    private lateinit var accessToken: String
    private lateinit var userName: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var exerciseRecordAdapter: ExerciseRecordAdapter
    private lateinit var viewModel: MyPageViewModel

    private lateinit var logOutDialog: LogOutDialog
    private lateinit var quitDialog: QuitDialog

    private val recordService = RetrofitInstance.getInstance().create(RecordService::class.java)
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)

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
        viewModel = ViewModelProvider(requireActivity())[MyPageViewModel::class.java]
        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false)

        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            userName = dataStore.getUserName().toString()
            setupView(binding.root)
            setupRecyclerView(binding.root)
            clickCalendarDate(binding.root)
            getAttendance()
        }
        clickProfileEditButton(binding.root)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.logout.setOnClickListener {
            logOutDialog = LogOutDialog(mContext!!)
            logOutDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            logOutDialog.show()
        }

        binding.withdrawal.setOnClickListener {
            quitDialog = QuitDialog(mContext!!)
            quitDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            quitDialog.show()
        }

        viewModel.selectedItem.observe(viewLifecycleOwner, Observer {
            if (viewModel.selectedItem.value != null) {
                findNavController().navigate(R.id.action_myPageFragment_to_recordDetailFragment)
            }
        })
    }
    private fun setupView(view: View) {
        binding.textViewMyPageName2.text = userName
        binding.textViewMyPageProfileCalendarTitle.visibility = View.INVISIBLE
        binding.textViewMyPageProfileCalendarTitle2.visibility = View.INVISIBLE
    }

    private fun setupRecyclerView(view: View) {
        val link = DataSelection()
        recyclerView = binding.recyclerViewMyPageRecord
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        exerciseRecordAdapter = ExerciseRecordAdapter(requireContext(), emptyList(), link)
        recyclerView.adapter = exerciseRecordAdapter
    }

    private fun clickCalendarDate(view: View) {
        binding.calendarViewMyPage.setOnDateChangedListener { widget, date, selected ->
            val selectedDate = Calendar.getInstance().apply {
                set(date.year, date.month, date.day)
            }.time
            binding.textViewMyPageProfileCalendarTitle.visibility = View.VISIBLE
            binding.textViewMyPageProfileCalendarTitle2.visibility = View.VISIBLE

            val resourceFormattedDate = String.format(
                resources.getString(R.string.stringMyPageProfileCalendarTitle),
                date.month,
                date.day
            )
            binding.textViewMyPageProfileCalendarTitle.text = resourceFormattedDate

            CoroutineScope(Dispatchers.IO).launch {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDateCalendar = Calendar.getInstance().apply {
                    time = selectedDate
                    add(Calendar.MONTH, -1)
                }
                val adjustedDate = selectedDateCalendar.time
                val formattedDate = dateFormat.format(adjustedDate)

                val accessToken = dataStore.getAccessToken().toString()
                val memberId = arguments?.getInt("memberId")
                val getExerciseRecordDeferred = async {
                    recordService.getRecord(
                        accessToken,
                        formattedDate
                    )
                }
                Log.d(
                    "ExerciseRecord",
                    "Date: $formattedDate, Member ID: $memberId, Access Token: $accessToken, Date: $selectedDate"
                )

                val getExerciseRecordResponse = getExerciseRecordDeferred.await()

                Log.d(
                    "ExerciseRecord",
                    "Response: $getExerciseRecordResponse , ${
                        getExerciseRecordResponse.errorBody()?.string()
                    } "
                )

                if (getExerciseRecordResponse.isSuccessful) {
                    val recordBody = getExerciseRecordResponse.body()!!.data
                    Log.d("recordData","data: ${recordBody}")
                    if (recordBody.isEmpty()) {
                        withContext(Dispatchers.Main) {
                            binding.noRecordBg.visibility = View.VISIBLE
                            binding.noRecordText.visibility = View.VISIBLE
                            binding.recyclerViewMyPageRecord.visibility = View.GONE
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            binding.recyclerViewMyPageRecord.visibility = View.VISIBLE
                            exerciseRecordAdapter.submitList(recordBody)
                            binding.noRecordBg.visibility = View.GONE
                            binding.noRecordText.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun clickProfileEditButton(view: View) {
        binding.imageViewMyPageProfile.setOnClickListener {
            binding.imageViewMyPageProfile.alpha = 0.5f
            binding.imageViewMyPageProfile.postDelayed({
                binding.imageViewMyPageProfile.alpha = 1f
            }, 100)
            findNavController().navigate(R.id.action_myPageFragment_to_ProfileFragment)
        }
    }

    private fun calculateDuration(startTime: String, endTime: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val startDate = dateFormat.parse(startTime)
        val endDate = dateFormat.parse(endTime)

        val durationMillis = endDate.time - startDate.time
        val seconds = (durationMillis / 1000) % 60
        val minutes = (durationMillis / (1000 * 60)) % 60
        val hours = (durationMillis / (1000 * 60 * 60)) % 24

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    private fun getAttendance(){
        CoroutineScope(Dispatchers.IO).launch {
            val attendanceDeferred = async { memberService.getMyAttendance(accessToken) }
            val attendanceResponse = attendanceDeferred.await()
            if (attendanceResponse.isSuccessful) {
                val body = JSONObject(attendanceResponse.body()?.string()).getString("data").toString()
                Log.d("attendanceResponse success","$body")
            }else{
                Log.d("attendanceResponse fail",attendanceResponse.errorBody()?.string().toString())
            }
        }
    }

    inner class DataSelection {
        fun getSelectedItem(data: ExerciseRecord) {
            Log.d("data", data.toString())
            viewModel.setSelectedItem(data)
        }
    }
}
