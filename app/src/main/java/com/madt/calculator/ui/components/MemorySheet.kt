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
import androidx.compose.material.icons.filled.Clear
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
import com.madt.calculator.model.MemoryItem
import com.madt.calculator.ui.theme.WinDarkBg
import com.madt.calculator.ui.theme.WinDarkTextPrimary
import com.madt.calculator.ui.theme.WinDarkTextSecondary
import com.madt.calculator.ui.theme.WinLightBg
import com.madt.calculator.ui.theme.WinLightTextPrimary
import com.madt.calculator.ui.theme.WinLightTextSecondary

/**
 * Bottom sheet displaying stored memory items.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorySheet(
    memoryList: List<MemoryItem>,
    onDismiss: () -> Unit,
    onClearMemory: () -> Unit,
    onDeleteItem: (String) -> Unit,
    onItemClick: (MemoryItem) -> Unit,
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
            // Header: "Memory" and Clear Memory button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.memory_title),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )

                if (memoryList.isNotEmpty()) {
                    IconButton(onClick = onClearMemory) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.clear_memory),
                            tint = textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (memoryList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_memory),
                        color = textSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(memoryList, key = { it.id }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onItemClick(item)
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.value,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )

                            IconButton(onClick = { onDeleteItem(item.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = stringResource(R.string.delete_memory_entry),
                                    tint = textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
