package com.endus.drivary.presentation.debug

data class DebugSettingsUiState(
    val modeText: String = "",
    /** 시연 모드에서만 앱에서 기어를 바꿀 수 있다. Pleos 모드는 Virtual Console로 바꾼다 */
    val isGearControlEnabled: Boolean = true,
    val currentGear: GearUiModel = GearUiModel.P,
    val extraDistanceText: String = "",
    val confirmDelaySeconds: Long = 10,
    val confirmDelayOptions: List<Long> = listOf(5, 10, 30),
    val distanceOptions: List<Double> = listOf(0.5, 3.0, 10.0),
    val places: List<PlaceUiModel> = emptyList(),
    val nextDestinationId: Long? = null,
)

data class PlaceUiModel(
    val id: Long,
    val name: String,
)

sealed interface DebugSettingsIntent {
    data class SelectGear(val gear: GearUiModel) : DebugSettingsIntent
    data class AddDistance(val km: Double) : DebugSettingsIntent
    data class SelectConfirmDelay(val seconds: Long) : DebugSettingsIntent
    data class SelectNextDestination(val placeId: Long) : DebugSettingsIntent
    data object ClickAddSampleTrips : DebugSettingsIntent
    data object ClickBack : DebugSettingsIntent
}

sealed interface DebugSettingsSideEffect {
    data object NavigateBack : DebugSettingsSideEffect
    data class ShowMessage(val message: String) : DebugSettingsSideEffect
}
