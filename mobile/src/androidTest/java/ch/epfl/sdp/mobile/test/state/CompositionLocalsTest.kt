package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.state.*
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class CompositionLocalsTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun missingAuthenticationApi_throwsException() {
    assertThrows(IllegalStateException::class.java) {
      rule.setContent { LocalAuthenticationFacade.current }
    }
  }

  @Test
  fun missingChessFacade_throwsException() {
    assertThrows(IllegalStateException::class.java) { rule.setContent { LocalChessFacade.current } }
  }

  @Test
  fun missingSocialFacade_throwsException() {
    assertThrows(IllegalStateException::class.java) {
      rule.setContent { LocalSocialFacade.current }
    }
  }

  @Test
  fun missingSpeechFacade_throwsException() {
    assertThrows(IllegalStateException::class.java) {
      rule.setContent { LocalSpeechFacade.current }
    }
  }

  @Test
  fun missingSettingsFacade_throwsException() {
    assertThrows(IllegalStateException::class.java) {
      rule.setContent { LocalSettingsFacade.current }
    }
  }
}
