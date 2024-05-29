package com.ajou.helpt.home.view.fragment

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
import com.ajou.helpt.home.model.NoticeData
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentNoticeBinding
import com.ajou.helpt.home.HomeInfoViewModel
import com.ajou.helpt.home.adapter.NoticeAdapter
import com.ajou.helpt.home.model.Gym
import com.ajou.helpt.home.model.GymRegisteredInfo
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.NoticeService
import kotlinx.coroutines.*
import org.json.JSONObject

class NoticeFragment : Fragment() {
    private var _binding: FragmentNoticeBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val noticeService = RetrofitInstance.getInstance().create(NoticeService::class.java)
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private var gymId: Int? = null
    private val dataStore = UserDataStore()
    private lateinit var adapter: NoticeAdapter
    private lateinit var viewModel: HomeInfoViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val link = AdapterToFragment()
        _binding = FragmentNoticeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeInfoViewModel::class.java]
        adapter = NoticeAdapter(mContext!!, emptyList(), link)
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken()
            refreshToken = dataStore.getRefreshToken()
            gymId = dataStore.getGymId()
            binding.noticeRv.adapter = adapter
            binding.noticeRv.layoutManager = LinearLayoutManager(mContext)
            getNoticesCallApi()
            binding.loadingBar.show()

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun getNoticesCallApi() {
        CoroutineScope(Dispatchers.IO).launch {
            val noticeDeferred = async { noticeService.getNotices(accessToken!!, gymId!!) }
            val noticeResponse = noticeDeferred.await()
            if (noticeResponse.isSuccessful) {
                if (noticeResponse.body()?.data?.size != 0) {
                    withContext(Dispatchers.Main) {
                        binding.loadingBar.hide()
                        adapter.updateList(noticeResponse.body()?.data!!)
                    }
                } else {
                    Log.d("data none", "none")
                }
            }
        }

    }

    inner class AdapterToFragment {
        fun getSelectedItem(data: NoticeData) {
            viewModel.setNotice(data)
            findNavController().navigate(R.id.action_noticeFragment_to_noticeDetailFragment)
        }
    }
}
