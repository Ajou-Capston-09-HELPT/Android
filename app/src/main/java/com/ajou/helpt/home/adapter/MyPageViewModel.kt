package com.ajou.helpt.home.adapter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.helpt.mypage.ExerciseRecord

class MyPageViewModel : ViewModel() {
    private val _selectedItem = MutableLiveData<ExerciseRecord>()
    val selectedItem : LiveData<ExerciseRecord>
        get() = _selectedItem

    fun setSelectedItem(data: ExerciseRecord){
        _selectedItem.postValue(data)
    }
}