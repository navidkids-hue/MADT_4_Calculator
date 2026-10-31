package com.madt.calculator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madt.calculator.R
import com.madt.calculator.engine.CalculatorEngine
import com.madt.calculator.ui.components.ButtonType
import com.madt.calculator.ui.components.CalculatorButton
import com.madt.calculator.ui.components.CalculatorDisplay
import com.madt.calculator.ui.components.HistorySheet
import com.madt.calculator.ui.components.MemoryRow
import com.madt.calculator.ui.components.MemorySheet
import com.madt.calculator.ui.theme.WinDarkBg
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinDarkTextSecondary
import com.madt.calculator.ui.theme.WinLightBg
import com.madt.calculator.ui.theme.WinLightTextPrimary
import com.madt.calculator.ui.theme.WinLightTextSecondary

/**
 * Main Calculator Screen mirroring the Windows 11 "Standard" Calculator interface.
 */
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val engine = remember { CalculatorEngine() }

    // State triggers for Compose recomposition
    var display by remember { mutableStateOf(engine.display) }
    var expression by remember { mutableStateOf(engine.expression) }
    var isError by remember { mutableStateOf(engine.isError) }
    var isMemoryAvailable by remember { mutableStateOf(engine.isMemoryAvailable) }

    // Dialog sheets visibility
    var showHistorySheet by remember { mutableStateOf(false) }
    var showMemorySheet by remember { mutableStateOf(false) }

    fun syncState() {
        display = engine.display
        expression = engine.expression
        isError = engine.isError
        isMemoryAvailable = engine.isMemoryAvailable
    }

    val bgColor = if (darkTheme) WinDarkBg else WinLightBg
    val textPrimary = if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
    val textSecondary = if (darkTheme) WinDarkTextSecondary else WinLightTextSecondary

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .safeDrawingPadding()
            .padding(bottom = 8.dp)
    ) {
        // ─── Header: "Standard" Title & History Action Button ──────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.standard_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )

            // History Button at top right
            IconButton(onClick = { showHistorySheet = true }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.desc_history_button),
                    tint = textSecondary
                )
            }
        }

        // ─── Calculator Display (Expression + Result) ─────────────────────────────
        CalculatorDisplay(
            expression = expression,
            display = display,
            isError = isError,
            modifier = Modifier.weight(1f)
        )

        // ─── Memory Row (MC, MR, M+, M-, MS, M▾) ──────────────────────────────────
        MemoryRow(
            isMemoryAvailable = isMemoryAvailable,
            onMemoryClear = {
                engine.memoryClear()
                syncState()
            },
            onMemoryRecall = {
                engine.memoryRecall()
                syncState()
            },
            onMemoryAdd = {
                engine.memoryAdd()
                syncState()
            },
            onMemorySubtract = {
                engine.memorySubtract()
                syncState()
            },
            onMemoryStore = {
                engine.memoryStore()
                syncState()
            },
            onMemoryOpen = {
                showMemorySheet = true
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ─── Button Grid (6 rows × 4 columns) ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            // Row 1: %   CE   C   ⌫
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_percent),
                    contentDescription = stringResource(R.string.desc_percent),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.percentage(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_ce),
                    contentDescription = stringResource(R.string.desc_ce),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.clearEntry(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_c),
                    contentDescription = stringResource(R.string.desc_c),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.clearAll(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_backspace),
                    contentDescription = stringResource(R.string.desc_backspace),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.backspace(); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2: 1/x  x²   √x   ÷
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_reciprocal),
                    contentDescription = stringResource(R.string.desc_reciprocal),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.reciprocal(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_square),
                    contentDescription = stringResource(R.string.desc_square),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.square(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_sqrt),
                    contentDescription = stringResource(R.string.desc_sqrt),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.squareRoot(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_divide),
                    contentDescription = stringResource(R.string.desc_divide),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.inputOperator("÷"); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3: 7    8    9    ×
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_7),
                    contentDescription = stringResource(R.string.desc_digit_7),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(7); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_8),
                    contentDescription = stringResource(R.string.desc_digit_8),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(8); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_9),
                    contentDescription = stringResource(R.string.desc_digit_9),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(9); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_multiply),
                    contentDescription = stringResource(R.string.desc_multiply),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.inputOperator("×"); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4: 4    5    6    −
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_4),
                    contentDescription = stringResource(R.string.desc_digit_4),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(4); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_5),
                    contentDescription = stringResource(R.string.desc_digit_5),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(5); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_6),
                    contentDescription = stringResource(R.string.desc_digit_6),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(6); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_subtract),
                    contentDescription = stringResource(R.string.desc_subtract),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.inputOperator("−"); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 5: 1    2    3    +
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_1),
                    contentDescription = stringResource(R.string.desc_digit_1),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(1); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_2),
                    contentDescription = stringResource(R.string.desc_digit_2),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(2); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_3),
                    contentDescription = stringResource(R.string.desc_digit_3),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(3); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_add),
                    contentDescription = stringResource(R.string.desc_add),
                    type = ButtonType.OPERATOR,
                    onClick = { engine.inputOperator("+"); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 6: +/-  0    .    =
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                CalculatorButton(
                    text = stringResource(R.string.btn_sign),
                    contentDescription = stringResource(R.string.desc_sign),
                    type = ButtonType.DIGIT,
                    onClick = { engine.toggleSign(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_digit_0),
                    contentDescription = stringResource(R.string.desc_digit_0),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDigit(0); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_decimal),
                    contentDescription = stringResource(R.string.desc_decimal),
                    type = ButtonType.DIGIT,
                    onClick = { engine.inputDecimal(); syncState() },
                    modifier = Modifier.weight(1f)
                )
                CalculatorButton(
                    text = stringResource(R.string.btn_equals),
                    contentDescription = stringResource(R.string.desc_equals),
                    type = ButtonType.ACCENT,
                    onClick = { engine.equals(); syncState() },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // ─── History Bottom Sheet ──────────────────────────────────────────────────
    if (showHistorySheet) {
        HistorySheet(
            history = engine.history,
            onDismiss = { showHistorySheet = false },
            onClearHistory = {
                engine.clearHistory()
                syncState()
            },
            onItemClick = { item ->
                engine.recallHistoryItem(item)
                syncState()
            }
        )
    }

    // ─── Memory Bottom Sheet ───────────────────────────────────────────────────
    if (showMemorySheet) {
        MemorySheet(
            memoryList = engine.memory,
            onDismiss = { showMemorySheet = false },
            onClearMemory = {
                engine.memoryClear()
                syncState()
            },
            onDeleteItem = { id ->
                engine.deleteMemoryItem(id)
                syncState()
            },
            onItemClick = { item ->
                engine.recallMemoryItem(item)
                syncState()
            }
        )
    }
}
