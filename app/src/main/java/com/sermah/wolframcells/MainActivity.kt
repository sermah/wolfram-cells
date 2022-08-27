package com.sermah.wolframcells

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Build
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sermah.wolframcells.components.CellGrid
import com.sermah.wolframcells.components.SettingsDialog
import com.sermah.wolframcells.data.OneDimCellData
import com.sermah.wolframcells.data.WfRule
import com.sermah.wolframcells.ui.theme.WolframCellsTheme

class MainActivity : ComponentActivity() {

    var currentRule by mutableStateOf(30.toUByte())
    var gridWidth by mutableStateOf(100)
    var gridHeight by mutableStateOf(100)
    var wrapSides by mutableStateOf(true)
    var startPattern = mutableStateListOf(1.toByte())
    var startPatternOffset by mutableStateOf((gridWidth - startPattern.size) / 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WolframCellsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val makeCells = { ->
                        OneDimCellData(
                            width = gridWidth,
                            height = gridHeight,
                            statesCount = 2,
                            wrapSides = wrapSides,
                            evolveRule = WfRule(currentRule)::execute
                        ).also {
                            val offsetCoerced =
                                startPatternOffset.coerceIn(0..gridWidth - startPattern.size)
                            for (i in 0 until startPattern.size.coerceAtMost(gridWidth - offsetCoerced))
                                it[offsetCoerced + i, 0] = startPattern[i]
                            it.completeSimulation()
                        }
                    }

                    var cells by remember {
                        mutableStateOf(
                            makeCells.invoke()
                        )
                    }

                    var totalOffset by remember { mutableStateOf(Offset(0f, 0f)) }
                    var cellSize by remember { mutableStateOf(4f) }

                    var showSettingsDialog by remember { mutableStateOf(false) }

                    Column {
                        Surface(
                            elevation = 8.dp,
                            color = MaterialTheme.colors.surface,
                            modifier = Modifier
                                .zIndex(1f)
                                .fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 8.dp)
                            ) {
                                Column (
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp),
                                ) {
                                    Text("Wolfram's Rule $currentRule", style = MaterialTheme.typography.h6)
                                    Text("$gridWidth × $gridHeight (${if (wrapSides) "Wrap" else "No wrap"})",
                                        style = MaterialTheme.typography.caption)
                                }
                                IconButton(onClick = {
                                    showSettingsDialog = true
                                }) {
                                    Icon(imageVector = Icons.Sharp.Build, contentDescription = "Settings")
                                }
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
                                        cellSize = (cellSize * zoom).coerceIn(2f..64f)
                                        totalOffset = totalOffset * cellSize - centroid
                                    }
                                }
                        )
                    }

                    if (showSettingsDialog) SettingsDialog(
                        rule = currentRule.toInt(),
                        gridWidth = cells.width,
                        gridHeight = cells.height,
                        wrapAround = cells.wrapSides,
                        startPattern = startPattern,
                        startPatternOffset = startPatternOffset,
                        onApplyClicked = {
                                rule: Int, newGridWidth: Int, newGridHeight: Int, wrapAround: Boolean,
                                newStartPattern: List<Byte>, newStartPatternOffset: Int ->

                            currentRule = rule.coerceIn(0..255).toUByte()
                            gridWidth = newGridWidth.coerceIn(1..1028)
                            gridHeight = newGridHeight.coerceIn(1..1028)
                            wrapSides = wrapAround
                            startPattern = newStartPattern.toMutableStateList()
                            startPatternOffset =
                                newStartPatternOffset.coerceIn(0..gridWidth - startPattern.size)

                            cells = makeCells()
                            showSettingsDialog = false
                        },
                        onDismissRequest = {
                            showSettingsDialog = false
                        }
                    )
                }
            }
        }
    }
}