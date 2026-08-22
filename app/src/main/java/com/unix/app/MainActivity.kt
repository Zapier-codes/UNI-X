package com.unix.app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unix.app.data.academic.TuitionInvoice
import com.unix.app.data.geo.GeoClient
import com.unix.app.data.learning.DefaultLearningPlatformRepository
import com.unix.app.data.model.UserRole
import com.unix.app.data.payments.PaymentClient
import com.unix.app.data.payments.PaymentRepository
import com.unix.app.data.repo.CampusRepository
import com.unix.app.data.session.SessionStore
import com.unix.app.ui.nav.Screen
import com.unix.app.ui.screens.*
import com.unix.app.ui.screens.payments.CheckoutScreen
import com.unix.app.ui.screens.payments.PaymentResultScreen
import com.unix.app.ui.screens.payments.TuitionScreen
import com.unix.app.ui.theme.UniXTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Holds a pending Korapay redirect (unix://payment-redirect?reference=...)
    // received via onNewIntent, consumed once by the Composable tree below.
    private var pendingPaymentReference by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingPaymentReference = extractReference(intent)
        val sessionStore = SessionStore(applicationContext)
        setContent {
            UniXApp(
                sessionStore = sessionStore,
                pendingPaymentReference = pendingPaymentReference,
                onPendingPaymentConsumed = { pendingPaymentReference = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractReference(intent)?.let { pendingPaymentReference = it }
    }

    /** unix://payment-redirect?reference=UNIX-xxxx&status=success */
    private fun extractReference(intent: Intent?): String? {
        val uri: Uri = intent?.data ?: return null
        if (uri.scheme != "unix" || uri.host != "payment-redirect") return null
        return uri.getQueryParameter("reference")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniXApp(
    sessionStore: SessionStore,
    pendingPaymentReference: String?,
    onPendingPaymentConsumed: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    // ---- Persisted session state (survives process death / app restart) ----
    val signedIn by sessionStore.isSignedIn.collectAsState(initial = null) // null = "still loading"
    val institutionUrl by sessionStore.institutionUrl.collectAsState(initial = "https://learn.your-institution.edu")
    val darkModePref by sessionStore.darkModePreference.collectAsState(initial = "system")
    val roleName by sessionStore.userRole.collectAsState(initial = "STUDENT")
    val role = runCatching { UserRole.valueOf(roleName) }.getOrDefault(UserRole.STUDENT)

    val darkOverride: Boolean? = when (darkModePref) {
        "on" -> true
        "off" -> false
        else -> null // "system"
    }

    UniXTheme(darkTheme = darkOverride ?: androidx.compose.foundation.isSystemInDarkTheme()) {
        when (signedIn) {
            null -> Unit // still reading DataStore — render nothing for a frame rather than flash the login screen
            false -> LoginScreen(
                onSignedIn = { scope.launch { sessionStore.setSignedIn(true) } },
                onDemoMode = { scope.launch { sessionStore.setSignedIn(true) } },
            )
            true -> SignedInApp(
                sessionStore = sessionStore,
                institutionUrl = institutionUrl,
                darkModePref = darkModePref,
                role = role,
                pendingPaymentReference = pendingPaymentReference,
                onPendingPaymentConsumed = onPendingPaymentConsumed,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignedInApp(
    sessionStore: SessionStore,
    institutionUrl: String,
    darkModePref: String,
    role: UserRole,
    pendingPaymentReference: String?,
    onPendingPaymentConsumed: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()

    val repository = remember { CampusRepository(api = null) }

    // Demo mode until both a Moodle token and an Open edX token are set in
    // Settings — see DefaultLearningPlatformRepository for the real wiring
    // point (both nullable clients flip from sample data to live data
    // independently of one another).
    val learningPlatformRepository = remember { DefaultLearningPlatformRepository(moodleApi = null, openEdxApi = null) }

    // Payment stack: the app only ever talks to our own hosted backend
    // (b-pay-backend.onrender.com) for anything money-related, and to
    // ipgeolocation.io purely for currency display. Both keys/URLs come
    // from BuildConfig, which is populated at BUILD TIME from GitHub
    // Secrets in CI (see .github/workflows/android-build.yml) or from
    // local.properties for local development only.
    val paymentRepository = remember {
        PaymentRepository(
            api = PaymentClient.create(debug = false),
            geoApi = GeoClient.create(debug = false),
            geoApiKey = BuildConfig.IPGEOLOCATION_API_KEY,
        )
    }

    var selectedInvoice by remember { mutableStateOf<TuitionInvoice?>(null) }

    LaunchedEffect(pendingPaymentReference) {
        pendingPaymentReference?.let { ref ->
            navController.navigate("payment-result/$ref") { launchSingleTop = true }
            onPendingPaymentConsumed()
        }
    }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val drawerGroups = remember(role) { Screen.drawerGroups(role) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "UNI X",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(20.dp),
                )
                drawerGroups.forEach { group ->
                    Text(
                        group.label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 4.dp),
                    )
                    group.items.forEach { screen ->
                        NavigationDrawerItem(
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate(screen.route) { launchSingleTop = true }
                            },
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )
                    }
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("UNI X") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                )
            },
            bottomBar = {
                NavigationBar {
                    Screen.bottomBarScreens.forEach { screen ->
                        NavigationBarItem(
                            selected = currentRoute == screen.route,
                            onClick = { navController.navigate(screen.route) { launchSingleTop = true } },
                            icon = { Icon(iconForBottomTab(screen), contentDescription = screen.label) },
                            label = { Text(screen.label) },
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(Screen.Dashboard.route) { DashboardScreen(repository) }
                composable(Screen.Courses.route) { CoursesScreen(repository) { id -> navController.navigate("courses/$id") } }
                composable(
                    Screen.CourseDetail.route,
                    arguments = listOf(navArgument("courseId") { type = NavType.IntType }),
                ) { entry -> CourseDetailScreen(entry.arguments?.getInt("courseId") ?: 0, repository) }
                composable(Screen.Grades.route) { GradesScreen(repository) }
                composable(Screen.Badges.route) { BadgesScreen(repository) }
                composable(Screen.OpenLearning.route) { OpenLearningScreen(learningPlatformRepository) }
                composable(Screen.Calendar.route) { CalendarScreen(repository) }
                composable(Screen.DegreeProgress.route) { DegreeProgressScreen(repository) }
                composable(Screen.Transcript.route) { TranscriptScreen(repository) }

                composable(Screen.Feed.route) { FeedScreen(repository) }
                composable(Screen.Forums.route) { ForumsScreen(repository) { id -> navController.navigate("forums/$id") } }
                composable(
                    Screen.ForumDetail.route,
                    arguments = listOf(navArgument("threadId") { type = NavType.IntType }),
                ) { entry -> ForumDetailScreen(entry.arguments?.getInt("threadId") ?: 0, repository) }
                composable(Screen.Messages.route) { MessagesScreen(repository) }
                composable(Screen.Clubs.route) { ClubsScreen(repository) }

                composable(Screen.Elections.route) { ElectionsScreen(repository) }
                composable(Screen.Committees.route) { CommitteesScreen(repository) }

                composable(Screen.Alumni.route) { AlumniScreen(repository) }
                composable(Screen.Jobs.route) { JobsScreen(repository) }

                composable(Screen.Admissions.route) { AdmissionsScreen(repository) }

                composable(Screen.Tuition.route) {
                    TuitionScreen(repository) { invoice ->
                        selectedInvoice = invoice
                        navController.navigate(Screen.Checkout.route)
                    }
                }
                composable(Screen.Checkout.route) {
                    val invoice = selectedInvoice
                    if (invoice != null) {
                        CheckoutScreen(
                            invoice = invoice,
                            studentName = "Amara Chukwu",
                            studentEmail = "amara.chukwu@student.unix.edu",
                            paymentRepository = paymentRepository,
                            onPaymentReference = { ref -> navController.navigate("payment-result/$ref") },
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate(Screen.Tuition.route) { popUpTo(Screen.Tuition.route) }
                        }
                    }
                }
                composable(
                    Screen.PaymentResult.route,
                    arguments = listOf(navArgument("reference") { type = NavType.StringType }),
                ) { entry ->
                    val ref = entry.arguments?.getString("reference") ?: ""
                    PaymentResultScreen(
                        reference = ref,
                        paymentRepository = paymentRepository,
                        onDone = { navController.navigate(Screen.Tuition.route) { popUpTo(Screen.Dashboard.route) } },
                    )
                }

                composable(Screen.Support.route) { SupportScreen(repository) }
                composable(Screen.Advising.route) { AdvisingScreen(repository) }
                composable(Screen.Scholarships.route) { ScholarshipsScreen(repository) }

                composable(Screen.Research.route) { ResearchScreen(repository) }
                composable(Screen.Repository.route) { RepositoryScreen(repository) }

                // Guarded here too, not just hidden from the drawer — a
                // student who deep-links or navigates directly into this
                // route still can't see it. A real deployment enforces
                // this server-side via the institution's own role check;
                // this is the client-side half of that.
                composable(Screen.StaffConsole.route) {
                    if (role == UserRole.STUDENT) {
                        AccessDeniedScreen()
                    } else {
                        StaffConsoleScreen(repository)
                    }
                }

                composable(Screen.Profile.route) { ProfileScreen(repository, role) }
                composable(Screen.Notifications.route) { NotificationsScreen(repository) }
                composable(Screen.Settings.route) {
                    SettingsScreen(
                        institutionUrl = institutionUrl,
                        onInstitutionUrlChange = { scope.launch { sessionStore.setInstitutionUrl(it) } },
                        darkModeOverride = darkModePref == "on",
                        onDarkModeOverrideChange = { on -> scope.launch { sessionStore.setDarkModePreference(if (on) "on" else "off") } },
                        role = role,
                        onRoleChange = { newRole -> scope.launch { sessionStore.setUserRole(newRole.name) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessDeniedScreen() {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null)
        Text("Staff access only", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(top = 12.dp))
        Text(
            "The Institution Console is restricted to faculty and administrative roles.",
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun iconForBottomTab(screen: Screen) = when (screen) {
    Screen.Dashboard -> androidx.compose.material.icons.Icons.Filled.Home
    Screen.Courses -> androidx.compose.material.icons.Icons.Filled.MenuBook
    Screen.Feed -> androidx.compose.material.icons.Icons.Filled.Groups
    Screen.Messages -> androidx.compose.material.icons.Icons.Filled.ChatBubble
    Screen.Profile -> androidx.compose.material.icons.Icons.Filled.Person
    else -> androidx.compose.material.icons.Icons.Filled.Circle
}
