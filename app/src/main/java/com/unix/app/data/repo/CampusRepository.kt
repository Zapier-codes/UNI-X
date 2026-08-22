package com.unix.app.data.repo

import com.unix.app.data.model.*
import com.unix.app.data.remote.MoodleApi
import kotlinx.coroutines.delay

/**
 * Single data-access seam for the whole app. Every function here is the
 * exact point where a real Moodle Web Services call (via [api]) replaces
 * the illustrative data below. Screens never talk to [MoodleApi] directly —
 * this keeps the "Open Learning as a Service" boundary in one place, so
 * swapping or adding a backend (Open edX, SIS, etc.) touches this file only.
 *
 * When [api] is null the repository runs in Demo Mode: every screen in the
 * app is fully navigable with realistic data before any institution has
 * been connected.
 */
class CampusRepository(private val api: MoodleApi?) {

    val isConnectedToInstitution: Boolean get() = api != null

    // ---------- Profile ----------
    suspend fun getProfile(role: com.unix.app.data.model.UserRole = com.unix.app.data.model.UserRole.STUDENT): UserProfile {
        delay(80)
        return when (role) {
            com.unix.app.data.model.UserRole.STUDENT ->
                UserProfile("Amara Chukwu", "Student", "BSc Computer Science (Theory)", "Year 2", "AC", role)
            com.unix.app.data.model.UserRole.FACULTY ->
                UserProfile("Dr. Priya Nair", "Faculty", "Mathematics & Statistics Dept.", "Senior Lecturer", "PN", role)
            com.unix.app.data.model.UserRole.ADMIN ->
                UserProfile("Femi Okoro", "Registrar's Office", "Academic Administration", "Staff", "FO", role)
        }
    }

    // ---------- Campus pulse (dashboard hero) ----------
    suspend fun getPulse(): CampusPulse {
        delay(120)
        return CampusPulse(
            onlineNow = 4821,
            postsToday = 1360,
            weeklyGrowthPercent = 12,
            newMembersThisWeek = 318,
            totalStudents = 26_940,
            totalAlumni = 9_112,
            countriesRepresented = 87,
        )
    }

    suspend fun getAnnouncements(): List<Announcement> {
        delay(100)
        return listOf(
            Announcement(1, "Course Rep elections open this week", "Student Union polls close Friday 23:59 UTC.", "Student Union"),
            Announcement(2, "New careers fair partners added", "Twelve new employers joined the virtual careers fair.", "Careers Office"),
            Announcement(3, "Library database maintenance", "JSTOR access will be briefly interrupted Sunday 02:00 UTC.", "Library Services"),
        )
    }

    suspend fun getCalendarEvents(): List<CalendarEvent> {
        delay(100)
        return listOf(
            CalendarEvent(1, "Problem Set 3 due — STAT150", "Tomorrow, 23:59", EventType.DEADLINE),
            CalendarEvent(2, "Live seminar: Constitutional Law", "Wed, 15:00", EventType.LIVE_CLASS),
            CalendarEvent(3, "Midterm — CS204", "Fri, 09:00", EventType.EXAM),
            CalendarEvent(4, "Virtual Careers Fair", "Sat, 12:00", EventType.CAMPUS_EVENT),
            CalendarEvent(5, "Course Rep election closes", "Fri, 23:59", EventType.ELECTION),
        )
    }

    // ---------- Category 1 & 2: Courses, content, assessment ----------
    suspend fun getCourses(): List<Course> {
        delay(150)
        return listOf(
            Course(1, "Introduction to Microeconomics", "ECON101", 62, "Social Sciences", "Dr. Femi Okoro", 4, "Fall 2026"),
            Course(2, "Algorithms & Computational Theory", "CS204", 34, "Computer Science", "Prof. Lin Wei", 5, "Fall 2026"),
            Course(3, "International Human Rights Law", "LAW310", 81, "Law", "Dr. Sarah Byrne", 4, "Fall 2026"),
            Course(4, "Applied Statistics I", "STAT150", 45, "Mathematics", "Dr. Priya Nair", 4, "Fall 2026"),
            Course(5, "Philosophy of Mind", "PHIL220", 18, "Humanities", "Prof. Daniel Osei", 3, "Fall 2026"),
        )
    }

    suspend fun getCourseSections(courseId: Int): List<CourseSection> {
        delay(100)
        return listOf(
            CourseSection("Lecture Materials", 8, SectionKind.RESOURCE),
            CourseSection("Assignments", 4, SectionKind.ASSIGNMENT),
            CourseSection("Quizzes", 3, SectionKind.QUIZ),
            CourseSection("Live Seminars", 2, SectionKind.LIVE_CLASS),
            CourseSection("Course Discussion", 1, SectionKind.FORUM),
            CourseSection("Mapped Competencies", 6, SectionKind.COMPETENCY),
        )
    }

    suspend fun getGrades(): List<GradeItem> {
        delay(120)
        return listOf(
            GradeItem("Midterm Essay", "A-", "Strong argument structure.", 25),
            GradeItem("Problem Set 3", "88 / 100", "Minor errors in Q4.", 15),
            GradeItem("Participation", "Excellent", null, 10),
            GradeItem("Final Project (in progress)", "—", null, 30),
        )
    }

