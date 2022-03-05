package ch.epfl.sdp.mobile.ui

import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test

class ApiCompositionLocalsTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun missingAuthenticationApi_throwsException() {
    assertThrows(IllegalStateException::class.java) {
      rule.setContent { LocalAuthenticationApi.current }
    }
  }
}
