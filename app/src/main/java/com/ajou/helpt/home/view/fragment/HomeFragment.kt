package com.ajou.helpt.home.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.auth.view.dialog.LogOutDialog
import com.ajou.helpt.auth.view.dialog.QuitDialog
import com.ajou.helpt.databinding.FragmentHomeBinding
import com.ajou.helpt.home.HomeInfoViewModel
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.MemberService
import com.ajou.helpt.network.api.MemberShipService
import kotlinx.coroutines.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDate
import kotlin.math.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private val dataStore = UserDataStore()
    private var accessToken: String? = null
    private var refreshToken: String? = null
    private lateinit var logOutDialog: LogOutDialog
    private lateinit var quitDialog: QuitDialog
    private val membershipService =
        RetrofitInstance.getInstance().create(MemberShipService::class.java)
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private lateinit var viewModel: HomeInfoViewModel
    private var name: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CoroutineScope(Dispatchers.IO).launch {
            name = dataStore.getUserName().toString()
            accessToken = dataStore.getAccessToken()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[HomeInfoViewModel::class.java]

        viewModel.hasTicket.observe(viewLifecycleOwner, Observer {
            if (viewModel.hasTicket.value == false) {
                binding.greetMsg.text =
                    String.format(mContext!!.resources.getString(R.string.home_greet_no, name))
                binding.nonTicketImg.visibility = View.VISIBLE
                binding.nonTicketText.visibility = View.VISIBLE
                binding.nonticketBack.visibility = View.VISIBLE
                binding.nonTicketTitle.visibility = View.VISIBLE
            } else {
                binding.ticketBack.visibility = View.VISIBLE
                binding.ticketImg.visibility = View.VISIBLE
                binding.ticketTitle.visibility = View.VISIBLE
                binding.greetMsg.text =
                    String.format(mContext!!.resources.getString(R.string.home_greet, name))
                binding.ticket.visibility = View.VISIBLE
            }
        })
        viewModel.gymRegistered.observe(viewLifecycleOwner, Observer {
            if (viewModel.membership.value != null) {
                Log.d("viewModel",viewModel.gymRegistered.value.toString())
                var sf = SimpleDateFormat("yyyy-MM-dd")
                val today = sf.parse("${LocalDate.now().year}-${LocalDate.now().monthValue}-${LocalDate.now().dayOfMonth}")
                var startDate = sf.parse(viewModel.membership.value!!.startDate)
                var endDate = sf.parse(viewModel.membership.value!!.endDate)
                var calDate =
                    "${ceil(((((endDate.time - startDate.time) / (60 * 60 * 24 * 1000))).toDouble() / 30)).toInt()}개월 회원권"
                val period =
                    "${viewModel.membership.value!!.startDate} ~ ${viewModel.membership.value!!.endDate}"
                val attendDate = "출석 ${viewModel.membership.value!!.attendanceDate}일"
                val remainDate =
                    "${(((endDate.time - today.time) / (60 * 60 * 24 * 1000)))}일 남음"
                binding.period.text = period
                binding.ticketName.text = calDate
                binding.gymName.text = viewModel.gymRegistered.value!!.gymName
                binding.attend.text = attendDate
                binding.remain.text = remainDate
                binding.mainNotice.text = "'${viewModel.gymRegistered.value!!.gymName}'의 공지사항을 확인해주세요"
            }else{
                binding.nonTicketTitle.visibility = View.VISIBLE
                binding.nonticketBack.visibility = View.VISIBLE
                binding.nonTicketText.visibility =View.VISIBLE
                binding.nonTicketImg.visibility = View.VISIBLE
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mainNotice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
        }
        binding.gym.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchGymFragment)
        }
        binding.searchBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchGymFragment)
        }
        binding.chatLink.setOnClickListener {
            openChatLink()
        }
        binding.ticket.setOnClickListener {
            val dialog = QRCreateDialogFragment()
            dialog.show(childFragmentManager, "QRCreateDialog")
        }

        binding.mainNotice.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_noticeFragment)
        }
    }

    private fun openChatLink(){
        CoroutineScope(Dispatchers.IO).launch {
            val chatLinkDeferred = async { gymService.getGymChatLink(accessToken!!, viewModel.gymRegistered.value!!.gymId) }
            val chatLinkResponse = chatLinkDeferred.await()
            if (chatLinkResponse.isSuccessful) {
                val url = JSONObject(chatLinkResponse.body()?.string()).getJSONObject("data").getString("chatLink").toString()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }else{
                Log.d("chatLinkResponse",chatLinkResponse.errorBody()?.string().toString())
            }
        }
    }
}