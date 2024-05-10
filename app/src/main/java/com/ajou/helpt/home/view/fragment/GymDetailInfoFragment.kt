package com.ajou.helpt.home.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.helpt.BuildConfig
import com.ajou.helpt.databinding.FragmentGymDetailInfoBinding
import com.ajou.helpt.home.adapter.GymDetailInfoRVAdapter
import com.ajou.helpt.home.adapter.PayInfoRVAdapter
import com.ajou.helpt.home.model.PayInfo
import com.skt.Tmap.TMapView


class GymDetailInfoFragment : Fragment() {
    private var _binding: FragmentGymDetailInfoBinding? = null
    private val binding get() = _binding!!
    private var mContext: Context? = null
    private var tmapView: TMapView? = null

    private val args: GymDetailInfoFragmentArgs by navArgs()

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

        binding.payRV.adapter = PayInfoRVAdapter(mContext!!,item.gymProduct)
        binding.payRV.layoutManager = LinearLayoutManager(mContext)

    }

    private fun initMap(longitude: String, latitude: String) {
        tmapView = TMapView(mContext!!)
        tmapView!!.setSKTMapApiKey(BuildConfig.TMAP_API_KEY)
        tmapView!!.zoomLevel = 15
        tmapView!!.setLocationPoint(longitude.toDouble(), latitude.toDouble()) // icon으로 표시할 위치 설정
        tmapView!!.setCenterPoint(longitude.toDouble(), latitude.toDouble()) // 지도의 중심 포인트 위치 설정
//        tmapView!!.setIconVisibility(true) // 현재 위치 표시 Icon은 추후에 변경 가능
    }
}