package com.sermah.wolframcells

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sermah.wolframcells.components.CellGrid
import com.sermah.wolframcells.data.OneDimCellData
import com.sermah.wolframcells.data.WfRule
import com.sermah.wolframcells.ui.theme.WolframCellsTheme
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.floor

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
                    val cells by remember {
                        mutableStateOf(
                            OneDimCellData(
                                width = 1000,
                                height = 1000,
                                statesCount = 2,
                                evolveRule = WfRule(30)::execute
                            )
                        ).also {
                            it.value[it.value.width/2, 0] = 1
                            it.value.completeSimulation()
                        }
                    }

                    var totalOffset by remember {
                        mutableStateOf(Offset(0f, 0f))
                    }
                    val cellSize by remember {
                        mutableStateOf(4f)
                    }

                    CellGrid(
                        data = cells,
                        totalOffset = totalOffset,
                        cellSize = cellSize.dp,
                        modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                            detectDragGestures { _, dragAmount -> totalOffset -= dragAmount }
                        }
                    )
                }
            }
        }
    }
}