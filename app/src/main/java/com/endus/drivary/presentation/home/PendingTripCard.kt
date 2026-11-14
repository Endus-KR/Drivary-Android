package com.endus.drivary.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryCard

/** 미처리 운행 1건을 묻는 카드 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PendingTripCard(
    card: PendingTripCardUiModel,
    step: PendingCardStep,
    purposeOptions: List<PurposeUiModel>,
    enabled: Boolean,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    DrivaryCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = card.dateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            if (card.isRedDay) {
                Text(
                    text = "빨간날 운행",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(end = 16.dp),
                )
            }
            Text(
                text = card.pendingCountText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = "${card.title} · ${card.distanceText}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        when (step) {
            PendingCardStep.Question -> QuestionButtons(card = card, enabled = enabled, onIntent = onIntent)
            PendingCardStep.PurposeSelection -> Column {
                Text(
                    text = "어떤 업무였나요?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    purposeOptions.forEach { purpose ->
                        DrivaryButton(
                            text = purpose.label,
                            onClick = { onIntent(HomeIntent.SelectPurpose(purpose)) },
                            enabled = enabled,
                        )
                    }
                    DrivaryButton(
                        text = "취소",
                        onClick = { onIntent(HomeIntent.ClickCancelPurposeSelection) },
                        style = DrivaryButtonStyle.Secondary,
                        enabled = enabled,
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionButtons(
    card: PendingTripCardUiModel,
    enabled: Boolean,
    onIntent: (HomeIntent) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (card.suggestedPurpose != null) {
            DrivaryButton(
                text = "맞아요",
                onClick = { onIntent(HomeIntent.ClickConfirmSuggestion) },
                enabled = enabled,
            )
            DrivaryButton(
                text = "다른 목적",
                onClick = { onIntent(HomeIntent.ClickChoosePurpose) },
                style = DrivaryButtonStyle.Secondary,
                enabled = enabled,
            )
        } else {
            DrivaryButton(
                text = "업무",
                onClick = { onIntent(HomeIntent.ClickChoosePurpose) },
                enabled = enabled,
            )
        }
        DrivaryButton(
            text = "개인",
            onClick = { onIntent(HomeIntent.ClickPersonal) },
            style = DrivaryButtonStyle.Secondary,
            enabled = enabled,
        )
    }
}
