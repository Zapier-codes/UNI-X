# UNI X — Android Client

A native Kotlin + Jetpack Compose Android app for a fully-remote digital
university. UNI X is a **client**, not a backend: it talks to an
institution's **Moodle** instance over the standard Web Services REST API
("Open Learning as a Service"), so the same app works for any institution
that runs Moodle — no fork required, just a site URL + token in Settings.

## Why this architecture

A mobile app cannot *be* a university — it can't run payroll, hold
accreditation, or manage grant budgets. Those are institutional back-office
systems (Moodle/Open edX server + a student information system), not
screens on a phone. What the app *can* do — and does — is give every
student- and staff-facing workflow a real, polished home, and give every
purely back-office workflow (HR, finance, accreditation) a lightweight
console surface that deep-links into the institution's admin platform.

## Feature coverage vs. the 120-feature blueprint

| Category | Coverage in this app |
|---|---|
| 1. Core Teaching & Management | ✅ Courses, content sections, assignments/quizzes surfaced, grading, forums, groups |
| 2. Massive-Scale & MOOC | ✅ Course catalogue, progress tracking, badges, in-app assessment surfaces (backed by Moodle/Open edX server-side) |
| 3. Integration & Ecosystem | ✅ This is the `data/remote` + `data/repo` layer — single seam to swap/extend backends |
| 4. Governance | ✅ Elections & voting, committees |
| 5. Community & Alumni | ✅ Feed, forums, messaging, clubs, alumni network, job board |
| 6. Excellence & Giving | ✅ Badges/certificates screen; scholarship disbursement status |
| 7. Unique Advantages | N/A — architectural properties, not screens |
| 8. Academic Administration | ✅ Admissions status, transcript, degree progress, tuition invoices per session/semester |
| 9. Student Life & Support | ✅ Support tickets, advising booking, financial aid |
| 10. Research | ✅ Research projects, institutional repository |
| 11. Operations & Infrastructure | ⚠️ Institution Console screen only — HR/finance/facilities systems of record stay server-side by design |
| 12. Continuous Improvement & Compliance | ⚠️ Same — compliance tracking surfaced as a status list, not reimplemented |

Everything marked ✅ is a real, navigable Compose screen wired to
`CampusRepository`. The repository currently runs in **Demo Mode**
(realistic sample data) so the whole app is explorable with zero backend
setup — connect a real Moodle site in Settings to go live.

## Tech stack

- Kotlin, Jetpack Compose, Material 3
- Navigation Compose (single-activity, one `NavHost`, drawer + bottom bar)
- Retrofit + Gson against **Moodle** (`webservice/rest/server.php`) and
  **Open edX** (`/api/courses/v1/courses/`, Course Blocks API) — unified
  behind `data/learning/LearningPlatformRepository` so screens never know
  which backend a course actually lives on
- Payments: the app talks only to the university's own hosted backend
  (`b-pay-backend.onrender.com`), which in turn talks to Korapay. The
  Korapay secret key never touches this app.
- Geolocation/currency: `ipapi.co` (free, no API key required, HTTPS),
  independent of the payment backend
- Session persistence: DataStore (`data/session/SessionStore.kt`) — sign-in
  state, institution URL, dark-mode preference, and role survive an app restart
- No DI framework, no local database yet — kept intentionally lightweight
- **Android only.** No iOS target.

## Two learning platforms, one app

| | Moodle | Open edX |
|---|---|---|
| Used for | Small seminars (15–30 students), instructor grading, gradebook | Massive open enrollment, self-paced, auto-graded |
| API | `webservice/rest/server.php?wsfunction=...` | `/api/courses/v1/courses/`, `/api/courses/v1/blocks/` |
| Client | `data/remote/MoodleApi.kt` | `data/openedx/OpenEdxApi.kt` |
| Unified as | `UnifiedCourse(backend = MOODLE, isSelfPaced = false)` | `UnifiedCourse(backend = OPEN_EDX, isSelfPaced = true)` |

`OpenLearningScreen` shows both side by side, tagged by backend, exactly as
the original blueprint's "Depth + Scale" unique advantage described.

## Academic calendar

One **Session** = one academic year = two **Semesters** (standard
private-sector school structure, not US-style quarters). See
`data/academic/AcademicModels.kt`. Tuition is invoiced per semester.

## Payments (Korapay, via your backend)

Strictly checkout — this app never collects card numbers, OTPs, or bank
details.

1. `TuitionScreen` shows invoices per session/semester, priced in the
   student's local currency (geolocation via ipapi.co, falling back
   to device locale instantly while the network call is in flight)
2. `CheckoutScreen` calls your backend's `POST /api/pay` → gets back a
   `checkout_url`
3. The app opens `checkout_url` in a Chrome Custom Tab — Korapay's own
   hosted page handles the actual payment
