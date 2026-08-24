import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// ---------------------------------------------------------------------
// All secrets load from environment variables at BUILD TIME (GitHub
// Actions -> Settings -> Secrets and variables -> Actions), never from a
// committed file. local.properties still works as a LOCAL DEV convenience
// (gitignored, never used in CI) but env vars always win when both are
// present, so a CI build is never accidentally using a stale local value.
//
// Secrets this build expects, if configured:
//   KEYSTORE_BASE64        - base64-encoded release .jks/.keystore file
//   KEYSTORE_PASSWORD
//   KEY_ALIAS
//   KEY_PASSWORD
// (Currency/geo lookup via ipapi.co needs no key — see data/geo — so it's
// no longer part of this secrets list.)
// See .github/workflows/android-build.yml for exactly how these are wired.
// ---------------------------------------------------------------------
// App identity — single source of truth for both the manifest/build config
// and the release APK's file name. The workflow's "Read app version" step
// greps appDisplayName/appVersionName straight from this file, so the
// shipped APK/Release always match whatever's set here.
// "Nothing more, nothing less" means the shipped file is literally
// UNI-X-<versionName>.apk — no arch suffix, no -unsigned, no -release.
val appDisplayName = "UNI-X"
val appVersionCode = 1
val appVersionName = "0.1.0"

// NOTE: "java" at script scope resolves to the Java plugin's `java { }`
// extension accessor, not the java.* package — writing java.util.Properties()
// here fails to compile ("Unresolved reference: util") because of that
// shadowing. Importing Properties directly sidesteps the ambiguity.
val localProperties = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun secret(name: String): String =
    System.getenv(name) ?: (localProperties[name] as? String) ?: ""


// The workflow decodes KEYSTORE_BASE64 to this path before Gradle runs.
// Locally, drop a keystore at this path (or point KEYSTORE_PATH env at it)
// to test signed builds; without it, release builds stay unsigned, which
// is fine for CI verification but not for distribution.
val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootProject.projectDir}/release.keystore"
val keystorePassword = secret("KEYSTORE_PASSWORD")
val keyAlias = secret("KEY_ALIAS")
val keyPassword = secret("KEY_PASSWORD")
val hasSigningConfig = keystorePassword.isNotBlank() && keyAlias.isNotBlank() &&
    keyPassword.isNotBlank() && file(keystorePath).exists()

android {
    namespace = "com.unix.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.unix.app"
        minSdk = 26
        targetSdk = 34
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"


        // Lightweight, single-ABI build: arm64-v8a only (covers the large
        // majority of active Android devices). No universal/fat APK.
        //
        // NOTE: this alone is sufficient — it restricts which native
        // libraries get packaged into the one APK produced. A splits.abi
        // block was previously also present as a "belt and suspenders"
        // measure, but AGP rejects that combination outright:
        //   Conflicting configuration: 'arm64-v8a' in ndk abiFilters
        //   cannot be present when splits abi filters are set: arm64-v8a
        // splits.abi is for generating multiple per-ABI APK variants
        // (with a universal-APK toggle) — a different mechanism aimed at
        // a different goal (one APK per ABI) than what's wanted here (one
        // APK, containing only arm64-v8a native code). ndk.abiFilters is
        // the right tool for that on its own.
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    signingConfigs {
        if (hasSigningConfig) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = keystorePassword
                this.keyAlias = keyAlias
                this.keyPassword = keyPassword
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (hasSigningConfig) {
                signingConfig = signingConfigs.getByName("release")
            }
            // else: unsigned APK — fine for CI build verification, not for
            // distribution. Add KEYSTORE_BASE64 + the three passwords as
            // GitHub secrets to get a properly signed artifact.
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// NOTE on APK naming: AGP's new Variant API deliberately does NOT support
// setting outputFileName directly (confirmed against AGP engineer guidance —
// the old applicationVariants.outputFileName mechanism was removed in AGP
// 4.1+ and never replaced with an equivalent for the new API; the supported
// pattern is registering a task that COPIES the built APK to a renamed
// file). Rather than depend on fragile, version-specific AGP internals for
// a cosmetic rename, the actual "UNI-X-<version>.apk" naming happens as a
// plain `mv` in the CI workflow after the build completes — see the
// "Rename APK" step in .github/workflows/android-build.yml. Simpler, and
// has zero AGP-version risk.

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Networking against the Moodle Web Services REST API
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Local prefs for session token (lightweight, no full DB needed yet)
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("androidx.browser:browser:1.8.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
