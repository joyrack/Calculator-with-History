package com.example.calculatorwithhistory

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val TAG = "MainViewModel"

    var inputNum1 by mutableStateOf("")
        private set

    var inputNum2 by mutableStateOf("")
        private set

    var operator by mutableStateOf("+")
        private set

    private val _calculationHistory = MutableStateFlow(listOf<String>())     // empty list
    val calculationHistory = _calculationHistory.asStateFlow()


    private val _result = MutableStateFlow<String>("")
    val result = _result.asStateFlow()

    fun updateHistory(job : Job) = viewModelScope.launch {
        Log.d(TAG, "updateHistory called")
        // wait for the job to finish
        job.join()
        Log.d(TAG, "job finished")
        // now that the job has finished, we will add the new calculation to the history
        if(_result.value != "") {   // we have a valid calculation
            val newCalculation = "$inputNum1 $operator $inputNum2 = ${_result.value}"
            Log.d(TAG, newCalculation)
            val temp = mutableListOf<String>()
            _calculationHistory.value.forEach {
                temp.add(it)
            }
            temp.add(newCalculation)
            _calculationHistory.value = temp    // assigning a new list to my state
        }
    }

    fun updateNum1(newVal : String) {
        inputNum1 = newVal
    }

    fun updateNum2(newVal : String) {
        inputNum2 = newVal
    }

    fun updateOperator(newOp : String) {
        operator = newOp
    }

    fun getResult() = viewModelScope.launch {
        Log.d(TAG, "getResult called")
        delay(2000L)        // mimicking a network call
        val n1 = inputNum1.toDoubleOrNull()
        val n2 = inputNum2.toDoubleOrNull()
        if(n1 == null || n2 == null) {
            _result.value = ""
        } else {
            _result.value = when(operator) {
                "+" -> (n1+n2).toString()
                "-" -> (n1-n2).toString()
                "x" -> (n1*n2).toString()
                else -> (n1/n2).toString()
            }
        }
        Log.d(TAG, "getResult completed")
    }

}
