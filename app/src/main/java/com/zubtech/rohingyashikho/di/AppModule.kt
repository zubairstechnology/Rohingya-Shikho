package com.zubtech.rohingyashikho.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.zubtech.rohingyashikho.data.local.RohingyaDatabase
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.repository.ContentRepositoryImpl
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): RohingyaDatabase {
        return Room.databaseBuilder(
            context,
            RohingyaDatabase::class.java,
            "rohingya_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideProgressDao(database: RohingyaDatabase): ProgressDao {
        return database.progressDao()
    }

    @Provides
    @Singleton
    fun provideContentRepository(
        @ApplicationContext context: Context,
        gson: Gson
    ): ContentRepository {
        return ContentRepositoryImpl(context, gson)
    }
}
