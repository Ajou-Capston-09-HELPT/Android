package com.ajou.helpt.auth.view

import android.net.Uri
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

    private val _img = MutableLiveData<String>()
    val img : LiveData<String>
    get() = _img

    private val _done = MutableLiveData<Boolean>(false)
    val done : LiveData<Boolean>
        get() = _done

    private val _profileImg = MutableLiveData<Uri?>()
    val profileImg : LiveData<Uri?>
        get() = _profileImg

    fun setSexInfo(data: String) {
        _sex.postValue(data)
    }

    fun setBirthInfo(data: LocalDate) {
        _birth.postValue(data)
    }

    fun setPhoneNumInfo(data: String) {
        _phoneNum.postValue(data)
    }

    fun setImg(data: String) {
        _img.postValue(data)
    }

    fun setDone(data: Boolean){
        _done.postValue(data)
    }

    fun setProfileImg(data: Uri?){
        _profileImg.postValue(data)
    }
}