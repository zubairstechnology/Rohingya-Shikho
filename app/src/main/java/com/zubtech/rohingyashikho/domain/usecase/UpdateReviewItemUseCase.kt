package com.zubtech.rohingyashikho.domain.usecase

import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.ReviewItemEntity
import javax.inject.Inject
import kotlin.math.max

class UpdateReviewItemUseCase @Inject constructor(
    private val progressDao: ProgressDao
) {
    /**
     * Simplified SM-2 algorithm
     * quality: 0-5 (0: total failure, 5: perfect response)
     */
    suspend operator fun invoke(lessonItemId: String, quality: Int, currentReview: ReviewItemEntity?) {
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        val (newInterval, newEaseFactor, newRepetitions) = if (quality >= 3) {
            val repetitions = (currentReview?.repetitions ?: 0) + 1
            val interval = when (repetitions) {
                1 -> 1
                2 -> 6
                else -> {
                    val prevInterval = currentReview?.interval ?: 1
                    val prevEase = currentReview?.easeFactor ?: 2.5f
                    (prevInterval * prevEase).toInt()
                }
            }
            val easeFactor = (currentReview?.easeFactor ?: 2.5f) + 
                (0.1f - (5 - quality) * (0.08f + (5 - quality) * 0.02f))
            
            Triple(interval, max(1.3f, easeFactor), repetitions)
        } else {
            Triple(1, currentReview?.easeFactor ?: 2.5f, 0)
        }

        val nextDueDate = now + (newInterval * dayInMillis)

        progressDao.updateReviewItem(
            ReviewItemEntity(
                lessonItemId = lessonItemId,
                interval = newInterval,
                easeFactor = newEaseFactor,
                dueDate = nextDueDate,
                repetitions = newRepetitions
            )
        )
    }
}
