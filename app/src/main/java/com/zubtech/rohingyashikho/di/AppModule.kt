package com.zubtech.rohingyashikho.di

import android.content.Context
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.room.Room
import com.google.gson.Gson
import com.zubtech.rohingyashikho.data.local.RohingyaDatabase
import com.zubtech.rohingyashikho.data.local.dao.ProgressDao
import com.zubtech.rohingyashikho.data.remote.GoogleCloudTtsApi
import com.zubtech.rohingyashikho.data.repository.ContentRepositoryImpl
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleCloudTtsApi(okHttpClient: OkHttpClient): GoogleCloudTtsApi {
        return Retrofit.Builder()
            .baseUrl(GoogleCloudTtsApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleCloudTtsApi::class.java)
    }

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

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheDir = File(context.cacheDir, "media")
        val evictor = LeastRecentlyUsedCacheEvictor(100 * 1024 * 1024) // 100MB
        val databaseProvider = StandaloneDatabaseProvider(context)
        return SimpleCache(cacheDir, evictor, databaseProvider)
    }

    @Provides
    @Singleton
    fun provideDataSourceFactory(
        @ApplicationContext context: Context,
        cache: Cache
    ): DataSource.Factory {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
        
        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(httpDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }
}
