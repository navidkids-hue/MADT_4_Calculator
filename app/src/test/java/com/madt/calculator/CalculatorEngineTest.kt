package com.madt.calculator

import com.madt.calculator.engine.CalculatorEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorEngineTest {

    private lateinit var engine: CalculatorEngine

    @Before
    fun setUp() {
        engine = CalculatorEngine()
    }

    @Test
    fun testSimpleAddition() {
        engine.inputDigit(1)
        engine.inputDigit(2)
        assertEquals("12", engine.display)

        engine.inputOperator("+")
        assertEquals("12 +", engine.expression)

        engine.inputDigit(8)
        assertEquals("8", engine.display)

        engine.equals()
        assertEquals("20", engine.display)
        assertEquals("12 + 8 =", engine.expression)
        assertEquals(1, engine.history.size)
        assertEquals("12 + 8 =", engine.history[0].expression)
        assertEquals("20", engine.history[0].result)
    }

    @Test
    fun testImmediateExecutionChaining() {
        // Windows Standard calculates immediately: 2 + 3 * 4 = 20 (not 14)
        engine.inputDigit(2)
        engine.inputOperator("+")
        engine.inputDigit(3)
        engine.inputOperator("×")

        // Right after pressing '×', 2 + 3 should be evaluated to 5 immediately!
        assertEquals("5", engine.display)
        assertEquals("5 ×", engine.expression)

        engine.inputDigit(4)
        engine.equals()
        assertEquals("20", engine.display)
        assertEquals("5 × 4 =", engine.expression)
    }

    @Test
    fun testDivisionByZero() {
        engine.inputDigit(9)
        engine.inputOperator("÷")
        engine.inputDigit(0)
        engine.equals()

        assertEquals("Cannot divide by zero", engine.display)
        assertTrue(engine.isError)

        // Typing a new digit resets after error
        engine.inputDigit(5)
        assertEquals("5", engine.display)
        assertFalse(engine.isError)
    }

    @Test
    fun testSquareRoot() {
        engine.inputDigit(9)
        engine.squareRoot()
        assertEquals("3", engine.display)
        assertEquals("√(9)", engine.expression)

        engine.equals()
        assertEquals("3", engine.display)
    }

    @Test
    fun testNegativeSquareRoot() {
        engine.inputDigit(9)
        engine.toggleSign()
        assertEquals("-9", engine.display)

        engine.squareRoot()
        assertEquals("Invalid input", engine.display)
        assertTrue(engine.isError)
    }

    @Test
    fun testSquaring() {
        engine.inputDigit(5)
        engine.square()
        assertEquals("25", engine.display)
        assertEquals("sqr(5)", engine.expression)
    }

    @Test
    fun testReciprocal() {
        engine.inputDigit(8)
        engine.reciprocal()
        assertEquals("0.125", engine.display)
        assertEquals("1/(8)", engine.expression)
    }

    @Test
    fun testReciprocalZero() {
        engine.inputDigit(0)
        engine.reciprocal()
        assertEquals("Cannot divide by zero", engine.display)
        assertTrue(engine.isError)
    }

    @Test
    fun testPercentageWithPendingOperation() {
        // 200 + 10% = 200 + 20 = 220
        engine.inputDigit(2)
        engine.inputDigit(0)
        engine.inputDigit(0)
        engine.inputOperator("+")
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.percentage()

        assertEquals("20", engine.display)
        engine.equals()
        assertEquals("220", engine.display)
    }

    @Test
    fun testBackspace() {
        engine.inputDigit(1)
        engine.inputDigit(2)
        engine.inputDigit(3)
        assertEquals("123", engine.display)

        engine.backspace()
        assertEquals("12", engine.display)

        engine.backspace()
        assertEquals("1", engine.display)

        engine.backspace()
        assertEquals("0", engine.display)
    }

    @Test
    fun testClearEntryVsClearAll() {
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputOperator("+")
        engine.inputDigit(5)
        assertEquals("5", engine.display)

        // Clear Entry (CE) resets current display to 0, but retains accumulator and operator
        engine.clearEntry()
        assertEquals("0", engine.display)
        assertEquals("10 +", engine.expression)

        engine.inputDigit(7)
        engine.equals()
        assertEquals("17", engine.display)

        // Clear All (C) resets everything
        engine.clearAll()
        assertEquals("0", engine.display)
        assertEquals("", engine.expression)
    }

    @Test
    fun testSignToggle() {
        engine.inputDigit(4)
        engine.inputDigit(2)
        assertEquals("42", engine.display)

        engine.toggleSign()
        assertEquals("-42", engine.display)

        engine.toggleSign()
        assertEquals("42", engine.display)
    }

    @Test
    fun testDecimalPoint() {
        engine.inputDigit(3)
        engine.inputDecimal()
        engine.inputDigit(1)
        engine.inputDigit(4)
        engine.inputDecimal() // Second decimal point should be ignored
        assertEquals("3.14", engine.display)
    }

    @Test
    fun testRepeatedEquals() {
        // 10 + 2 = 12, then = gives 14, then = gives 16
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputOperator("+")
        engine.inputDigit(2)
        engine.equals()
        assertEquals("12", engine.display)

        engine.equals()
        assertEquals("14", engine.display)

        engine.equals()
        assertEquals("16", engine.display)
    }

    @Test
    fun testMemoryOperations() {
        assertFalse(engine.isMemoryAvailable)

        // MS (Memory Store)
        engine.inputDigit(5)
        engine.inputDigit(0)
        engine.memoryStore()
        assertTrue(engine.isMemoryAvailable)
        assertEquals("50", engine.memory[0].value)

        // Change display and test MR (Memory Recall)
        engine.inputDigit(9)
        engine.memoryRecall()
        assertEquals("50", engine.display)

        // M+ (Memory Add): add 10 to 50 = 60
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.memoryAdd()
        assertEquals("60", engine.memory[0].value)

        // M- (Memory Subtract): subtract 20 from 60 = 40
        engine.inputDigit(2)
        engine.inputDigit(0)
        engine.memorySubtract()
        assertEquals("40", engine.memory[0].value)

        // MC (Memory Clear)
        engine.memoryClear()
        assertFalse(engine.isMemoryAvailable)
    }

    @Test
    fun testHistoryRecall() {
        engine.inputDigit(7)
        engine.inputOperator("×")
        engine.inputDigit(8)
        engine.equals()
        assertEquals("56", engine.display)

        val item = engine.history.first()
        assertEquals("7 × 8 =", item.expression)
        assertEquals("56", item.result)

        engine.clearAll()
        assertEquals("0", engine.display)

        engine.recallHistoryItem(item)
        assertEquals("56", engine.display)
    }

    @Test
    fun testUnaryThenBinaryChaining() {
        // 4 + 9 sqrt + 5 = 12
        engine.inputDigit(4)
        engine.inputOperator("+")
        engine.inputDigit(9)
        engine.squareRoot()
        assertEquals("3", engine.display)

        // When pressing '+', previous accumulator 4 and unary result 3 should evaluate to 7
        engine.inputOperator("+")
        assertEquals("7", engine.display)
        assertEquals("7 +", engine.expression)

        engine.inputDigit(5)
        engine.equals()
        assertEquals("12", engine.display)
        assertEquals("7 + 5 =", engine.expression)

        // 10 + 5 sqr + 2 = 37
        engine.clearAll()
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.inputOperator("+")
        engine.inputDigit(5)
        engine.square()
        assertEquals("25", engine.display)

        engine.inputOperator("+")
        assertEquals("35", engine.display)
        assertEquals("35 +", engine.expression)

        engine.inputDigit(2)
        engine.equals()
        assertEquals("37", engine.display)
    }

    @Test
    fun testRecallSpecificMemoryItem() {
        // Store first item: 10
        engine.inputDigit(1)
        engine.inputDigit(0)
        engine.memoryStore()

        // Store second item: 25
        engine.inputDigit(2)
        engine.inputDigit(5)
        engine.memoryStore()

        assertEquals(2, engine.memory.size)
        val olderItem = engine.memory[1] // "10"
        val newerItem = engine.memory[0] // "25"

        assertEquals("25", newerItem.value)
        assertEquals("10", olderItem.value)

        // Recall older item specifically
        engine.clearAll()
        assertEquals("0", engine.display)

        engine.recallMemoryItem(olderItem)
        assertEquals("10", engine.display)
        assertFalse(engine.isError)

        // Recall newer item specifically
        engine.clearAll()
        engine.recallMemoryItem(newerItem)
        assertEquals("25", engine.display)
    }
}
