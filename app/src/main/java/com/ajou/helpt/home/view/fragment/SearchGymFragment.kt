package com.ajou.helpt.home.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.databinding.FragmentSearchGymBinding
import com.ajou.helpt.home.adapter.SearchGymRVAdapter
import com.ajou.helpt.home.model.Gym
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.MemberService
import com.ajou.helpt.network.api.ProductService
import com.ajou.helpt.home.model.GymRegisteredInfo
import kotlinx.coroutines.*
import org.json.JSONObject

class SearchGymFragment : Fragment() {
    private var _binding : FragmentSearchGymBinding? = null
    private val binding get() = _binding!!
    private var mContext : Context? = null
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private val memberService = RetrofitInstance.getInstance().create(MemberService::class.java)
    private val productService = RetrofitInstance.getInstance().create(ProductService::class.java)
    private var accessToken : String? = null
    private var refreshToken: String? = null
    private val dataStore = UserDataStore()
    private lateinit var adapter : SearchGymRVAdapter

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
        CoroutineScope(Dispatchers.IO).launch {
            accessToken = dataStore.getAccessToken()
            refreshToken = dataStore.getRefreshToken()
            searchGymCallApi(null)
            binding.loadingBar.show()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val link = AdapterToFragment()
        adapter = SearchGymRVAdapter(mContext!!, emptyList(), link)
        binding.gymRv.adapter = adapter
        binding.gymRv.layoutManager = LinearLayoutManager(mContext)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.gym.setOnEditorActionListener { view, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_SEARCH){
                binding.loadingBar.show()
                searchGymCallApi(binding.gym.text.toString())
//                adapter.filterList(binding.gym.text.toString())
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().window.decorView.applicationWindowToken, 0)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.removeBtn.setOnClickListener {
            binding.gym.setText("")
        }
    }

    private fun searchGymCallApi(keyword: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchDeferred = async { gymService.searchGyms(accessToken!!, keyword) }
            val searchResponse = searchDeferred.await()
            if (searchResponse.isSuccessful) {
                val body = searchResponse.body()?.data
                withContext(Dispatchers.Main) {
                    if (body?.size != 0) {
                        binding.loadingBar.hide()
                        adapter.updateList(body!!)
                    } else {
                        binding.loadingBar.hide()
                        adapter.updateList(emptyList())
                    }
                }
            } else {
                Log.d("searchResponse fail", searchResponse.errorBody()?.string().toString())
            }
        }

    }

    inner class AdapterToFragment {
        fun getSelectedItem(data : GymRegisteredInfo) {
            CoroutineScope(Dispatchers.IO).launch {
                val equipDeferred = async { gymService.getGymEquips(accessToken!!,data.gymId) }
                val equipResponse = equipDeferred.await()
                val productDeferred = async { productService.getGymProducts(accessToken!!,data.gymId)}
                val productResponse = productDeferred.await()
                if (equipResponse.isSuccessful && productResponse.isSuccessful) {
                    withContext(Dispatchers.Main){
                        val gymInfo = Gym(data, equipResponse.body()?.data!!, productResponse.body()?.data!!)
                        val action = SearchGymFragmentDirections.actionSearchGymFragmentToGymDetailInfoFragment(gymInfo)
                        findNavController().navigate(action)
                    }
                }else{
                    Log.d("equipResponse faill",equipResponse.errorBody()?.string().toString())
                    Log.d("productResponse faill",productResponse.errorBody()?.string().toString())
                }
            }
        }
    }
}