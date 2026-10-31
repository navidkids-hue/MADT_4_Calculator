package com.madt.calculator.model

import java.util.UUID

/**
 * Represents a single calculation entry in the runtime history log.
 *
 * @property id Unique identifier for list keys and animations.
 * @property expression The full mathematical expression string (e.g. "12 + 8 =").
 * @property result The evaluated result (e.g. "20").
 * @property timestamp Time the calculation was performed.
 */
data class CalculationHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
