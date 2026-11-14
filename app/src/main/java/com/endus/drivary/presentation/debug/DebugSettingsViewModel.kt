package com.endus.drivary.presentation.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endus.drivary.domain.usecase.AddDemoDistanceUseCase
import com.endus.drivary.domain.usecase.AddSampleTripsUseCase
import com.endus.drivary.domain.usecase.GetPlacesUseCase
import com.endus.drivary.domain.usecase.ObserveNextDestinationUseCase
import com.endus.drivary.domain.usecase.SetNextDestinationUseCase
import com.endus.drivary.domain.usecase.ObserveConfirmDelayUseCase
import com.endus.drivary.domain.usecase.ObserveCurrentGearUseCase
import com.endus.drivary.domain.usecase.ObserveDemoExtraDistanceUseCase
import com.endus.drivary.domain.usecase.GetVehicleModeUseCase
import com.endus.drivary.domain.model.VehicleMode
import com.endus.drivary.domain.usecase.SetConfirmDelayUseCase
import com.endus.drivary.domain.usecase.SetDemoGearUseCase
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
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DebugSettingsViewModel @Inject constructor(
    observeCurrentGearUseCase: ObserveCurrentGearUseCase,
    observeDemoExtraDistanceUseCase: ObserveDemoExtraDistanceUseCase,
    getVehicleModeUseCase: GetVehicleModeUseCase,
    observeConfirmDelayUseCase: ObserveConfirmDelayUseCase,
    private val setDemoGearUseCase: SetDemoGearUseCase,
    private val addDemoDistanceUseCase: AddDemoDistanceUseCase,
    private val setConfirmDelayUseCase: SetConfirmDelayUseCase,
    private val addSampleTripsUseCase: AddSampleTripsUseCase,
    private val getPlacesUseCase: GetPlacesUseCase,
    observeNextDestinationUseCase: ObserveNextDestinationUseCase,
    private val setNextDestinationUseCase: SetNextDestinationUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DebugSettingsUiState())
    val uiState: StateFlow<DebugSettingsUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<DebugSettingsSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<DebugSettingsSideEffect> = _sideEffect.receiveAsFlow()

    private val kmFormat = DecimalFormat("#,##0.0")

    init {
        observeCurrentGearUseCase()
            .onEach { gear -> _uiState.update { it.copy(currentGear = gear.toUiModel()) } }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)

        getVehicleModeUseCase()
            .onSuccess { mode ->
                _uiState.update {
                    it.copy(
                        modeText = mode.toText(),
                        isGearControlEnabled = mode == VehicleMode.DEMO,
                    )
                }
            }
            .onFailure { throwable -> Timber.e(throwable) }

        observeDemoExtraDistanceUseCase()
            .onEach { km -> _uiState.update { it.copy(extraDistanceText = "+${kmFormat.format(km)} km") } }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)

        loadPlaces()

        observeNextDestinationUseCase()
            .onEach { placeId -> _uiState.update { it.copy(nextDestinationId = placeId) } }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)

        observeConfirmDelayUseCase()
            .onEach { delay -> _uiState.update { it.copy(confirmDelaySeconds = delay.inWholeSeconds) } }
            .catch { throwable -> Timber.e(throwable) }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: DebugSettingsIntent) {
        when (intent) {
            is DebugSettingsIntent.SelectGear -> selectGear(intent.gear)
            is DebugSettingsIntent.AddDistance -> addDistance(intent.km)
            is DebugSettingsIntent.SelectConfirmDelay -> selectConfirmDelay(intent.seconds)
            is DebugSettingsIntent.SelectNextDestination -> selectNextDestination(intent.placeId)
            DebugSettingsIntent.ClickAddSampleTrips -> addSampleTrips()
            DebugSettingsIntent.ClickBack -> sendSideEffect(DebugSettingsSideEffect.NavigateBack)
        }
    }

    private fun selectGear(gear: GearUiModel) {
        viewModelScope.launch {
            setDemoGearUseCase(gear.toDomain())
                .onFailure { throwable -> handleFailure(throwable, "기어를 바꾸지 못했어요") }
        }
    }

    private fun addDistance(km: Double) {
        viewModelScope.launch {
            addDemoDistanceUseCase(km)
                .onFailure { throwable -> handleFailure(throwable, "주행거리를 늘리지 못했어요") }
        }
    }

    private fun selectConfirmDelay(seconds: Long) {
        viewModelScope.launch {
            setConfirmDelayUseCase(seconds.seconds)
                .onFailure { throwable -> handleFailure(throwable, "확정 대기 시간을 바꾸지 못했어요") }
        }
    }

    private fun loadPlaces() {
        viewModelScope.launch {
            getPlacesUseCase()
                .onSuccess { places ->
                    _uiState.update { state ->
                        state.copy(places = places.map { PlaceUiModel(id = it.id, name = it.name) })
                    }
                }
                .onFailure { throwable -> handleFailure(throwable, "장소 목록을 불러오지 못했어요") }
        }
    }

    private fun selectNextDestination(placeId: Long) {
        viewModelScope.launch {
            setNextDestinationUseCase(placeId)
                .onFailure { throwable -> handleFailure(throwable, "다음 도착지를 바꾸지 못했어요") }
        }
    }

    private fun addSampleTrips() {
        viewModelScope.launch {
            addSampleTripsUseCase()
                .onSuccess { trips -> sendSideEffect(DebugSettingsSideEffect.ShowMessage("샘플 운행 ${trips.size}건을 넣었어요")) }
                .onFailure { throwable -> handleFailure(throwable, "샘플 운행을 넣지 못했어요") }
        }
    }

    private fun handleFailure(throwable: Throwable, message: String) {
        Timber.e(throwable)
        sendSideEffect(DebugSettingsSideEffect.ShowMessage(message))
    }

    private fun sendSideEffect(effect: DebugSettingsSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}

private fun VehicleMode.toText(): String = when (this) {
    VehicleMode.PLEOS -> "Pleos 차량 연결됨"
    VehicleMode.DEMO -> "시연 모드 (Fake 차량)"
}
