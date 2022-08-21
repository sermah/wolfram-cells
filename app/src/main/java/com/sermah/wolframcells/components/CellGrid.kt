package com.sermah.wolframcells.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
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
    Canvas(modifier = modifier) {
        val gridSize = IntSize(ceil(this.size.width / cellSize.toPx()).toInt(), ceil(this.size.height / cellSize.toPx()).toInt())
        val bitmap = Bitmap.createBitmap(
            gridSize.width + 1, gridSize.height + 1, Bitmap.Config.ARGB_8888
        )
        val topLeftCellPos = (totalOffset / cellSize.toPx()).run { IntOffset(floor(x).toInt(), floor(y).toInt()) }
        val topLeftCellOffset = IntOffset.Zero
        for (ix in topLeftCellPos.x.coerceAtLeast(0) ..
                (topLeftCellPos.x + gridSize.width).coerceAtMost(data.width - 1)) {
            for (iy in topLeftCellPos.y.coerceAtLeast(0) ..
                    (topLeftCellPos.y + gridSize.height).coerceAtMost(data.height - 1)) {
                bitmap[ix - topLeftCellPos.x, iy - topLeftCellPos.y] = if (data[ix, iy] == 0.toByte()) Color.White.toArgb() else Color.Black.toArgb()
            }
        }
        drawImage(
            image = bitmap.asImageBitmap(),
            srcOffset = topLeftCellOffset,
            srcSize = gridSize,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
            filterQuality = FilterQuality.None,
        )
    }
}