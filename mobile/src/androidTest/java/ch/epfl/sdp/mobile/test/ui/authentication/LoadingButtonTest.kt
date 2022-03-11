package ch.epfl.sdp.mobile.test.ui.authentication

import androidx.compose.ui.semantics.ProgressBarRangeInfo.Companion.Indeterminate
import androidx.compose.ui.semantics.SemanticsProperties.ProgressBarRangeInfo
import androidx.compose.ui.test.SemanticsMatcher.Companion.expectValue
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.ui.authentication.LoadingButton
import org.junit.Rule
import org.junit.Test

class LoadingButtonTest {

  @get:Rule val rule = createComposeRule()

  @Test
  fun loadingButton_hasProgressSemantics() {
    rule.setContent { LoadingButton(loading = true, onClick = {}) {} }
    rule.onNode(expectValue(ProgressBarRangeInfo, Indeterminate)).assertExists()
  }

  @Test
  fun notLoadingButton_hasNoProgressSemantics() {
    rule.setContent { LoadingButton(loading = false, onClick = {}) {} }
    rule.onNode(keyIsDefined(ProgressBarRangeInfo)).assertDoesNotExist()
  }
}
