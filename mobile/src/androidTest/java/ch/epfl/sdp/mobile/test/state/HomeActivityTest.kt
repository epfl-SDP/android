package ch.epfl.sdp.mobile.test.state

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sdp.mobile.state.HomeActivity
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {

  @get:Rule val rule = ActivityScenarioRule(HomeActivity::class.java)

  @Test
  fun activity_getsCreated() {
    rule.scenario.moveToState(Lifecycle.State.CREATED)
    assertThat(rule.scenario.state).isEqualTo(Lifecycle.State.CREATED)
  }
}
