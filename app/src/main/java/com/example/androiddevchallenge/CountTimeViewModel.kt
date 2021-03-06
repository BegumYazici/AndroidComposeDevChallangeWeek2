package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CountTimeViewModel : ViewModel() {

    private var countDownTimer: CountDownTimer? = null

    val isRunning: LiveData<Boolean>
        get() = _isRunning

    private val _seconds = MutableLiveData(0)
    val seconds: LiveData<Int>
        get() = _seconds

    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int>
        get() = _minutes

    private val _hours = MutableLiveData(0)
    val hours: LiveData<Int>
        get() = _hours

    private val _isRunning = MutableLiveData(false)

    private val _progress = MutableLiveData(1f)
    val progress: LiveData<Float>
        get() = _progress

    private val _time = MutableLiveData("00:00:00")
    val time: LiveData<String>
        get() = _time

    var totalTime = 0L

}