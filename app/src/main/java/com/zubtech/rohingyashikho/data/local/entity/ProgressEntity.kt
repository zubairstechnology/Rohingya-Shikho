package com.zubtech.rohingyashikho.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey val lessonItemId: String,
    val masteryPercentage: Int,
    val lastAccessed: Long
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
    val currentStreak: Int,
    val lastLessonDate: Long,
    val totalPoints: Int
)
