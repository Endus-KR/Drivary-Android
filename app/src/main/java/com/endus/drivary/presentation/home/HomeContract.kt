package com.endus.drivary.presentation.home

data class HomeUiState(
    val trips: List<TripUiModel> = emptyList(),
)

sealed interface HomeIntent {
    data object ClickDebugSettings : HomeIntent
    data object ClickLogbook : HomeIntent
}

sealed interface HomeSideEffect {
    data object NavigateToDebugSettings : HomeSideEffect
    data object NavigateToLogbook : HomeSideEffect
}
