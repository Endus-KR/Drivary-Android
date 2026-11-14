package com.endus.drivary.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryCard
import com.endus.drivary.presentation.designsystem.component.DrivaryEmptyState
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
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
                DrivaryButton(
                    text = "디버그 설정",
                    onClick = { onIntent(HomeIntent.ClickDebugSettings) },
                    style = DrivaryButtonStyle.Secondary,
                )
            },
        )
        uiState.recordingStatus?.let { status ->
            RecordingStatusBanner(
                status = status,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            )
        }
        if (uiState.trips.isEmpty()) {
            DrivaryEmptyState(message = "아직 기록된 운행이 없어요")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                uiState.pendingCard?.let { card ->
                    item(key = "pending-card") {
                        PendingTripCard(
                            card = card,
                            step = uiState.cardStep,
                            purposeOptions = uiState.purposeOptions,
                            enabled = !uiState.isClassifying,
                            onIntent = onIntent,
                        )
                    }
                }
                items(items = uiState.trips, key = { it.id }) { trip ->
                    TripItem(trip = trip)
                }
            }
        }
    }
}

@Composable
private fun TripItem(trip: TripUiModel, modifier: Modifier = Modifier) {
    DrivaryCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${trip.dateText}  ${trip.timeRangeText}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "${trip.placeText} · ${trip.classificationText}",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (trip.isUnclassified) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    text = trip.odometerText,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(text = trip.distanceText, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

private val previewTrip = TripUiModel(
    id = 1,
    dateText = "11/15(일)",
    timeRangeText = "10:20:05 ~ 10:45:31",
    odometerText = "12,345.6 → 12,357.0 km",
    distanceText = "11.4 km",
    placeText = "대성부품 대리점",
    classificationText = "미처리",
    isUnclassified = true,
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
                trips = listOf(previewTrip),
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
