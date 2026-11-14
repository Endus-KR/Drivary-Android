package com.endus.drivary.presentation.home

data class HomeUiState(
    val trips: List<TripUiModel> = emptyList(),
)

sealed interface HomeIntent {
    data object ClickDebugSettings : HomeIntent
}

sealed interface HomeSideEffect {
    data object NavigateToDebugSettings : HomeSideEffect
}
