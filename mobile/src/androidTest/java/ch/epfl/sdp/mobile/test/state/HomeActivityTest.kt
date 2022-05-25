package ch.epfl.sdp.mobile.test.state

import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sdp.mobile.state.HomeActivity
import org.junit.Rule

class HomeActivityTest {

  @get:Rule val rule = ActivityScenarioRule(HomeActivity::class.java)

  /**
   * @Test fun activity_getsCreated() { rule.scenario.moveToState(Lifecycle.State.CREATED)
   * assertThat(rule.scenario.state).isEqualTo(Lifecycle.State.CREATED) }
   */
}
