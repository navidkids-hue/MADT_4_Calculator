package com.madt.calculator.engine

import com.madt.calculator.model.CalculationHistoryItem
import com.madt.calculator.model.MemoryItem
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

/**
 * Pure Kotlin evaluation engine for Windows 11 Standard Calculator.
 *
 * Implements immediate-execution calculation (each operator evaluates immediately),
 * high-precision arithmetic using BigDecimal, unary transformations (sqrt, sqr, 1/x, %),
 * full runtime calculation history, and multi-item memory management.
 */
class CalculatorEngine {

    companion object {
        const val ERROR_DIV_ZERO = "Cannot divide by zero"
        const val ERROR_INVALID_INPUT = "Invalid input"
        private const val MAX_SCALE = 12
        private val MATH_CONTEXT = MathContext.DECIMAL64
    }

    var display: String = "0"
        private set

    var expression: String = ""
        private set

    var isError: Boolean = false
        private set

    private val _history = mutableListOf<CalculationHistoryItem>()
    val history: List<CalculationHistoryItem>
        get() = _history.toList()

    private val _memory = mutableListOf<MemoryItem>()
    val memory: List<MemoryItem>
        get() = _memory.toList()

    val isMemoryAvailable: Boolean
        get() = _memory.isNotEmpty()

    // Internal calculation state
    private var accumulator: BigDecimal? = null
    private var pendingOperator: String? = null
    private var isNewInput: Boolean = true
    private var lastOperation: Pair<String, BigDecimal>? = null
    private var unaryExprPending: String? = null

    // ─────────────────────────────────────────────────────────────────────────────
    // Digit & Decimal Input
    // ─────────────────────────────────────────────────────────────────────────────

    fun inputDigit(digit: Int) {
        if (isError) {
            clearAll()
        }

        if (isNewInput) {
            display = digit.toString()
            isNewInput = false
            unaryExprPending = null
        } else {
            if (display == "0") {
                display = digit.toString()
            } else {
                display += digit.toString()
            }
        }
    }

