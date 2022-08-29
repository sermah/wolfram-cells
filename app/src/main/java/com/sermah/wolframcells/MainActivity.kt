package com.sermah.wolframcells

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.sermah.wolframcells.components.CellGrid
import com.sermah.wolframcells.data.CellData
import com.sermah.wolframcells.data.simulation.Simulation
import com.sermah.wolframcells.data.simulation.WfSimulation
import com.sermah.wolframcells.data.simulation.WfSimulationSaver
import com.sermah.wolframcells.ui.theme.WolframCellsTheme
import com.sermah.wolframcells.util.OffsetSaver

val cellSizeRange = 1f..64f

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WolframCellsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val simulation: Simulation = rememberSaveable(saver = WfSimulationSaver) {
                        WfSimulation()
                    }

                    var cellData: CellData by remember {
                        mutableStateOf(simulation.generate())
                    }

                    var totalOffset by rememberSaveable(stateSaver = OffsetSaver) {
                        mutableStateOf(Offset(0f, 0f))
                    }

                    var cellSize by rememberSaveable { mutableStateOf(4f) }
                    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

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
                                    Text(
                                        simulation.title,
                                        style = MaterialTheme.typography.h6
                                    )
                                    Text(
                                        simulation.subtitle,
                                        style = MaterialTheme.typography.caption
                                    )
                                }
                                IconButton(onClick = {
                                    showSettingsDialog = true
                                }) {
                                    Icon(imageVector = Icons.Sharp.Build, contentDescription = "Settings")
                                }
                            }
                        }
                        CellGrid(
                            data = cellData,
                            totalOffset = totalOffset,
                            cellSize = cellSize.dp,
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(Unit) {
                                    detectTransformGestures { centroid, pan, zoom, _ ->
                                        totalOffset -= pan
                                        totalOffset = (totalOffset + centroid) / cellSize
                                        cellSize = (cellSize * zoom).coerceIn(cellSizeRange)
                                        totalOffset = totalOffset * cellSize - centroid
                                    }
                                }
                        )
                    }

                    if (showSettingsDialog) simulation.PreferencesDialog(
                        onApply = {
                            cellData = simulation.generate()
                            showSettingsDialog = false
                        },
                        onDismiss = {
                            showSettingsDialog = false
                        }
                    )
                }
            }
        }
    }
}