package com.sermah.wolframcells.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.round
import androidx.core.graphics.set
import com.sermah.wolframcells.data.CellData
import kotlin.math.roundToInt

@Composable
fun CellGrid(
    data: CellData,
    totalOffset: Offset,
    cellSize: Dp,
    modifier: Modifier
) {
    val bitmap by remember(data.hashCode()) { mutableStateOf(cellBitmap(data)) }
    
    Canvas(modifier = modifier) {
        drawImage(
            image = bitmap.asImageBitmap(),
            dstOffset = -totalOffset.round(),
            dstSize = IntSize(
                (bitmap.width * cellSize.toPx()).roundToInt(),
                (bitmap.height * cellSize.toPx()).roundToInt()
            ),
            filterQuality = FilterQuality.None,
        )
    }
}

fun cellBitmap(data: CellData): Bitmap =
    Bitmap.createBitmap(
        data.width, data.height, Bitmap.Config.ARGB_8888
    ).also {
        for (ix in 0 until data.width) {
            for (iy in 0 until data.height) {
                it[ix, iy] = if (data[ix, iy] == 0.toByte()) Color.White.toArgb() else Color.Black.toArgb()
            }
        }
    }