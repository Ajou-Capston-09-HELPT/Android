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
    private var mContext : Context?= null
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
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val link = AdapterToFragment()
        adapter = SearchGymRVAdapter(mContext!!, emptyList(), link)

        binding.gym.setOnEditorActionListener { view, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_SEARCH){
                binding.gymRv.adapter = adapter
                binding.gymRv.layoutManager = LinearLayoutManager(mContext)
                searchGymCallApi(binding.gym.text.toString())
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(requireActivity().window.decorView.applicationWindowToken, 0)
                binding.loadingBar.show()
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }

        binding.removeBtn.setOnClickListener {
            binding.gym.setText("")

        }
    }

    private fun searchGymCallApi(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val searchDeferred = async { gymService.searchGyms(accessToken!!, keyword) }
            val searchResponse = searchDeferred.await()
            if (searchResponse.isSuccessful) {
                if (searchResponse.body()?.data?.size != 0) {
                    withContext(Dispatchers.Main) {
                        binding.loadingBar.hide()
                        adapter.updateList(searchResponse.body()?.data!!)
                    }
                }else{
                    Log.d("data none","none")
                }

            } else {
                val tokenDeferred = async { memberService.getNewToken(refreshToken!!) }
                val tokenResponse = tokenDeferred.await()
                if (tokenResponse.isSuccessful) {
                    val tokenBody = JSONObject(tokenResponse.body()?.string())
                    Log.d("tokenBody",tokenBody.toString())
                    val newAccessToken = "Bearer " + tokenBody.getJSONObject("data").getString("accessToken").toString()
                    val newRefreshToken = "Bearer " + tokenBody.getJSONObject("data").getString("refreshToken").toString()
                    dataStore.saveAccessToken(newAccessToken)
                    dataStore.saveRefreshToken(newRefreshToken)
                    val reSearchDeferred = async { gymService.searchGyms(newAccessToken, keyword) }
                    val reSearchResponse = reSearchDeferred.await()
                    if (reSearchResponse.isSuccessful) {
                        Log.d("reeeesearchResponse", reSearchResponse.toString())
                    }else{
                        Log.d("reeeesearchResponse", reSearchResponse.errorBody()?.string().toString())
                    }
                } else{
                    Log.d("tokenBody fail", tokenResponse.errorBody()?.string().toString())
                }
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
                        Log.d("equipResponse",equipResponse.body().toString())
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