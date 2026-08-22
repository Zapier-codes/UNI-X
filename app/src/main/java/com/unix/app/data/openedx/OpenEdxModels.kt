package com.unix.app.data.openedx

import com.google.gson.annotations.SerializedName

data class OpenEdxCourseListResponse(
    val results: List<OpenEdxCourse> = emptyList(),
    val pagination: OpenEdxPagination? = null,
)

data class OpenEdxPagination(
    val next: String? = null,
    val previous: String? = null,
    val count: Int = 0,
)

data class OpenEdxCourse(
    val id: String, // e.g. "course-v1:UNIX+CS204+2026_S1"
    val name: String,
    val org: String,
    val number: String,
    val start: String? = null,
    val end: String? = null,
    @SerializedName("enrollment_start") val enrollmentStart: String? = null,
    @SerializedName("enrollment_end") val enrollmentEnd: String? = null,
    @SerializedName("short_description") val shortDescription: String? = null,
    val effort: String? = null,
    val media: OpenEdxMedia? = null,
)

data class OpenEdxMedia(
    @SerializedName("course_image") val courseImage: OpenEdxImageRef? = null,
)

data class OpenEdxImageRef(val uri: String? = null)

data class OpenEdxBlocksResponse(
    val root: String? = null,
    val blocks: Map<String, OpenEdxBlock> = emptyMap(),
)

data class OpenEdxBlock(
    val id: String,
    val type: String, // "chapter" | "sequential" | "vertical" | "video" | "problem" | ...
    @SerializedName("display_name") val displayName: String,
    val children: List<String> = emptyList(),
)
