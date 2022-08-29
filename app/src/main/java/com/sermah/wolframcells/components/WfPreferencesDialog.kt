package com.sermah.wolframcells.components

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.Delete
import androidx.compose.material.icons.sharp.VerticalAlignCenter
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.set
import com.sermah.wolframcells.data.simulation.WfSimulation
import com.sermah.wolframcells.ui.theme.Typography
import kotlin.math.floor

@Composable
fun WfPreferencesDialog(
    simulation: WfSimulation,
    onApplyClicked: (
        rule: Int,
        width: Int,
        height: Int,
        wrap: Boolean,
        startPattern: List<Byte>,
        startPatternOffset: Int
    ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var ruleField by rememberSaveable { mutableStateOf(simulation.rule.toString()) }
    var gridWidthField by rememberSaveable { mutableStateOf(simulation.width.toString()) }
    var gridHeightField by rememberSaveable { mutableStateOf(simulation.height.toString()) }
    var wrapAroundSwitch by rememberSaveable { mutableStateOf(simulation.wrap) }
    var startPatternOffsetField by rememberSaveable { mutableStateOf(simulation.startPatternOffset.toString()) }
    val startPatternEditor = rememberSaveable(
        saver = run {
            listSaver(
                save = { it.toList() },
                restore = { it.toMutableStateList() }
            )
        }
    ) { simulation.startPattern.toMutableStateList() }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = true)
    ) {
        @Composable
        fun textField(
            value: String,
            label: String,
            onChange: (String) -> Unit,
            fillWidth: Boolean = true,
            modifier: Modifier = Modifier
        ) {
            TextField(
                label = { Text(label) },
                value = value,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = if (fillWidth) modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth()
                else modifier
                    .padding(vertical = 4.dp),
                onValueChange = onChange
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacers (especially bottom one) are needed for shadows to show up correctly
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onDismissRequest() })
            Surface(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(8.dp),
                elevation = 24.dp,
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Parameters",
                        style = Typography.h6,
                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                    )
                    Text("Rules", Modifier.padding(top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.subtitle2)
                    textField(
                        label = "Wolfram's Rule (0..255)",
                        value = ruleField,
                        onChange = { str -> ruleField = str },
                    )
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wrap Around")
                        Switch(checked = wrapAroundSwitch,
                            onCheckedChange = { ch -> wrapAroundSwitch = ch })
                    }
                    Text("Field size", Modifier.padding(top = 16.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.subtitle2)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        textField(
                            label = "Width",
                            value = gridWidthField,
                            onChange = { str -> gridWidthField = str },
                            fillWidth = false,
                            modifier = Modifier.weight(1f)
                        )
                        Text("×", Modifier.padding(horizontal = 8.dp))
                        textField(
                            label = "Height (Steps)",
                            value = gridHeightField,
                            onChange = { str -> gridHeightField = str },
                            fillWidth = false,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Start pattern",
                            style = MaterialTheme.typography.subtitle2,
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp, end = 8.dp)
                        )
                        Text(
                            "(size = ${startPatternEditor.size})",
                            style = MaterialTheme.typography.caption,
                            modifier = Modifier
                                .padding(top = 16.dp, bottom = 8.dp)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        PatternEditor(
                            startPattern = startPatternEditor,
                            stateCount = 2,
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) { idx ->
                            val i = idx.coerceIn(0 until startPatternEditor.size)
                            startPatternEditor[i] = ((startPatternEditor[i] + 1) % 2).toByte()
                        }
                        IconButton(
                            onClick = { startPatternEditor.add(0.toByte()) },
                            Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Sharp.Add, "", Modifier.size(24.dp))
                        }
                        IconButton(
                            enabled = startPatternEditor.size > 1,
                            onClick = { if (startPatternEditor.size > 1) startPatternEditor.removeLast() },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(Icons.Sharp.Delete, "", Modifier.size(24.dp))
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        textField(
                            label = "Start Pattern Offset (0 = Left)",
                            value = startPatternOffsetField,
                            fillWidth = false,
                            onChange = { str -> startPatternOffsetField = str },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        )
                        IconButton(
                            onClick = {
                                startPatternOffsetField = (((gridWidthField.toIntOrNull()
                                    ?: 1) - startPatternEditor.size) / 2)
                                    .coerceAtLeast(0)
                                    .toString()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.Sharp.VerticalAlignCenter,
                                "",
                                Modifier
                                    .size(24.dp)
                                    .rotate(90f)
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                    ) {
                        TextButton(onClick = {
                            onApplyClicked(
                                ruleField.toIntOrNull() ?: simulation.rule.toInt(),
                                gridWidthField.toIntOrNull() ?: simulation.width,
                                gridHeightField.toIntOrNull() ?: simulation.height,
                                wrapAroundSwitch,
                                startPatternEditor.toList(),
                                startPatternOffsetField.toIntOrNull()
                                    ?: simulation.startPatternOffset
                            )
                        }) {
                            Text("Apply")
                        }
                    }
                }
            }
            Spacer(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onDismissRequest() })
        }
    }
}

