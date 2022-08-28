package com.sermah.wolframcells.data.simulation

import androidx.compose.runtime.Composable
import com.sermah.wolframcells.data.CellData

interface Simulation {
    // Title in top bar for describing ost important aspects of simulation
    val title: String

    // For short secondary info like the size of field
    val subtitle: String

    fun generate(): CellData

    @Composable
    fun PreferencesDialog(
        onApply: () -> Unit,
        onDismiss: () -> Unit,
    )
}