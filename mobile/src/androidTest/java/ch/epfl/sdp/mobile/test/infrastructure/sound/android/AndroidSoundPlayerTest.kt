package ch.epfl.sdp.mobile.test.infrastructure.sound.android

import android.content.Context
import android.media.MediaPlayer
import ch.epfl.sdp.mobile.infrastructure.sound.android.AndroidSoundPlayer
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class AndroidSoundPlayerTest {
  @Test
  fun given_androidSoundPlayer_when_playSound_then_mediaPlayerStartCalled() = runTest {
    val mediaPlayer = mockk<MediaPlayer>()
    val context = mockk<Context>()
    val soundPlayer = AndroidSoundPlayer(context = context, mediaPlayer = mediaPlayer)
    every { mediaPlayer.start() } returns Unit
    soundPlayer.playChessSound()
    verify { mediaPlayer.start() }
  }
}
