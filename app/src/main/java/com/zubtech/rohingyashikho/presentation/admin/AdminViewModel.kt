package com.zubtech.rohingyashikho.presentation.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.domain.model.AppUpdateInfo
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdminUiState(
    val updateInfo: AppUpdateInfo = AppUpdateInfo(),
    val isUpdating: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repository: ContentRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    val isAdmin: StateFlow<Boolean> = combine(
        userPreferences.userName,
        userPreferences.userAge
    ) { name, age ->
        name?.trim() == "RohingyaShikhoZubairAdmin" && age?.trim() == "2026"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            repository.getAppUpdateInfo().collect { info ->
                _uiState.value = _uiState.value.copy(updateInfo = info)
            }
        }
    }

    fun updateAppInfo(info: AppUpdateInfo) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUpdating = true)
            try {
                repository.updateAppInfo(info)
                _uiState.value = _uiState.value.copy(isUpdating = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isUpdating = false, error = e.message)
            }
        }
    }
}
