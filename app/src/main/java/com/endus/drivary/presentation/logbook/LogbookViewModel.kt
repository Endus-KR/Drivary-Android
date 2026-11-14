package com.endus.drivary.presentation.logbook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endus.drivary.domain.model.MonthlyLogbook
import com.endus.drivary.domain.usecase.BuildLogbookCsvUseCase
import com.endus.drivary.domain.usecase.GetMonthlyLogbookUseCase
import com.endus.drivary.domain.usecase.UnclassifiedTripsRemainException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Clock
import java.time.YearMonth
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class LogbookViewModel @Inject constructor(
    getMonthlyLogbookUseCase: GetMonthlyLogbookUseCase,
    private val buildLogbookCsvUseCase: BuildLogbookCsvUseCase,
    clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogbookUiState())
    val uiState: StateFlow<LogbookUiState> = _uiState.asStateFlow()

    private val _sideEffect = Channel<LogbookSideEffect>(Channel.BUFFERED)
    val sideEffect: Flow<LogbookSideEffect> = _sideEffect.receiveAsFlow()

    /** 보고 있는 달. 처음에는 이번 달 */
    private val month = MutableStateFlow(YearMonth.now(clock))

    /** 내보내기에 쓰는 지금 화면의 운행기록부 */
    private var currentLogbook: MonthlyLogbook? = null

    init {
        month
            .flatMapLatest { getMonthlyLogbookUseCase(it) }
            .onEach { logbook ->
                currentLogbook = logbook
                _uiState.value = logbook.toUiState()
            }
            .catch { throwable ->
                Timber.e(throwable)
                sendSideEffect(LogbookSideEffect.ShowMessage("운행기록부를 불러오지 못했어요"))
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: LogbookIntent) {
        when (intent) {
            LogbookIntent.ClickPreviousMonth -> month.update { it.minusMonths(1) }
            LogbookIntent.ClickNextMonth -> month.update { it.plusMonths(1) }
            LogbookIntent.ClickExport -> export()
            LogbookIntent.ClickBack -> sendSideEffect(LogbookSideEffect.NavigateBack)
        }
    }

    private fun export() {
        val logbook = currentLogbook ?: return
        buildLogbookCsvUseCase(logbook)
            .onSuccess { csv -> sendSideEffect(LogbookSideEffect.ShareCsv(csv.fileName, csv.content)) }
            .onFailure { throwable ->
                Timber.e(throwable)
                val message = if (throwable is UnclassifiedTripsRemainException) {
                    "미처리 운행 ${throwable.count}건을 먼저 분류해 주세요"
                } else {
                    "운행기록부 파일을 만들지 못했어요"
                }
                sendSideEffect(LogbookSideEffect.ShowMessage(message))
            }
    }

    private fun sendSideEffect(effect: LogbookSideEffect) {
        viewModelScope.launch { _sideEffect.send(effect) }
    }
}
