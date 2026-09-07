package com.zubtech.rohingyashikho.presentation.quiz

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class QuizQuestion(
    val item: LessonItem,
    val options: List<String>,
    val type: QuizType
)

enum class QuizType {
    SCRIPT_TO_MEANING,
    MEANING_TO_SCRIPT,
    AUDIO_TO_SCRIPT
}

data class QuizUiState(
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val isLoading: Boolean = true
) {
    val currentQuestion: QuizQuestion? get() = questions.getOrNull(currentQuestionIndex)
}

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val contentRepository: ContentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val unitId: String = checkNotNull(savedStateHandle["unitId"])
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            contentRepository.getUnit(unitId).collect { unit ->
                unit?.let {
                    val allItems = unit.lessons.flatMap { it.items }
                    val questions = allItems.map { item ->
                        generateQuestion(item, allItems)
                    }.shuffled()
                    _uiState.update { it.copy(questions = questions, isLoading = false) }
                }
            }
        }
    }

    private fun generateQuestion(item: LessonItem, allItems: List<LessonItem>): QuizQuestion {
        val type = QuizType.entries.toTypedArray().random()
        val options = mutableListOf<String>()
        
        when (type) {
            QuizType.SCRIPT_TO_MEANING -> {
                options.add(item.englishMeaning)
                val distractors = allItems.filter { it.id != item.id }.map { it.englishMeaning }.shuffled().take(3)
                options.addAll(distractors)
            }
            QuizType.MEANING_TO_SCRIPT -> {
                options.add(item.scriptText)
                val distractors = allItems.filter { it.id != item.id }.map { it.scriptText }.shuffled().take(3)
                options.addAll(distractors)
            }
            QuizType.AUDIO_TO_SCRIPT -> {
                options.add(item.scriptText)
                val distractors = allItems.filter { it.id != item.id }.map { it.scriptText }.shuffled().take(3)
                options.addAll(distractors)
            }
        }
        
        return QuizQuestion(item, options.shuffled(), type)
    }

    fun submitAnswer(answer: String) {
        val currentState = _uiState.value
        val currentQuestion = currentState.currentQuestion ?: return
        
        val isCorrect = when (currentQuestion.type) {
            QuizType.SCRIPT_TO_MEANING -> answer == currentQuestion.item.englishMeaning
            QuizType.MEANING_TO_SCRIPT, QuizType.AUDIO_TO_SCRIPT -> answer == currentQuestion.item.scriptText
        }

        if (isCorrect) {
            _uiState.update { it.copy(score = it.score + 1) }
        }

        val nextIndex = currentState.currentQuestionIndex + 1
        if (nextIndex < currentState.questions.size) {
            _uiState.update { it.copy(currentQuestionIndex = nextIndex) }
        } else {
            _uiState.update { it.copy(isFinished = true) }
        }
    }
}
