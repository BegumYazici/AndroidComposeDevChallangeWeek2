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

    fun startCountDown() {
        if (countDownTimer != null) {
            cancelTimer()
        }

        totalTime = (getSeconds() * 1000).toLong()

        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisecs: Long) {
                // Seconds
                val secs = (millisecs / MSECS_IN_SEC % SECS_IN_MINUTES).toInt()
                if (secs != seconds.value) {
                    _seconds.postValue(secs)
                }
                // Minutes
                val minutes = (millisecs / MSECS_IN_SEC / SECS_IN_MINUTES % SECS_IN_MINUTES).toInt()
                if (minutes != this@CountTimeViewModel.minutes.value) {
                    _minutes.postValue(minutes)
                }
                // Hours
                val hours = (millisecs / MSECS_IN_SEC / MINUTES_IN_HOUR / SECS_IN_MINUTES).toInt()
                if (hours != this@CountTimeViewModel.hours.value) {
                    _hours.postValue(hours)
                }

                _progress.postValue(millisecs.toFloat() / totalTime.toFloat())
                _time.postValue(formatHourMinuteSecond(hours, minutes, secs))
            }

            override fun onFinish() {
                _progress.postValue(1.0f)
                _isRunning.postValue(false)
            }
        }
        countDownTimer?.start()
        _isRunning.postValue(true)
    }

    fun modifyTime(timeUnit: TimeUnit, timeOperator: TimeOperator) {
        var seconds = seconds.value ?: 0
        var minutes = minutes.value ?: 0
        var hours = hours.value ?: 0

        when (timeUnit) {
            TimeUnit.SEC -> {
                seconds = updateTime(seconds, timeOperator).coerceIn(0, 59)
            }
            TimeUnit.MIN ->{
                minutes = updateTime(minutes, timeOperator).coerceIn(0, 59)
            }
            TimeUnit.HOUR ->{
                hours = updateTime(hours, timeOperator).coerceIn(0, 23)
            }
        }

        // update time
        _seconds.postValue(seconds)
        _minutes.postValue(minutes)
        _hours.postValue(hours)

        _time.postValue(formatHourMinuteSecond(hours, minutes, seconds))
    }

    private fun formatHourMinuteSecond(hours : Int,minutes : Int,seconds : Int) =
        String.format("%02d:%02d:%02d", hours, minutes, seconds)

    fun cancelTimer() {
        countDownTimer?.cancel()
        _isRunning.postValue(false)
    }

    override fun onCleared() {
        super.onCleared()
        cancelTimer()
    }

    private fun getSeconds() = ((hours.value ?: 0) * MINUTES_IN_HOUR * SECS_IN_MINUTES) + ((minutes.value
        ?: 0) * SECS_IN_MINUTES) + (seconds.value ?: 0)

    private fun updateTime(currentValue: Int, timeOperator: TimeOperator): Int {
        return when (timeOperator) {
            TimeOperator.INCREASE -> currentValue + 1
            TimeOperator.DECREASE -> currentValue - 1
        }
    }

    companion object {
        enum class TimeOperator {
            INCREASE, DECREASE
        }

        enum class TimeUnit {
            SEC, MIN, HOUR
        }

        const val MINUTES_IN_HOUR = 60
        const val SECS_IN_MINUTES = 60
        const val MSECS_IN_SEC = 1000
    }
}