package com.ajou.helpt.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ajou.helpt.UserDataStore
import com.ajou.helpt.home.adapter.NoticeAdapter
import com.ajou.helpt.home.model.GymRegisteredInfo
import com.ajou.helpt.home.model.Membership
import com.ajou.helpt.home.model.NoticeData
import com.ajou.helpt.network.RetrofitInstance
import com.ajou.helpt.network.api.GymService
import com.ajou.helpt.network.api.MemberShipService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class HomeInfoViewModel : ViewModel() {
    private val membershipService =
        RetrofitInstance.getInstance().create(MemberShipService::class.java)
    private val gymService = RetrofitInstance.getInstance().create(GymService::class.java)
    private val dataStore = UserDataStore()
    private var accessToken: String? = null
    var isError = MutableLiveData<Boolean>(false)

    private val _membership = MutableLiveData<Membership>()
    val membership: LiveData<Membership>
        get() = _membership

    private val _gymRegistered = MutableLiveData<GymRegisteredInfo>()
    val gymRegistered: LiveData<GymRegisteredInfo>
        get() = _gymRegistered

    private val _hasTicket = MutableLiveData<Boolean>()
    val hasTicket: LiveData<Boolean>
        get() = _hasTicket

    private val _notice = MutableLiveData<NoticeData>()
    val notice: LiveData<NoticeData>
        get() = _notice

    fun setMembership(data: Membership) {
        _membership.postValue(data)
    }

    fun setGymRegistered(data: GymRegisteredInfo) {
        _gymRegistered.postValue(data)
    }

    fun setNotice(data: NoticeData) {
        _notice.postValue(data)
    }

    init {
        viewModelScope.launch {
            accessToken = dataStore.getAccessToken().toString()
            val membershipDeferred = async { membershipService.getMembershipDetail(accessToken!!) }
            val membershipResponse = membershipDeferred.await()

            if (membershipResponse.isSuccessful) {
                if (membershipResponse.body()?.data == null) _hasTicket.value = false
                else {
//                    setMembershipData(membershipResponse.body()!!.data)
                    _membership.value = membershipResponse.body()!!.data
                    _hasTicket.value = true
                    dataStore.saveGymId(membershipResponse.body()!!.data.gymId)
                }
                if (_hasTicket.value == true) {
                    val gymRegisteredDeferred =
                        async { gymService.getOneGym(accessToken!!, _membership.value!!.gymId) }
                    val gymRegisteredResponse = gymRegisteredDeferred.await()
                    if (gymRegisteredResponse.isSuccessful) {
                        setGymRegistered(gymRegisteredResponse.body()!!.data)

                    }
                }
            } else {
                Log.d("membership", membershipResponse.errorBody()?.string().toString())
            }
        }
    }

    fun setHasTicket(data: Boolean) {
        _hasTicket.postValue(data)
    }

    fun setRegisterValue(data: GymRegisteredInfo) {
        _gymRegistered.postValue(data)
    }

    fun setMembershipData(data: Membership) {
        _membership.postValue(data)
    }

    val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        isError.postValue(true) // isError를 추적하다가 isError일 경우 dialog 뷰에서 띄우기
    }
}