    suspend fun getBadges(): List<Badge> {
        delay(100)
        return listOf(
            Badge(1, "First-Class Discussion", "Top 5% forum contribution, Term 1", "Mar 2026"),
            Badge(2, "Peer Reviewer", "Completed 10 peer assessments", "Apr 2026"),
            Badge(3, "Perfect Attendance", "Attended every live seminar this term", null),
        )
    }

    // ---------- Community: forums, feed, messages ----------
    suspend fun getForumThreads(): List<ForumThread> {
        delay(120)
        return listOf(
            ForumThread(1, "Week 4 problem set — anyone else stuck on Q3?", "Amara O.", 12, "STAT150", "2h ago"),
            ForumThread(2, "Guest lecture recording is up!", "Prof. Idris", 4, "ECON101", "5h ago"),
            ForumThread(3, "Study group forming for finals", "Kwame A.", 27, "CS204", "1d ago"),
        )
    }

    suspend fun getForumPosts(threadId: Int): List<ForumPost> {
        delay(100)
        return listOf(
            ForumPost("Amara O.", "Has anyone worked out the recursion in Q3 yet? I keep hitting a stack overflow.", "2h ago", false),
            ForumPost("Prof. Idris", "Check the base case — it should terminate at n=1, not n=0.", "1h ago", true),
            ForumPost("Kwame A.", "That fixed it for me too, thank you!", "40m ago", false),
        )
    }

    suspend fun getFeed(): List<FeedPost> {
        delay(120)
        return listOf(
            FeedPost(1, "UNI X Debate Society", "We placed 2nd at the Global Online Debate Invitational! 🎉", 214, 31, "3h ago"),
            FeedPost(2, "Kwame A.", "Anyone else's dashboard looking extra green this week? Love the streak tracker.", 58, 9, "6h ago"),
            FeedPost(3, "Careers Office", "42 new remote internships posted this week across Law, CS and Business.", 133, 12, "1d ago"),
        )
    }

    suspend fun getConversations(): List<Conversation> {
        delay(100)
        return listOf(
            Conversation(1, "Dr. Priya Nair", "Sure, office hours moved to 3pm Thursday.", 1, "10m ago"),
            Conversation(2, "STAT150 Study Group", "Kwame: pushing the notes to the shared doc now", 3, "1h ago"),
            Conversation(3, "Academic Advising", "Your Term 3 plan looks good — one note attached.", 0, "Yesterday"),
        )
    }

    // ---------- Category 4: Governance ----------
    suspend fun getElections(): List<Election> {
        delay(100)
        return listOf(
            Election(
                1, "Course Representative — CS204", "Course Rep", "Closes Fri 23:59 UTC",
                listOf(
                    Candidate("Kwame Asante", "Weekly office-hour recap notes for everyone.", 142),
                    Candidate("Yuki Tanaka", "Push for recorded seminar captions.", 118),
                ),
                hasVoted = false,
            ),
            Election(
                2, "Student Union President", "Union Executive", "Closes in 9 days",
                listOf(
                    Candidate("Amara Chukwu", "Expand mental-health support hours.", 980),
                    Candidate("Daniel Osei", "Launch a peer-mentoring guarantee.", 875),
                ),
                hasVoted = true,
            ),
        )
    }

    suspend fun getCommittees(): List<Committee> {
        delay(80)
        return listOf(
            Committee("Academic Standards Committee", "Student Observer", "Sep 3, 14:00 UTC"),
            Committee("Curriculum Review Board — CS", "Course Rep", "Sep 10, 16:00 UTC"),
        )
    }

    // ---------- Category 5 & 6: Community, alumni, excellence ----------
    suspend fun getAlumni(): List<AlumniProfile> {
        delay(120)
        return listOf(
            AlumniProfile(1, "Ngozi Adeyemi", 2023, "Product Manager", "Flutterwave", true),
            AlumniProfile(2, "Michael Chen", 2021, "PhD Candidate", "ETH Zürich", true),
            AlumniProfile(3, "Fatima Zahra", 2024, "Policy Analyst", "UNDP", false),
        )
    }

    suspend fun getJobs(): List<JobPosting> {
        delay(100)
        return listOf(
            JobPosting(1, "Junior Data Analyst", "Paystack", "Internship", "Careers Office"),
            JobPosting(2, "Legal Research Assistant", "Amnesty International", "Remote, part-time", "Alumni Network"),
            JobPosting(3, "Software Engineering Intern", "Andela", "Internship", "Careers Office"),
        )
    }

    suspend fun getClubs(): List<Club> {
        delay(100)
        return listOf(
            Club(1, "UNI X Debate Society", 412, "Public Speaking", "Weekly, Thu 18:00"),
            Club(2, "AI & Ethics Reading Group", 187, "Academic", "Biweekly, Sun 16:00"),
            Club(3, "Founders Circle", 260, "Entrepreneurship", "Weekly, Tue 19:00"),
        )
    }

