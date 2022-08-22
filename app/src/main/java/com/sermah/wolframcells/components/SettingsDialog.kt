package com.sermah.wolframcells.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.sermah.wolframcells.ui.theme.Typography

@Composable
fun SettingsDialog(
    rule: Int,
    gridWidth: Int,
    gridHeight: Int,
    wrapAround: Boolean,
    startPattern: List<Byte>,
    startPatternOffset: Int,
    onApplyClicked: (
        rule: Int,
        gridWidth: Int,
        gridHeight: Int,
        wrapAround: Boolean,
        startPattern: List<Byte>,
        startPatternOffset: Int
    ) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        var ruleField by remember { mutableStateOf(rule.toString()) }
        var gridWidthField by remember { mutableStateOf(gridWidth.toString()) }
        var gridHeightField by remember { mutableStateOf(gridHeight.toString()) }
        var wrapAroundCheckBox by remember { mutableStateOf(wrapAround) }
        // var startPatternField by remember { mutableStateOf(startPattern) }
        var startPatternOffsetField by remember { mutableStateOf(startPatternOffset.toString()) }

        @Composable
        fun textField(
            value: String,
            label: String,
            onChange: (String) -> Unit,
            modifier: Modifier = Modifier
        ) {
            TextField(
                label = { Text(label) },
                value = value,
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                modifier = modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                onValueChange = onChange
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Spacers (especially bottom one) are needed for shadows to show up correctly
            Spacer(Modifier.weight(1f))
            Surface(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(8.dp),
                elevation = 24.dp,
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Settings",
                        style = Typography.h6,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )
                    textField(
                        label = "Wolfram's Rule",
                        value = ruleField,
                        onChange = { str -> ruleField = str },
                    )
                    textField(
                        label = "Grid Width",
                        value = gridWidthField,
                        onChange = { str -> gridWidthField = str },
                    )
                    textField(
                        label = "Grid Height",
                        value = gridHeightField,
                        onChange = { str -> gridHeightField = str },
                    )
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = wrapAroundCheckBox,
                            onCheckedChange = { ch -> wrapAroundCheckBox = ch })
                        Text("Wrap Around")
                    }
                    textField(
                        label = "Start Pattern Offset",
                        value = startPatternOffsetField,
                        onChange = { str -> startPatternOffsetField = str },
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        TextButton(onClick = {
                            onApplyClicked(
                                ruleField.toIntOrNull() ?: rule,
                                gridWidthField.toIntOrNull() ?: gridWidth,
                                gridHeightField.toIntOrNull() ?: gridHeight,
                                wrapAroundCheckBox,
                                startPattern,
                                startPatternOffsetField.toIntOrNull() ?: startPatternOffset
                            )
                        }) {
                            Text("Apply")
                        }
                    }
                }
            }
            Spacer(Modifier.weight(1f))
        }
    }
}