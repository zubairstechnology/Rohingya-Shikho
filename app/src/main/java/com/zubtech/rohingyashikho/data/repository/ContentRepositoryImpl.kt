package com.zubtech.rohingyashikho.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.model.Lesson
import com.zubtech.rohingyashikho.domain.model.LessonItem
import com.zubtech.rohingyashikho.domain.model.Unit
import com.zubtech.rohingyashikho.domain.repository.ContentRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) : ContentRepository {

    private var cachedCourse: Course? = null

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
}
