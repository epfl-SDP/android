package ch.epfl.sdp.mobile.infrastructure.sound.android

import android.content.Context
import android.media.MediaPlayer
import ch.epfl.sdp.mobile.R
import ch.epfl.sdp.mobile.infrastructure.sound.SoundPlayer

/** A class providing the ability to play a chess sound. */
class AndroidSoundPlayer(
    context: Context,
    private val mediaPlayer: MediaPlayer = MediaPlayer.create(context, R.raw.chess_piece_sound)
) : SoundPlayer {

  override fun playChessSound() {
    mediaPlayer.start()
  }
}
