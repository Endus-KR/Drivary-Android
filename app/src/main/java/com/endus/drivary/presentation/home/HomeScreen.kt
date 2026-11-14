package com.endus.drivary.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                HomeSideEffect.NavigateToDebugSettings -> onNavigateToDebugSettings()
                HomeSideEffect.NavigateToLogbook -> onNavigateToLogbook()
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
        if (uiState.trips.isEmpty()) {
            DrivaryEmptyState(message = "아직 기록된 운행이 없어요")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
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
                Text(text = trip.odometerText, style = MaterialTheme.typography.titleLarge)
            }
            Text(text = trip.distanceText, style = MaterialTheme.typography.headlineMedium)
        }
    }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun HomeScreenPreview() {
    DrivaryTheme {
        HomeScreen(
            uiState = HomeUiState(
                trips = listOf(
                    TripUiModel(
                        id = 1,
                        dateText = "11/14(토)",
                        timeRangeText = "13:20:05 ~ 13:45:31",
                        odometerText = "12,345.6 → 12,357.0 km",
                        distanceText = "11.4 km",
                    ),
                ),
            ),
            onIntent = {},
        )
    }
}
