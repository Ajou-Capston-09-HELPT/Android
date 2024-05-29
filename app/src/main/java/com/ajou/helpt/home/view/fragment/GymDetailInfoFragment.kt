package com.ajou.helpt.home.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.helpt.BuildConfig
import com.ajou.helpt.R
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentGymDetailInfoBinding
import com.ajou.helpt.home.adapter.GymDetailInfoRVAdapter
import com.ajou.helpt.home.adapter.PayInfoRVAdapter
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymAdmissionService
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.PaymentService
import com.ajou.helpt.setOnSingleClickListener
import com.skt.Tmap.TMapView
import kotlinx.coroutines.*
import org.json.JSONObject

class GymDetailInfoFragment : Fragment() {
    private var _binding: FragmentGymDetailInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private var tmapView: TMapView? = null
    private val dataStore = UserDataStore()
    private val args: GymDetailInfoFragmentArgs by navArgs()
    private val gymAdmissionService =
        RetrofitInstance.getInstance().create(GymAdmissionService::class.java)
    private val paymentService = RetrofitInstance.getInstance().create(PaymentService::class.java)
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private var accessToken: String? = null
    private var productId : Int? = null
    private var link : String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initMap(
            args.item.gymRegisteredInfo.address.longitude,
            args.item.gymRegisteredInfo.address.latitude
        )
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken().toString()
            val linkDeferred = async { gymService.getGymChatLink(accessToken!!, args.item.gymRegisteredInfo.gymId) }
            val linkResponse = linkDeferred.await()
            if (linkResponse.isSuccessful) {
                link = JSONObject(linkResponse.body()?.string()).getJSONObject("data").getString("chatLink")
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGymDetailInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = args.item


        binding.gymName.text = item.gymRegisteredInfo.gymName
        binding.address.text = item.gymRegisteredInfo.address.fullAddress
        binding.equipRV.adapter = GymDetailInfoRVAdapter(mContext!!, item.equipList)
        binding.equipRV.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)

        binding.mapContainer.addView(tmapView)
        val link = AdapterToFragment()
        binding.payRV.adapter = PayInfoRVAdapter(mContext!!, item.gymProduct, null, link)
        binding.payRV.layoutManager = LinearLayoutManager(mContext)

        binding.chatLink.setOnClickListener {
            openChatLink()

        }

        binding.linkBtn.setOnSingleClickListener {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("센터 연동 신청")
            builder.setMessage("연동 신청하시겠습니까?\n\n연동 신청하면 회원님의 정보가 센터에 전달됩니다.")
            builder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(mContext, "연동 취소", Toast.LENGTH_SHORT).show()
            })
            builder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                Toast.makeText(mContext, "연동 신청 완료", Toast.LENGTH_SHORT).show()
                postLink()
            })
            builder.create()
            builder.show()
        }

        binding.registBtn.setOnSingleClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("payment",productId.toString())
                val paymentDeferred = async { paymentService.postPayment(accessToken!!, productId!!) }
                val paymentResponse = paymentDeferred.await()
                if (paymentResponse.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        val body = JSONObject(paymentResponse.body()?.string())
                        val url = body.getJSONObject("data").getString("next_redirect_mobile_url").toString()
                        Log.d("body",body.toString())
                        val action = GymDetailInfoFragmentDirections.actionGymDetailInfoFragmentToKakaoPayWebViewFragment(url)
                        findNavController().navigate(action)
                    }
                } else {
                    Log.d("paymentResponse faill", paymentResponse.errorBody()?.string().toString())
                }
            }
        }
    }

    private fun initMap(longitude: String, latitude: String) {
        tmapView = TMapView(mContext!!)
        tmapView!!.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tmapView!!.zoomLevel = 15
        tmapView!!.setLocationPoint(longitude.toDouble(), latitude.toDouble()) // icon으로 표시할 위치 설정
        tmapView!!.setCenterPoint(longitude.toDouble(), latitude.toDouble()) // 지도의 중심 포인트 위치 설정

        val icon: Bitmap? = ContextCompat.getDrawable(mContext!!, R.drawable.home_logo_filled)?.toBitmap()
        tmapView!!.setIcon(icon)
        tmapView!!.setIconVisibility(true) // 현재 위치 표시 Icon은 추후에 변경 가능
    }

    private fun postLink() {
        CoroutineScope(Dispatchers.IO).launch {
            val linkDeferred = async {
                gymAdmissionService.postAdmission(
                    accessToken!!,
                    args.item.gymRegisteredInfo.gymId
                )
            }
            val linkResponse = linkDeferred.await()
            if (linkResponse.isSuccessful) {
                Log.d("linkResponse ", linkResponse.body().toString())
            } else {
                Log.d("linkResponse faill", linkResponse.errorBody()?.string().toString())
            }
        }
    }

    inner class AdapterToFragment {
        fun getSelectedItem(id: Int) { // productId
            productId = id
            binding.registBtn.isEnabled = true
        }
    }

    private fun openChatLink(){
        CoroutineScope(Dispatchers.IO).launch {
            val chatLinkDeferred = async { gymService.getGymChatLink(accessToken!!, args.item.gymRegisteredInfo.gymId) }
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