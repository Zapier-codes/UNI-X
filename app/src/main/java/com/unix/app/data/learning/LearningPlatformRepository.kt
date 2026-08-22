package com.unix.app.data.learning

/** Which backend actually hosts a given course. Kept visible (not hidden)
 *  because the two platforms genuinely behave differently — a MOOC-style
 *  Open edX course is self-paced with thousands of peers; a Moodle seminar
 *  course has 15–30 students and a real instructor grading by hand. The UI
 *  is allowed to treat them differently; the data layer just refuses to
 *  make screens care about *which API* to call. */
enum class LearningBackend { MOODLE, OPEN_EDX }

data class UnifiedCourse(
    val backend: LearningBackend,
    val backendCourseId: String, // Moodle numeric id (as string) or edX course-v1:... key
    val title: String,
    val org: String,
    val shortDescription: String?,
    val enrollmentOpen: Boolean,
    val startLabel: String?,
    val isSelfPaced: Boolean, // true for typical Open edX MOOCs, false for Moodle seminars
)

/**
 * Single seam every screen talks to for "what courses exist" and "what's
 * in this course" — internally fans out to Moodle and/or Open edX and
 * merges the results. This is where Category 3 (Integration & Unified
 * Ecosystem) actually lives in code, rather than being a slide.
 */
interface LearningPlatformRepository {
    suspend fun listAllCourses(): List<UnifiedCourse>
    suspend fun listMoodleCourses(): List<UnifiedCourse>
    suspend fun listOpenEdxCourses(): List<UnifiedCourse>
}
