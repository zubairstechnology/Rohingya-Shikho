package com.zubtech.rohingyashikho.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.local.entity.ProgressEntity
import com.zubtech.rohingyashikho.data.local.entity.ReviewItemEntity
import com.zubtech.rohingyashikho.data.local.entity.UserStatsEntity

@Database(
    entities = [ProgressEntity::class, ReviewItemEntity::class, UserStatsEntity::class],
    version = 8,
    exportSchema = false
)
abstract class RohingyaDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
}
