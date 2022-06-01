package ch.epfl.sdp.mobile.infrastructure.sound.android

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import ch.epfl.sdp.mobile.infrastructure.sound.SoundPlayer

/** A class providing the ability to play a chess sound. */
class AndroidSoundPlayer(context: Context, private val mediaPlayer: MediaPlayer = MediaPlayer()) :
    SoundPlayer {
  init {
    // val uri: Uri by lazy { R.raw.ChessPieceSound }
    val uri: Uri by lazy { TODO() }
    mediaPlayer.setDataSource(context, uri)
  }

  override fun playChessSound() {
    mediaPlayer.start()
  }
}
