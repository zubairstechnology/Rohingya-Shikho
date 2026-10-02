package com.zubtech.rohingyashikho.presentation.alphabet

import com.zubtech.rohingyashikho.domain.model.LessonItem

data class QuizQuestionData(
    val correctItem: LessonItem,
    val options: List<LessonItem>
)
