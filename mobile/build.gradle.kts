import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  id("com.android.application")
  id("com.ncorti.ktfmt.gradle")
  id("org.jetbrains.kotlin.android")
  id("jacoco")
  id("com.google.gms.google-services")
}

android {
  compileSdk = 31

  defaultConfig {
    applicationId = "ch.epfl.sdp.mobile"
    minSdk = 24
    targetSdk = 31
    versionCode = 1
    versionName = "0.1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    debug {
      isTestCoverageEnabled = true
      isMinifyEnabled = false
    }
    release { isMinifyEnabled = false }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  testOptions { packagingOptions { jniLibs { useLegacyPackaging = true } } }

  composeOptions { kotlinCompilerExtensionVersion = libs.versions.compose.get() }
  packagingOptions { resources.excludes.add("META-INF/*") }
  buildFeatures { compose = true }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }

  testCoverage { jacocoVersion = libs.versions.jacoco.get() }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
  kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
  kotlinOptions.freeCompilerArgs += "-opt-in=androidx.compose.animation.ExperimentalAnimationApi"
  kotlinOptions.freeCompilerArgs +=
      "-opt-in=androidx.compose.animation.core.ExperimentalTransitionApi"
}

dependencies {
  // Testing.
  testImplementation(libs.coroutines.test)
  testImplementation(libs.androidx.test.core)
  testImplementation(libs.androidx.test.runner)
  testImplementation(libs.androidx.test.rules)
  testImplementation(libs.junit4)
  testImplementation(libs.truth)
  testImplementation(libs.mockk.mockk)
  testImplementation(libs.mockk.agent.jvm)
  androidTestImplementation(libs.junit4)
  androidTestImplementation(libs.mockk.android)
  androidTestImplementation(libs.androidx.test.junit)
  androidTestImplementation(libs.androidx.test.truth)
  androidTestImplementation(libs.androidx.test.espresso.core)
  androidTestImplementation(libs.androidx.test.espresso.intents)
  androidTestImplementation(libs.compose.ui.test.junit4)
  androidTestImplementation(libs.kotlin.reflect)
  debugImplementation(libs.compose.ui.test.manifest)
  debugImplementation(libs.compose.ui.tooling.tooling)

  // Firebase
  implementation(platform(libs.firebase.bom))
  implementation("com.google.firebase:firebase-auth-ktx")
  implementation("com.google.firebase:firebase-firestore-ktx")

  // Immutable collections.
  implementation(libs.kotlinx.immutable.collections)

  // Kotlin and coroutines.
  implementation(libs.bundles.coroutines.android)

  // Jetpack Compose
  implementation(libs.bundles.compose.android)

  implementation(libs.arsceneview)

  implementation("com.opencsv:opencsv:5.6")
}

jacoco { toolVersion = libs.versions.jacoco.get() }

configurations.forEach { config ->
  config.resolutionStrategy {
    eachDependency { if (requested.group == "org.jacoco") useVersion(libs.versions.jacoco.get()) }
  }
}

tasks.withType<Test> {
  configure<JacocoTaskExtension> {
    isIncludeNoLocationClasses = true
    excludes = listOf("jdk.internal.*")
  }
}

task<JacocoReport>("jacocoTestReport") {
  dependsOn("testDebugUnitTest", "createDebugCoverageReport")
  reports {
    xml.required.set(true)
    html.required.set(true)
  }
  val fileFilter =
      listOf(
          "**/R.class",
          "**/R$*.class",
          "**/BuildConfig.*",
          "**/Manifest*.*",
          "**/*Test*.*",
          "android/**/*.*",
          // Exclude Hilt generated classes
          "**/*Hilt*.*",
          "hilt_aggregated_deps/**",
          "**/*_Factory.class",
          "**/*_MembersInjector.class",
      )

  val debugTree =
      fileTree(baseDir = "${project.buildDir}/tmp/kotlin-classes/debug") { setExcludes(fileFilter) }
  val mainSrc = "${project.projectDir}/src/main/java"
  sourceDirectories.setFrom(files(listOf(mainSrc)))
  classDirectories.setFrom(files(listOf(debugTree)))
  executionData.setFrom(
      fileTree(baseDir = project.buildDir) {
        setIncludes(
            listOf(
                "outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec"),
        )
      },
  )
}

tasks["connectedCheck"].finalizedBy(
    "jacocoTestReport",
)
