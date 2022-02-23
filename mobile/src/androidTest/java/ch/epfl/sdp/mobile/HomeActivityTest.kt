package ch.epfl.sdp.mobile

import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {

  @get:Rule val rule = ActivityScenarioRule(HomeActivity::class.java)

  @Test
  fun activity_getsCreated() {
    rule.scenario.moveToState(Lifecycle.State.CREATED)
    assertTrue(rule.scenario.state.isAtLeast(Lifecycle.State.CREATED))
  }
}
