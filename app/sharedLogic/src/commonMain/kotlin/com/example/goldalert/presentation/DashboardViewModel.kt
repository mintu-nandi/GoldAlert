package com.example.goldalert.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldalert.data.local.entity.GoldPriceEntity
import com.example.goldalert.domain.repository.GoldRepository
import com.example.goldalert.domain.usecase.SyncGoldPriceUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import com.example.goldalert.util.watch

sealed interface SyncState {
    object Idle : SyncState
    object Loading : SyncState
    object Success : SyncState
    data class Error(val message: String) : SyncState
}

class DashboardViewModel(
    private val repository: GoldRepository,
    private val syncGoldPriceUseCase: SyncGoldPriceUseCase
) : ViewModel() {

    val latestPrice: StateFlow<GoldPriceEntity?> = repository.observeLatestPrice()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val priceHistory: StateFlow<List<GoldPriceEntity>> = repository.observePriceHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var autoRefreshJob: Job? = null

    init {
        startAutoRefreshLoop()
    }

    fun startAutoRefreshLoop() {
        autoRefreshJob?.cancel()
        autoRefreshJob = viewModelScope.launch {
            while (isActive) {
                executeSync()
                delay(60.seconds)
            }
        }
    }

    fun refreshPrice() {
        viewModelScope.launch {
            executeSync()
            startAutoRefreshLoop()
        }
    }

    private suspend fun executeSync() {
        _syncState.value = SyncState.Loading
        val result = syncGoldPriceUseCase()
        if (result.isSuccess) {
            _syncState.value = SyncState.Success
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Failed to fetch live gold price."
            _syncState.value = SyncState.Error(errorMsg)
        }
    }

    override fun onCleared() {
        super.onCleared()
        autoRefreshJob?.cancel()
    }

    fun watchLatestPrice(block: (GoldPriceEntity?) -> Unit): () -> Unit = latestPrice.watch(block)
    fun watchPriceHistory(block: (List<GoldPriceEntity>) -> Unit): () -> Unit = priceHistory.watch(block)
    fun watchSyncState(block: (SyncState) -> Unit): () -> Unit = syncState.watch(block)
}