4. Korapay redirects to `unix://payment-redirect?reference=...` (registered
   as a deep link in the manifest), which reopens the app
5. `PaymentResultScreen` calls your backend's `GET /api/verify/{reference}`
   and **only trusts that server-verified response** — never the redirect
   URL's own query params — before showing "payment confirmed"

⚠️ Field names in `data/payments/PaymentModels.kt` are inferred from
Korapay's own initialize/verify conventions since I couldn't read your
backend's actual route handlers. Send me a sample request/response from
`/api/pay` and `/api/verify` and I'll match them exactly in one pass.

## Role-based access

`data/model/UserRole` (STUDENT / FACULTY / ADMIN) gates the Institution
Console both in the drawer (hidden entirely for students) and at the route
level (`AccessDeniedScreen` if a student deep-links into it directly). In a
real deployment this reads from the institution's actual Moodle role for
the signed-in user — Settings currently has a manual role picker labeled
"demo only" for previewing each view.

## Build size strategy

This build intentionally ships **one ABI only — `arm64-v8a`** (the
architecture the large majority of active Android devices use), not a
universal/fat APK:

```kotlin
defaultConfig {
    ndk { abiFilters += listOf("arm64-v8a") }
}
splits {
    abi {
        isEnable = true
        include("arm64-v8a")
        isUniversalApk = false
    }
}
```

Combined with `isMinifyEnabled` + `isShrinkResources` on release builds.

## Running it

**Android Studio (recommended):** open the project root, let it sync, hit
Run. Studio manages the Gradle wrapper jar automatically.

**Command line:**
```bash
# first time only, if gradlew isn't already runnable
gradle wrapper --gradle-version 8.7
./gradlew assembleDebug
```
The debug APK lands in `app/build/outputs/apk/debug/`.

## GitHub Actions

`.github/workflows/android-build.yml` builds on every push to `main`, PR,
or manual trigger. **Release only — no debug build, no CI artifacts.**
1. JDK 17 + Gradle 8.7 (via `gradle/actions/setup-gradle`, no wrapper jar
   needed in the repo)
2. Decodes `KEYSTORE_BASE64` (if set) to a keystore file
3. `assembleRelease` — minified, `arm64-v8a`-only, and **signed** if
   signing secrets are present (unsigned otherwise)
4. The output file is named exactly `UNI-X-<versionName>.apk` (e.g.
   `UNI-X-0.1.0.apk`) — controlled by the `androidComponents` block in
   `app/build.gradle.kts`, not by anything in the workflow
5. On push (not on PRs), the APK is published straight to a **GitHub
   Release** tagged `v<versionName>` via `softprops/action-gh-release` —
   there is no `actions/upload-artifact` step anywhere in this workflow.
   Pushing again under the same version updates that release's asset in
   place rather than creating a duplicate; bump `appVersionName` in
   `app/build.gradle.kts` to cut a new release instead.
6. The decoded keystore is deleted from the runner at the end either way

Grab the APK from the repo's **Releases** page, or:
```bash
gh release download v0.1.0 --pattern '*.apk'
```

See **GitHub Secrets**, below, for exactly which secrets to add.

## GitHub Secrets — required for a real build

All keys load from environment variables at **build time**, injected by
the workflow from your repo's secrets. Nothing is ever committed, and
`local.properties` is local-dev-only now (gitignored, ignored entirely in CI).

Go to **repo Settings → Secrets and variables → Actions → New repository secret** and add:

| Secret | Required? | What it's for |
|---|---|---|
| `KEYSTORE_BASE64` | Only for a distributable release | `base64 -w0 your-release.keystore` output. Without it, `assembleRelease` still succeeds but produces an **unsigned** APK — fine to verify the build, not fine to publish. |
| `KEYSTORE_PASSWORD` | With `KEYSTORE_BASE64` | Your keystore's store password |
| `KEY_ALIAS` | With `KEYSTORE_BASE64` | The key alias inside the keystore |
| `KEY_PASSWORD` | With `KEYSTORE_BASE64` | That key's password |

Currency detection needs no secret at all — see below.

If you don't have a release keystore yet:
```bash
keytool -genkey -v -keystore release.keystore -alias unix -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 release.keystore   # paste this output as KEYSTORE_BASE64
```
Keep the original `.keystore` file somewhere safe outside the repo — losing
it means you can never publish an update under the same app signature again.


## Payments — Korapay via your own backend

The app is wired to your hosted payment backend, **not** Korapay directly:

```
https://b-pay-backend.onrender.com
  POST /api/pay              -> initializes a Korapay checkout, returns checkout_url + reference
  GET  /api/verify/{reference} -> server-verified payment status
```

Flow (strictly checkout — the app never touches card/bank data):
1. Student picks an invoice on the **Tuition & Fees** screen.
2. `CheckoutScreen` calls `POST /api/pay` with amount, currency, reference,
   and student email/name, then opens the returned `checkout_url` in a
   Chrome Custom Tab.
