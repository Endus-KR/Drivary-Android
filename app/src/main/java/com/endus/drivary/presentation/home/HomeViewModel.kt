package com.endus.drivary.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endus.drivary.domain.usecase.ObserveTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeTripsUseCase: ObserveTripsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<HomeSideEffect> = _sideEffect.receiveAsFlow()

    init {
        observeTripsUseCase()
            .onEach { trips ->
                // 최근 운행이 위에 오도록 뒤집는다
                _uiState.update { state -> state.copy(trips = trips.asReversed().map { it.toUiModel() }) }
            }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.ClickDebugSettings -> sendSideEffect(HomeSideEffect.NavigateToDebugSettings)
            HomeIntent.ClickLogbook -> sendSideEffect(HomeSideEffect.NavigateToLogbook)
        }
    }

    private fun sendSideEffect(effect: HomeSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}
