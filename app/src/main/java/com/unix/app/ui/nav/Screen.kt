package com.unix.app.ui.nav

sealed class Screen(val route: String, val label: String) {
    // Core
    data object Login : Screen("login", "Sign in")
    data object Dashboard : Screen("dashboard", "Campus")
    data object Profile : Screen("profile", "Profile")
    data object Settings : Screen("settings", "Settings")
    data object Notifications : Screen("notifications", "Notifications")
    data object Calendar : Screen("calendar", "Calendar")

    // Category 1 & 2: Academics
    data object Courses : Screen("courses", "My Courses")
    data object CourseDetail : Screen("courses/{courseId}", "Course")
    data object Grades : Screen("grades", "Grades")
    data object Badges : Screen("badges", "Badges & Achievements")
    data object OpenLearning : Screen("open-learning", "Open Learning (Moodle + edX)")

    // Community
    data object Feed : Screen("feed", "Community Feed")
    data object Forums : Screen("forums", "Forums")
    data object ForumDetail : Screen("forums/{threadId}", "Discussion")
    data object Messages : Screen("messages", "Messages")
    data object Clubs : Screen("clubs", "Clubs & Societies")

    // Category 4: Governance
    data object Elections : Screen("elections", "Elections & Voting")
    data object Committees : Screen("committees", "Committees")

    // Category 5 & 6: Alumni & Career
    data object Alumni : Screen("alumni", "Alumni Network")
    data object Jobs : Screen("jobs", "Careers & Internships")

    // Category 8: Academic administration
    data object Admissions : Screen("admissions", "Admissions Status")
    data object Transcript : Screen("transcript", "Transcript")
    data object DegreeProgress : Screen("degree-progress", "Degree Progress")
    data object Tuition : Screen("tuition", "Tuition & Fees")
    data object Checkout : Screen("checkout", "Checkout")
    data object PaymentResult : Screen("payment-result/{reference}", "Payment Result")

    // Category 9: Student life & support
    data object Support : Screen("support", "Support Tickets")
    data object Advising : Screen("advising", "Academic Advising")
    data object Scholarships : Screen("scholarships", "Financial Aid & Scholarships")

    // Category 10: Research
    data object Research : Screen("research", "Research Projects")
    data object Repository : Screen("repository", "Institutional Repository")

    // Category 11 & 12: Staff / institution console
    data object StaffConsole : Screen("staff-console", "Institution Console")

    companion object {
        val bottomBarScreens = listOf(Dashboard, Courses, Feed, Messages, Profile)

        data class DrawerGroup(val label: String, val items: List<Screen>)

        /** StaffConsole is filtered out for STUDENT role — this is a demo-mode
         *  approximation of what a real deployment would enforce server-side
         *  via the Moodle role returned for the signed-in user. */
        fun drawerGroups(role: com.unix.app.data.model.UserRole): List<DrawerGroup> {
            val all = listOf(
                DrawerGroup("Academics", listOf(Courses, OpenLearning, Grades, Badges, Calendar, DegreeProgress, Transcript)),
                DrawerGroup("Community", listOf(Feed, Forums, Messages, Clubs)),
                DrawerGroup("Governance", listOf(Elections, Committees)),
                DrawerGroup("Career & Alumni", listOf(Alumni, Jobs)),
                DrawerGroup("Admissions & Records", listOf(Admissions, Tuition)),
                DrawerGroup("Student Support", listOf(Support, Advising, Scholarships)),
                DrawerGroup("Research", listOf(Research, Repository)),
                DrawerGroup(
                    "Institution",
                    if (role == com.unix.app.data.model.UserRole.STUDENT) listOf(Settings) else listOf(StaffConsole, Settings),
                ),
            )
            return all
        }
    }
}
