package com.madt.calculator.model

import java.util.UUID

/**
 * Represents an entry in the calculator's memory stack.
 *
 * @property id Unique identifier.
 * @property value Stored numerical value as a formatted string.
 * @property timestamp Time the memory entry was saved or modified.
 */
data class MemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val value: String,
    val timestamp: Long = System.currentTimeMillis()
)
