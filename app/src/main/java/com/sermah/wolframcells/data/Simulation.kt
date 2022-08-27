package com.sermah.wolframcells.data

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver

class WfSimulation(
    startRule: Int = 30,
    width: Int = 100,
    height: Int = 100,
    wrap: Boolean = true
) {
    var rule by mutableStateOf(startRule.toUByte())
    var width by mutableStateOf(width)
    var height by mutableStateOf(height)
    var wrap by mutableStateOf(wrap)
    var startPattern = mutableStateListOf(1.toByte())
    var startPatternOffset by mutableStateOf((this.width - startPattern.size) / 2)

    fun generate() = OneDimCellData(
        width = width,
        height = height,
        statesCount = 2,
        wrapSides = wrap,
        evolveRule = WfRule(rule)::execute
    ).also {
        val offsetCoerced =
            startPatternOffset.coerceIn(0..width - startPattern.size)
        for (i in 0 until startPattern.size.coerceAtMost(width - offsetCoerced))
            it[offsetCoerced + i, 0] = startPattern[i]
        it.completeSimulation()
    }

    fun update(
        rule: Int = this.rule.toInt(),
        width: Int = this.width,
        height: Int = this.height,
        wrap: Boolean = this.wrap,
        startPattern: List<Byte> = this.startPattern,
        startPatternOffset: Int = this.startPatternOffset,
    ) {
        this.rule = rule.coerceIn(0..255).toUByte()
        this.width = width.coerceIn(1..1028)
        this.height = height.coerceIn(1..1028)
        this.wrap = wrap
        this.startPattern = startPattern.toMutableStateList()
        this.startPatternOffset =
            startPatternOffset.coerceIn(0..width - startPattern.size)
    }


    companion object {
        val saver = run {
            val iRule = 0
            val iWidth = 1
            val iHeight = 2
            val iWrap = 3
            val iOffset = 4

            listSaver<WfSimulation, Int>(
                save = { sim ->
                    mutableListOf(
                        sim.rule.toInt(),
                        sim.width,
                        sim.height,
                        if (sim.wrap) 1 else 0,
                        sim.startPatternOffset
                    ).also { list ->
                        list.addAll(sim.startPattern.map { it.toInt() })
                    }
                },
                restore = { list ->
                    WfSimulation(
                        list[iRule],
                        list[iWidth],
                        list[iHeight],
                        list[iWrap] > 0,
                    ).also { sim ->
                        sim.update(
                            startPatternOffset = list[iOffset],
                            startPattern = list.subList(iOffset + 1, list.size).map { it.toByte() }
                        )
                    }
                },
            )
        }
    }
}