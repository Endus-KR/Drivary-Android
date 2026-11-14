package com.endus.drivary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endus.drivary.domain.model.RecordingStatus
import com.endus.drivary.domain.model.TripClassification
import com.endus.drivary.domain.usecase.ClassifyTripUseCase
import com.endus.drivary.domain.usecase.ObservePendingTripCardUseCase
import com.endus.drivary.domain.usecase.ObserveRecordingStatusUseCase
import com.endus.drivary.domain.usecase.ObserveTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    observeRecordingStatusUseCase: ObserveRecordingStatusUseCase,
    private val clock: Clock,
    observeTripsUseCase: ObserveTripsUseCase,
    observePendingTripCardUseCase: ObservePendingTripCardUseCase,
    private val classifyTripUseCase: ClassifyTripUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<HomeSideEffect> = _sideEffect.receiveAsFlow()

    init {
        observeRecordingStatusUseCase()
            .flatMapLatest { status ->
                if (status is RecordingStatus.ConfirmPending) {
                    // 확정까지 남은 시간을 줄여 가며 보여 준다
                    flow {
                        while (true) {
                            emit(status.toUiModel(clock.instant()))
                            delay(COUNTDOWN_TICK_MS)
                        }
                    }
                } else {
                    flowOf(status.toUiModel(clock.instant()))
                }
            }
            .onEach { status -> _uiState.update { it.copy(recordingStatus = status) } }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)

        observeTripsUseCase()
            .onEach { trips ->
                // 최근 운행이 위에 오도록 뒤집는다
                _uiState.update { state -> state.copy(trips = trips.asReversed().map { it.toUiModel() }) }
            }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)

        observePendingTripCardUseCase()
            .onEach { card ->
                val newCard = card?.toUiModel()
                _uiState.update { state ->
                    // 다른 운행으로 넘어가면 첫 질문부터 다시 묻는다
                    val step = if (state.pendingCard?.tripId == newCard?.tripId) {
                        state.cardStep
                    } else {
                        PendingCardStep.Question
                    }
                    state.copy(pendingCard = newCard, cardStep = step)
                }
            }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ClickDebugSettings -> sendSideEffect(HomeSideEffect.NavigateToDebugSettings)
            HomeIntent.ClickLogbook -> sendSideEffect(HomeSideEffect.NavigateToLogbook)
            HomeIntent.ClickConfirmSuggestion -> {
                val purpose = _uiState.value.pendingCard?.suggestedPurpose ?: return
                classify(TripClassification.Business(purpose.toDomain()))
            }
            HomeIntent.ClickChoosePurpose -> _uiState.update { it.copy(cardStep = PendingCardStep.PurposeSelection) }
            HomeIntent.ClickCancelPurposeSelection -> _uiState.update { it.copy(cardStep = PendingCardStep.Question) }
            HomeIntent.ClickPersonal -> classify(TripClassification.Personal)
            is HomeIntent.SelectPurpose -> classify(TripClassification.Business(intent.purpose.toDomain()))
        }
    }

    private fun classify(classification: TripClassification) {
        val state = _uiState.value
        val tripId = state.pendingCard?.tripId ?: return
        if (state.isClassifying) return
        _uiState.update { it.copy(isClassifying = true) }
        viewModelScope.launch {
            classifyTripUseCase(tripId, classification)
                .onFailure { throwable ->
                    Timber.e(throwable)
                    sendSideEffect(HomeSideEffect.ShowMessage("분류를 저장하지 못했어요"))
                }
            _uiState.update { it.copy(isClassifying = false) }
        }
    }

    private fun sendSideEffect(effect: HomeSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }

    private companion object {
        const val COUNTDOWN_TICK_MS = 250L
    }
}
