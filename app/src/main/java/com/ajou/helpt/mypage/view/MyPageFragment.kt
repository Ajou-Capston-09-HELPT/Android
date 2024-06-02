package com.ajou.helpt.mypage.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentMyPageBinding
import com.ajou.helpt.home.adapter.MyPageViewModel
import com.ajou.helpt.mypage.ExerciseRecord
import com.ajou.helpt.mypage.ExerciseRecordAdapter
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.EquipmentService
import com.ajou.helpt.network.api.RecordService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
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
    private lateinit var viewModel : MyPageViewModel

    private val recordService = RetrofitInstance.getInstance().create(RecordService::class.java)
    private val equipmentService = RetrofitInstance.getInstance().create(EquipmentService::class.java)

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
        }
        clickProfileEditButton(binding.root)
        return binding.root
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
            Log.d("FormattedDate", "Formatted date string: $resourceFormattedDate")
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

                Log.d("ExerciseRecord", "Response: $getExerciseRecordResponse , ${getExerciseRecordResponse.errorBody()?.string()} ")

                if (getExerciseRecordResponse.isSuccessful) {
                    val exerciseRecordResponse = JSONObject(getExerciseRecordResponse.body()?.string())

                    Log.d("ExerciseRecord", "Exercise Record Response: $exerciseRecordResponse")
                    if (!exerciseRecordResponse.isNull("data")) {
                        if (exerciseRecordResponse.getJSONArray("data").length() == 0){
                            withContext(Dispatchers.Main) {
                                binding.noRecordBg.visibility = View.VISIBLE
                                binding.noRecordText.visibility = View.VISIBLE
                                binding.recyclerViewMyPageRecord.visibility = View.GONE
                            }
                        }else {
                            val jsonArray = exerciseRecordResponse.getJSONArray("data")
                            val exerciseRecords = if (jsonArray != null) {
                                List(jsonArray.length()) { i ->
                                    val jsonObject = jsonArray.getJSONObject(i)
                                    val equipmentResponse = equipmentService.getEquipment(accessToken, jsonObject.getInt("equipmentId"))
                                    val equipmentResponseBody = equipmentResponse.body()?.string()
                                    val equipmentName = if (equipmentResponseBody != null) {
                                        val equipmentJSONObject = JSONObject(equipmentResponseBody)
                                        Log.d("ExerciseRecord2", "Equipment Response: $equipmentJSONObject")
                                        equipmentJSONObject.getJSONObject("data").getString("equipmentName")
                                    } else {
                                        "장비 이름 없음"
                                    }

                                    val startTime = jsonObject.getString("startTime")
                                    val endTime = jsonObject.getString("endTime")
                                    val duration = calculateDuration(startTime, endTime)

//                                    ExerciseRecord(
//                                        equipmentName,
//                                        jsonObject.getInt("count"),
//                                        jsonObject.getInt("setNumber"),
//                                        duration
//                                    )
                                }
                            } else {
                                emptyList()
                            }
                            withContext(Dispatchers.Main) {
                                binding.recyclerViewMyPageRecord.visibility = View.VISIBLE
//                                exerciseRecordAdapter.submitList(exerciseRecords)
                                binding.noRecordBg.visibility = View.GONE
                                binding.noRecordText.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.d("ExerciseRecord", "No data in response")
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

    inner class DataSelection {
        fun getSelectedItem(data: ExerciseRecord) {
            Log.d("data", data.toString())
            viewModel.setSelectedItem(data)
        }
    }
}
