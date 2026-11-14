package com.endus.drivary.presentation.logbook

data class LogbookUiState(
    val monthText: String = "",
    /** 사용자 칸(부서, 성명). 화면에서는 줄마다 반복하지 않고 한 번만 보여 준다 */
    val driverText: String = "",
    val rows: List<LogbookRowUiModel> = emptyList(),
    val totalDistanceText: String = "0 km",
    val businessUseDistanceText: String = "0 km",
    val businessUseRatioText: String = "0.0%",
    val isExportEnabled: Boolean = false,
    /** 내보낼 수 없는 이유. 내보낼 수 있으면 null */
    val exportBlockedMessage: String? = null,
)

sealed interface LogbookIntent {
    data object ClickPreviousMonth : LogbookIntent
    data object ClickNextMonth : LogbookIntent
    data object ClickExport : LogbookIntent
    data object ClickBack : LogbookIntent
}

sealed interface LogbookSideEffect {
    data object NavigateBack : LogbookSideEffect

    /** CSV 파일을 만들어 안드로이드 공유 시트로 보낸다 */
    data class ShareCsv(val fileName: String, val content: String) : LogbookSideEffect

    data class ShowMessage(val message: String) : LogbookSideEffect
}
