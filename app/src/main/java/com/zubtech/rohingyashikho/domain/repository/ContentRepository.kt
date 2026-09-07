package com.zubtech.rohingyashikho.domain.repository

import com.zubtech.rohingyashikho.domain.model.Course
import com.zubtech.rohingyashikho.domain.model.Lesson
import com.zubtech.rohingyashikho.domain.model.Unit
import kotlinx.coroutines.flow.Flow

interface ContentRepository {
    fun getCourse(): Flow<Course?>
    fun getUnit(unitId: String): Flow<Unit?>
    fun getLesson(lessonId: String): Flow<Lesson?>
}
