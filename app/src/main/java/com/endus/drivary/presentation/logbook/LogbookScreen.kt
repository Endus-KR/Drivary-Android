package com.endus.drivary.presentation.logbook

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryCard
import com.endus.drivary.presentation.designsystem.component.DrivaryEmptyState
import com.endus.drivary.presentation.designsystem.component.DrivaryTag
import com.endus.drivary.presentation.designsystem.component.DrivaryTagStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
import com.endus.drivary.presentation.designsystem.theme.DrivaryStatusColors
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme
import timber.log.Timber

@Composable
fun LogbookRoute(
    onNavigateBack: () -> Unit,
    viewModel: LogbookViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                LogbookSideEffect.NavigateBack -> onNavigateBack()
                is LogbookSideEffect.ShareCsv ->
                    runCatching { context.shareLogbookCsv(effect.fileName, effect.content) }
                        .onFailure { throwable ->
                            Timber.e(throwable)
                            Toast.makeText(context, "운행기록부를 보내지 못했어요", Toast.LENGTH_SHORT).show()
                        }

                is LogbookSideEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LogbookScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@Composable
fun LogbookScreen(
    uiState: LogbookUiState,
    onIntent: (LogbookIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DrivaryTopBar(
            title = "월간 운행기록부",
            navigation = {
                DrivaryButton(
                    text = "뒤로",
                    onClick = { onIntent(LogbookIntent.ClickBack) },
                    style = DrivaryButtonStyle.Secondary,
                )
            },
            actions = {
                DrivaryButton(
                    text = "이전 달",
                    onClick = { onIntent(LogbookIntent.ClickPreviousMonth) },
                    style = DrivaryButtonStyle.Secondary,
                )
                Text(
                    text = uiState.monthText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
                DrivaryButton(
                    text = "다음 달",
                    onClick = { onIntent(LogbookIntent.ClickNextMonth) },
                    style = DrivaryButtonStyle.Secondary,
                )
            },
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = uiState.driverText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                LogbookHeader()
                if (uiState.rows.isEmpty()) {
                    DrivaryEmptyState(message = "이 달에는 운행 기록이 없어요")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = uiState.rows, key = { it.id }) { row ->
                            LogbookRowItem(row = row)
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
            LogbookSummaryPanel(
                uiState = uiState,
                onExport = { onIntent(LogbookIntent.ClickExport) },
                modifier = Modifier.width(360.dp),
            )
        }
    }
}

/** 표의 칸 너비 비율. 국세청 서식 순서: 사용일자 · 주행 전 · 주행 후 · 주행거리 · 출·퇴근용 · 일반 업무용 · 비고 */
private val columnWeights = listOf(1.1f, 1.1f, 1.1f, 0.9f, 0.9f, 1f, 1f)

/** 숫자 칸(주행 전 ~ 일반 업무용)은 오른쪽 정렬 */
private val numberColumns = 1..5

@Composable
private fun LogbookHeader() {
    val labels = listOf("사용일자", "주행 전", "주행 후", "주행거리", "출·퇴근용", "일반 업무용", "비고")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .padding(vertical = 16.dp),
    ) {
        labels.forEachIndexed { index, label ->
            Cell(
                text = label,
                weight = columnWeights[index],
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                alignEnd = index in numberColumns,
            )
        }
    }
}

@Composable
private fun LogbookRowItem(row: LogbookRowUiModel) {
    val numbers = listOf(row.odometerBeforeText, row.odometerAfterText, row.distanceText, row.commuteText, row.generalBusinessText)
    val base = MaterialTheme.typography.titleLarge
    // 데이터 모양은 그대로 두고, 이미 있는 값으로 상태를 고른다 (개인 = 비고가 비어 있고 미처리가 아님)
    val isPersonal = !row.isUnclassified && row.noteText.isEmpty()
    val isWeekend = row.dateText.endsWith("(토)") || row.dateText.endsWith("(일)")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(if (row.isUnclassified) DrivaryStatusColors.Unclassified.copy(alpha = 0.08f) else Color.Transparent),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Cell(
            text = row.dateText,
            weight = columnWeights[0],
            style = base,
            color = if (isWeekend) DrivaryStatusColors.RedDay else MaterialTheme.colorScheme.onSurface,
            alignEnd = false,
        )
        numbers.forEachIndexed { i, text ->
            val index = i + 1
            Cell(
                text = text,
                weight = columnWeights[index],
                // 주행거리는 굵게, 업무용 칸의 0과 -는 흐리게
                style = if (index == 3) base.copy(fontWeight = FontWeight.Bold) else base,
                color = if (index >= 4 && (text == "0" || text == "-")) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                alignEnd = true,
            )
        }
        Box(
            modifier = Modifier
                .weight(columnWeights.last())
                .padding(horizontal = 12.dp),
        ) {
            when {
                row.isUnclassified -> DrivaryTag(text = "미처리", style = DrivaryTagStyle.Unclassified)
                isPersonal -> DrivaryTag(text = "개인", style = DrivaryTagStyle.Personal)
                else -> DrivaryTag(text = row.noteText, style = DrivaryTagStyle.Business)
            }
        }
    }
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, style: TextStyle, color: Color, alignEnd: Boolean) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        maxLines = 1,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 12.dp),
    )
}

@Composable
private fun LogbookSummaryPanel(
    uiState: LogbookUiState,
    onExport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DrivaryCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "업무사용비율",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = uiState.businessUseRatioText,
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(20.dp))
            SummaryLine(label = "총주행거리", value = uiState.totalDistanceText)
            Spacer(modifier = Modifier.height(12.dp))
            SummaryLine(label = "업무용", value = uiState.businessUseDistanceText)
        }
        Spacer(modifier = Modifier.weight(1f))
        uiState.exportBlockedMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = DrivaryStatusColors.Unclassified,
            )
        }
        DrivaryButton(
            text = "내보내기",
            onClick = onExport,
            enabled = uiState.isExportEnabled,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(text = value, style = MaterialTheme.typography.headlineMedium)
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun LogbookScreenPreview() {
    DrivaryTheme {
        LogbookScreen(
            uiState = LogbookUiState(
                monthText = "2026년 11월",
                driverText = "사용자 영업팀 · 홍길동",
                rows = listOf(
                    LogbookRowUiModel(1, "11/3(화)", "12,000", "12,013", "13", "13", "0", "출퇴근", false),
                    LogbookRowUiModel(2, "11/3(화)", "12,013", "12,051", "38", "0", "38", "납품", false),
                    LogbookRowUiModel(3, "11/4(수)", "12,051", "12,073", "22", "0", "22", "영업", false),
                    LogbookRowUiModel(5, "11/6(금)", "12,073", "12,078", "5", "0", "0", "", false),
                    LogbookRowUiModel(4, "11/7(토)", "12,078", "12,094", "16", "-", "-", "미처리", true),
                ),
                totalDistanceText = "94 km",
                businessUseDistanceText = "73 km",
                businessUseRatioText = "77.7%",
                isExportEnabled = false,
                exportBlockedMessage = "미처리 운행 1건을 먼저 분류해 주세요",
            ),
            onIntent = {},
        )
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun LogbookScreenEmptyPreview() {
    DrivaryTheme {
        LogbookScreen(
            uiState = LogbookUiState(
                monthText = "2026년 10월",
                driverText = "사용자 영업팀 · 홍길동",
                exportBlockedMessage = "이 달에는 내보낼 운행이 없어요",
            ),
            onIntent = {},
        )
    }
}
