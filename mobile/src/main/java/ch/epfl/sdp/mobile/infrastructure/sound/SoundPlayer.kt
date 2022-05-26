package ch.epfl.sdp.mobile.infrastructure.sound

import android.net.Uri
import androidx.compose.runtime.Composable

/** An interface providing the ability to play a sound. */
interface SoundPlayer {

  /**
   * Plays the provided sound.
   *
   * @param uri the uri of the sound to play.
   */
  @Composable fun playSound(uri: Uri)
}
