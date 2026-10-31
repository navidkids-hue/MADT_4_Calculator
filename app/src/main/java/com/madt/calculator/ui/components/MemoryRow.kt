package com.madt.calculator.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madt.calculator.R
import com.madt.calculator.ui.theme.WinDarkTextDisabled
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinLightTextDisabled
import com.madt.calculator.ui.theme.WinLightTextPrimary

/**
 * Windows 11 Standard Calculator Memory Row.
 * Contains: MC, MR, M+, M−, MS, M▾
 * MC and MR are disabled when memory is empty.
 */
@Composable
fun MemoryRow(
    isMemoryAvailable: Boolean,
    onMemoryClear: () -> Unit,
    onMemoryRecall: () -> Unit,
    onMemoryAdd: () -> Unit,
    onMemorySubtract: () -> Unit,
    onMemoryStore: () -> Unit,
    onMemoryOpen: () -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val textEnabled = if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
    val textDisabled = if (darkTheme) WinDarkTextDisabled else WinLightTextDisabled

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemoryButton(
            label = stringResource(R.string.btn_mc),
            contentDesc = stringResource(R.string.desc_mc),
            enabled = isMemoryAvailable,
            onClick = onMemoryClear,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
        MemoryButton(
            label = stringResource(R.string.btn_mr),
            contentDesc = stringResource(R.string.desc_mr),
            enabled = isMemoryAvailable,
            onClick = onMemoryRecall,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
        MemoryButton(
            label = stringResource(R.string.btn_m_add),
            contentDesc = stringResource(R.string.desc_m_add),
            enabled = true,
            onClick = onMemoryAdd,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
        MemoryButton(
            label = stringResource(R.string.btn_m_sub),
            contentDesc = stringResource(R.string.desc_m_sub),
            enabled = true,
            onClick = onMemorySubtract,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
        MemoryButton(
            label = stringResource(R.string.btn_ms),
            contentDesc = stringResource(R.string.desc_ms),
            enabled = true,
            onClick = onMemoryStore,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
        MemoryButton(
            label = stringResource(R.string.btn_m_open),
            contentDesc = stringResource(R.string.desc_m_open),
            enabled = true,
            onClick = onMemoryOpen,
            textEnabled = textEnabled,
            textDisabled = textDisabled
        )
    }
}

@Composable
private fun MemoryButton(
    label: String,
    contentDesc: String,
    enabled: Boolean,
    onClick: () -> Unit,
    textEnabled: Color,
    textDisabled: Color,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(4.dp),
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = textEnabled,
            disabledContentColor = textDisabled
        ),
        modifier = modifier
            .height(34.dp)
            .semantics { contentDescription = contentDesc }
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
