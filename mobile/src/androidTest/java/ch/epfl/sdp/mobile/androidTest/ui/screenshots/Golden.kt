package ch.epfl.sdp.mobile.androidTest.ui.screenshots

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.test.platform.app.InstrumentationRegistry
import ch.epfl.sdp.mobile.androidTest.ui.screenshots.matchers.MSSIMMatcher
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * Asserts that the currently selected node matches the screenshot in the assets with the provided
 * [name].
 *
 * @param name the file to compare to.
 * @param matcher the [BitmapMatcher] to use for the comparison.
 * @param failIfAbsent true if the test should fail on missing golden screenshot.
 */
fun SemanticsNodeInteraction.assertAgainstGolden(
    name: String,
    matcher: BitmapMatcher = MSSIMMatcher(),
    failIfAbsent: Boolean = true,
) {
  val assets = InstrumentationRegistry.getInstrumentation().context.resources.assets
  val actual = captureToImage().asAndroidBitmap()

  try {
    val expected = assets.open("$name.png").use { BitmapFactory.decodeStream(it) }

    val result =
        matcher.compareBitmaps(
            expected = expected.toIntArray(),
            given = actual.toIntArray(),
            width = actual.width,
            height = actual.height,
        )
    if (!result.matches) {
      val timestamp = System.currentTimeMillis()
      saveScreenshot("$name-actual-$timestamp", actual)
      saveScreenshot("$name-diff-$timestamp", requireNotNull(result.diff))
      throw AssertionError("Mismatching screenshots!")
    }
  } catch (ex: FileNotFoundException) {
    val timestamp = System.currentTimeMillis()
    saveScreenshot("missing-$timestamp", actual)
    if (failIfAbsent)
        throw AssertionError("Missing screenshot! Are you sure it's in the assets folder?")
  }
}

/** Maps a [Bitmap] to an [IntArray] with its pixel values. */
private fun Bitmap.toIntArray(): IntArray {
  val bitmapArray = IntArray(width * height)
  getPixels(bitmapArray, 0, width, 0, 0, width, height)
  return bitmapArray
}

/** Saves a [Bitmap] to a file with the provided [filename]. */
private fun saveScreenshot(filename: String, bmp: Bitmap) {
  val path =
      InstrumentationRegistry.getInstrumentation()
              .targetContext
              .getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
          .canonicalPath
  FileOutputStream("$path/$filename.png").use { out ->
    bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
  }
  val filePath = "$path/$filename.png"
  Log.w(
      "Screenshots",
      "Saved $filePath. Use \"adb pull $filePath img.png\" to pull it.",
  )
}
