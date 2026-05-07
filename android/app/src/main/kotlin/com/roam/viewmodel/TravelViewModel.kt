package com.roam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.roam.data.ApiClient
import com.roam.data.TravelRepository
import com.roam.data.TravelSearchRequest
import com.roam.data.TravelUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TravelViewModel : ViewModel() {
    private val repository = TravelRepository(ApiClient.apiService)

    private val _uiState = MutableStateFlow<TravelUiState>(TravelUiState.Idle)
    val uiState: StateFlow<TravelUiState> = _uiState.asStateFlow()

    fun searchTravelOptions(
        origin: String,
        destination: String,
        startDate: String,
        endDate: String,
        budget: Int,
        currency: String = "USD"
    ) {
        viewModelScope.launch {
            _uiState.value = TravelUiState.Loading
            val request = TravelSearchRequest(
                origin = origin,
                destination = destination,
                startDate = startDate,
                endDate = endDate,
                budget = budget,
                currency = currency
            )
            repository.searchTravelOptions(request)
                .onSuccess { response ->
                    _uiState.value = TravelUiState.Success(response, currency)
                }
                .onFailure { error ->
                    _uiState.value = TravelUiState.Error(error.message ?: "Unknown error")
                }
        }
    }

    fun resetState() {
        _uiState.value = TravelUiState.Idle
    }
}

