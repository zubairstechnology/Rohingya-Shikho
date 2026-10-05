package com.zubtech.rohingyashikho.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zubtech.rohingyashikho.domain.model.AppUpdateInfo
import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.model.Lesson
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.model.Unit
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ContentRepository {

    private var cachedCourse: Course? = null
    private val database = FirebaseDatabase.getInstance("https://rohingya-shikho-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val updateRef = database.getReference("app_update")

    override fun getCourse(): Flow<Course?> = flow {
        if (cachedCourse == null) {
            val jsonString = context.assets.open("content.json").bufferedReader().use { it.readText() }
            cachedCourse = gson.fromJson(jsonString, Course::class.java)
        }
        emit(cachedCourse)
    }

    override fun getUnit(unitId: String): Flow<Unit?> = flow {
        getCourse().collect { course ->
            val unit = course?.levels?.flatMap { it.units }?.find { it.id == unitId }
            emit(unit)
        }
    }

    override fun getLesson(lessonId: String): Flow<Lesson?> = flow {
        getCourse().collect { course ->
            val lesson = course?.levels?.flatMap { it.units }?.flatMap { it.lessons }?.find { it.id == lessonId }
            emit(lesson)
        }
    }

    override fun getConsonants(): Flow<List<LessonItem>> = flow {
        val jsonString = context.assets.open("consonants.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<LessonItem>>() {}.type
        val items: List<LessonItem> = gson.fromJson(jsonString, type)
        emit(items)
    }

    override fun getVowels(): Flow<List<LessonItem>> = flow {
        val jsonString = context.assets.open("vowels.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<LessonItem>>() {}.type
        val items: List<LessonItem> = gson.fromJson(jsonString, type)
        emit(items)
    }

    override fun getNumbers(): Flow<List<LessonItem>> = flow {
        val jsonString = context.assets.open("numbers.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<LessonItem>>() {}.type
        val items: List<LessonItem> = gson.fromJson(jsonString, type)
        emit(items)
    }

    override fun getConsonantsForWriting(): Flow<List<LessonItem>> = flow {
        val jsonString = context.assets.open("consonants_writing.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<LessonItem>>() {}.type
        val items: List<LessonItem> = gson.fromJson(jsonString, type)
        emit(items)
    }

    override fun getAppUpdateInfo(): Flow<AppUpdateInfo> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val info = snapshot.getValue(AppUpdateInfo::class.java) ?: AppUpdateInfo()
                    trySend(info)
                } catch (e: Exception) {
                    Log.e("Firebase", "Error parsing AppUpdateInfo", e)
                    trySend(AppUpdateInfo())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Database error: ${error.message}")
                // Instead of closing with error (which causes crash), send default info
                trySend(AppUpdateInfo())
            }
        }
        updateRef.addValueEventListener(listener)
        awaitClose { updateRef.removeEventListener(listener) }
    }

    override suspend fun updateAppInfo(info: AppUpdateInfo) {
        try {
            updateRef.setValue(info).await()
        } catch (e: Exception) {
            Log.e("Firebase", "Error updating AppUpdateInfo", e)
            throw e
        }
    }
}
