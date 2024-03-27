package com.ajou.helpt.home.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.helpt.databinding.FragmentSearchGymBinding
import com.ajou.helpt.home.adapter.SearchGymRVAdapter
import com.ajou.helpt.home.model.Gym

class SearchGymFragment : Fragment() {
    private var _binding : FragmentSearchGymBinding ?= null
    private val binding get() = _binding!!
    private var mContext : Context?= null

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
        _binding = FragmentSearchGymBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val equipList = listOf<String>("덤벨 숄더 프레스","랫 풀 다운","덤벨 숄더 프레스","랫 풀 다운","덤벨 숄더 프레스","랫 풀 다운")
        val dataList = listOf(Gym("헬스장1","경기도 수원시 영통구 영통로 127 302호",equipList),Gym("헬스장2","경기도 수원시 영통구 영통로 127 302호",equipList),Gym("헬스장3","경기도 수원시 영통구 영통로 127 302호",equipList))
        val link = AdapterToFragment()
        var adapter : SearchGymRVAdapter = SearchGymRVAdapter(mContext!!, dataList, link) // TODO 서버 연결 후에는 listOf()로 변경 후, 통신 이후에 list를 채워주는 방식으로 LNG
        var gymName : String = ""

//        binding.gymRv.adapter = adapter
//        binding.gymRv.layoutManager = LinearLayoutManager(mContext)

        binding.searchBtn.setOnClickListener {
            gymName = binding.gym.text.toString()
            // 추후에 get 통신 추가
            binding.gymRv.adapter = adapter
            binding.gymRv.layoutManager = LinearLayoutManager(mContext) // 현재는 테스트 위해서 이 안에 배치이나 이후 변경 예정
        }

        binding.gym.setOnEditorActionListener { view, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE){
                gymName = binding.gym.text.toString()
                binding.gymRv.adapter = adapter
                binding.gymRv.layoutManager = LinearLayoutManager(mContext) // 위와 동일
                return@setOnEditorActionListener true
            }else return@setOnEditorActionListener false
        }

    }

    inner class AdapterToFragment {
        fun getSelectedItem(data : Gym) {
            Log.d("선택된 데이터", data.toString())
            val action =
                SearchGymFragmentDirections.actionSearchGymFragmentToGymDetailInfoFragment(data)
            findNavController().navigate(action)
        }
    }
}