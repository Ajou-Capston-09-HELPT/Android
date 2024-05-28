package com.ajou.helpt.train.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.helpt.home.model.GymEquipment

class TrainInfoViewModel : ViewModel() {
    private val _train = MutableLiveData<GymEquipment?>()
    val train get() = _train

    private val _time = MutableLiveData<String>()
    val time get() = _time

    private val _doneSet = MutableLiveData<Int>()
    val doneSet get() = _doneSet

    private val _doneCount = MutableLiveData<Int>()
    val doneCount get() = _doneCount

    private val _rate = MutableLiveData<Int>()
    val rate get() = _rate

    fun setTrain(data: GymEquipment?) {
        _train.postValue(data)
    }

    fun setTime(data: String) {
        _time.postValue(data)
    }

    fun setDoneSet(data: Int) {
        _doneSet.postValue(data)
    }

    fun setDoneCount(data: Int) {
        _doneCount.postValue(data)
    }

    fun setRate(data: Int) {
        _rate.postValue(data)
    }
}