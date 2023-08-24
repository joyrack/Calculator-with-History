package com.example.calculatorwithhistory

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatorwithhistory.data.CalculatorState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val TAG = "MainViewModel"

    var inputNum1 by mutableStateOf("")
        private set

    var inputNum2 by mutableStateOf("")
        private set

    var operator by mutableStateOf("+")
        private set

    private val _calculatorState = MutableStateFlow(CalculatorState())
    val calculatorState = _calculatorState.asStateFlow()

    fun updateHistory(job : Job) = viewModelScope.launch {
        Log.d(TAG, "updateHistory called")
        // wait for the job to finish
        job.join()
        Log.d(TAG, "job finished")

        val result = _calculatorState.value.result
        // now that the job has finished, we will add the new calculation to the history
        if(result != "" && result != "Error") {   // we have a valid calculation
            val newCalculation = "$inputNum1 $operator $inputNum2 = $result"
            Log.d(TAG, newCalculation)

            _calculatorState.update { currentState ->
                currentState.history.add(newCalculation)
                currentState.copy()
            }
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
            _calculatorState.update { currentState ->       // updating the state
                currentState.copy(
                    result = ""
                )
            }
        } else {
            _calculatorState.update { currentState ->
                currentState.copy(
                    result = when(operator) {
                        "+" -> String.format("%.1f", n1+n2)
                        "-" -> String.format("%.1f", n1-n2)
                        "x" -> String.format("%.1f", n1*n2)
                        else -> if(n2.toInt() == 0) "Error"
                            else String.format("%.1f", n1/n2)
                    }
                )
            }
        }
        Log.d(TAG, "getResult completed")
    }

}
