package com.ajou.helpt.train.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TrainInfoViewModel: ViewModel() {
    private val _train = MutableLiveData<String>()
    val train get() = _train

    fun setTrain(data: String) {
        _train.postValue(data)
    }
}