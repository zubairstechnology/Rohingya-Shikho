package com.zubtech.rohingyashikho.presentation.alphabet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.data.audio.AudioPlayer
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.ProgressEntity
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlphabetViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    private val progressDao: ProgressDao,
    private val audioPlayer: AudioPlayer
) : ViewModel() {

    val alphabetItems: StateFlow<List<LessonItem>> = contentRepository.getCourse()
        .map { course ->
            course?.levels?.find { it.id == "lvl_beginner" }
                ?.units?.find { it.id == "unit_hanifi_1" }
                ?.lessons?.flatMap { it.items } ?: emptyList()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val consonants: StateFlow<List<LessonItem>> = contentRepository.getConsonants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val consonantsForWriting: StateFlow<List<LessonItem>> = contentRepository.getConsonantsForWriting()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vowels: StateFlow<List<LessonItem>> = contentRepository.getVowels()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProgress: StateFlow<List<ProgressEntity>> = progressDao.getAllProgress()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userStats: StateFlow<UserStatsEntity> = progressDao.getUserStats()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserStatsEntity())

    private val _playingItemId = MutableStateFlow<String?>(null)
    val playingItemId: StateFlow<String?> = _playingItemId.asStateFlow()

    val isAudioPlaying: StateFlow<Boolean> = audioPlayer.isPlaying

    fun playAudio(item: LessonItem) {
        if (item.audioFileRef.isNotEmpty()) {
            _playingItemId.value = item.id
            audioPlayer.playAsset(item.audioFileRef)
        }
    }

    fun recordClick(itemId: String) {
        viewModelScope.launch {
            val currentProgress = allProgress.value.find { it.lessonItemId == itemId }
            val newClickCount = (currentProgress?.clickCount ?: 0) + 1
            val newMastery = (newClickCount * 20).coerceAtMost(100)
            
            progressDao.updateProgress(
                ProgressEntity(
                    lessonItemId = itemId,
                    clickCount = newClickCount,
                    masteryPercentage = newMastery,
                    lastAccessed = System.currentTimeMillis()
                )
            )

            updateUserStatsProgress()
        }
    }

    private suspend fun updateUserStatsProgress() {
        val currentStats = userStats.value
        val totalConsonants = consonants.value.size
        if (totalConsonants == 0) return

        val progressedConsonants = allProgress.value.count { it.clickCount >= 5 && consonants.value.any { c -> c.id == it.lessonItemId } }
        val newConsonantProgress = progressedConsonants.toFloat() / totalConsonants

        progressDao.updateUserStats(
            currentStats.copy(consonantProgress = newConsonantProgress)
        )
    }

    fun recordVowelClick(itemId: String) {
        viewModelScope.launch {
            val currentProgress = allProgress.value.find { it.lessonItemId == itemId }
            val newClickCount = (currentProgress?.clickCount ?: 0) + 1
            val newMastery = (newClickCount * 20).coerceAtMost(100)
            
            progressDao.updateProgress(
                ProgressEntity(
                    lessonItemId = itemId,
                    clickCount = newClickCount,
                    masteryPercentage = newMastery,
                    lastAccessed = System.currentTimeMillis()
                )
            )

            updateVowelStatsProgress()
        }
    }

    private suspend fun updateVowelStatsProgress() {
        val currentStats = userStats.value
        val totalVowels = vowels.value.size
        if (totalVowels == 0) return

        val progressedVowels = allProgress.value.count { it.clickCount >= 5 && vowels.value.any { v -> v.id == it.lessonItemId } }
        val newVowelProgress = progressedVowels.toFloat() / totalVowels

        progressDao.updateUserStats(
            currentStats.copy(vowelProgress = newVowelProgress)
        )
    }

    fun unlockAlphabetQuiz() {
        viewModelScope.launch {
            val currentStats = userStats.value
            progressDao.updateUserStats(currentStats.copy(isAlphabetQuizUnlocked = true))
        }
    }

    fun updateRecognitionProgress(progress: Float) {
        viewModelScope.launch {
            val currentStats = userStats.value
            progressDao.updateUserStats(currentStats.copy(recognitionProgress = progress))
        }
    }

    fun completeConsonantLevel(score: Int) {
        viewModelScope.launch {
            val currentStats = userStats.value
            val quizProgress = (score.toFloat() / 28f).coerceAtMost(1f)
            progressDao.updateUserStats(
                currentStats.copy(
                    quizProgress = quizProgress,
                    levelsPassed = currentStats.levelsPassed.coerceAtLeast(1),
                    totalPoints = currentStats.totalPoints + (score * 10)
                )
            )
        }
    }

    fun completeVowelLevel(score: Int) {
        viewModelScope.launch {
            val currentStats = userStats.value
            val totalVowels = vowels.value.size
            if (totalVowels == 0) return@launch
            
            val quizProgress = (score.toFloat() / totalVowels.toFloat()).coerceAtMost(1f)
            progressDao.updateUserStats(
                currentStats.copy(
                    vowelQuizProgress = quizProgress,
                    levelsPassed = currentStats.levelsPassed.coerceAtLeast(2),
                    totalPoints = currentStats.totalPoints + (score * 15)
                )
            )
        }
    }
    
    fun playAlphabetSong() {
        // Placeholder for alphabet song if available
        // audioPlayer.playAsset("audio/alphabet_song.mp3")
    }
}
