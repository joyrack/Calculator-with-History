package com.example.calculatorwithhistory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.calculatorwithhistory.ui.theme.CalculatorWithHistoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculatorWithHistoryTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorApp()
                }
            }
        }
    }
}

@Composable
fun CalculatorApp(
    mainViewModel: MainViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val calculatorState = mainViewModel.calculatorState.collectAsState()

    Column(
        modifier = modifier
            .padding(vertical = 24.dp, horizontal = 16.dp)
            .fillMaxSize(),
    ) {
        Text("Enter the numbers", fontSize = 24.sp)
        InputRow(
            inputNum1 = mainViewModel.inputNum1,
            inputNum2 = mainViewModel.inputNum2,
            operator = mainViewModel.operator,
            result = calculatorState.value.result,
            updateNum1 = { mainViewModel.updateNum1(it) },
            updateNum2 = { mainViewModel.updateNum2(it) },
            updateOperator = { mainViewModel.updateOperator(it) },
            modifier = modifier
        )
        Button(onClick = {
            val answer = mainViewModel.getResult()
            // wait till you get the result and then add it to history
            mainViewModel.updateHistory(answer)
        }) {
            Text("Calculate")
        }
        CalculatorHistory(
            history = calculatorState.value.history,
            modifier = modifier
        )
    }
}

@Composable
fun CalculatorHistory(
    history: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier.padding(top = 48.dp)
    ) {
        Text("History", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(history) { calculation ->
                Text(calculation, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRow(
    inputNum1: String,
    inputNum2: String,
    operator: String,
    result: String,
    updateNum1: (String) -> Unit,
    updateNum2: (String) -> Unit,
    updateOperator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(top = 12.dp, bottom = 16.dp)
    ) {
        InputNumberField(initialVal = inputNum1, onTextChange = updateNum1)
        OperatorDropdown(
            operator = operator,
            updateOperator = updateOperator
        )
        InputNumberField(initialVal = inputNum2, onTextChange = updateNum2)
        Text(" = ", fontSize = 28.sp)
        OutlinedTextField(
            value = result,
            onValueChange = {},
            modifier = Modifier.width(70.dp),
            readOnly = true
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputNumberField(initialVal: String, onTextChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = initialVal,
        onValueChange = onTextChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.width(70.dp)
    )
}

@Composable
fun OperatorDropdown(
    operator: String,
    updateOperator: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val operations = arrayOf("+" , "-" , "x" , "/")

    Column {
        Box {
            TextButton(onClick = { expanded = true }) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = operator, fontSize = 28.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.Gray)
                }
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                operations.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            expanded = false
                            updateOperator(it)
                        }
                    )
                }
            }
        }
    }
}