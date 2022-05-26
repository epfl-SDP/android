package ch.epfl.sdp.mobile.infrastructure.sound.android

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import ch.epfl.sdp.mobile.infrastructure.sound.SoundPlayer

/** A class providing the ability to play a sound. */
class AndroidSoundPlayer : SoundPlayer {
  @Composable
  override fun playSound(uri: Uri) {
    val context = LocalContext.current
    val mMediaPlayer = MediaPlayer.create(context, uri)
    mMediaPlayer.start()
  }
}
