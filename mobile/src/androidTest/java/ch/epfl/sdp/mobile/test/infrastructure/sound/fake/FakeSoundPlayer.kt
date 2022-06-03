package ch.epfl.sdp.mobile.test.infrastructure.sound.fake

import ch.epfl.sdp.mobile.infrastructure.sound.SoundPlayer

/** An object that allows mocking the [SoundPlayer]. */
object FakeSoundPlayer : SoundPlayer {
  override fun playChessSound() {}
}
