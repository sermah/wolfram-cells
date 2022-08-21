package com.sermah.wolframcells.data

data class WfRule(
    val rule: Byte
) {
    fun execute(l: Byte, c: Byte, r: Byte) = (rule.toInt() shr (
            (l.toInt() shl 2) or (c.toInt() shl 1) or (r.toInt())
            ) and 1).toByte()
}