3. Korapay's own hosted page handles the actual payment.
4. Korapay redirects to `unix://payment-redirect?reference=...`, which
   `MainActivity` catches (`android:scheme="unix" android:host="payment-redirect"`
   in the manifest) and routes straight to `PaymentResultScreen`.
5. `PaymentResultScreen` calls `GET /api/verify/{reference}` and treats
   **that** response — never the redirect URL's own query params — as the
   source of truth for whether tuition was actually paid.

**Field names are inferred**, not confirmed: `amount`, `currency`,
`reference`, `customer.name`, `customer.email`, `redirect_url`, `narration`
on the way in; `data.checkout_url` / `data.reference` and
`data.status` / `data.reference` on the way back — these match Korapay's
own initialize/verify shapes since your backend was built from their docs.
If your actual route handlers use different field names, only
`data/payments/PaymentApi.kt` and `PaymentModels.kt` need to change — send
me a sample request/response and I'll match it exactly.

## Currency detection — ipapi.co

Every screen shows tuition first in an instant, offline guess from the
device locale (`CurrencyLocator.fromDeviceLocale`), then refines it with a
real IP lookup via [ipapi.co](https://ipapi.co) once that request returns.
This is **display only** — the actual amount/currency sent to Korapay at
checkout is whatever `CheckoutScreen` has resolved by the time the student
taps Pay.

No signup, no API key, no credit card, HTTPS supported on the free tier
(1,000 lookups/day) — genuinely nothing to configure. This intentionally
replaces an earlier draft that used ipgeolocation.io, which despite being
"free" still requires registering for a key; ipapi.co doesn't.

Currently mapped currencies (matching Korapay's collection currencies):
NGN, GHS, KES, ZAR, XOF, XAF — everything else falls back to USD. FX rates
used for on-screen conversion are static snapshot values in
`data/payments/CurrencyLocator.kt` (`FxRates`) — swap in a live rate source
before relying on these for real pricing.

## Academic calendar

`data/academic/AcademicModels.kt` models the school's actual structure:
one **Session** = one academic year = two **Semesters**, matching standard
private-sector school terms (not US-style quarters/trimesters). Tuition
invoices are scoped to a session + semester; see `TuitionScreen`.



## Connecting a real Moodle instance

1. On the Moodle site: enable Web Services + REST protocol, create a token
   for a service exposing the functions listed in
   `data/remote/MoodleApi.kt` (`core_enrol_get_users_courses`,
   `core_course_get_contents`, `mod_forum_get_forum_discussions`, etc.)
2. In the app's Settings screen, enter the site URL.
3. Wire the token into `CampusRepository`'s constructor (currently `null` =
   demo mode) — this is the one line that flips the whole app from demo
   data to live institutional data.

## Connecting a real Open edX instance

1. Register an OAuth2 client (or use JWT auth) on the LMS for the mobile
   app; the Courses API is otherwise public-readable for published courses.
2. Enter the Open edX site URL in Settings.
3. Pass a live `OpenEdxApi` (built via `OpenEdxClient.create(baseUrl) { token }`)
   into `DefaultLearningPlatformRepository` in `MainActivity` instead of
   `null` — same one-line flip as Moodle, independent of it.

## What's next (real gaps, not glossed over)

Closed this pass: session/sign-in persistence (DataStore), release signing
via GitHub Secrets, role-gated Institution Console (client-side; pair with
a server-side check once real Moodle roles are wired), local-currency
checkout via ipapi.co (no API key needed).

Still open:
- **Push notifications.** Deliberately not wired yet. Firebase Cloud
  Messaging needs a `google-services.json` from your own Firebase project,
  and naively gating the FCM Gradle plugin + dependency on "does this file
  exist" makes the build fragile — it either silently no-ops or breaks
  depending on exactly how the plugin's resource generation is skipped.
  The safer path: once you've created a Firebase project, base64-encode
  `google-services.json` the same way as the keystore, add it as a
  `GOOGLE_SERVICES_JSON_BASE64` secret, and I'll wire the plugin, the
  `FirebaseMessagingService`, and a `POST_NOTIFICATIONS` permission prompt
  properly against a build that's guaranteed to have the file.
- Offline caching of course content (currently everything re-fetches on
  each screen visit; fine for demo mode, worth a Room cache before going
  live with slow/expensive Moodle calls)
- Real Moodle token exchange + role read (currently `CampusRepository`
  runs in permanent demo mode; `SettingsScreen`'s role picker is a manual
  stand-in until that's wired)
- Accessibility pass (content descriptions, touch target sizing, contrast
  check against WCAG 2.1 AA — feature #26 in your blueprint, not yet audited)
- Google Play publishing metadata (store listing, privacy policy URL,
  data-safety form) — needed regardless of how solid the code is
