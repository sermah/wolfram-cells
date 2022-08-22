package com.sermah.wolframcells

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.sermah.wolframcells.components.CellGrid
import com.sermah.wolframcells.data.OneDimCellData
import com.sermah.wolframcells.data.WfRule
import com.sermah.wolframcells.ui.theme.WolframCellsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WolframCellsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val makeCellsForRule = { rule: UByte ->
                        OneDimCellData(
                            width = 1000,
                            height = 1000,
                            statesCount = 2,
                            evolveRule = WfRule(rule)::execute
                        ).also {
                            it[it.width/2, 0] = 1
                            it.completeSimulation()
                        }
                    }

                    var currentRule by remember {
                        mutableStateOf(30.toUByte())
                    }

                    var cells by remember {
                        mutableStateOf(
                            makeCellsForRule.invoke(currentRule)
                        )
                    }

                    var totalOffset by remember {
                        mutableStateOf(Offset(0f, 0f))
                    }
                    var cellSize by remember {
                        mutableStateOf(4f)
                    }

                    var fieldValue by remember {
                        mutableStateOf("30")
                    }

                    Column {
                        Row (
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        ){
                            TextField(value = fieldValue, modifier = Modifier.weight(1f), onValueChange = { str -> fieldValue = str })
                            Button(modifier = Modifier.padding(start = 16.dp), onClick = {
                                val newRule = fieldValue.toUIntOrNull()
                                if (newRule != null && newRule < 256U) {
                                    currentRule = newRule.toUByte()
                                    cells = makeCellsForRule.invoke(currentRule)
                                }
                            }) {
                                Text("Generate")
                            }
                        }
                        CellGrid(
                            data = cells,
                            totalOffset = totalOffset,
                            cellSize = cellSize.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTransformGestures { centroid, pan, zoom, _ ->
                                        totalOffset -= pan
                                        totalOffset = (totalOffset + centroid) / cellSize
                                        cellSize *= zoom
                                        totalOffset = totalOffset * cellSize - centroid
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}