    fun inputDecimal() {
        if (isError) {
            clearAll()
        }

        if (isNewInput) {
            display = "0."
            isNewInput = false
            unaryExprPending = null
        } else if (!display.contains(".")) {
            display += "."
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Editing & Clearing
    // ─────────────────────────────────────────────────────────────────────────────

    fun backspace() {
        if (isError || isNewInput) return

        if (display.length == 1 || (display.length == 2 && display.startsWith("-"))) {
            display = "0"
            isNewInput = true
        } else {
            display = display.dropLast(1)
        }
    }

    fun clearEntry() {
        if (isError) {
            clearAll()
            return
        }
        display = "0"
        isNewInput = true
        unaryExprPending = null
    }

    fun clearAll() {
        display = "0"
        expression = ""
        isError = false
        accumulator = null
        pendingOperator = null
        isNewInput = true
        lastOperation = null
        unaryExprPending = null
    }

    fun toggleSign() {
        if (isError || display == "0") return

        display = if (display.startsWith("-")) {
            display.substring(1)
        } else {
            "-$display"
        }
        unaryExprPending = null
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Binary Operations (+, −, ×, ÷) - Immediate Execution
    // ─────────────────────────────────────────────────────────────────────────────

    fun inputOperator(op: String) {
        if (isError) return

        val currentValue = parseBigDecimal(display) ?: return

        if (accumulator != null && pendingOperator != null && (!isNewInput || unaryExprPending != null)) {
            // Immediate evaluation of previous operator
            val evaluated = evaluateBinary(accumulator!!, currentValue, pendingOperator!!)
            if (evaluated == null) return // error occurred

            display = formatBigDecimal(evaluated)
            accumulator = evaluated
        } else {
            accumulator = currentValue
        }

        pendingOperator = op
        isNewInput = true
        lastOperation = null
        unaryExprPending = null

        val accStr = formatBigDecimal(accumulator!!)
        val opSymbol = operatorDisplaySymbol(op)
        expression = "$accStr $opSymbol"
    }

    fun equals() {
        if (isError) return

        val currentValue = parseBigDecimal(display) ?: return

        if (pendingOperator != null && accumulator != null) {
            val op = pendingOperator!!
            val left = accumulator!!
            val evaluated = evaluateBinary(left, currentValue, op)
            if (evaluated == null) return

            val resultStr = formatBigDecimal(evaluated)
            val leftStr = formatBigDecimal(left)
            val opSymbol = operatorDisplaySymbol(op)
            val rightStr = unaryExprPending ?: formatBigDecimal(currentValue)
            val exprStr = "$leftStr $opSymbol $rightStr ="

            expression = exprStr
            display = resultStr

            // Save to calculation history
            _history.add(0, CalculationHistoryItem(expression = exprStr, result = resultStr))

            lastOperation = Pair(op, currentValue)
            accumulator = evaluated
            pendingOperator = null
            isNewInput = true
            unaryExprPending = null
        } else if (lastOperation != null) {
            // Repeated equals operation (Windows 11 behavior)
            val (op, operand) = lastOperation!!
            val left = currentValue
            val evaluated = evaluateBinary(left, operand, op)
            if (evaluated == null) return

            val resultStr = formatBigDecimal(evaluated)
            val leftStr = formatBigDecimal(left)
            val opSymbol = operatorDisplaySymbol(op)
            val rightStr = formatBigDecimal(operand)
            val exprStr = "$leftStr $opSymbol $rightStr ="

            expression = exprStr
            display = resultStr

            _history.add(0, CalculationHistoryItem(expression = exprStr, result = resultStr))
            accumulator = evaluated
            isNewInput = true
        } else {
            // No operator pending; e.g. "42 ="
            val currentStr = formatBigDecimal(currentValue)
            val exprStr = "$currentStr ="
            expression = exprStr
            _history.add(0, CalculationHistoryItem(expression = exprStr, result = currentStr))
            isNewInput = true
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Unary Operations (√x, x², 1/x, %)
    // ─────────────────────────────────────────────────────────────────────────────

    fun squareRoot() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return

        if (current < BigDecimal.ZERO) {
            setError(ERROR_INVALID_INPUT)
            return
        }

        val d = current.toDouble()
        val sqrtVal = Math.sqrt(d)
        val res = BigDecimal(sqrtVal, MATH_CONTEXT).setScale(MAX_SCALE, RoundingMode.HALF_UP)
        val resStr = formatBigDecimal(res)
        val currentStr = formatBigDecimal(current)
        val formattedExpr = "√($currentStr)"

        updateUnaryResult(formattedExpr, resStr)
    }

    fun square() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return

        val res = current.multiply(current, MATH_CONTEXT).setScale(MAX_SCALE, RoundingMode.HALF_UP)
        val resStr = formatBigDecimal(res)
        val currentStr = formatBigDecimal(current)
        val formattedExpr = "sqr($currentStr)"

        updateUnaryResult(formattedExpr, resStr)
    }

    fun reciprocal() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return

        if (current.compareTo(BigDecimal.ZERO) == 0) {
            setError(ERROR_DIV_ZERO)
            return
        }

        val res = BigDecimal.ONE.divide(current, MAX_SCALE, RoundingMode.HALF_UP)
        val resStr = formatBigDecimal(res)
        val currentStr = formatBigDecimal(current)
        val formattedExpr = "1/($currentStr)"

        updateUnaryResult(formattedExpr, resStr)
    }

    fun percentage() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return

        if (accumulator != null && pendingOperator != null) {
            // Windows standard: B % of A = (A * B) / 100
            val percentVal = accumulator!!.multiply(current, MATH_CONTEXT)
                .divide(BigDecimal(100), MAX_SCALE, RoundingMode.HALF_UP)
            val resStr = formatBigDecimal(percentVal)
            display = resStr
            unaryExprPending = resStr
            isNewInput = true
        } else {
            // Standalone percentage evaluates to 0 in Windows Standard
            display = "0"
            expression = "0"
            isNewInput = true
        }
    }

    private fun updateUnaryResult(unaryExpr: String, resultStr: String) {
        display = resultStr
        unaryExprPending = unaryExpr
        isNewInput = true

        if (accumulator != null && pendingOperator != null) {
            val accStr = formatBigDecimal(accumulator!!)
            val opSymbol = operatorDisplaySymbol(pendingOperator!!)
            expression = "$accStr $opSymbol $unaryExpr"
        } else {
            expression = unaryExpr
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Memory Operations (MC, MR, M+, M-, MS)
    // ─────────────────────────────────────────────────────────────────────────────

    fun memoryClear() {
        _memory.clear()
    }

    fun memoryRecall() {
        if (_memory.isNotEmpty()) {
            display = _memory.first().value
            isNewInput = true
            unaryExprPending = null
        }
    }

    fun memoryStore() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return
        val formatted = formatBigDecimal(current)
        _memory.add(0, MemoryItem(value = formatted))
        isNewInput = true
    }

    fun memoryAdd() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return
        if (_memory.isEmpty()) {
            memoryStore()
            return
        }
        val top = parseBigDecimal(_memory.first().value) ?: BigDecimal.ZERO
        val updated = top.add(current, MATH_CONTEXT)
        val formatted = formatBigDecimal(updated)
        _memory[0] = _memory.first().copy(value = formatted, timestamp = System.currentTimeMillis())
        isNewInput = true
    }

    fun memorySubtract() {
        if (isError) return
        val current = parseBigDecimal(display) ?: return
        if (_memory.isEmpty()) {
            val negated = current.negate()
            _memory.add(0, MemoryItem(value = formatBigDecimal(negated)))
            isNewInput = true
            return
        }
        val top = parseBigDecimal(_memory.first().value) ?: BigDecimal.ZERO
        val updated = top.subtract(current, MATH_CONTEXT)
        val formatted = formatBigDecimal(updated)
        _memory[0] = _memory.first().copy(value = formatted, timestamp = System.currentTimeMillis())
        isNewInput = true
    }

    fun deleteMemoryItem(id: String) {
        _memory.removeAll { it.id == id }
    }

    fun recallMemoryItem(item: MemoryItem) {
        display = item.value
        isNewInput = true
        isError = false
        unaryExprPending = null
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // History Actions
    // ─────────────────────────────────────────────────────────────────────────────

    fun clearHistory() {
        _history.clear()
    }

    fun recallHistoryItem(item: CalculationHistoryItem) {
        display = item.result
        isNewInput = true
        isError = false
        unaryExprPending = null
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────────

    private fun evaluateBinary(left: BigDecimal, right: BigDecimal, op: String): BigDecimal? {
        return try {
            when (op) {
                "+", "add" -> left.add(right, MATH_CONTEXT)
                "−", "-", "sub" -> left.subtract(right, MATH_CONTEXT)
                "×", "*", "mul" -> left.multiply(right, MATH_CONTEXT)
                "÷", "/", "div" -> {
                    if (right.compareTo(BigDecimal.ZERO) == 0) {
                        setError(ERROR_DIV_ZERO)
                        null
                    } else {
                        left.divide(right, MAX_SCALE, RoundingMode.HALF_UP)
                    }
                }
                else -> right
            }
        } catch (e: ArithmeticException) {
            setError(ERROR_INVALID_INPUT)
            null
        }
    }

    private fun setError(message: String) {
        display = message
        isError = true
        accumulator = null
        pendingOperator = null
        lastOperation = null
        unaryExprPending = null
    }

    private fun parseBigDecimal(str: String): BigDecimal? {
        return try {
            BigDecimal(str.trim())
        } catch (e: Exception) {
            null
        }
    }

    private fun formatBigDecimal(value: BigDecimal): String {
        if (value.compareTo(BigDecimal.ZERO) == 0) return "0"
        val stripped = value.stripTrailingZeros()
        return stripped.toPlainString()
    }

    private fun operatorDisplaySymbol(op: String): String {
        return when (op) {
            "+", "add" -> "+"
            "−", "-", "sub" -> "−"
            "×", "*", "mul" -> "×"
            "÷", "/", "div" -> "÷"
            else -> op
        }
    }
}
