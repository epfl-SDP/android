package ch.epfl.sdp.mobile.test.state

import androidx.test.ext.junit.rules.ActivityScenarioRule
import ch.epfl.sdp.mobile.state.HomeActivity
import org.junit.Rule

class HomeActivityTest {

  @get:Rule val rule = ActivityScenarioRule(HomeActivity::class.java)

  /** This test is not isolated and causes other test to fail. TODO: create github issue */
  /**
   * @Test fun activity_getsCreated() { rule.scenario.moveToState(Lifecycle.State.CREATED)
   * assertThat(rule.scenario.state).isEqualTo(Lifecycle.State.CREATED) }
   */
}
