package com.unix.app.data.openedx

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Client for an Open edX instance's public Courses API
 * (/api/courses/v1/courses/). This is UNI X's second learning-platform
 * connector, used for massive-scale/MOOC-style delivery, sitting alongside
 * MoodleApi (small-seminar, deep academic management). Both are unified
 * behind LearningPlatformRepository so screens never care which backend a
 * given course actually lives on.
 */
interface OpenEdxApi {

    @GET("api/courses/v1/courses/")
    suspend fun listCourses(
        @Query("org") org: String? = null,
        @Query("username") username: String? = null,
    ): OpenEdxCourseListResponse

    @GET("api/courses/v1/courses/{course_id}/")
    suspend fun getCourse(course_id: String): OpenEdxCourse

    // Course Blocks API: structure (sections/units/videos/problems) for a
    // given course + user, used to render a syllabus-style outline.
    @GET("api/courses/v1/blocks/")
    suspend fun getCourseBlocks(
        @Query("course_id") courseId: String,
        @Query("username") username: String,
        @Query("depth") depth: String = "all",
        @Query("all_blocks") allBlocks: Boolean = false,
    ): OpenEdxBlocksResponse
}
