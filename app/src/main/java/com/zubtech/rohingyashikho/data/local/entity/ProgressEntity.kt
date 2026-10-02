package com.zubtech.rohingyashikho.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val lessonItemId: String,
    val masteryPercentage: Int = 0,
    val clickCount: Int = 0,
    val lastAccessed: Long = System.currentTimeMillis()
)

@Entity(tableName = "review_items")
data class ReviewItemEntity(
    @PrimaryKey val lessonItemId: String,
    val interval: Int, // days
    val easeFactor: Float,
    val dueDate: Long,
    val repetitions: Int
)

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String = "current_user",
    val userName: String = "",
    val userAge: String = "",
    val currentStreak: Int = 0,
    val lastLessonDate: Long = 0,
    val totalPoints: Int = 0,
    val levelsPassed: Int = 0,
    val isAlphabetQuizUnlocked: Boolean = false,
    val consonantProgress: Float = 0f,
    val vowelProgress: Float = 0f,
    val numberProgress: Float = 0f,
    val recognitionProgress: Float = 0f,
    val quizProgress: Float = 0f,
    val vowelRecognitionProgress: Float = 0f,
    val vowelQuizProgress: Float = 0f,
    val numberRecognitionProgress: Float = 0f,
    val numberQuizProgress: Float = 0f
)
