package com.example.calculatorwithhistory.data

data class CalculatorState(
    val result: String = "",
    val history: MutableList<String> = mutableListOf()
)
