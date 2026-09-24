import java.util.Base64
import org.gradle.api.GradleException

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

val releaseSigningSecretNames = listOf(
    "ANDROID_KEYSTORE_BASE64",
    "ANDROID_KEYSTORE_PASSWORD",
    "ANDROID_KEY_ALIAS",
    "ANDROID_KEY_PASSWORD",
)
val releaseSigningValues = releaseSigningSecretNames.associateWith { name ->
    providers.environmentVariable(name).orNull?.takeIf(String::isNotBlank)
}
val releaseSigningConfigured = releaseSigningValues.values.all { it != null }
val releaseSigningKeyFile = layout.buildDirectory
    .file("release-signing/sleep-inducer-release.p12")
    .get()
    .asFile

if (releaseSigningConfigured) {
    val encodedKeystore = requireNotNull(
        releaseSigningValues.getValue("ANDROID_KEYSTORE_BASE64")
    )
    val keystoreBytes = try {
        Base64.getMimeDecoder().decode(encodedKeystore)
    } catch (error: IllegalArgumentException) {
        throw GradleException("ANDROID_KEYSTORE_BASE64 is not valid Base64.", error)
    }
    if (keystoreBytes.isEmpty()) {
        throw GradleException("ANDROID_KEYSTORE_BASE64 decoded to an empty keystore.")
    }
    val signingDirectory = releaseSigningKeyFile.parentFile
    if (!signingDirectory.mkdirs() && !signingDirectory.isDirectory) {
        throw GradleException("Could not create the release signing build directory.")
    }
    if (!signingDirectory.setReadable(false, false) ||
        !signingDirectory.setReadable(true, true) ||
        !signingDirectory.setWritable(false, false) ||
        !signingDirectory.setWritable(true, true)
    ) {
        throw GradleException("Could not restrict access to the release signing build directory.")
    }
    releaseSigningKeyFile.writeBytes(keystoreBytes)
    if (!releaseSigningKeyFile.setReadable(false, false) ||
        !releaseSigningKeyFile.setReadable(true, true) ||
        !releaseSigningKeyFile.setWritable(false, false) ||
        !releaseSigningKeyFile.setWritable(true, true)
    ) {
        releaseSigningKeyFile.delete()
        throw GradleException("Could not restrict access to the temporary release keystore.")
    }
}

val releaseArtifactTasks = setOf("assembleRelease", "bundleRelease", "packageRelease")
val releaseArtifactRequested = gradle.startParameter.taskNames.any {
    it.substringAfterLast(':') in releaseArtifactTasks
}
if (releaseArtifactRequested && !releaseSigningConfigured) {
    val missingSecrets = releaseSigningValues
        .filterValues { it == null }
        .keys
        .joinToString()
    throw GradleException(
        "Release APKs must be signed. Configure these environment variables: $missingSecrets"
    )
}

android {
    namespace = "com.sleepinducer.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.sleepinducer.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "0.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testOptions {
        animationsDisabled = true
    }

    signingConfigs {
        create("release") {
            if (releaseSigningConfigured) {
                storeFile = releaseSigningKeyFile
                storePassword = releaseSigningValues.getValue("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = releaseSigningValues.getValue("ANDROID_KEY_ALIAS")
                keyPassword = releaseSigningValues.getValue("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.12.01")

    implementation(composeBom)
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    testImplementation("junit:junit:4.13.2")

    debugImplementation("androidx.compose.ui:ui-tooling")

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
}
