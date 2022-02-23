plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("jacoco")
}

val jacocoVersion = "0.8.8-SNAPSHOT"

android {
  compileSdk = 31

  defaultConfig {
    applicationId = "ch.epfl.sdp.mobile"
    minSdk = 23
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

  composeOptions { kotlinCompilerExtensionVersion = "1.1.0" }
  packagingOptions { resources.excludes.add("META-INF/*") }
  buildFeatures { compose = true }
  kotlinOptions { jvmTarget = JavaVersion.VERSION_1_8.toString() }

  testCoverage { jacocoVersion = this@Build_gradle.jacocoVersion }
}

dependencies {
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.1")
  implementation("com.google.android.material:material:1.5.0")
  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.1.0")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.1.0")

  implementation("androidx.activity:activity-compose:1.4.0")
  implementation("androidx.compose.material:material:1.1.0")
}

jacoco { toolVersion = jacocoVersion }

configurations.forEach { config ->
  config.resolutionStrategy {
    eachDependency { if (requested.group == "org.jacoco") useVersion(jacocoVersion) }
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
      fileTree(baseDir = "$project.buildDir/tmp/kotlin-classes/debug") { setExcludes(fileFilter) }
  val mainSrc = "$project.projectDir/src/main/java"
  sourceDirectories.setFrom(files(listOf(mainSrc)))
  classDirectories.setFrom(files(listOf(debugTree)))
  executionData.setFrom(
      fileTree(baseDir = project.buildDir) {
        setIncludes(
            listOf(
                "jacoco/testDebugUnitTest.exec",
                "outputs/code_coverage/debugAndroidTest/connected/*coverage.ec",
            ),
        )
      },
  )
}

tasks["connectedCheck"].finalizedBy(
    "jacocoTestReport",
)
