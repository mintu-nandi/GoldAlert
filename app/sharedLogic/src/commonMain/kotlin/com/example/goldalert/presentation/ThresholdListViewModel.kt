package com.example.goldalert.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goldalert.data.local.entity.ThresholdEntity
import com.example.goldalert.domain.usecase.AddThresholdUseCase
import com.example.goldalert.domain.usecase.DeleteThresholdUseCase
import com.example.goldalert.domain.usecase.ObserveAllThresholdsUseCase
import com.example.goldalert.domain.usecase.ToggleThresholdUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.goldalert.util.watch

/**
 * Shared KMP Presentation ViewModel for managing user price alert thresholds.
 */
class ThresholdListViewModel(
    observeAllThresholdsUseCase: ObserveAllThresholdsUseCase,
    private val addThresholdUseCase: AddThresholdUseCase,
    private val toggleThresholdUseCase: ToggleThresholdUseCase,
    private val deleteThresholdUseCase: DeleteThresholdUseCase
) : ViewModel() {

    /**
     * Observes the complete list of alert thresholds.
     */
    val thresholds: StateFlow<List<ThresholdEntity>> = observeAllThresholdsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * Inserts a new alert threshold.
     */
    fun addThreshold(price: Double, direction: String, label: String = "") {
        viewModelScope.launch {
            addThresholdUseCase(price, direction, label)
        }
    }

    /**
     * Toggles the active state of a threshold.
     */
    fun toggleThreshold(threshold: ThresholdEntity) {
        viewModelScope.launch {
            toggleThresholdUseCase(threshold)
        }
    }

    /**
     * Deletes a threshold.
     */
    fun deleteThreshold(threshold: ThresholdEntity) {
        viewModelScope.launch {
            deleteThresholdUseCase(threshold)
        }
    }

    /**
     * Deletes a threshold by ID.
     */
    fun deleteThresholdById(id: Long) {
        viewModelScope.launch {
            deleteThresholdUseCase(id)
        }
    }

    fun watchThresholds(block: (List<ThresholdEntity>) -> Unit): () -> Unit = thresholds.watch(block)
}
