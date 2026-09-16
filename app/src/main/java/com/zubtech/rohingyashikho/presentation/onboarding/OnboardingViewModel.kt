package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    fun updateName(name: String) {
        _userName.value = name
    }

    fun updateAge(age: String) {
        _userAge.value = age
    }

    fun completeOnboarding(onComplete: () -> Unit) {
        viewModelScope.launch {
            val name = _userName.value
            val age = _userAge.value
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
