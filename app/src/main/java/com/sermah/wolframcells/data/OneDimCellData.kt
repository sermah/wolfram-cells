package com.sermah.wolframcells.data

data class OneDimCellData (
    override val width: Int,
    override val height: Int,
    override val statesCount: Int,
    val wrapSides: Boolean = true,
    val noWrapWallState: Byte = 0,
    val evolveRule: (Byte, Byte, Byte) -> Byte,
) : CellData {

    init {
        require(width > 0) { "Width must be greater than 0!" }
        require(height > 0) { "Height must be greater than 0!" }
        require(statesCount in 1..255) { "StatesCount must in [1, 255]!" }
        require(noWrapWallState in 0 until statesCount) { "NoWrapWallState must in [0, statesCount)!" }
    }

    private var data = Array(width) { ByteArray(height) { 0.toByte() } }
    var lastGen: Int = 0

    override fun get(x: Int, y: Int): Byte = data[x][y]

    override fun set(x: Int, y: Int, to: Byte) {
        data[x][y] = to
    }

    fun simulate() {
        if (lastGen == height - 1) return
        for (ix in if (wrapSides) (0 until width) else (1 until width-1)) {
            this[ix, lastGen + 1] = evolveRule(this[(ix - 1).mod(width), lastGen], this[ix, lastGen], this[(ix + 1).mod(width), lastGen])
        }
        lastGen++
    }

    fun completeSimulation() {
        while (lastGen < height - 1) simulate()
    }

    override fun toString() = buildString {
        for (y in 0 until height)
            for (x in 0 until width)
                append(if (this@OneDimCellData[x, y] == 0.toByte()) "." else "#")
    }
}