@Composable
fun PatternEditor(
    startPattern: List<Byte>,
    stateCount: Int,
    modifier: Modifier = Modifier,
    onCellClicked: (Int) -> Unit,
) {
    val cellSize = 40.dp
    val borderThickness = 2.dp
    val borderColor = MaterialTheme.colors.primary

    var hash = 0
    startPattern.forEach { hash += hash * stateCount + it }

    val bitmap by remember(hash) {
        mutableStateOf(Bitmap.createBitmap(startPattern.size, 1, Bitmap.Config.ARGB_8888).also {
            for (i in startPattern.indices)
                it[i, 0] = if (startPattern[i] == 0.toByte()) Color.WHITE else Color.BLACK
        })
    }

    val scrollState = rememberScrollState()

    Row(
        modifier
            .horizontalScroll(scrollState)
    ) {
        Canvas(
            Modifier
                .size(
                    cellSize * bitmap.width + borderThickness,
                    cellSize + borderThickness
                )
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            if (offset.x >= (borderThickness.toPx() / 2f) &&
                                offset.x < (size.width - borderThickness.toPx() / 2f)
                            ) {
                                onCellClicked(
                                    floor((offset.x - borderThickness.toPx() / 2f) / cellSize.toPx()).toInt()
                                )
                            }
                        }
                    )
                }
        ) {
            drawImage(
                image = bitmap.asImageBitmap(),
                dstSize = IntSize(cellSize.roundToPx() * bitmap.width, cellSize.roundToPx()),
                dstOffset = IntOffset(borderThickness.roundToPx() / 2, borderThickness.roundToPx() / 2),
                filterQuality = FilterQuality.None,
            )
            drawLine( // topleft - topright
                color = borderColor,
                strokeWidth = borderThickness.toPx(),
                start = Offset(0f, borderThickness.toPx() / 2),
                end = Offset(size.width, borderThickness.toPx() / 2),
            )
            drawLine( // topright - botright
                color = borderColor,
                strokeWidth = borderThickness.toPx(),
                start = Offset(size.width - borderThickness.toPx() / 2, borderThickness.toPx()),
                end = Offset(size.width - borderThickness.toPx() / 2, size.height - borderThickness.toPx()),
            )
            drawLine( // botright - botleft
                color = borderColor,
                strokeWidth = borderThickness.toPx(),
                start = Offset(size.width, size.height - borderThickness.toPx() / 2),
                end = Offset(0f, size.height - borderThickness.toPx() / 2),
            )
            for (i in startPattern.indices)
                drawLine( // botleft - topleft for each cell
                    color = borderColor,
                    strokeWidth = borderThickness.toPx(),
                    start = Offset(borderThickness.toPx() / 2 + cellSize.toPx() * i, size.height - borderThickness.toPx() / 2),
                    end = Offset(borderThickness.toPx() / 2 + cellSize.toPx() * i, borderThickness.toPx() / 2),
                )
        }
    }
}