package com.ajou.helpt.auth.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class UserInfoViewModel : ViewModel() {
    private val _sex = MutableLiveData<String>()
    val sex: LiveData<String>
        get() = _sex

    private val _birth = MutableLiveData<LocalDate>()
    val birth: LiveData<LocalDate>
        get() = _birth

    private val _phoneNum = MutableLiveData<String>()
    val phoneNum: LiveData<String>
        get() = _phoneNum

//    private val _height = MutableLiveData<Int>()
//    val height : LiveData<Int>
//        get() = _height
//
//    private val _weight = MutableLiveData<Int>()
//    val weight : LiveData<Int>
//        get() = _weight

    private val _done = MutableLiveData<Boolean>(false)
    val done : LiveData<Boolean>
        get() = _done

    fun setSexInfo(data: String) {
        _sex.postValue(data)
    }

    fun setBirthInfo(data: LocalDate) {
        _birth.postValue(data)
    }

    fun setPhoneNumInfo(data: String) {
        _phoneNum.postValue(data)
    }

//    fun setWeight(data: Int) {
//        _weight.postValue(data)
//    }
//
//    fun setHeight(data: Int) {
//        _height.postValue(data)
//    }

    fun setDone(data: Boolean){
        _done.postValue(data)
    }
}