    // ---------- Category 8: Academic administration ----------
    suspend fun getAdmission(): AdmissionApplication {
        delay(80)
        return AdmissionApplication("MSc Data Science", "Under Review", "Upload final transcript by Sep 5", "Aug 2, 2026")
    }

    suspend fun getTranscript(): List<TranscriptEntry> {
        delay(120)
        return listOf(
            TranscriptEntry("ECON101", "Introduction to Microeconomics", "In progress", 4, "Fall 2026"),
            TranscriptEntry("CS101", "Foundations of Programming", "A", 4, "Spring 2026"),
            TranscriptEntry("MATH110", "Calculus I", "B+", 4, "Spring 2026"),
            TranscriptEntry("PHIL101", "Critical Thinking", "A-", 3, "Fall 2025"),
        )
    }

    suspend fun getDegreeProgress(): DegreeProgress {
        delay(80)
        return DegreeProgress("BSc Computer Science (Theory)", 58, 120, "Spring 2028")
    }

    // ---------- Category 9: Student life & support ----------
    suspend fun getSupportTickets(): List<SupportTicket> {
        delay(100)
        return listOf(
            SupportTicket(1, "Cannot access STAT150 quiz", "Technical", "Open", "2h ago"),
            SupportTicket(2, "Tuition payment plan question", "Finance", "Resolved", "3d ago"),
        )
    }

    suspend fun getAdvisingSlots(): List<AdvisingSlot> {
        delay(80)
        return listOf(
            AdvisingSlot("Dr. Priya Nair", "Thu, Sep 4 — 15:00 UTC", "Term 3 module selection"),
            AdvisingSlot("Careers Office", "Fri, Sep 5 — 10:00 UTC", "Internship application review"),
        )
    }

    suspend fun getScholarships(): List<ScholarshipAward> {
        delay(80)
        return listOf(
            ScholarshipAward("First-Class Merit Award", "$2,000 / term", "Active"),
            ScholarshipAward("Global Access Grant", "$1,200 / term", "Pending renewal"),
        )
    }

    // ---------- Category 10: Research ----------
    suspend fun getResearchProjects(): List<ResearchProject> {
        delay(100)
        return listOf(
            ResearchProject(1, "Low-resource NLP for African Languages", "Prof. Lin Wei", "Active", "UNI X Research Grant"),
            ResearchProject(2, "Digital Constitutionalism Survey", "Dr. Sarah Byrne", "Data collection", null),
        )
    }

    suspend fun getRepositoryItems(): List<RepositoryItem> {
        delay(100)
        return listOf(
            RepositoryItem(1, "Adaptive Assessment in Online Law Courses", "Byrne, S.", "Journal Article", 2026),
            RepositoryItem(2, "A Corpus for Low-Resource NLP", "Wei, L. et al.", "Dataset", 2025),
        )
    }

    // ---------- Category 11 & 12: staff console (role-gated in a real deployment) ----------
    suspend fun getStaffTasks(): List<StaffTask> {
        delay(80)
        return listOf(
            StaffTask("Approve Term 3 module change requests", "Registrar", "Due Sep 6"),
            StaffTask("Review accreditation self-study draft", "Quality Assurance", "Due Sep 20"),
        )
    }

    suspend fun getComplianceItems(): List<ComplianceItem> {
        delay(80)
        return listOf(
            ComplianceItem("Annual data protection audit (GDPR)", "In progress", "IT Governance"),
            ComplianceItem("External accreditation renewal", "Not started", "Dean's Office"),
        )
    }

    // ---------- Academic sessions & tuition ----------
    suspend fun getAcademicSessions(): List<com.unix.app.data.academic.AcademicSession> {
        delay(100)
        return listOf(
            com.unix.app.data.academic.AcademicSession(
                id = 1, label = "2026/2027", isCurrent = true,
                semesters = listOf(
                    com.unix.app.data.academic.Semester(1, "Semester 1", "Sep 2026", "Jan 2027", isCurrent = true),
                    com.unix.app.data.academic.Semester(2, "Semester 2", "Feb 2027", "Jun 2027", isCurrent = false),
                ),
            ),
            com.unix.app.data.academic.AcademicSession(
                id = 2, label = "2025/2026", isCurrent = false,
                semesters = listOf(
                    com.unix.app.data.academic.Semester(3, "Semester 1", "Sep 2025", "Jan 2026", isCurrent = false),
                    com.unix.app.data.academic.Semester(4, "Semester 2", "Feb 2026", "Jun 2026", isCurrent = false),
                ),
            ),
        )
    }

    suspend fun getTuitionInvoices(): List<com.unix.app.data.academic.TuitionInvoice> {
        delay(100)
        return listOf(
            com.unix.app.data.academic.TuitionInvoice(
                sessionLabel = "2026/2027", semesterLabel = "Semester 1",
                programme = "BSc Computer Science (Theory)",
                baseAmountUsd = 950.0, amountPaid = 0.0,
                status = com.unix.app.data.academic.InvoiceStatus.UNPAID,
                dueLabel = "Due before Semester 1 registration closes",
            ),
        )
    }
}
