package com.ajou.helpt.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.home.model.GymRegisteredInfo
import com.ajou.helpt.home.model.Membership
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.MemberShipService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeInfoViewModel : ViewModel() {
    private val membershipService = RetrofitInstance.getInstance().create(MemberShipService::class.java)
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private val dataStore = UserDataStore()
    private var accessToken : String? = null
    var isError = MutableLiveData<Boolean>(false)

    private val _membership = MutableLiveData<Membership>()
    val membership : LiveData<Membership>
        get() = _membership

    private val _gymRegistered = MutableLiveData<GymRegisteredInfo>()
    val gymRegistered : LiveData<GymRegisteredInfo>
        get() = _gymRegistered

    private val _hasTicket = MutableLiveData<Boolean>()
    val hasTicket : LiveData<Boolean>
        get() = _hasTicket

    fun setMembership(data: Membership) {
        _membership.postValue(data)
    }

    fun setGymRegistered(data: GymRegisteredInfo) {
        _gymRegistered.postValue(data)
    }

    init {
        viewModelScope.launch {
            Log.d("viewmodel","checked")
            accessToken = dataStore.getAccessToken().toString()
            val membershipDeferred = async { membershipService.getMembershipDetail(accessToken!!) }
            val membershipResponse = membershipDeferred.await()

            if (membershipResponse.isSuccessful) {
                Log.d("viewmodel membership",membershipResponse.body()?.data.toString())
                if (membershipResponse.body()?.data == null) _hasTicket.value = false
                else {
//                    setMembershipData(membershipResponse.body()!!.data)
                    _membership.value = membershipResponse.body()!!.data
                   _hasTicket.value = true
                }
                Log.d("viewmodel hasticket",hasTicket.value.toString())
                if (_hasTicket.value == true){
                    val gymRegisteredDeferred = async { gymService.getOneGym(accessToken!!, _membership.value!!.gymId) }
                    val gymRegisteredResponse = gymRegisteredDeferred.await()
                    if (gymRegisteredResponse.isSuccessful) {
                        setGymRegistered(gymRegisteredResponse.body()!!.data)

                    }
                }
                // viewmodel로 데이터 담아서 담아오는 속도 빠르게 하기
            }else{
                Log.d("membership",membershipResponse.errorBody()?.string().toString())
            }
        }
    }
    fun setHasTicket(data:Boolean){
        _hasTicket.postValue(data)
    }
    fun setRegisterValue(data:GymRegisteredInfo){
        _gymRegistered.postValue(data)
    }

    fun setMembershipData(data:Membership) {
        _membership.postValue(data)
    }

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        Log.d("exceptionHandler",exception.message.toString())
        isError.postValue(true) // isError를 추적하다가 isError일 경우 dialog 뷰에서 띄우기
    }
}