package ch.epfl.sdp.mobile.test.state

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.sdp.mobile.application.TournamentDocument
import ch.epfl.sdp.mobile.infrastructure.persistence.store.asFlow
import ch.epfl.sdp.mobile.state.StatefulCreateTournamentScreen
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StatefulCreateTournamentScreenTest {
  @get:Rule val rule = createComposeRule()

  @Test
  fun given_statefulCreateTournamentScreen_when_displayingIt_then_itIsDisplayed() = runTest {
    val (_, _, strings) =
        rule.setContentWithTestEnvironment {
          StatefulCreateTournamentScreen(user = user, navigateToTournament = {}, cancelClick = {})
        }

    rule.onNodeWithText(strings.tournamentsCreateTitle).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateRules).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateBestOf).assertExists()
    rule.onNodeWithText(strings.tournamentsCreatePlayers).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateMaximumPlayerHint).assertExists()
    rule.onNodeWithText(strings.tournamentsCreatePoolSize).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateDirectElimination).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateQualifierSize0).assertExists()
    rule.onNodeWithText(strings.tournamentsCreateElimDepthFinal).assertExists()
  }

  @Test
  fun given_statefulCreateTournamentScreen_when_inputParameters_then_canClickCreate() = runTest {
    val (_, _, strings) =
        rule.setContentWithTestEnvironment {
          StatefulCreateTournamentScreen(user = user, navigateToTournament = {}, cancelClick = {})
        }

    rule.onNodeWithText(strings.tournamentsCreateActionCreate).assertIsNotEnabled()

    rule.onNodeWithText(strings.tournamentsCreateNameHint).performTextInput("Test name")
    rule.onNodeWithText("1").performClick()
    rule.onNodeWithText(strings.tournamentsCreateMaximumPlayerHint).performTextInput("4")
    rule.onNodeWithText(strings.tournamentsCreateQualifierSize0).performClick()
    rule.onNodeWithText(strings.tournamentsCreateElimDemomN(2)).performClick()

    rule.onNodeWithText(strings.tournamentsCreateActionCreate).assertIsEnabled()
  }

  @Test
  fun given_statefulCreateTournamentScreen_when_inputParameters_then_correspondingTournamentCreated() =
      runTest {
    val (_, infra, strings, user) =
        rule.setContentWithTestEnvironment {
          StatefulCreateTournamentScreen(
              user = user,
              navigateToTournament = {},
              cancelClick = {},
          )
        }

    rule.onNodeWithText(strings.tournamentsCreateNameHint).performTextInput("Test name")
    rule.onNodeWithText("1").performClick()
    rule.onNodeWithText(strings.tournamentsCreateMaximumPlayerHint).performTextInput("4")
    rule.onNodeWithText(strings.tournamentsCreateQualifierSize0).performClick()
    rule.onNodeWithText(strings.tournamentsCreateElimDemomN(2)).performClick()

    rule.onNodeWithText(strings.tournamentsCreateActionCreate).performClick()

    val doc =
        infra
            .store
            .collection(TournamentDocument.Collection)
            .whereEquals("name", "Test name")
            .asFlow<TournamentDocument>()
            .first()
            .firstOrNull()

    assertThat(doc).isNotNull()

    doc?.let {
      assertThat(doc.adminId).isEqualTo(user.uid)
      assertThat(doc.name).isEqualTo("Test name")
      assertThat(doc.bestOf).isEqualTo(1)
      assertThat(doc.maxPlayers).isEqualTo(4)
      assertThat(doc.poolSize).isEqualTo(0)
      assertThat(doc.eliminationRounds).isEqualTo(2)
    }
  }
}
