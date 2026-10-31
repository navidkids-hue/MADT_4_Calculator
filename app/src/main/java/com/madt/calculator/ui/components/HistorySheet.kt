package com.madt.calculator.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.madt.calculator.R
import com.madt.calculator.model.CalculationHistoryItem
import com.madt.calculator.ui.theme.WinDarkBg
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinDarkTextSecondary
import com.madt.calculator.ui.theme.WinLightBg
import com.madt.calculator.ui.theme.WinLightTextPrimary
import com.madt.calculator.ui.theme.WinLightTextSecondary

/**
 * Bottom Sheet displaying calculation history.
 * Displays history entries (e.g. "12 + 8 =" and "20"), a clear-all trash button,
 * and allows tapping an entry to recall it into the active calculator display.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySheet(
    history: List<CalculationHistoryItem>,
    onDismiss: () -> Unit,
    onClearHistory: () -> Unit,
    onItemClick: (CalculationHistoryItem) -> Unit,
    modifier: Modifier = Modifier,
    darkTheme: Boolean = isSystemInDarkTheme()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val textPrimary = if (darkTheme) WinDarkTextPrimary else WinLightTextPrimary
    val textSecondary = if (darkTheme) WinDarkTextSecondary else WinLightTextSecondary
    val bgColor = if (darkTheme) WinDarkBg else WinLightBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .heightIn(min = 200.dp, max = 500.dp)
        ) {
            // Header: title and trash button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.history_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )

                if (history.isNotEmpty()) {
                    IconButton(onClick = onClearHistory) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.clear_history),
                            tint = textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_history),
                        color = textSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(history, key = { it.id }) { item ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemClick(item)
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = item.expression,
                                fontSize = 14.sp,
                                color = textSecondary,
                                textAlign = TextAlign.End
                            )
                            Text(
                                text = item.result,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}
