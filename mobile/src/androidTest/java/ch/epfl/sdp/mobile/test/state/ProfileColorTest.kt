package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.graphics.Color
import ch.epfl.sdp.mobile.application.Profile.Color as ProfileColor
import ch.epfl.sdp.mobile.state.toColor
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ProfileColorTest {

  @Test
  fun toColor_worksWithGoodColor() {
    assertThat(ProfileColor("#01234567").toColor()).isEqualTo(Color(0x01234567))
  }

  @Test
  fun toColor_withMissingLeadingHashtag_usesDefault() {
    assertThat(ProfileColor("01234567").toColor()).isEqualTo(ProfileColor.Default.toColor())
  }
}
