package com.endus.drivary.presentation.home

data class HomeUiState(
    /** 운행 중 / 확정 대기 표시. 주차 중이면 null */
    val recordingStatus: RecordingStatusUiModel? = null,
    val trips: List<TripUiModel> = emptyList(),
    /** 가장 오래된 미처리 운행. 없으면 카드를 숨긴다 */
    val pendingCard: PendingTripCardUiModel? = null,
    val cardStep: PendingCardStep = PendingCardStep.Question,
    val purposeOptions: List<PurposeUiModel> = PurposeUiModel.ALL,
    /** 분류 저장 중에는 카드 버튼을 막는다 */
    val isClassifying: Boolean = false,
)

/** 미처리 카드 단계 */
enum class PendingCardStep {
    /** 맞아요/다른 목적/개인 또는 업무/개인 */
    Question,

    /** 업무 목적 고르기 */
    PurposeSelection,
}

sealed interface HomeIntent {
    data object ClickDebugSettings : HomeIntent
    data object ClickLogbook : HomeIntent

    /** 제안한 목적이 맞아요 */
    data object ClickConfirmSuggestion : HomeIntent

    /** 다른 목적 또는 업무 → 목적 고르기 */
    data object ClickChoosePurpose : HomeIntent
    data object ClickPersonal : HomeIntent
    data class SelectPurpose(val purpose: PurposeUiModel) : HomeIntent
    data object ClickCancelPurposeSelection : HomeIntent
}

sealed interface HomeSideEffect {
    data object NavigateToDebugSettings : HomeSideEffect
    data object NavigateToLogbook : HomeSideEffect
    data class ShowMessage(val message: String) : HomeSideEffect
}
