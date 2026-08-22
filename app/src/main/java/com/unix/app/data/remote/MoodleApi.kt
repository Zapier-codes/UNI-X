package com.unix.app.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Thin client over Moodle's Web Services REST layer
 * (webservice/rest/server.php?wsfunction=...).
 *
 * Uni X treats an institution's Moodle instance as "Open Learning as a
 * Service": the mobile app never owns academic data directly, it is a
 * client against whatever institution backend is configured (Moodle today,
 * Open edX addable later behind the same Repository interface).
 *
 * All calls go through the single REST endpoint with a wsfunction query
 * param, per Moodle's web service convention.
 */
interface MoodleApi {

    @GET("webservice/rest/server.php?moodlewsrestformat=json")
    suspend fun call(
        @Query("wstoken") token: String,
        @Query("wsfunction") function: String,
        @Query("userid") userId: Int? = null,
        @Query("courseid") courseId: Int? = null,
    ): retrofit2.Response<okhttp3.ResponseBody>

    companion object {
        // Common wsfunction names this app relies on; kept here so screens
        // don't scatter magic strings.
        const val FN_SITE_INFO = "core_webservice_get_site_info"
        const val FN_USER_COURSES = "core_enrol_get_users_courses"
        const val FN_COURSE_CONTENTS = "core_course_get_contents"
        const val FN_FORUM_DISCUSSIONS = "mod_forum_get_forum_discussions"
        const val FN_GRADES = "gradereport_user_get_grade_items"
        const val FN_CALENDAR = "core_calendar_get_calendar_upcoming_view"
        const val FN_MESSAGES = "core_message_get_conversations"
        const val FN_BADGES = "core_badges_get_user_badges"
        const val FN_COMPETENCIES = "core_competency_list_course_competencies"
    }
}
