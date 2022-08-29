package com.sermah.wolframcells.data

interface CellData {
    val width: Int
    val height: Int
    val statesCount: Int
    val stateColors: Array<Int>

    operator fun get(x: Int, y: Int): Byte
    operator fun set(x: Int, y: Int, to: Byte)
}