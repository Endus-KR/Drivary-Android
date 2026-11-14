package com.endus.drivary.presentation.debug

data class DebugSettingsUiState(
    val currentGear: GearUiModel = GearUiModel.P,
    val odometerText: String = "",
    val confirmDelaySeconds: Long = 10,
    val confirmDelayOptions: List<Long> = listOf(5, 10, 30),
    val distanceOptions: List<Double> = listOf(0.5, 3.0, 10.0),
)

sealed interface DebugSettingsIntent {
    data class SelectGear(val gear: GearUiModel) : DebugSettingsIntent
    data class AddDistance(val km: Double) : DebugSettingsIntent
    data class SelectConfirmDelay(val seconds: Long) : DebugSettingsIntent
    data object ClickBack : DebugSettingsIntent
}

sealed interface DebugSettingsSideEffect {
    data object NavigateBack : DebugSettingsSideEffect
    data class ShowMessage(val message: String) : DebugSettingsSideEffect
}
