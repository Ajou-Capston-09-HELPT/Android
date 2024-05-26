package com.ajou.helpt.train.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ajou.helpt.home.model.GymEquipment

class TrainInfoViewModel: ViewModel() {
    private val _train = MutableLiveData<GymEquipment?>()
    val train get() = _train

    fun setTrain(data: GymEquipment?) {
        _train.postValue(data)
    }
}