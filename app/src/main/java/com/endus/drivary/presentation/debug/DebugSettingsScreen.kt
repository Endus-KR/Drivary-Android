package com.endus.drivary.presentation.debug

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.endus.drivary.presentation.designsystem.component.DrivaryButton
import com.endus.drivary.presentation.designsystem.component.DrivaryButtonStyle
import com.endus.drivary.presentation.designsystem.component.DrivaryCard
import com.endus.drivary.presentation.designsystem.component.DrivaryTopBar
import com.endus.drivary.presentation.designsystem.theme.DrivaryTheme
import java.text.DecimalFormat

@Composable
fun DebugSettingsRoute(
    onNavigateBack: () -> Unit,
    viewModel: DebugSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                DebugSettingsSideEffect.NavigateBack -> onNavigateBack()
                is DebugSettingsSideEffect.ShowMessage ->
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    DebugSettingsScreen(uiState = uiState, onIntent = viewModel::onIntent)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DebugSettingsScreen(
    uiState: DebugSettingsUiState,
    onIntent: (DebugSettingsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        DrivaryTopBar(
            title = "디버그 설정 (시연 모드)",
            navigation = {
                DrivaryButton(
                    text = "뒤로",
                    onClick = { onIntent(DebugSettingsIntent.ClickBack) },
                    style = DrivaryButtonStyle.Secondary,
                )
            },
        )
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            DrivaryCard {
                SectionTitle(text = "차량 데이터: ${uiState.modeText}")
            }
            DrivaryCard {
                SectionTitle(text = "기어  ${uiState.currentGear.label}")
                if (!uiState.isGearControlEnabled) {
                    Text(
                        text = "Pleos 모드에서는 Virtual Console에서 기어를 바꿔요",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }
                OptionRow {
                    GearUiModel.entries.forEach { gear ->
                        DrivaryButton(
                            text = gear.label,
                            onClick = { onIntent(DebugSettingsIntent.SelectGear(gear)) },
                            enabled = uiState.isGearControlEnabled,
                            style = if (gear == uiState.currentGear) {
                                DrivaryButtonStyle.Primary
                            } else {
                                DrivaryButtonStyle.Secondary
                            },
                        )
                    }
                }
            }
            DrivaryCard {
                SectionTitle(text = "추가 주행거리 (계기판 값에 더함)  ${uiState.extraDistanceText}")
                OptionRow {
                    uiState.distanceOptions.forEach { km ->
                        DrivaryButton(
                            text = "+${distanceFormat.format(km)} km",
                            onClick = { onIntent(DebugSettingsIntent.AddDistance(km)) },
                            style = DrivaryButtonStyle.Secondary,
                        )
                    }
                }
            }
            DrivaryCard {
                SectionTitle(text = "다음 도착지 (다음에 확정되는 운행)")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    uiState.places.forEach { place ->
                        DrivaryButton(
                            text = place.name,
                            onClick = { onIntent(DebugSettingsIntent.SelectNextDestination(place.id)) },
                            style = if (place.id == uiState.nextDestinationId) {
                                DrivaryButtonStyle.Primary
                            } else {
                                DrivaryButtonStyle.Secondary
                            },
                        )
                    }
                }
            }
            DrivaryCard {
                SectionTitle(text = "오늘을 평일로 보기 (주말 시연용)")
                Text(
                    text = "켜면 오늘 운행도 평일처럼 자동 분류해요. 오늘 날짜에만 적용돼요.",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                DrivaryButton(
                    text = if (uiState.treatTodayAsWeekday) "켜짐" else "꺼짐",
                    onClick = { onIntent(DebugSettingsIntent.ClickToggleTreatTodayAsWeekday) },
                    style = if (uiState.treatTodayAsWeekday) {
                        DrivaryButtonStyle.Primary
                    } else {
                        DrivaryButtonStyle.Secondary
                    },
                )
            }
            DrivaryCard {
                SectionTitle(text = "샘플 데이터")
                DrivaryButton(
                    text = "이번 달 샘플 운행 5건 넣기",
                    onClick = { onIntent(DebugSettingsIntent.ClickAddSampleTrips) },
                    style = DrivaryButtonStyle.Secondary,
                )
            }
            DrivaryCard {
                SectionTitle(text = "확정 대기 시간 (P 이후)")
                OptionRow {
                    uiState.confirmDelayOptions.forEach { seconds ->
                        DrivaryButton(
                            text = "${seconds}초",
                            onClick = { onIntent(DebugSettingsIntent.SelectConfirmDelay(seconds)) },
                            style = if (seconds == uiState.confirmDelaySeconds) {
                                DrivaryButtonStyle.Primary
                            } else {
                                DrivaryButtonStyle.Secondary
                            },
                        )
                    }
                }
            }
        }
    }
}

private val distanceFormat = DecimalFormat("#,##0.#")

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(bottom = 16.dp),
    )
}

@Composable
private fun OptionRow(content: @Composable () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { content() }
}

@Preview(widthDp = 1280, heightDp = 720)
@Composable
private fun DebugSettingsScreenPreview() {
    DrivaryTheme {
        DebugSettingsScreen(
            uiState = DebugSettingsUiState(
                modeText = "시연 모드 (Fake 차량)",
                currentGear = GearUiModel.D,
                extraDistanceText = "+3.0 km",
            ),
            onIntent = {},
        )
    }
}
