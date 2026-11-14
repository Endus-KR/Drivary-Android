package com.endus.drivary.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryCard
import com.endus.drivary.presentation.designsystem.component.DrivaryTag
import com.endus.drivary.presentation.designsystem.component.DrivaryTagStyle
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme

/** 큰 버튼 최소 너비. 운전석에서 손가락으로 누르기 쉽게 */
private val AnswerButtonMinWidth = 200.dp

/** 미처리 운행 1건을 묻는 카드. 질문 문장이 가장 크고, 버튼은 크고 구분되게 */
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DrivaryTag(text = card.pendingCountText, style = DrivaryTagStyle.Unclassified)
            if (card.isRedDay) DrivaryTag(text = "빨간날", style = DrivaryTagStyle.RedDay)
            Text(
                text = card.dateText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 28.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(
                text = card.title,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = card.distanceText,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 24.dp),
            )
        }
        when (step) {
            PendingCardStep.Question -> QuestionButtons(card = card, enabled = enabled, onIntent = onIntent)
            PendingCardStep.PurposeSelection -> Column {
                Text(
                    text = "어떤 업무였나요?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            modifier = Modifier.widthIn(min = 160.dp),
                        )
                    }
                    DrivaryButton(
                        text = "취소",
                        onClick = { onIntent(HomeIntent.ClickCancelPurposeSelection) },
                        style = DrivaryButtonStyle.Secondary,
                        enabled = enabled,
                        modifier = Modifier.widthIn(min = 160.dp),
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
    val wide = Modifier.widthIn(min = AnswerButtonMinWidth)
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        if (card.suggestedPurpose != null) {
            DrivaryButton(
                text = "맞아요",
                onClick = { onIntent(HomeIntent.ClickConfirmSuggestion) },
                enabled = enabled,
                modifier = wide,
            )
            DrivaryButton(
                text = "다른 목적",
                onClick = { onIntent(HomeIntent.ClickChoosePurpose) },
                style = DrivaryButtonStyle.Secondary,
                enabled = enabled,
                modifier = wide,
            )
        } else {
            DrivaryButton(
                text = "업무",
                onClick = { onIntent(HomeIntent.ClickChoosePurpose) },
                enabled = enabled,
                modifier = wide,
            )
        }
        DrivaryButton(
            text = "개인",
            onClick = { onIntent(HomeIntent.ClickPersonal) },
            style = DrivaryButtonStyle.Secondary,
            enabled = enabled,
            modifier = wide,
        )
    }
}

private val previewCard = PendingTripCardUiModel(
    tripId = 1,
    title = "대성부품 대리점에 영업하러 방문하셨나요?",
    distanceText = "11.4km",
    dateText = "11/15(일)  10:20:05 ~ 10:45:31",
    isRedDay = true,
    suggestedPurpose = PurposeUiModel(id = "SALES", label = "영업"),
    pendingCountText = "미처리 1건",
)

@Preview(widthDp = 760, heightDp = 420)
@Composable
private fun PendingTripCardQuestionPreview() {
    DrivaryTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(24.dp)) {
            PendingTripCard(
                card = previewCard,
                step = PendingCardStep.Question,
                purposeOptions = PurposeUiModel.ALL,
                enabled = true,
                onIntent = {},
            )
        }
    }
}

@Preview(widthDp = 760, heightDp = 520)
@Composable
private fun PendingTripCardPurposePreview() {
    DrivaryTheme {
        Column(modifier = Modifier.background(MaterialTheme.colorScheme.background).padding(24.dp)) {
            PendingTripCard(
                card = previewCard.copy(title = "미래오토 정비소 방문", isRedDay = false, suggestedPurpose = null),
                step = PendingCardStep.PurposeSelection,
                purposeOptions = PurposeUiModel.ALL,
                enabled = true,
                onIntent = {},
            )
        }
    }
}
