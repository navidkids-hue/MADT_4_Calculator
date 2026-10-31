package com.madt.calculator.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinDarkTextSecondary
import com.madt.calculator.ui.theme.WinLightTextPrimary
import com.madt.calculator.ui.theme.WinLightTextSecondary

/**
 * Windows 11 Standard Calculator Display.
 * Displays the previous expression (e.g. "12 + 8 =") above the large current value/result.
 */
@Composable
fun CalculatorDisplay(
    expression: String,
    display: String,
    isError: Boolean,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val textPrimary = if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
    val textSecondary = if (darkTheme) WinDarkTextSecondary else WinLightTextSecondary
    val scrollState = rememberScrollState()

    // Auto-scroll to end on new digits when content actually overflows
    LaunchedEffect(display, scrollState.maxValue) {
        if (scrollState.maxValue > 0 && scrollState.maxValue != Int.MAX_VALUE) {
            scrollState.scrollTo(scrollState.maxValue)
        }
    }

    // Dynamic font sizing for long results
    val fontSize = when {
        display.length > 14 -> 28.sp
        display.length > 10 -> 34.sp
        display.length > 7 -> 40.sp
        else -> 48.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        // Previous expression line (e.g. "12 + 8 =")
        Text(
            text = expression.ifEmpty { " " },
            color = textSecondary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
        )

        // Main display / result line
        Text(
            text = display,
            color = textPrimary,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState, reverseScrolling = false)
        )
    }
}
