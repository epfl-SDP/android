plugins {
  id("com.android.application").version("7.1.1").apply(false)
  id("com.android.library").version("7.1.1").apply(false)
  id("com.ncorti.ktfmt.gradle").version("0.8.0").apply(false)
  id("org.jetbrains.kotlin.android").version("1.6.10").apply(false)
  id("com.google.gms.google-services").version("4.3.10").apply(false)
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }
