package com.sermah.wolframcells.util

import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.geometry.Offset

val OffsetSaver = listSaver<Offset, Float>(
    save = { oft -> listOf(oft.x, oft.y) },
    restore = { list -> Offset(list[0], list[1]) }
)