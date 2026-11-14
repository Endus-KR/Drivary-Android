package com.endus.drivary.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryCard
import com.endus.drivary.presentation.designsystem.component.DrivaryEmptyState
import com.endus.drivary.presentation.designsystem.component.DrivaryTag
import com.endus.drivary.presentation.designsystem.component.DrivaryTagStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
import com.endus.drivary.presentation.designsystem.theme.DrivaryStatusColors
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme

@Composable
fun HomeRoute(
    onNavigateToDebugSettings: () -> Unit,
    onNavigateToLogbook: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeSideEffect.NavigateToDebugSettings -> onNavigateToDebugSettings()
                HomeSideEffect.NavigateToLogbook -> onNavigateToLogbook()
                is HomeSideEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    HomeScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

/**
 * 홈. 가로 화면을 둘로 나눠 왼쪽에는 지금 할 일(운행 중 표시, 미처리 카드),
 * 오른쪽에는 최근 운행 목록을 둔다.
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DrivaryTopBar(
            title = "운행 기록",
            actions = {
                DrivaryButton(
                    text = "운행기록부",
                    onClick = { onIntent(HomeIntent.ClickLogbook) },
                )
            },
            // 시연 영상에 보이지 않도록 디버그 설정은 제목을 길게 눌러 연다
            onTitleLongPress = { onIntent(HomeIntent.ClickDebugSettings) },
        )
        val hasNow = uiState.recordingStatus != null || uiState.pendingCard != null
        if (uiState.trips.isEmpty() && !hasNow) {
            DrivaryEmptyState(message = "아직 기록된 운행이 없어요")
            return@Column
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp, end = 32.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
        ) {
            Column(
                modifier = Modifier
                    .weight(1.15f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                uiState.recordingStatus?.let { status -> RecordingStatusBanner(status = status) }
                uiState.pendingCard?.let { card ->
                    PendingTripCard(
                        card = card,
                        step = uiState.cardStep,
                        purposeOptions = uiState.purposeOptions,
                        enabled = !uiState.isClassifying,
                        onIntent = onIntent,
                    )
                }
                if (!hasNow) DrivaryEmptyState(message = "확인할 운행이 없어요")
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                Text(
                    text = "최근 운행",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(items = uiState.trips, key = { it.id }) { trip ->
                        TripItem(trip = trip)
                    }
                }
            }
        }
    }
}

@Composable
private fun TripItem(trip: TripUiModel, modifier: Modifier = Modifier) {
    // 이미 있는 문구로 상태 꼬리표를 고른다 ("미처리" / "개인" / "업무 · 영업")
    val (tagText, tagStyle) = when {
        trip.isUnclassified -> "미처리" to DrivaryTagStyle.Unclassified
        trip.classificationText == "개인" -> "개인" to DrivaryTagStyle.Personal
        else -> trip.classificationText.substringAfter("· ", trip.classificationText) to DrivaryTagStyle.Business
    }
    val isWeekend = trip.dateText.endsWith("(토)") || trip.dateText.endsWith("(일)")
    DrivaryCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = trip.dateText,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isWeekend) DrivaryStatusColors.RedDay else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    DrivaryTag(text = tagText, style = tagStyle)
                }
                Text(
                    text = trip.placeText,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 6.dp),
                )
                Text(
                    text = trip.odometerText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = trip.distanceText, style = MaterialTheme.typography.headlineLarge)
        }
    }
}

private val previewTrips = listOf(
    TripUiModel(
        id = 1,
        dateText = "11/15(일)",
        timeRangeText = "10:20:05 ~ 10:45:31",
        odometerText = "12,345.6 → 12,357.0 km",
        distanceText = "11.4 km",
        placeText = "대성부품 대리점",
        classificationText = "미처리",
        isUnclassified = true,
    ),
    TripUiModel(
        id = 2,
        dateText = "11/13(금)",
        timeRangeText = "14:40:00 ~ 15:12:00",
        odometerText = "12,313.2 → 12,334.6 km",
        distanceText = "21.4 km",
        placeText = "한빛물류센터",
        classificationText = "업무 · 납품",
        isUnclassified = false,
    ),
    TripUiModel(
        id = 3,
        dateText = "11/12(목)",
        timeRangeText = "18:30:00 ~ 18:42:00",
        odometerText = "12,308.0 → 12,313.2 km",
        distanceText = "5.2 km",
        placeText = "동네 마트",
        classificationText = "개인",
        isUnclassified = false,
    ),
)

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun HomeScreenPreview() {
    DrivaryTheme {
        HomeScreen(
            uiState = HomeUiState(
                recordingStatus = RecordingStatusUiModel(
                    title = "주차 · 운행 확정까지 7초",
                    detail = "이번 주행 3.0km · 그 전에 다시 출발하면 같은 운행으로 이어져요",
                    isConfirmPending = true,
                ),
                trips = previewTrips,
                pendingCard = PendingTripCardUiModel(
                    tripId = 1,
                    title = "대성부품 대리점에 영업하러 방문하셨나요?",
                    distanceText = "11.4km",
                    dateText = "11/15(일)  10:20:05 ~ 10:45:31",
                    isRedDay = true,
                    suggestedPurpose = PurposeUiModel(id = "SALES", label = "영업"),
                    pendingCountText = "미처리 1건",
                ),
            ),
            onIntent = {},
        )
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun HomeScreenDrivingPreview() {
    DrivaryTheme {
        HomeScreen(
            uiState = HomeUiState(
                recordingStatus = RecordingStatusUiModel(
                    title = "운행 중",
                    detail = "10:20 출발 · 출발 계기판 12,357.0km",
                    isConfirmPending = false,
                ),
                trips = previewTrips.drop(1),
            ),
            onIntent = {},
        )
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun HomeScreenEmptyPreview() {
    DrivaryTheme {
        HomeScreen(uiState = HomeUiState(), onIntent = {})
    }
}
