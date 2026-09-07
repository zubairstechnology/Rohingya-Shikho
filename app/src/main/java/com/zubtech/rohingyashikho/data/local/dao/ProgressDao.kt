package com.zubtech.rohingyashikho.data.local.dao

import androidx.room.*
import com.zubtech.rohingyashikho.data.local.entity.ProgressEntity
import com.zubtech.rohingyashikho.data.local.entity.ReviewItemEntity
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress")
    fun getAllProgress(): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateProgress(progress: ProgressEntity)

    @Query("SELECT * FROM review_items WHERE dueDate <= :currentTime")
    fun getDueReviewItems(currentTime: Long): Flow<List<ReviewItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateReviewItem(reviewItem: ReviewItemEntity)

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getUserStats(userId: String = "current_user"): Flow<UserStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserStats(stats: UserStatsEntity)
}
