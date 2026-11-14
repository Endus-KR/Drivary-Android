package com.endus.drivary.presentation.logbook

import android.widget.Toast
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
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
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
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
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                LogbookHeader()
                HorizontalDivider()
                if (uiState.rows.isEmpty()) {
                    DrivaryEmptyState(message = "이 달에는 운행 기록이 없어요")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(items = uiState.rows, key = { it.id }) { row ->
                            LogbookRowItem(row = row)
                            HorizontalDivider()
                        }
                    }
                }
            }
            LogbookSummaryPanel(
                uiState = uiState,
                onExport = { onIntent(LogbookIntent.ClickExport) },
                modifier = Modifier.width(320.dp),
            )
        }
    }
}

/** 표의 칸 너비 비율. 국세청 서식 순서: 사용일자 · 주행 전 · 주행 후 · 주행거리 · 출·퇴근용 · 일반 업무용 · 비고 */
private val columnWeights = listOf(1.1f, 1.1f, 1.1f, 0.9f, 0.9f, 1f, 0.9f)

/** 숫자 칸(주행 전 ~ 일반 업무용)은 오른쪽 정렬 */
private val numberColumns = 1..5

@Composable
private fun LogbookHeader() {
    val labels = listOf("사용일자", "주행 전", "주행 후", "주행거리", "출·퇴근용", "일반 업무용", "비고")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
    ) {
        labels.forEachIndexed { index, label ->
            Cell(
                text = label,
                weight = columnWeights[index],
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                alignEnd = index in numberColumns,
            )
        }
    }
}

@Composable
private fun LogbookRowItem(row: LogbookRowUiModel) {
    val cells = listOf(
        row.dateText, row.odometerBeforeText, row.odometerAfterText, row.distanceText,
        row.commuteText, row.generalBusinessText, row.noteText,
    )
    val base = MaterialTheme.typography.titleLarge
    val unclassifiedNote = base.copy(color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        cells.forEachIndexed { index, text ->
            val isNote = index == cells.lastIndex
            Cell(
                text = text,
                weight = columnWeights[index],
                style = if (isNote && row.isUnclassified) unclassifiedNote else base,
                alignEnd = index in numberColumns,
            )
        }
    }
}

@Composable
private fun RowScope.Cell(text: String, weight: Float, style: TextStyle, alignEnd: Boolean) {
    Text(
        text = text,
        style = style,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
        maxLines = 1,
        modifier = Modifier
            .weight(weight)
            .padding(horizontal = 8.dp),
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
            SummaryLine(label = "총주행거리", value = uiState.totalDistanceText)
            Spacer(modifier = Modifier.height(16.dp))
            SummaryLine(label = "업무용 사용거리", value = uiState.businessUseDistanceText)
            Spacer(modifier = Modifier.height(16.dp))
            SummaryLine(label = "업무사용비율", value = uiState.businessUseRatioText, emphasize = true)
        }
        Spacer(modifier = Modifier.weight(1f))
        uiState.exportBlockedMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
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
private fun SummaryLine(label: String, value: String, emphasize: Boolean = false) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Text(
                text = value,
                style = if (emphasize) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineMedium,
                color = if (emphasize) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
        }
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
                    LogbookRowUiModel(3, "11/6(금)", "12,073", "12,078", "5", "0", "0", "", false),
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
