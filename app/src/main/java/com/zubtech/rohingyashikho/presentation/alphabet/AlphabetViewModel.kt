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

    val numbers: StateFlow<List<LessonItem>> = contentRepository.getNumbers()
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
        val newConsonantProgress = progressedConsonants.toFloat() / totalConsonants.toFloat()

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
        val newVowelProgress = progressedVowels.toFloat() / totalVowels.toFloat()

        progressDao.updateUserStats(
            currentStats.copy(vowelProgress = newVowelProgress)
        )
    }

    fun recordNumberClick(itemId: String) {
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
            updateNumberStatsProgress()
        }
    }

    private suspend fun updateNumberStatsProgress() {
        val currentStats = userStats.value
        val totalNumbers = numbers.value.size
        if (totalNumbers == 0) return

        val progressedNumbers = allProgress.value.count { it.clickCount >= 5 && numbers.value.any { n -> n.id == it.lessonItemId } }
        val newNumberProgress = progressedNumbers.toFloat() / totalNumbers.toFloat()

        progressDao.updateUserStats(
            currentStats.copy(numberProgress = newNumberProgress)
        )
    }

    // --- Separate Review Mode Tracking ---

    fun recordReviewClick(itemId: String, category: String) {
        viewModelScope.launch {
            val reviewId = "rev_${category}_${itemId}"
            val currentProgress = allProgress.value.find { it.lessonItemId == reviewId }
            val newClickCount = (currentProgress?.clickCount ?: 0) + 1
            val newMastery = (newClickCount * 20).coerceAtMost(100)
            
            progressDao.updateProgress(
                ProgressEntity(
                    lessonItemId = reviewId,
                    clickCount = newClickCount,
                    masteryPercentage = newMastery,
                    lastAccessed = System.currentTimeMillis()
                )
            )

            updateReviewStatsProgress(category)
        }
    }

    private suspend fun updateReviewStatsProgress(category: String) {
        val currentStats = userStats.value
        val items = when(category) {
            "consonant" -> consonants.value
            "vowel" -> vowels.value
            "number" -> numbers.value
            else -> emptyList()
        }
        if (items.isEmpty()) return

        val totalItems = items.size
        val progressedItems = allProgress.value.count { p -> 
            p.lessonItemId.startsWith("rev_${category}_") && p.clickCount >= 5 
        }
        val newProgress = progressedItems.toFloat() / totalItems.toFloat()

        val updatedStats = when(category) {
            "consonant" -> currentStats.copy(recognitionProgress = newProgress)
            "vowel" -> currentStats.copy(vowelRecognitionProgress = newProgress)
            "number" -> currentStats.copy(numberRecognitionProgress = newProgress)
            else -> currentStats
        }
        
        progressDao.updateUserStats(updatedStats)
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

    fun updateVowelRecognitionProgress(progress: Float) {
        viewModelScope.launch {
            val currentStats = userStats.value
            progressDao.updateUserStats(currentStats.copy(vowelRecognitionProgress = progress))
        }
    }

    fun updateNumberRecognitionProgress(progress: Float) {
        viewModelScope.launch {
            val currentStats = userStats.value
            progressDao.updateUserStats(currentStats.copy(numberRecognitionProgress = progress))
        }
    }

    fun completeConsonantLevel(score: Int) {
        viewModelScope.launch {
            val currentStats = userStats.value
            val totalCons = if (consonants.value.isNotEmpty()) consonants.value.size.toFloat() else 28f
            val quizProgress = (score.toFloat() / totalCons).coerceAtMost(1f)
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

    fun completeNumberLevel(score: Int) {
        viewModelScope.launch {
            val currentStats = userStats.value
            val totalNum = numbers.value.size
            if (totalNum == 0) return@launch
            
            val quizProgress = (score.toFloat() / totalNum.toFloat()).coerceAtMost(1f)
            progressDao.updateUserStats(
                currentStats.copy(
                    numberQuizProgress = quizProgress,
                    levelsPassed = currentStats.levelsPassed.coerceAtLeast(3),
                    totalPoints = currentStats.totalPoints + (score * 20)
                )
            )
        }
    }
    
    fun playAlphabetSong() {
        // Placeholder for alphabet song if available
        // audioPlayer.playAsset("audio/alphabet_song.mp3")
    }
}
