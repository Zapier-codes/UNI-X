package com.unix.app.data.model

// ---- Category 1 & 2: Teaching, MOOC, Assessment ----
data class Course(
    val id: Int,
    val fullName: String,
    val shortName: String,
    val progress: Int,
    val category: String,
    val instructor: String,
    val credits: Int,
    val term: String,
)

data class CourseSection(
    val title: String,
    val itemCount: Int,
    val kind: SectionKind,
)

enum class SectionKind { RESOURCE, ASSIGNMENT, QUIZ, FORUM, LIVE_CLASS, COMPETENCY }

data class GradeItem(
    val itemName: String,
    val grade: String,
    val feedback: String?,
    val weight: Int,
)

data class Badge(
    val id: Int,
    val name: String,
    val description: String,
    val earnedOn: String?,
)

// ---- Community, forums, chat ----
data class ForumThread(
    val id: Int,
    val title: String,
    val author: String,
    val replies: Int,
    val courseName: String,
    val lastActivity: String,
)

data class ForumPost(
    val author: String,
    val body: String,
    val postedAt: String,
    val isInstructor: Boolean,
)

data class Conversation(
    val id: Int,
    val withName: String,
    val lastMessage: String,
    val unread: Int,
    val timestamp: String,
)

data class FeedPost(
    val id: Int,
    val author: String,
    val body: String,
    val likes: Int,
    val comments: Int,
    val timestamp: String,
)

data class Announcement(
    val id: Int,
    val title: String,
    val body: String,
    val fromRole: String,
)

data class CalendarEvent(
    val id: Int,
    val title: String,
    val dateLabel: String,
    val type: EventType,
)

enum class EventType { DEADLINE, LIVE_CLASS, EXAM, CAMPUS_EVENT, ELECTION }

// ---- Category 4: Governance ----
data class Election(
    val id: Int,
    val title: String,
    val role: String,
    val closesLabel: String,
    val candidates: List<Candidate>,
    val hasVoted: Boolean,
)

data class Candidate(val name: String, val manifesto: String, val votes: Int)

data class Committee(val name: String, val role: String, val nextMeeting: String)

// ---- Category 5 & 6: Community, Alumni, Excellence ----
data class AlumniProfile(
    val id: Int,
    val name: String,
    val gradYear: Int,
    val role: String,
    val company: String,
    val openToMentor: Boolean,
)

data class JobPosting(
    val id: Int,
    val title: String,
    val company: String,
    val kind: String,
    val postedBy: String,
)

data class Club(
    val id: Int,
    val name: String,
    val members: Int,
    val category: String,
    val meetingCadence: String,
)

// ---- Category 8: Academic administration ----
data class AdmissionApplication(
    val programme: String,
    val status: String,
    val nextStep: String,
    val submittedOn: String,
)

data class TranscriptEntry(
    val courseCode: String,
    val title: String,
    val grade: String,
    val credits: Int,
    val term: String,
)

data class DegreeProgress(
    val programme: String,
    val creditsCompleted: Int,
    val creditsRequired: Int,
    val expectedGraduation: String,
)

// ---- Category 9: Student life & support ----
data class SupportTicket(
    val id: Int,
    val subject: String,
    val category: String,
    val status: String,
    val updatedAt: String,
)

data class AdvisingSlot(val advisor: String, val dateLabel: String, val topic: String)

data class ScholarshipAward(val name: String, val amount: String, val status: String)

// ---- Category 10: Research ----
data class ResearchProject(
    val id: Int,
    val title: String,
    val lead: String,
    val status: String,
    val fundingSource: String?,
)

data class RepositoryItem(
    val id: Int,
    val title: String,
    val author: String,
    val type: String,
    val year: Int,
)

// ---- Category 11 & 12: Ops, institutional effectiveness (staff-facing) ----
data class StaffTask(val title: String, val dept: String, val dueLabel: String)

data class ComplianceItem(val requirement: String, val status: String, val owner: String)

// ---- Cross-cutting ----
data class CampusPulse(
    val onlineNow: Int,
    val postsToday: Int,
    val weeklyGrowthPercent: Int,
    val newMembersThisWeek: Int,
    val totalStudents: Int,
    val totalAlumni: Int,
    val countriesRepresented: Int,
)

enum class UserRole { STUDENT, FACULTY, ADMIN }

data class UserProfile(
    val name: String,
    val role: String,
    val programme: String,
    val yearLabel: String,
    val avatarInitials: String,
    val userRole: UserRole = UserRole.STUDENT,
)
