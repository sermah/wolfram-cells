package com.sermah.wolframcells.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.core.graphics.set
import com.sermah.wolframcells.data.CellData
import kotlin.math.ceil
import kotlin.math.floor

@Composable
fun CellGrid(
    data: CellData,
    totalOffset: Offset,
    cellSize: Dp,
    modifier: Modifier
) {
    var dataHash by remember { mutableStateOf(data.hashCode()) }
    var bitmap by remember { mutableStateOf(cellBitmap(data)) }

    if (dataHash != data.hashCode()) {
        bitmap = cellBitmap(data)
        dataHash = data.hashCode()
    }
    
    Canvas(modifier = modifier) {
        val gridSize = IntSize(ceil(this.size.width / cellSize.toPx()).toInt(), ceil(this.size.height / cellSize.toPx()).toInt())

        val topLeftCellPos = (totalOffset / cellSize.toPx()).run { IntOffset(floor(x).toInt(), floor(y).toInt()) }
//        val topLeftCellOffset = IntOffset.Zero

        drawImage(
            image = bitmap.asImageBitmap(),
            srcOffset = topLeftCellPos,
            srcSize = gridSize,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
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