package com.sermah.wolframcells.util

import androidx.compose.runtime.Stable
import androidx.compose.ui.geometry.Offset

@Stable
fun Offset.mod(operand: Float) = Offset(x.mod(operand), y.mod(operand))