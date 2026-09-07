package com.zubtech.rohingyashikho.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.local.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userPreferences: UserPreferences
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
            userPreferences.saveUserProfile(_userName.value, _userAge.value)
            // Here you would also add Firebase Firestore logic if initialized
            onComplete()
        }
    }

    suspend fun isOnboardingCompleted(): Boolean {
        return userPreferences.onboardingCompleted.first()
    }
}
