package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val progressDao: ProgressDao
) : ViewModel() {

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _userAge = MutableStateFlow("")
    val userAge: StateFlow<String> = _userAge

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    fun updateName(name: String) {
        _userName.value = name
        checkAdminStatus()
    }

    fun updateAge(age: String) {
        _userAge.value = age
        checkAdminStatus()
    }

    private fun checkAdminStatus() {
        _isAdmin.value = _userName.value.trim() == "RohingyaShikhoZubairAdmin" && 
                         _userAge.value.trim() == "2026"
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val name = _userName.value.trim()
            val age = _userAge.value.trim()
            userPreferences.saveUserProfile(name, age)
            
            // Sync to Room UserStatsEntity
            progressDao.updateUserStats(
                UserStatsEntity(
                    userId = "current_user",
                    userName = name,
                    userAge = age
                )
            )

            onComplete()
        }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return userPreferences.onboardingCompleted.first()
    }
}
