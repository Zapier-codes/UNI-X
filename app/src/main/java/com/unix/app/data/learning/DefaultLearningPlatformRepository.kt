package com.unix.app.data.learning

import com.unix.app.data.openedx.OpenEdxApi
import com.unix.app.data.remote.MoodleApi
import kotlinx.coroutines.delay

/**
 * [moodleApi] / [openEdxApi] are nullable exactly like [com.unix.app.data.repo.CampusRepository]'s
 * api field: null means "no institution connected yet, run demo mode."
 * Real wiring point: once both site URLs + tokens are set in Settings,
 * pass live clients here and every screen using [LearningPlatformRepository]
 * goes live with zero further changes.
 */
class DefaultLearningPlatformRepository(
    private val moodleApi: MoodleApi?,
    private val openEdxApi: OpenEdxApi?,
) : LearningPlatformRepository {

    override suspend fun listAllCourses(): List<UnifiedCourse> {
        return listMoodleCourses() + listOpenEdxCourses()
    }

    override suspend fun listMoodleCourses(): List<UnifiedCourse> {
        if (moodleApi == null) {
            delay(100)
            return listOf(
                UnifiedCourse(LearningBackend.MOODLE, "1", "Introduction to Microeconomics", "UNI X", "Small-seminar, instructor-graded", true, "Sep 2026", isSelfPaced = false),
                UnifiedCourse(LearningBackend.MOODLE, "3", "International Human Rights Law", "UNI X", "Small-seminar, instructor-graded", true, "Sep 2026", isSelfPaced = false),
            )
        }
        // Real call: MoodleApi.call(token, MoodleApi.FN_USER_COURSES) -> map to UnifiedCourse.
        return emptyList()
    }

    override suspend fun listOpenEdxCourses(): List<UnifiedCourse> {
        if (openEdxApi == null) {
            delay(100)
            return listOf(
                UnifiedCourse(LearningBackend.OPEN_EDX, "course-v1:UNIX+CS101+2026_MOOC", "Foundations of Programming (Open Enrollment)", "UNIX", "Self-paced, open to 10,000+ learners", true, "Rolling enrollment", isSelfPaced = true),
                UnifiedCourse(LearningBackend.OPEN_EDX, "course-v1:UNIX+DATASCI+2026_MOOC", "Data Science for Everyone", "UNIX", "Self-paced MOOC, auto-graded", true, "Rolling enrollment", isSelfPaced = true),
            )
        }
        val response = openEdxApi.listCourses()
        return response.results.map { c ->
            UnifiedCourse(
                backend = LearningBackend.OPEN_EDX,
                backendCourseId = c.id,
                title = c.name,
                org = c.org,
                shortDescription = c.shortDescription,
                enrollmentOpen = true,
                startLabel = c.start,
                isSelfPaced = true,
            )
        }
    }
}
