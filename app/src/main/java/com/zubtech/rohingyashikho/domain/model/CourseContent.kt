package com.zubtech.rohingyashikho.domain.model

data class Course(
    val id: String,
    val title: String,
    val levels: List<Level>
)

data class Level(
    val id: String,
    val title: String,
    val units: List<Unit>
)

data class Unit(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val title: String,
    val items: List<LessonItem>
)

data class LessonItem(
    val id: String,
    val scriptText: String, // Hanifi Unicode
    val transliteration: String,
    val ipa: String? = null,
    val englishMeaning: String,
    val audioFileRef: String,
    val exampleSentence: String? = null
)
